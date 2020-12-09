package com.sunnsoft.rabbitmq.email.sms.common.constants;

/**
 * redis key 常量类
 * 命名规则:
 * 通过":"来区分层次
 * 通过"_"来分割命名
 *
 * @author yzc
 * @date 2019/10/12 17:45
 */
public interface RedisKeyConstants {

    String GLOBAL_KEY_PREFIX = "zy:";

    /**
     * 验证码
     */
    String VALIDATE_CODE = GLOBAL_KEY_PREFIX + "validate_code:";

    /**
     * 短信验证码前缀
     */
    String SMS_CODE = GLOBAL_KEY_PREFIX + "sms_code:";

    /**
     * 短信验证码有效期 10分钟
     */
    Integer SMS_CODE_EXPIRE = 10;

    /**
     * 用户购买的商品信息
     */
    String BUY_GOODS = GLOBAL_KEY_PREFIX + "buy_goods_info:";
}
