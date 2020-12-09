package com.sunnsoft.rabbitmq.email.sms.common.platform;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lgk
 * @Description: 阿里云短信
 * @Date: 2019-12-26
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ali")
public class AliSmsConfig {
    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    public static final String PRODUCT = "Dysmsapi";

    /**
     * 产品:云名称通信短信API产品,开发者无需替换
     */
    public static final String DOMAIN = "dysmsapi.aliyuncs.com";

    /**
     *区域id
     */
    public static final String REGION = "cn-hangzhou";

    /**
     * appId
     */
    private String appId;

    /**
     *appSecret
     */
    private String appSecret;

    /**
     * 短信模板
     */
    private String templateId;

    /**
     * 短信签名
     */
    private String smsSign;
}
