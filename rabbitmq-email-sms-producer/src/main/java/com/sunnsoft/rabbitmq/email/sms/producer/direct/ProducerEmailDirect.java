package com.sunnsoft.rabbitmq.email.sms.producer.direct;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sunnsoft.rabbitmq.email.sms.common.constants.EmailConstants;
import com.sunnsoft.rabbitmq.email.sms.common.utils.HttpClientUtils;
import com.sunnsoft.rabbitmq.email.sms.config.RabbitConfirmAndReturn;
import com.sunnsoft.rabbitmq.email.sms.config.direct.DirectRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 生产者发送消息出去之后，不知道到底有没有发送到RabbitMQ服务器，
 * 默认是不知道的。而且有的时候我们在发送消息之后，后面的逻辑出问题了，我们不想要发送之前的消息了，需要撤回该怎么做。
 *   解决方案:
 *   1.AMQP 事务机制
 *   2.Confirm 模式
 * 事务模式:
 *  txSelect  将当前channel设置为transaction模式
 *  txCommit  提交当前事务
 *  txRollback  事务回滚
 */
@Component
@Slf4j
public class ProducerEmailDirect {

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
    public void sendForNotScheduledForConfirm(String exchangeName){
        JSONObject jsonObject = new JSONObject();
        //TOPIC：一对多，即一个消息发送者可以被多个消息接收者监听
        String userName = System.currentTimeMillis() + "";
        try {
            jsonObject.put("userName", userName);
            jsonObject.put(EmailConstants.TO_EMAIL_KEY,EmailConstants.TO_EMAIL);
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
        //PS:发送消息,该操作是异步的
        rabbitTemplate.convertAndSend(exchangeName, DirectRabbitConfig.EMAIL_ROUTING_KEY, message,correlationData);
    }

    /**
     *
     * AMQP消息确认机制：
     *  根据交换机名称和路由键精确匹配到对应的消息队列
     * @param exchangeName 交换机名称
     */
    public void sendForNotScheduledForAMQP(String exchangeName) throws Exception{
        JSONObject jsonObject = new JSONObject();
        //TOPIC：一对多，即一个消息发送者可以被多个消息接收者监听
        String userName = System.currentTimeMillis() + "";
        try {
            jsonObject.put("userName", userName);
            jsonObject.put(EmailConstants.TO_EMAIL_KEY,EmailConstants.TO_EMAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String msg = jsonObject.toString();
        String messageId=UUID.randomUUID() + "";
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8").setMessageId(messageId).build();
        // 1、获取连接
        Connection connection =null;
        Channel channel =null;
        try {
            connection=connectionFactory.createConnection();
            // 2、创建通道
            channel=connection.createChannel(true);
            // 3.创建交换机声明
            //channel.exchangeDeclare(DirectRabbitConfig.EMAIL_QUEUE_NAME, true, false, false, null);
            // 4、将当前管道设置为 txSelect 将当前channel设置为transaction模式 开启事务
            channel.txSelect();
            //5、发送消息,该操作是异步的
            channel.basicPublish(exchangeName, DirectRabbitConfig.EMAIL_ROUTING_KEY,true,null,msg.getBytes());
            String toEmail = "2590392428@qq.com";
            // 请求地址
            String emailUrl = "http://127.0.0.1:9004/email/getEmail?toEmail=" + toEmail;
            log.info("(direct交换机)邮件消费者开始调用第三方邮件服务器,emailUrl:" + emailUrl);
            String result = HttpClientUtils.doGet(emailUrl);
            if(result==null){
                // PS:如果调用第三方邮件接口无法访问，手动抛出异常,从而导致回滚事务
                throw new Exception("调用第三方邮件服务器接口失败!");
            }
           log.info("(direct交换机)邮件消费者结束调用第三方邮件服务器成功,result:" + result + "程序执行结束");
            //6、没有出现异常， 提交事务
           log.info("没有出现异常， 提交事务");
            channel.txCommit();
        } catch (Exception e) {
            e.printStackTrace();
           log.info("出现异常，回滚事务");
            //7、出现异常，回滚事务
            channel.txRollback();
            //sendForNotScheduledForAMQP(DirectRabbitConfig.EXCHANGE_NAME);
        }
    }

    /**
     * 根据交换机名称和路由键精确匹配到对应的消息队列
     */
    //@Scheduled(fixedDelay = 5000)
    public void sendForScheduled() {
        JSONObject jsonObject = new JSONObject();
        //TOPIC：一对多，即一个消息发送者可以被多个消息接收者监听
        String userName = System.currentTimeMillis() + "";
        try {
            jsonObject.put("userName", userName);
            jsonObject.put(EmailConstants.TO_EMAIL_KEY,EmailConstants.TO_EMAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String msg = jsonObject.toString();
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8").setMessageId(UUID.randomUUID() + "").build();
        log.info("(direct交换机)邮件生产者向消费者发送的内容:" + msg);
        rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_NAME, DirectRabbitConfig.EMAIL_ROUTING_KEY,message);
    }

}
