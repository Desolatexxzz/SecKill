package com.zhouyue.seckill.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */

@Service
@Slf4j
public class MQReceiver {
    // Fanout模式
    @RabbitListener(queues = "queue_fanout01")
    public void receiveFanout01(Object msg){
        log.info("QUEUE01接收消息(Fanout): " + msg);
    }
    @RabbitListener(queues = "queue_fanout02")
    public void receiveFanout02(Object msg){
        log.info("QUEUE02接收消息(Fanout): " + msg);
    }
    //Direct模式
    @RabbitListener(queues = "queue_direct01")
    public void receiveDirect01(Object msg){
        log.info("QUEUE01接收消息(Direct): " + msg);
    }
    @RabbitListener(queues = "queue_direct02")
    public void receiveDirect02(Object msg){
        log.info("QUEUE02接收消息(Direct): " + msg);
    }
    //Topic模式
    @RabbitListener(queues = "queue_topic01")
    public void receiveTopic01(Object msg){
        log.info("QUEUE01接收消息(Topic): " + msg);
    }
    @RabbitListener(queues = "queue_topic02")
    public void receiveTopic02(Object msg){
        log.info("QUEUE02接收消息(Topic): " + msg);
    }
    //Headers模式
    @RabbitListener(queues = "queue_header01")
    public void receiveHeader01(Message message){
        log.info("QUEUE01接收Message对象: " + message);
        log.info("QUEUE01接收消息: " + new String(message.getBody()));
    }
    @RabbitListener(queues = "queue_header02")
    public void receiveHeader02(Message message){
        log.info("QUEUE02接收Message对象: " + message);
        log.info("QUEUE02接收消息: " + new String(message.getBody()));
    }
}
