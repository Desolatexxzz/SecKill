package com.zhouyue.seckill.controller;

import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 商品
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;
    /**
     * 跳转到商品列表
     * @param model
     * @param ticket
     * @return
     */
    @RequestMapping("/toList")
    public String toList(Model model, User user){
//        if (ObjectUtils.isEmpty(ticket)){
//            return "login";
//        }
////        User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(ticket, request, response);
        if (user == null){
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }
}
