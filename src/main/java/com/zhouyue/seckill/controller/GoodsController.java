package com.zhouyue.seckill.controller;

import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IGoodsService;
import com.zhouyue.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    private IGoodsService goodsService;
    /**
     * 跳转到商品列表
     * @param model
     * @return
     */
    @RequestMapping("/toList")
    public String toList(Model model, User user){
        if (user == null){
            return "login";
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }

    /**
     * 跳转商品详情页
     * @return
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetails(Model model, User user, @PathVariable Long goodsId){
        model.addAttribute("user", user);
        model.addAttribute("goods", goodsService.findGoodsVoByGoodsId(goodsId));
        return "goodsDetail";
    }
}
