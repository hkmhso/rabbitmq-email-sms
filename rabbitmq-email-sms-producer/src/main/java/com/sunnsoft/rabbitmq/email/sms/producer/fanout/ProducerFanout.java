package com.sunnsoft.rabbitmq.email.sms.producer.fanout;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sunnsoft.rabbitmq.email.sms.common.constants.EmailConstants;
import com.sunnsoft.rabbitmq.email.sms.common.constants.PhoneConstants;
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
public class ProducerFanout {

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
        JSONObject jsonObject = new JSONObject();
        String userName = System.currentTimeMillis() + "";
        //TOPIC：一对多，即一个消息发送者可以被多个消息接收者监听
        try {
            jsonObject.put("userName", userName);
            jsonObject.put(EmailConstants.TO_EMAIL_KEY,EmailConstants.TO_EMAIL);
            jsonObject.put(PhoneConstants.TO_PHONE_KEY,PhoneConstants.TO_PHONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String msg = jsonObject.toString();
        String messageId=UUID.randomUUID() + "";
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8").setMessageId(messageId).build();
        // 构建回调返回的数据
        CorrelationData correlationData = new CorrelationData(messageId);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        //设置确认消息是否发送到交换机(Exchange)回调函数
        rabbitTemplate.setConfirmCallback(rabbitConfirmAndReturn);
        //设置确认消息是否发送到队列(Queue)回调函数
        rabbitTemplate.setReturnCallback(rabbitConfirmAndReturn);
        /**
         * PS:发送消息,该操作是异步的,
         * 注意：此处有个人坑，就是如果是fanout交换机，消息队列和交换机是没有绑定键，
         * 生产者投递消息给交换机也是不用携带路由键的，但是调用convertAndSend时
         * 一定要传个“”的路由键给交换机。
         */
        rabbitTemplate.convertAndSend(exchangeName,"",message,correlationData);
    }

}
