package com.zhouyue.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyue.seckill.mapper.OrderMapper;
import com.zhouyue.seckill.pojo.Order;
import com.zhouyue.seckill.pojo.SeckillGoods;
import com.zhouyue.seckill.pojo.SeckillOrder;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IOrderService;
import com.zhouyue.seckill.service.ISeckillGoodsService;
import com.zhouyue.seckill.service.ISeckillOrderService;
import com.zhouyue.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-27
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀商品
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        boolean result = seckillGoodsService
                .update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1")
                        .eq("goods_id", goodsVo.getId())
                        .gt("stock_count", 0));
        if (!result){
            return null;
        }
        //创建订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId()+":"+goodsVo.getId(), seckillOrder);

        return order;

    }
}
