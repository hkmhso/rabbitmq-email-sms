package com.sunnsoft.rabbitmq.email.sms.config.topic;

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
public class TopicDLXRabbitConfig {

    public static final String TOPIC_MATCH_ONE_DLX_QUEUE_NAME = "topic_match_one_queue_dlx";
    public static final String TOPIC_MATCH_ONE_DLX_ROUTING_KEY = "topic_match_one_routing_key_dlx";
    public static final String TOPIC_MATCH_MULTI_DLX_QUEUE_NAME = "topic_match_multi_queue_dlx";
    public static final String TOPIC_MATCH_MULTI_DLX_ROUTING_KEY = "topic_match_multi_routing_key_dlx";
    public static final String TOPIC_DLX_EXCHANGE_NAME = "topic_dlx_exchange";
    /**
     * 定义优先级队列
     */
    public static final Long DEAD_LETTER_MAX_PRIORITY = 15L;
    /**
     * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
     */
    public static final Long DEAD_LETTER_MESSAGE_TTL = 10000L;

    //*通配符死信消息队列
    @Bean
    public Queue topicQueueMathchOneDLX() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(TopicDLXRabbitConfig.TOPIC_MATCH_ONE_DLX_QUEUE_NAME,true,false, false);
        //return QueueBuilder.durable(DLXRabbitConfig.TOPIC_EMAIL_DLX_QUEUE_NAME).build();

    }

    //#通配符死信消息队列
    @Bean
    public Queue topicQueueMathchMultiDLX() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(TopicDLXRabbitConfig.TOPIC_MATCH_MULTI_DLX_QUEUE_NAME,true,false, false);
        //return QueueBuilder.durable(DLXRabbitConfig.TOPIC_SMS_DLX_QUEUE_NAME).build();

    }

    //死信交换机
    @Bean
    public DirectExchange topicExchangeDLX() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange(TopicDLXRabbitConfig.TOPIC_DLX_EXCHANGE_NAME,true,false);
    }

    //绑定  将*通配符死信消息队列和死信交换机绑定, 并设置绑定键
    @Bean
    public Binding bindingTopicDLXWithMathchOne() {
        return BindingBuilder.bind(topicQueueMathchOneDLX()).to(topicExchangeDLX()).with(TopicDLXRabbitConfig.TOPIC_MATCH_ONE_DLX_ROUTING_KEY);
    }

    //绑定  将#通配符死信消息队列和死信交换机绑定, 并设置绑定键
    @Bean
    public Binding bindingTopicDLXWithMathchMulti() {
        return BindingBuilder.bind(topicQueueMathchMultiDLX()).to(topicExchangeDLX()).with(TopicDLXRabbitConfig.TOPIC_MATCH_MULTI_DLX_ROUTING_KEY);
    }


}
