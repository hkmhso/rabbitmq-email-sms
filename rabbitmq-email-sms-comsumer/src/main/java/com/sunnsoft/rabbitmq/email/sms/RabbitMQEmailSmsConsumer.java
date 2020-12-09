package com.sunnsoft.rabbitmq.email.sms;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PS:scanBasePackages：表示既会扫描该模块的包，也会扫描该模块所依赖的其他模块的包
 */
@SpringBootApplication(scanBasePackages="com.sunnsoft.rabbitmq.email.sms.**")
@EnableRabbit
public class RabbitMQEmailSmsConsumer {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQEmailSmsConsumer.class, args);
    }
}
