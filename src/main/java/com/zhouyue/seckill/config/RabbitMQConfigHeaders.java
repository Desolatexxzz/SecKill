package com.zhouyue.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RabbitMQ配置类-Headers模式
 */
@Configuration
public class RabbitMQConfigHeaders {

    private static final String QUEUE01="queue_header01";
    private static final String QUEUE02="queue_header02";
    private static final String EXCHANGE="headerExchange";

    @Bean
    public Queue queue01(){
        return new Queue(QUEUE01);
    }
    @Bean
    public Queue queue02(){
        return new Queue(QUEUE02);
    }
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(EXCHANGE);
    }
    @Bean
    public Binding binding01(){
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "low");
        // whereAny 匹配 map 中的一个键值对就行
        return BindingBuilder.bind(queue01()).to(headersExchange()).whereAny(map).match();
    }
    @Bean
    public Binding binding02(){
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "fast");
        // whereAll 匹配 map 中的所有键值对才行
        return BindingBuilder.bind(queue02()).to(headersExchange()).whereAll(map).match();
    }
}
