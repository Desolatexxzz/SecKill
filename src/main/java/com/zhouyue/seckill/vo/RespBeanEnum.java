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
    SUCCESS(200, "SUCCESS"),
    Error(500, "服务端异常");


    private final Integer code;
    private final String message;
}
