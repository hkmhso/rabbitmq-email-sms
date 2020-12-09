package com.sunnsoft.rabbitmq.email.sms.config.direct;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : hkm
 * @CreateTime : 2020/10/22
 * @Description :direct交换机
 **/
@Configuration
public class DirectRabbitConfig {

    public static final String SMS_QUEUE_NAME = "direct_queue_sms";
    public static final String SMS_ROUTING_KEY = "direct_routing_key_sms";
    public static final String EMAIL_QUEUE_NAME = "direct_queue_email";
    public static final String EMAIL_ROUTING_KEY = "direct_routing_key_email";
    public static final String EXCHANGE_NAME = "direct_exchange";
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
    public Queue directQueueSms() {

        Map<String, Object> map = new HashMap<>();
        /**
         * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
         */
        //map.put(DirectRabbitConfig.DEAD_LETTER_MESSAGE_TTL, DirectDLXRabbitConfig.DEAD_LETTER_MESSAGE_TTL);
        /**
         * key:业务消息队列和死信交换机绑定的标识符(DLX)  value:死信交换机的名称
         */
        map.put(DirectRabbitConfig.DEAD_LETTER_EXCHANGE_KEY, DirectDLXRabbitConfig.DIRECT_DLX_EXCHANGE_NAME);
        /**
         * key:业务消息队列和死信交换机的绑定键的标识符(DLK) value:业务消息队列和死信交换机的绑定键
         */
        map.put(DirectRabbitConfig.DEAD_LETTER_ROUTING_KEY, DirectDLXRabbitConfig.DIRECT_SMS_DLX_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        map.put(DirectRabbitConfig.DEAD_LETTER_MAX_PRIORITY, DirectDLXRabbitConfig.DEAD_LETTER_MAX_PRIORITY);
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        /**
         * 将业务消息队列和死信交换机进行绑定，并设置绑定键
         */
        return new Queue(DirectRabbitConfig.SMS_QUEUE_NAME,true,false, false, map);
        //return QueueBuilder.durable(DirectRabbitConfig.SMS_QUEUE_NAME).withArguments(map).build();
    }

    //邮件消息队列
    @Bean
    public Queue directQueueEmail() {

        Map<String, Object> map = new HashMap<>();
        /**
         * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
         */
        //map.put(DirectRabbitConfig.DEAD_LETTER_MESSAGE_TTL, DirectDLXRabbitConfig.DEAD_LETTER_MESSAGE_TTL);
        /**
         * key:业务消息队列和死信交换机绑定的标识符(DLX)  value:死信交换机的名称
         */
        map.put(DirectRabbitConfig.DEAD_LETTER_EXCHANGE_KEY, DirectDLXRabbitConfig.DIRECT_DLX_EXCHANGE_NAME);
        /**
         * key:业务消息队列和死信交换机的绑定键的标识符(DLK) value:业务消息队列和死信交换机的绑定键
         */
        map.put(DirectRabbitConfig.DEAD_LETTER_ROUTING_KEY, DirectDLXRabbitConfig.DIRECT_EMAIL_DLX_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        map.put(DirectRabbitConfig.DEAD_LETTER_MAX_PRIORITY, DirectDLXRabbitConfig.DEAD_LETTER_MAX_PRIORITY);

        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        /**
         * 将业务消息队列和死信交换机进行绑定，并设置绑定键
         */
        return new Queue(DirectRabbitConfig.EMAIL_QUEUE_NAME,true,false, false, map);
        //return QueueBuilder.durable(DirectRabbitConfig.EMAIL_QUEUE_NAME).withArguments(map).build();

    }

    //direct交换机
    @Bean
    public DirectExchange directExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange(DirectRabbitConfig.EXCHANGE_NAME,true,false);
    }

    //交换机,但是不绑定队列
    @Bean
    public DirectExchange directExchangeNotBindQueue() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange("notBindQueue",true,false);
    }

    //绑定  将短信消息队列和交换机绑定, 并设置绑定键
    @Bean
    public Binding bindingDirectWithSms() {
        return BindingBuilder.bind(directQueueSms()).to(directExchange()).with(DirectRabbitConfig.SMS_ROUTING_KEY);
    }

    //绑定  将邮件消息队列和交换机绑定, 并设置绑定键
    @Bean
    public Binding bindingDirectWithEmail() {
        return BindingBuilder.bind(directQueueEmail()).to(directExchange()).with(DirectRabbitConfig.EMAIL_ROUTING_KEY);
    }

}