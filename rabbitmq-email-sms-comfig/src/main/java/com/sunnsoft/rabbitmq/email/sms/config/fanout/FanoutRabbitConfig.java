package com.sunnsoft.rabbitmq.email.sms.config.fanout;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : hkm
 * @CreateTime : 2020/10/22
 * @Description :fanout交换机
 **/
@Configuration
public class FanoutRabbitConfig {

    public static final String SMS_QUEUE_NAME = "fanout_queue_sms";
    public static final String EMAIL_QUEUE_NAME = "fanout_queue_email";
    public static final String EXCHANGE_NAME = "fanout_exchange";
    /**
     * 业务消息队列和死信交换机绑定的标识符
     */
    public static final String DEAD_LETTER_EXCHANGE_KEY = "x-dead-letter-exchange";
    /**
     * 业务消息队列和死信交换机的绑定键的标识符
     */
    public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    /**
     * 定义优先级队列
     */
    public static final String DEAD_LETTER_MAX_PRIORITY = "x-max-prioritye";
    /**
     * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
     */
    public static final String DEAD_LETTER_MESSAGE_TTL = "x-message-ttl";


    //短信消息队列
    @Bean
    public Queue fanoutQueueSms() {

        Map<String, Object> map = new HashMap<>();
        /**
         * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
         */
        //map.put(FanoutRabbitConfig.DEAD_LETTER_MESSAGE_TTL, FanoutDLXRabbitConfig.DEAD_LETTER_MESSAGE_TTL);
        /**
         * key:业务消息队列和死信交换机绑定的标识符(DLX)  value:死信交换机的名称
         */
        map.put(FanoutRabbitConfig.DEAD_LETTER_EXCHANGE_KEY, FanoutDLXRabbitConfig.FANOUT_DLX_EXCHANGE_NAME);
        /**
         * key:业务消息队列和死信交换机的绑定键的标识符(DLK) value:业务消息队列和死信交换机的绑定键
         */
        map.put(FanoutRabbitConfig.DEAD_LETTER_ROUTING_KEY, FanoutDLXRabbitConfig.FANOUT_SMS_DLX_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        map.put(FanoutRabbitConfig.DEAD_LETTER_MAX_PRIORITY, FanoutDLXRabbitConfig.DEAD_LETTER_MAX_PRIORITY);
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(FanoutRabbitConfig.SMS_QUEUE_NAME,true,false, false,map);
        //return QueueBuilder.durable(FanoutRabbitConfig.SMS_QUEUE_NAME).withArguments(map).build();
    }

    //邮件消息队列
    @Bean
    public Queue fanoutQueueEmail() {
        Map<String, Object> map = new HashMap<>();
        /**
         * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
         */
        //map.put(FanoutRabbitConfig.DEAD_LETTER_MESSAGE_TTL, FanoutDLXRabbitConfig.DEAD_LETTER_MESSAGE_TTL);
        /**
         * key:业务消息队列和死信交换机绑定的标识符(DLX)  value:死信交换机的名称
         */
        map.put(FanoutRabbitConfig.DEAD_LETTER_EXCHANGE_KEY, FanoutDLXRabbitConfig.FANOUT_DLX_EXCHANGE_NAME);
        /**
         * key:业务消息队列和死信交换机的绑定键的标识符(DLK) value:业务消息队列和死信交换机的绑定键
         */
        map.put(FanoutRabbitConfig.DEAD_LETTER_ROUTING_KEY, FanoutDLXRabbitConfig.FANOUT_EMAIL_DLX_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        map.put(FanoutRabbitConfig.DEAD_LETTER_MAX_PRIORITY, FanoutDLXRabbitConfig.DEAD_LETTER_MAX_PRIORITY);
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(FanoutRabbitConfig.EMAIL_QUEUE_NAME,true,false, false,map);
        //return QueueBuilder.durable(FanoutRabbitConfig.EMAIL_QUEUE_NAME).withArguments(map).build();
    }

    //fanout交换机
    @Bean
    public FanoutExchange fanoutExchange() {
        //  return new fanoutExchange("TestfanoutExchange",true,true);
        return new FanoutExchange(FanoutRabbitConfig.EXCHANGE_NAME,true,false);
    }

    //绑定  将短信消息队列和交换机绑定, 不用设置绑定键，即使设置了也没用
    @Bean
    public Binding bindingFanoutWithSms() {
        return BindingBuilder.bind(fanoutQueueSms()).to(fanoutExchange());
    }

    //绑定  将邮件消息队列和交换机绑定, 不用设置绑定键，即使设置了也没用
    @Bean
    public Binding bindingFanoutWithEmail() {
        return BindingBuilder.bind(fanoutQueueEmail()).to(fanoutExchange());
    }

}