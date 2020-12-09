package com.sunnsoft.rabbitmq.email.sms.consumer.direct;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sunnsoft.rabbitmq.email.sms.common.constants.PhoneConstants;
import com.sunnsoft.rabbitmq.email.sms.common.utils.JsonUtils;
import com.sunnsoft.rabbitmq.email.sms.common.utils.SmsUtils;
import com.sunnsoft.rabbitmq.email.sms.config.direct.DirectRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ConsumerSMSDirect {

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
    @RabbitListener(queues = DirectRabbitConfig.SMS_QUEUE_NAME,containerFactory = "rabbitListenerContainerFactory")
    public void processForManualAckForDLX(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        // 获取全局MessageID
        String messageId = message.getMessageProperties().getMessageId();
        //获取投递的消息
        String msg = new String(message.getBody(), "UTF-8");
        log.info("(direct交换机)短信消费者获取生产者消息：\n messageId:" + messageId + "    投递的消息:" + msg);
        JSONObject jsonObject = JsonUtils.jsonToPojo(msg, JSONObject.class);
        String toPhone = jsonObject.getString(PhoneConstants.TO_PHONE_KEY);
        Long deliveryTag=(Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        log.info("deliveryTag:"+ deliveryTag);
        //Long deliveryTag2=message.getMessageProperties().getDeliveryTag();
        //log.info("deliveryTag2:"+ deliveryTag2);
        try {
            int i = 1 / 0;
            // 手动ack
            // 手动签收,第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, true);
            SmsUtils.sendValidation(toPhone);
        }catch (Exception e){
            e.printStackTrace();
            log.info("出现异常，丢弃该消息,交给DLX交换机进行消费....");
            //PS:如果出现异常，丢弃该消息，从而消息会交给DLX交换机进行消费
            /**
             * 第一个参数依然是当前消息到的数据的唯一id;
             * 第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。
             * 第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去。
             * 同样使用不确认后重新入列这个确认模式要谨慎，因为这里也可能因为考虑不周出现消息一直被重新丢回去的情况，导致积压。
             * PS:特别注意：第三个参数必须设置为false,这样才会将短信从业务消息队列中移除掉，死信交换机才会进行触发，
             *            从而接收并消费丢弃的消息；如果设置为true，那么消费者就会执行重试机制，一直不停的消费，
             *            知道没有异常发生，才会消费成功，从而将短信从业务消息队列中移除掉。
             */
            channel.basicNack(deliveryTag, true, false);
        }
    }

}
