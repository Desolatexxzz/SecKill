package com.zhouyue.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyue.seckill.exception.GlobalException;
import com.zhouyue.seckill.pojo.Order;
import com.zhouyue.seckill.pojo.SeckillMessage;
import com.zhouyue.seckill.pojo.SeckillOrder;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.rabbitmq.MQSender;
import com.zhouyue.seckill.service.IGoodsService;
import com.zhouyue.seckill.service.IOrderService;
import com.zhouyue.seckill.service.ISeckillGoodsService;
import com.zhouyue.seckill.service.ISeckillOrderService;
import com.zhouyue.seckill.vo.GoodsVo;
import com.zhouyue.seckill.vo.RespBean;
import com.zhouyue.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
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
    /**
     * 秒杀
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSIONERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断订单，该用户是否已购买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATEERROR.getMessage());
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
