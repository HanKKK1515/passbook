package com.hll.passbook.vo;

import com.hll.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h1>优惠券模板信息</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PassTemplateInfo {
    /** 优惠券模板 */
    private PassTemplate passTemplate;

    /** 优惠券对应的商户信息 */
    private Merchants merchants;
}
