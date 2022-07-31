package com.zhouyue.seckill.controller;

import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IGoodsService;
import com.zhouyue.seckill.service.IUserService;
import com.zhouyue.seckill.vo.GoodsVo;
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
import java.util.Date;

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
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)){
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goodsDetail";
    }
}
