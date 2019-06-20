package com.hll.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h1>统一错误信息</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorInfo<T> {
    /** 统一错误码 */
    public static final Integer ERROR = -1;

    /** 特定错误码 */
    private Integer code;

    /** 错误信息 */
    private String message;

    /** 请求的 url */
    private String url;

    /** 请求返回的数据 */
    private T data;
}
