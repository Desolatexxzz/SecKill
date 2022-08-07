package com.zhouyue.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.zhouyue.seckill.exception.GlobalException;
import com.zhouyue.seckill.pojo.*;
import com.zhouyue.seckill.rabbitmq.MQSender;
import com.zhouyue.seckill.service.IGoodsService;
import com.zhouyue.seckill.service.IOrderService;
import com.zhouyue.seckill.service.ISeckillGoodsService;
import com.zhouyue.seckill.service.ISeckillOrderService;
import com.zhouyue.seckill.validator.AccessLimit;
import com.zhouyue.seckill.vo.GoodsVo;
import com.zhouyue.seckill.vo.RespBean;
import com.zhouyue.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
@Slf4j
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();
    @Autowired
    private RedisScript<Long> script;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    /**
     * 秒杀
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path,  User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSIONERROR);
        }
        // 判断路径
        ValueOperations valueOperations = redisTemplate.opsForValue();
       boolean check = orderService.checkPath(user, goodsId, path);
       if (!check){
           return RespBean.error(RespBeanEnum.REQUESTILLEGAL);
       }
        //判断订单，该用户是否已购买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATEERROR);
        }
        // 内存标记，减少redis访问
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTYSTOCK);
        }
        // 该方法每执行一次就将对应key的值减一，并且操作是原子性的，用来预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0){
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTYSTOCK);
        }
        // 使用 lua 脚本来预减库存
//        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
//        if (stock < 0){
//            emptyStockMap.put(goodsId, true);
//            return RespBean.error(RespBeanEnum.EMPTYSTOCK);
//        }
        //使用rabbitmq 发送消息
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mqSender.sendSeckillMessage(objectMapper.writeValueAsString(seckillMessage));
            return RespBean.success(0);
        } catch (JsonProcessingException e) {
            throw new GlobalException(RespBeanEnum.Error);
        }

        /*
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTYSTOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTYSTOCK);
        }
        //判断订单，该用户是否已购买
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATEERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATEERROR);
        }
        Order order = orderService.seckill(user, goodsVo);
        return RespBean.success(order);
         */

    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId: 成功 -1: 秒杀失败 0: 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSIONERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        //判断验证码
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERRORCAPTCHA);
        }
        // 判断商品是否为秒杀状态，在秒杀状态才返回路径
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsId));
        Date startDate = seckillGoods.getStartDate();
        Date endDate = seckillGoods.getEndDate();
        Date nowDate = new Date();
        if (nowDate.before(startDate)){
            return RespBean.error(RespBeanEnum.REQUESTILLEGAL);
        }else if (nowDate.after(endDate)){
            return RespBean.error(RespBeanEnum.REQUESTILLEGAL);
        }
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSIONERROR);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    /**
     * 获取验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void captcha(User user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUESTILLEGAL);
        }
        // 设置响应头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入 redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }

    /**
     * 系统初始化，将商品库存数量加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        for (GoodsVo goodsVo : list) {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        }
    }

}
