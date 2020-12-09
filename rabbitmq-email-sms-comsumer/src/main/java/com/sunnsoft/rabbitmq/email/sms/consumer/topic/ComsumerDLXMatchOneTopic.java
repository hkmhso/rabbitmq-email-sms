package com.sunnsoft.rabbitmq.email.sms.consumer.topic;

import com.rabbitmq.client.Channel;
import com.sunnsoft.rabbitmq.email.sms.config.topic.TopicDLXRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ComsumerDLXMatchOneTopic {
    /**
     * *通配符死信消息队列
     * @param message
     * @param headers
     * @param channel
     * @throws Exception
     */
    @RabbitHandler
    @RabbitListener(queues = TopicDLXRabbitConfig.TOPIC_MATCH_ONE_DLX_QUEUE_NAME,containerFactory = "rabbitListenerContainerFactory")
    public void processForManualAckForDLX(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        try {
            // 获取全局MessageID
            String messageId = message.getMessageProperties().getMessageId();
            //获取投递的消息
            String msg = new String(message.getBody(), "UTF-8");
            // 手动ack
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            //Long deliveryTag2=message.getMessageProperties().getDeliveryTag();
            //log.info("deliveryTag2:"+ deliveryTag2);
            // 手动签收,第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
