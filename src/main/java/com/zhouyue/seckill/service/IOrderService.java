package com.zhouyue.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhouyue.seckill.pojo.Order;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.vo.GoodsVo;
import com.zhouyue.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-27
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀商品
     * @param user
     * @param goodsVo
     * @return
     */
    Order seckill(User user, GoodsVo goodsVo);

    /**
     * 查询订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);
}
