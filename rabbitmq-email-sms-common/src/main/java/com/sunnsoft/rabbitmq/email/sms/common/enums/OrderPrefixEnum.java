package com.sunnsoft.rabbitmq.email.sms.common.enums;

/**
 * @Author: lgk
 * @Description:
 * @Date: 2020-06-12
 * @Version: 1.0
 */
public enum OrderPrefixEnum {
    OD("OD"),
    CD("CD");

    private String prefix;

    OrderPrefixEnum(String prefix) {
        this.prefix = prefix;
    }
}
