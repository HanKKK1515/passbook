package com.hll.passbook.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.hll.passbook.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * <h1>用户领取的优惠券</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pass {
    /** 用户 id */
    private Long userId;

    /** pass 在 hbase 中的 rowKey */
    private String rowKey;

    /** passTemplate 在 hbase 中的 rowKey */
    private String templateId;

    /** 优惠券的 token ， 有可能为 null ，则填充 -1 */
    private String token;

    /** 领取日期 */
    @JSONField(format = Constants.DATE_FORMAT)
    private Date assignedDate;

    /** 消费日期，不为空则代表已经被消费 */
    @JSONField(format = Constants.DATE_FORMAT)
    private Date conDate;
}
