package com.zhouyue.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhouyue.seckill.pojo.SeckillOrder;
import com.zhouyue.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-27
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(User user, Long goodsId);
}
