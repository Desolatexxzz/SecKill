package com.zhouyue.seckill.controller;


import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-24
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 用户信息测试
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean getInfo(User user){
        return RespBean.success(user);
    }

}
