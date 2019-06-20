package com.hll.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h1>用户领取优惠券的请求对象</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GainPassTemplateRequest {
    /** 用户 id */
    private Long userId;

    /** PassTemplate 对象 */
    private PassTemplate passTemplate;
}
