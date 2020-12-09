package com.sunnsoft.rabbitmq.email.sms.producer.test.topic;

import com.sunnsoft.rabbitmq.email.sms.RabbitMQEmailSmsProducer;
import com.sunnsoft.rabbitmq.email.sms.config.topic.TopicRabbitConfig;
import com.sunnsoft.rabbitmq.email.sms.producer.topic.ProducerMatchMultiTopic;
import com.sunnsoft.rabbitmq.email.sms.producer.topic.ProducerMatchOneTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//spring整合junit
@RunWith(SpringJUnit4ClassRunner.class)
//当前类为springboot的测试类，并启动springboot
@SpringBootTest(classes = {RabbitMQEmailSmsProducer.class})
public class TestSendMessage {
    @Autowired
    private ProducerMatchOneTopic producerMatchOneTopic;

    @Autowired
    private ProducerMatchMultiTopic producerMatchMultiTopic;

    //*通配符发送消息,confirm消息确认机制：发送邮件,交换机和队列都存在
    @Test
    public void testProducerMatchOneTopic() {
        producerMatchOneTopic.sendForNotScheduledForConfirm(TopicRabbitConfig.EXCHANGE_NAME);
    }

    //#通配符发送消息,confirm消息确认机制：发送邮件,交换机和队列都存在
    @Test
    public void testProducerMatchMultiTopic() {
        producerMatchMultiTopic.sendForNotScheduledForConfirm(TopicRabbitConfig.EXCHANGE_NAME);
    }

}