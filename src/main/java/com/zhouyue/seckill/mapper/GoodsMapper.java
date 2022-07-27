package com.zhouyue.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouyue.seckill.pojo.Goods;
import com.zhouyue.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-27
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
