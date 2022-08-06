package com.zhouyue.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * 消息发送者
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;


//    public void sendFanout(Object msg){
//        log.info("发送消息: " +msg);
//        rabbitTemplate.convertAndSend("fanoutExchange","", msg);
//    }
//
//    public void sendDirect01(Object msg){
//        log.info("发送red消息: " + msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
//    }
//
//    public void sendDirect02(Object msg){
//        log.info("发送green消息: " + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
//    }
//
//    public void sendTopic01(Object msg){
//        log.info("发送direct01消息: " + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
//    }
//
//    public void sendTopic0102(Object msg){
//        log.info("发送direct01,02消息: " + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "double.queue.red.message", msg);
//    }
//
//    public void sendHeader0102(String msg){
//        log.info("发送header0102消息: " + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color", "red");
//        properties.setHeader("speed", "fast");
//        Message message = new Message(msg.getBytes(),properties);
//        rabbitTemplate.convertAndSend("headerExchange", "", message);
//    }
//
//    public void sendHeader01(String msg){
//        log.info("发送header01消息: " + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color", "red");
//        properties.setHeader("speed", "normal");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headerExchange", "", message);
//    }

    /**
     * 发送秒杀信息
     * @param message
     */
    public void sendSeckillMessage(String message){
        log.info("发送消息: " + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }
}
