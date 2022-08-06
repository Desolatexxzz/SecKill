package com.zhouyue.seckill.controller;


import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.rabbitmq.MQSender;
import com.zhouyue.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
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
//    @Autowired
//    private MQSender mqSender;
//
//
//    /**
//     * 测试fanout模式
//     */
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq01(){
//        mqSender.sendFanout("Hello,World");
//    }
//
//    /**
//     * 测试 direct 模式
//     */
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq02(){
//        mqSender.sendDirect01("Hello,Red");
//    }
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq03(){
//        mqSender.sendDirect02("Hello,Green");
//    }
//
//    /**
//     * 测试 topic 模式
//     */
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq04(){
//        mqSender.sendTopic01("Hello,Topic01");
//    }
//    @RequestMapping("/mq/topic0102")
//    @ResponseBody
//    public void mq05(){
//        mqSender.sendTopic0102("Hello,Topic0102");
//    }
//
//    /**
//     * 测试 headers 模式
//     */
//    @RequestMapping("/mq/header01")
//    @ResponseBody
//    public void mq06(){
//        mqSender.sendHeader01("Hello,Header01");
//    }
//    @RequestMapping("/mq/header0102")
//    @ResponseBody
//    public void mq07(){
//        mqSender.sendHeader0102("Hello,Header0102");
//    }


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
