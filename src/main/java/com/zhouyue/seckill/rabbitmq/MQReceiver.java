package com.zhouyue.seckill.rabbitmq;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyue.seckill.exception.GlobalException;
import com.zhouyue.seckill.pojo.SeckillMessage;
import com.zhouyue.seckill.pojo.SeckillOrder;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IGoodsService;
import com.zhouyue.seckill.service.IOrderService;
import com.zhouyue.seckill.vo.GoodsVo;
import com.zhouyue.seckill.vo.RespBean;
import com.zhouyue.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */

@Service
@Slf4j
public class MQReceiver {
//    // Fanout模式
//    @RabbitListener(queues = "queue_fanout01")
//    public void receiveFanout01(Object msg){
//        log.info("QUEUE01接收消息(Fanout): " + msg);
//    }
//    @RabbitListener(queues = "queue_fanout02")
//    public void receiveFanout02(Object msg){
//        log.info("QUEUE02接收消息(Fanout): " + msg);
//    }
//    //Direct模式
//    @RabbitListener(queues = "queue_direct01")
//    public void receiveDirect01(Object msg){
//        log.info("QUEUE01接收消息(Direct): " + msg);
//    }
//    @RabbitListener(queues = "queue_direct02")
//    public void receiveDirect02(Object msg){
//        log.info("QUEUE02接收消息(Direct): " + msg);
//    }
//    //Topic模式
//    @RabbitListener(queues = "queue_topic01")
//    public void receiveTopic01(Object msg){
//        log.info("QUEUE01接收消息(Topic): " + msg);
//    }
//    @RabbitListener(queues = "queue_topic02")
//    public void receiveTopic02(Object msg){
//        log.info("QUEUE02接收消息(Topic): " + msg);
//    }
//    //Headers模式
//    @RabbitListener(queues = "queue_header01")
//    public void receiveHeader01(Message message){
//        log.info("QUEUE01接收Message对象: " + message);
//        log.info("QUEUE01接收消息: " + new String(message.getBody()));
//    }
//    @RabbitListener(queues = "queue_header02")
//    public void receiveHeader02(Message message){
//        log.info("QUEUE02接收Message对象: " + message);
//        log.info("QUEUE02接收消息: " + new String(message.getBody()));
//    }

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收消息: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        SeckillMessage seckillMessage = null;
        try {
            seckillMessage = objectMapper.readValue(message, SeckillMessage.class);
        } catch (JsonProcessingException e) {
            throw new GlobalException(RespBeanEnum.Error);
        }
        User user = seckillMessage.getUser();
        Long goodId = seckillMessage.getGoodId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        if (goodsVo.getStockCount() < 1){
            return;
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        if (seckillOrder != null){
            return;
        }
        // 下单操作
        orderService.seckill(user, goodsVo);
    }
}
