package com.zhouyue.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 公共返回对象枚举
 */
@Getter
@AllArgsConstructor
@ToString
public enum RespBeanEnum {
    // 通用枚举
    SUCCESS(200, "SUCCESS"),
    Error(500, "服务端异常"),
    // 登录模块枚举
    LOGINERROR(500210, "用户名或密码错误"),
    // 手机校验枚举
    MOBILEERROR(500211, "手机号码格式错误"),
    BINDERROR(500212, "参数校验异常"),
    EMPTYSTOCK(500500, "库存不足"),
    REPEATEERROR(500501, "该商品每人限购一件"),
    SESSIONERROR(500215, "用户不存在"),
    ORDERNOTEXIST(500300, "订单信息不存在"),
    REQUESTILLEGAL(500502, "请求非法，请重试"),
    ERRORCAPTCHA(500503, "验证码错误，请重新输入"),
    ACCESSLIMITREAHCED(500504, "访问过于频繁，请稍后再试")
    ;


    private final Integer code;
    private final String message;
}
