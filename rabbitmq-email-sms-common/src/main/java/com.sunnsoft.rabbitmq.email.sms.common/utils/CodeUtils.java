package com.sunnsoft.rabbitmq.email.sms.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.sunnsoft.rabbitmq.email.sms.common.enums.OrderPrefixEnum;

import java.util.Random;

/**
 * 编号生成工具
 *
 * @author yzc
 * @date 2019/11/29 14:44
 */
public final class CodeUtils {

  private CodeUtils() {}

  /**
   * 获取编号(订单号, 流水号)
   *
   * @return
   */
  public static String orderCode() {
    Long time = System.currentTimeMillis();
    return OrderPrefixEnum.OD + String.valueOf(time).substring(0, 7) + RandomUtil.randomNumbers(5);
  }

  /**
   * 短信验证码(4位)
   *
   * @return
   */
  public static String smsCode() {
    return String.format("%04d", new Random().nextInt(9999));
  }
}
