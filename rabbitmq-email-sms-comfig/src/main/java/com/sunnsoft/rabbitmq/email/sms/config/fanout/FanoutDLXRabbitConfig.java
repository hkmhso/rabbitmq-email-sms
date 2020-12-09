package com.sunnsoft.rabbitmq.email.sms.config.fanout;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : hkm
 * @CreateTime : 2020/10/27
 * @Description :死信交换机
 **/
@Configuration
public class FanoutDLXRabbitConfig {

    public static final String FANOUT_EMAIL_DLX_QUEUE_NAME = "fanout_email_queue_dlx";
    public static final String FANOUT_EMAIL_DLX_ROUTING_KEY = "fanout_email_routing_key_dlx";
    public static final String FANOUT_SMS_DLX_QUEUE_NAME = "fanout_sms_queue_dlx";
    public static final String FANOUT_SMS_DLX_ROUTING_KEY = "fanout_sms_routing_key_dlx";
    public static final String FANOUT_DLX_EXCHANGE_NAME = "fanout_dlx_exchange";
    /**
     * 定义优先级队列
     */
    public static final Long DEAD_LETTER_MAX_PRIORITY = 15L;
    /**
     * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
     */
    public static final Long DEAD_LETTER_MESSAGE_TTL = 10000L;

    //邮件死信消息队列
    @Bean
    public Queue fanoutQueueEmailDLX() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(FanoutDLXRabbitConfig.FANOUT_EMAIL_DLX_QUEUE_NAME,true,false, false);
        //return QueueBuilder.durable(DLXRabbitConfig.FANOUT_EMAIL_DLX_QUEUE_NAME).build();

    }

    //短信死信消息队列
    @Bean
    public Queue fanoutQueueSmsDLX() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(FanoutDLXRabbitConfig.FANOUT_SMS_DLX_QUEUE_NAME,true,false, false);
        //return QueueBuilder.durable(DLXRabbitConfig.FANOUT_SMS_DLX_QUEUE_NAME).build();

    }

    //死信交换机
    @Bean
    public DirectExchange fanoutExchangeDLX() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange(FanoutDLXRabbitConfig.FANOUT_DLX_EXCHANGE_NAME,true,false);
    }

    //绑定  将邮件死信消息队列和死信交换机绑定, 并设置绑定键
    @Bean
    public Binding bindingFanoutDLXWithEmail() {
        return BindingBuilder.bind(fanoutQueueEmailDLX()).to(fanoutExchangeDLX()).with(FanoutDLXRabbitConfig.FANOUT_EMAIL_DLX_ROUTING_KEY);
    }

    //绑定  将短信死信消息队列和死信交换机绑定, 并设置绑定键
    @Bean
    public Binding bindingFanoutDLXWithSms() {
        return BindingBuilder.bind(fanoutQueueSmsDLX()).to(fanoutExchangeDLX()).with(FanoutDLXRabbitConfig.FANOUT_SMS_DLX_ROUTING_KEY);
    }


}
