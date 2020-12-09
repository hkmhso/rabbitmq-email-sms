package com.sunnsoft.rabbitmq.email.sms.consumer.direct;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sunnsoft.rabbitmq.email.sms.common.constants.EmailConstants;
import com.sunnsoft.rabbitmq.email.sms.common.utils.EmailUtils;
import com.sunnsoft.rabbitmq.email.sms.common.utils.JsonUtils;
import com.sunnsoft.rabbitmq.email.sms.config.direct.DirectRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ConsumerEmailDirect {

    //自动ack，可能需要重试
    /**
     * 情况1:  
     *      消费者获取到消息后，调用第三方接口，但接口暂时无法访问，是否需要重试?      需要重试
     * 情况2:  
     *      消费者获取到消息后，抛出数据转换异常，是否需要重试?     不需要重试                                 不需要重试
     * 总结：对于情况2，如果消费者代码抛出异常是需要发布新版本才能解决的问题，那么不需要重试，
     * 重试也无济于事。应该采用日志记录+定时任务job健康检查+人工进行补偿
     *
     * RabbitMQ的重试机制原理：(最好启用手动ack)
     *  1、@RabbitListener 底层 使用Aop进行拦截，如果程序没有抛出异常，自动提交事务
     *  2、如果Aop使用异常通知拦截 获取异常信息的话，自动实现补偿机制 ，该消息会缓存到rabbitmq服务器端进行存放，一直重试到不抛异常为准。
     *  3、修改重试机制策略 一般默认情况下 间隔5秒重试一次
     * MQ重试机制需要注意的问题：
     *  1、MQ消费者幂等性问题如何解决：使用全局ID
     *
     */
    /*@RabbitHandler
    @RabbitListener(queues = DirectRabbitConfig.EMAIL_QUEUE_NAME,containerFactory = "rabbitListenerContainerFactory")
    public void processForAutoAckWithRetry(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        try{
            // 获取全局MessageID
            String messageId = message.getMessageProperties().getMessageId();
            //获取投递的消息
            String msg = new String(message.getBody(), "UTF-8");
            PrintUtil.print(this.getClass(), "-->process()->>(direct交换机)邮件消费者获取生产者消息：\n messageId:" + messageId + "    投递的消息:" + msg);
            JSONObject jsonObject = JsonUtil.jsonToPojo(msg, JSONObject.class);
            String userName = jsonObject.getString("userName");
            String toEmail = jsonObject.getString("toEmail");
            // 请求地址
            String emailUrl = "http://127.0.0.1:9004/email/sendEmail?toEmail=" + toEmail;
            PrintUtil.print(this.getClass(),"->>processForAutoAck()->>邮件消费者开始调用第三方邮件服务器,emailUrl:" + emailUrl);
            String result = HttpClientUtils.doGet(emailUrl);
            // 如果调用第三方邮件接口无法访问，如何实现自动重试.
            if (result == null) {
                throw new Exception("调用第三方邮件服务器接口失败!,实现重试机制");
            }
            PrintUtil.print(this.getClass(),"->>processForAutoAck()->>邮件消费者结束调用第三方邮件服务器成功,result:" + result + "程序执行结束");
            sendSimpleMail(toEmail, userName);
        }catch (Exception e){
            e.printStackTrace();
            //PS:如果出现异常，手动抛出异常，从而实现重试机制。
            throw new Exception("出现异常！！实现重试机制");
        }
    }*/

    //手动ack,可能需要重试
    /**
     * 情况1:  
     *      消费者获取到消息后，调用第三方接口，但接口暂时无法访问，是否需要重试?      需要重试
     * 情况2:  
     *      消费者获取到消息后，抛出数据转换异常，是否需要重试?     不需要重试                                 不需要重试
     * 总结：对于情况2，如果消费者代码抛出异常是需要发布新版本才能解决的问题，那么不需要重试，
     * 重试也无济于事。应该采用日志记录+定时任务job健康检查+人工进行补偿
     *
     * RabbitMQ的重试机制原理：(最好启用手动ack)
     *  1、@RabbitListener 底层 使用Aop进行拦截，如果程序没有抛出异常，自动提交事务
     *  2、如果Aop使用异常通知拦截 获取异常信息的话，自动实现补偿机制 ，该消息会缓存到rabbitmq服务器端进行存放，一直重试到不抛异常为准。
     *  3、修改重试机制策略 一般默认情况下 间隔5秒重试一次
     * MQ重试机制需要注意的问题：
     *  1、MQ消费者幂等性问题如何解决：使用全局ID
     *
     */
    /*@RabbitHandler
    @RabbitListener(queues = DirectRabbitConfig.EMAIL_QUEUE_NAME,containerFactory = "rabbitListenerContainerFactory")
    public void processForManualAckWithRetry(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        try {
            // 获取全局MessageID
            String messageId = message.getMessageProperties().getMessageId();
            //获取投递的消息
            String msg = new String(message.getBody(), "UTF-8");
            log.info("(direct交换机)邮件消费者获取生产者消息：\n messageId:" + messageId + "    投递的消息:" + msg);
            JSONObject jsonObject = JsonUtils.jsonToPojo(msg, JSONObject.class);
            String userName = jsonObject.getString("userName");
            String toEmail = jsonObject.getString("toEmail");
            // 请求地址
            String emailUrl = "http://127.0.0.1:9004/email/sendEmail?toEmail=" + toEmail;
            log.info( "->>processForManualAck()->>(direct交换机)邮件消费者开始调用第三方邮件服务器,emailUrl:" + emailUrl);
            String result = HttpClientUtils.doGet(emailUrl);
            if (result == null) {
                // PS:如果调用第三方邮件接口无法访问，手动抛出异常，从而实现重试机制。
                throw new Exception("调用第三方邮件服务器接口失败!,实现重试机制");
            }
            log.info( "->>processForManualAck()->>(direct交换机)邮件消费者结束调用第三方邮件服务器成功,result:" + result + "程序执行结束");
            // 手动ack
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            // 手动签收,第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, true);
            sendSimpleMail(toEmail, userName);
        }catch (Exception e){
            e.printStackTrace();
            //PS:如果出现异常，手动抛出异常，从而实现重试机制。
            throw new Exception("出现异常！！实现重试机制");
        }
    }*/

    /**
     * 死信队列被触发的情况：(最好使用手动ack)
     *  1.消息被拒绝（basic.reject或basic.nack）并且requeue=false.
     *  2.消息TTL过期
     *  3.队列达到最大长度（队列满了，无法再添加数据到mq中）
     * 死信队列被触发的执行流程：
     * 生产者   -->  消息 --> 交换机  --> 队列  --> 变成死信  --> DLX交换机 -->队列 --> 消费者
     *
     */
    @RabbitHandler
    @RabbitListener(queues = DirectRabbitConfig.EMAIL_QUEUE_NAME,containerFactory = "rabbitListenerContainerFactory")
    public void processForManualAckForDLX(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        // 获取全局MessageID
        String messageId = message.getMessageProperties().getMessageId();
        //获取投递的消息
        String msg = new String(message.getBody(), "UTF-8");
        log.info("(direct交换机)邮件消费者获取生产者消息：\n messageId:" + messageId + "    投递的消息:" + msg);
        JSONObject jsonObject = JsonUtils.jsonToPojo(msg, JSONObject.class);
        String userName = jsonObject.getString("userName");
        String toEmail = jsonObject.getString(EmailConstants.TO_EMAIL_KEY);
        Long deliveryTag=(Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        log.info("deliveryTag:"+ deliveryTag);
        //Long deliveryTag2=message.getMessageProperties().getDeliveryTag();
        //log.info("deliveryTag2:"+ deliveryTag2);
        try {
            int i = 1 / 0;
            // 手动ack
            // 手动签收,第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, true);
            EmailUtils.sendSimpleMail(toEmail, EmailConstants.FROM_EMAIL,userName);
        }catch (Exception e){
            e.printStackTrace();
            log.info("出现异常，丢弃该消息,交给DLX交换机进行消费....");
            //PS:如果出现异常，丢弃该消息，从而消息会交给DLX交换机进行消费
            /**
             * 第一个参数依然是当前消息到的数据的唯一id;
             * 第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。
             * 第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去。
             * 同样使用不确认后重新入列这个确认模式要谨慎，因为这里也可能因为考虑不周出现消息一直被重新丢回去的情况，导致积压。
             * PS:特别注意：第三个参数必须设置为false,这样才会将信息从业务消息队列中移除掉，死信交换机才会进行触发，
             *            从而接收并消费丢弃的消息；如果设置为true，那么消费者就会执行重试机制，一直不停的消费，
             *            知道没有异常发生，才会消费成功，从而将信息从业务消息队列中移除掉。
             */
            channel.basicNack(deliveryTag, true, false);
        }
    }


}
