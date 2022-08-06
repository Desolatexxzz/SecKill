package com.zhouyue.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhouyue.seckill.pojo.Order;
import com.zhouyue.seckill.pojo.SeckillOrder;
import com.zhouyue.seckill.pojo.User;
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

import java.util.List;

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
        }
    }
}
