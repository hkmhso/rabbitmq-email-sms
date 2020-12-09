package com.sunnsoft.rabbitmq.email.sms.producer.topic;

import com.sunnsoft.rabbitmq.email.sms.config.RabbitConfirmAndReturn;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProducerMatchMultiTopic {
    public static final String MATCH_MULTI_ROUTING_KEY = "topic_routing_key.match.multi";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfirmAndReturn rabbitConfirmAndReturn;

    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * confirm消息确认机制：
     * 根据交换机名称和路由键精确匹配到对应的消息队列
     * @param exchangeName 交换机名称
     */
    public void sendForNotScheduledForConfirm(String exchangeName) {
        String msg = "发送给消费者的路由键为" + ProducerMatchMultiTopic.MATCH_MULTI_ROUTING_KEY;
        String messageId=UUID.randomUUID() + "";
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT)
                .setContentEncoding("utf-8").setMessageId(messageId).build();
        // 构建回调返回的数据
        CorrelationData correlationData = new CorrelationData(messageId);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        //设置确认消息是否发送到交换机(Exchange)回调函数
        rabbitTemplate.setConfirmCallback(rabbitConfirmAndReturn);
        //设置确认消息是否发送到队列(Queue)回调函数
        rabbitTemplate.setReturnCallback(rabbitConfirmAndReturn);
        rabbitTemplate.convertAndSend(exchangeName,ProducerMatchMultiTopic.MATCH_MULTI_ROUTING_KEY,message,correlationData);
    }

}
