package com.zhouyue.seckill.controller;


import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IOrderService;
import com.zhouyue.seckill.vo.OrderDetailVo;
import com.zhouyue.seckill.vo.RespBean;
import com.zhouyue.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-27
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSIONERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }

}
