package com.zbw.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionTypeEnum {

    /**
     * 错误类型
     */
    OBJECT_NOT_FOUND(0,"对象不存在"),

    INVALID_PARAMS(1,"参数不正确"),

    RESULT_IS_NONE(2,"结果为空");

    /**
     * 错误码
     */
    private int code;

    /**
     * 提示信息
     */
    private String msg;
}
