package com.zhouyue.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyue.seckill.mapper.OrderMapper;
import com.zhouyue.seckill.mapper.SeckillOrderMapper;
import com.zhouyue.seckill.pojo.Order;
import com.zhouyue.seckill.pojo.SeckillOrder;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-27
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (order != null){
            return order.getId();
        }else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }else {
            return 0L;
        }


    }
}
