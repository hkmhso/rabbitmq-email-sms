package com.sunnsoft.rabbitmq.email.sms.consumer.fanout;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sunnsoft.rabbitmq.email.sms.common.constants.EmailConstants;
import com.sunnsoft.rabbitmq.email.sms.common.utils.EmailUtils;
import com.sunnsoft.rabbitmq.email.sms.common.utils.JsonUtils;
import com.sunnsoft.rabbitmq.email.sms.config.fanout.FanoutDLXRabbitConfig;
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
public class ComsumerDLXEmailFanout {

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 邮件死信消息队列
     * @param message
     * @param headers
     * @param channel
     * @throws Exception
     */
    @RabbitHandler
    @RabbitListener(queues = FanoutDLXRabbitConfig.FANOUT_EMAIL_DLX_QUEUE_NAME,containerFactory = "rabbitListenerContainerFactory")
    public void processForManualAckForDLX(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        try {
            // 获取全局MessageID
            String messageId = message.getMessageProperties().getMessageId();
            //获取投递的消息
            String msg = new String(message.getBody(), "UTF-8");
            log.info("(死信交换机)邮件DLX交换机开始进行消费....获取生产者消息：\n messageId:" + messageId + "    投递的消息:" + msg);
            JSONObject jsonObject = JsonUtils.jsonToPojo(msg, JSONObject.class);
            String userName = jsonObject.getString("userName");
            String toEmail = jsonObject.getString(EmailConstants.TO_EMAIL_KEY);
            // 手动ack
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            //Long deliveryTag2=message.getMessageProperties().getDeliveryTag();
            //log.info("deliveryTag2:"+ deliveryTag2);
            // 手动签收,第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, true);
            //EmailUtils.sendSimpleMail(toEmail,EmailConstants.FROM_EMAIL, userName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
