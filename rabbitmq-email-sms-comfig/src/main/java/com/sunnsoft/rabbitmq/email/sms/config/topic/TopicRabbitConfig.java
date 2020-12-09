package com.sunnsoft.rabbitmq.email.sms.config.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author : hkm
 * @CreateTime : 2020/10/22
 * @Description :topic交换机
 **/
@Configuration
public class TopicRabbitConfig {

    public static final String MATCH_ONE_QUEUE_NAME = "topic_queue_match_one";
    //特别注意：其他字符和通配符必须以.隔开，其他的符号不行
    public static final String MATCH_ONE_ROUTING_KEY = "topic_routing_key.*";
    public static final String MATCH_MULTI_QUEUE_NAME = "topic_queue_match_multi";
    //特别注意：其他字符和通配符必须以.隔开，其他的符号不行
    public static final String MATCH_MULTI_ROUTING_KEY = "topic_routing_key.#";
    public static final String EXCHANGE_NAME = "topic_exchange";
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

    //*通配符消息队列
    @Bean
    public Queue topicQueueMatchOne() {
        Map<String, Object> map = new HashMap<>();
        /**
         * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
         */
        //map.put(TopicRabbitConfig.DEAD_LETTER_MESSAGE_TTL, TopicDLXRabbitConfig.DEAD_LETTER_MESSAGE_TTL);
        /**
         * key:业务消息队列和死信交换机绑定的标识符(DLX)  value:死信交换机的名称
         */
        map.put(TopicRabbitConfig.DEAD_LETTER_EXCHANGE_KEY, TopicDLXRabbitConfig.TOPIC_DLX_EXCHANGE_NAME);
        /**
         * key:业务消息队列和死信交换机的绑定键的标识符(DLK) value:业务消息队列和死信交换机的绑定键
         */
        map.put(TopicRabbitConfig.DEAD_LETTER_ROUTING_KEY, TopicDLXRabbitConfig.TOPIC_MATCH_ONE_DLX_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        map.put(TopicRabbitConfig.DEAD_LETTER_MAX_PRIORITY, TopicDLXRabbitConfig.DEAD_LETTER_MAX_PRIORITY);
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(TopicRabbitConfig.MATCH_ONE_QUEUE_NAME,true,false, false,map);
    }

    //#通配符消息队列
    @Bean
    public Queue topicQueueMatchMulti() {
        Map<String, Object> map = new HashMap<>();
        /**
         * 设置消息发送到消息队列之后多久被丢弃，单位：毫秒
         */
        //map.put(TopicRabbitConfig.DEAD_LETTER_MESSAGE_TTL, TopicDLXRabbitConfig.DEAD_LETTER_MESSAGE_TTL);
        /**
         * key:业务消息队列和死信交换机绑定的标识符(DLX)  value:死信交换机的名称
         */
        map.put(TopicRabbitConfig.DEAD_LETTER_EXCHANGE_KEY, TopicDLXRabbitConfig.TOPIC_DLX_EXCHANGE_NAME);
        /**
         * key:业务消息队列和死信交换机的绑定键的标识符(DLK) value:业务消息队列和死信交换机的绑定键
         */
        map.put(TopicRabbitConfig.DEAD_LETTER_ROUTING_KEY, TopicDLXRabbitConfig.TOPIC_MATCH_MULTI_DLX_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        map.put(TopicRabbitConfig.DEAD_LETTER_MAX_PRIORITY, TopicDLXRabbitConfig.DEAD_LETTER_MAX_PRIORITY);
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，true:只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，默认也是false,这样会消息导致挤压，true:当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(TopicRabbitConfig.MATCH_MULTI_QUEUE_NAME,true,false, false,map);
    }

    //topic交换机
    @Bean
    public TopicExchange topicExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new TopicExchange(TopicRabbitConfig.EXCHANGE_NAME,true,false);
    }

    /**
     *  绑定  将消息队列1和交换机绑定, 并使用通配符的形式设置绑定键
     *  *  (星号) 用来表示一个单词 (必须出现的)
     *  #  (井号) 用来表示任意数量（零个或多个）单词
     * 通配的绑定键是跟队列进行绑定的，举个小例子
     * 队列Q1 绑定键为 *.TT.*     队列Q2绑定键为  TT.#
     * 如果一条消息携带的路由键为 A.TT.B，那么队列Q1将会收到；
     * 如果一条消息携带的路由键为TT.AA.BB，那么队列Q2将会收到；
     */
    @Bean
    public Binding bindingTopicWithMatchOne() {
        return BindingBuilder.bind(topicQueueMatchOne()).to(topicExchange()).with(TopicRabbitConfig.MATCH_ONE_ROUTING_KEY);
    }

    /**
     *  绑定  将消息队列1和交换机绑定, 并使用通配符的形式设置绑定键
     *  *  (星号) 用来表示一个单词 (必须出现的)
     *  #  (井号) 用来表示任意数量（零个或多个）单词
     * 通配的绑定键是跟队列进行绑定的，举个小例子
     * 队列Q1 绑定键为 *.TT.*     队列Q2绑定键为  TT.#
     * 如果一条消息携带的路由键为 A.TT.B，那么队列Q1将会收到；
     * 如果一条消息携带的路由键为TT.AA.BB，那么队列Q2将会收到；
     */
    @Bean
    public Binding bindingTopicWithMatchMulti() {
        return BindingBuilder.bind(topicQueueMatchMulti()).to(topicExchange()).with(TopicRabbitConfig.MATCH_MULTI_ROUTING_KEY);
    }


}