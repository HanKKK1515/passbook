package com.hll.passbook.vo;

import com.hll.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h1>用户领取的优惠券信息</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PassInfo {
    /** 优惠券 */
    private Pass pass;

    /** 优惠券模板 */
    private PassTemplate passTemplate;

    /** 优惠券对应的商户 */
    private Merchants merchants;
}
