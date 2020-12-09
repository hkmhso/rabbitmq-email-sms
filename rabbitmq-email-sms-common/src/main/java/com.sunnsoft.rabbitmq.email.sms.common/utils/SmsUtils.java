package com.sunnsoft.rabbitmq.email.sms.common.utils;

import cn.hutool.json.JSONUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.sunnsoft.rabbitmq.email.sms.common.constants.RedisKeyConstants;
import com.sunnsoft.rabbitmq.email.sms.common.platform.AliSmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName 阿里云短信工具类
 * @Description TODO
 * @Author HKM
 * @Date 2020/11/27 14:12
 * @Version 1.0
 **/
@Component
@Slf4j
public class SmsUtils {

    private static AliSmsConfig aliSmsConfig;

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setAliSmsConfig(AliSmsConfig aliSmsConfig) {
        if(SmsUtils.aliSmsConfig == null) {
            SmsUtils.aliSmsConfig = aliSmsConfig;
        }
    }

    public static AliSmsConfig getAliSmsConfig() {
        return aliSmsConfig;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        if(SmsUtils.redisTemplate == null) {
            SmsUtils.redisTemplate = redisTemplate;
        }
    }

    public static RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    //发送验证码
    public static void sendValidation(String toPhone) throws Exception {
        String smsCode = CodeUtils.smsCode();
        Map<String, String> jsonMap = new HashMap<>(2);
        jsonMap.put("code", smsCode);
        String json = JSONUtil.toJsonStr(jsonMap);

        sendSms(toPhone, json, aliSmsConfig.getTemplateId());

        //缓存短信验证码
        String key = RedisKeyConstants.SMS_CODE + toPhone;
        redisTemplate.opsForValue().set(key, smsCode, RedisKeyConstants.SMS_CODE_EXPIRE, TimeUnit.MINUTES);
    }

    /**
     * 发送短信验证码
     *
     * @param toPhone    接收手机号
     * @param json      验证码参数
     * @param templateId 短信模板id
     * @return
     */
    public static void sendSms(String toPhone, String json, String templateId){
        log.info("接收手机号\t" + toPhone + "\t短信参数\t" + json + "\t模板id\t" + templateId);
        try{
            // 初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile(AliSmsConfig.REGION, aliSmsConfig.getAppId(), aliSmsConfig.getAppSecret());
            DefaultProfile.addEndpoint(AliSmsConfig.REGION, AliSmsConfig.REGION, AliSmsConfig.PRODUCT, AliSmsConfig.DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            // 组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            // 必填:接收手机号
            request.setPhoneNumbers(toPhone);
            // 必填:短信签名-可在短信控制台中找到
            request.setSignName(aliSmsConfig.getSmsSign());
            // 必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateId);
            // 可选:模板中的变量替换JSON串
            request.setTemplateParam(json);
            // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            request.setOutId("yourOutId");

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            boolean flag = sendSmsResponse.getCode() == null || !sendSmsResponse.getCode().equals("OK");
            log.info("短信发送结果\t" + sendSmsResponse.getCode() +"\t" + sendSmsResponse.getMessage());
        }catch (Exception e){
            log.info(e.toString());
            throw new RuntimeException(e.getMessage());
        }
    }

}
