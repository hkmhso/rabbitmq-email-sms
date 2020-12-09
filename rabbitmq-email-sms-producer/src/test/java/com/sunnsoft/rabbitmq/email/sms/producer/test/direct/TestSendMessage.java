package com.sunnsoft.rabbitmq.email.sms.producer.test.direct;

import com.sunnsoft.rabbitmq.email.sms.RabbitMQEmailSmsProducer;
import com.sunnsoft.rabbitmq.email.sms.common.constants.EmailConstants;
import com.sunnsoft.rabbitmq.email.sms.common.utils.EmailUtils;
import com.sunnsoft.rabbitmq.email.sms.common.utils.SmsUtils;
import com.sunnsoft.rabbitmq.email.sms.common.utils.SpringUtils;
import com.sunnsoft.rabbitmq.email.sms.config.direct.DirectRabbitConfig;
import com.sunnsoft.rabbitmq.email.sms.producer.direct.ProducerEmailDirect;
import com.sunnsoft.rabbitmq.email.sms.producer.direct.ProducerSmsDirect;
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
    private ProducerEmailDirect producerEmailDirect;

    @Autowired
    private ProducerSmsDirect producerSmsDirect;

    //发送邮件：confirm消息确认机制：发送邮件,交换机和队列都存在
    @Test
    public void testProducerEmailDirectForConfirm() {
        //System.out.println(0.0/0.0 == 0.0/0.0);
        producerEmailDirect.sendForNotScheduledForConfirm(DirectRabbitConfig.EXCHANGE_NAME);
    }

    //发送邮件：confirm消息确认机制：发送邮件,交换机和队列不存在
    @Test
    public void testProducerEmailDirectNotExistExchangeForConfirm() {
        producerEmailDirect.sendForNotScheduledForConfirm("notExistExchange");
    }

    //发送邮件：confirm消息确认机制：发送邮件,交换机存在，队列不存在
    @Test
    public void testProducerEmailDirectNotExistQueueForConfirm() {
        producerEmailDirect.sendForNotScheduledForConfirm("notBindQueue");
    }

    //发送邮件：AMQP消息确认机制
    @Test
    public void testProducerEmailDirectForAMQP() {
        try {
            producerEmailDirect.sendForNotScheduledForAMQP(DirectRabbitConfig.EXCHANGE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送短信：confirm消息确认机制：发送邮件,交换机和队列都存在
    @Test
    public void testProducerSmsDirectForConfirm() {
        producerSmsDirect.sendForNotScheduledForConfirm(DirectRabbitConfig.EXCHANGE_NAME);
    }

    @Test
    public void testSpring(){
        //获取spring管理的所有对象的标识(也就是bean的name)
        String[] beanDefinitionNames = SpringUtils.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }
    }

    //发送邮件
    @Test
    public void testSendEmail(){
        try {
            EmailUtils.sendSimpleMail("2590392428@qq.com", EmailConstants.FROM_EMAIL,"hkm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送验证码
    @Test
    public void testSendValidation(){
        try {
            SmsUtils.sendValidation("13711769935");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}