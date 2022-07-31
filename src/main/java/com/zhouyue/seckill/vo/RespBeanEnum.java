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
    REPEATEERROR(500501, "该商品每人限购一件")
    ;


    private final Integer code;
    private final String message;
}
