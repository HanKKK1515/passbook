package com.hll.passbook.utils;

import com.hll.passbook.vo.Feedback;
import com.hll.passbook.vo.GainPassTemplateRequest;
import com.hll.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * <h1> RowKey 生成器工具类</h1>
 */
@Slf4j
public class RowKeyGenUtils {
    /**
     * <h2>根据传入的 PassTemplate 生成 RowKey </h2>
     * @param passTemplate {@link: PassTemplate}
     * @return String RowKey
     */
    public static String genPassTemplateRowKey(PassTemplate passTemplate) {
        String passInfo = String.valueOf(passTemplate.getId() + "_" + passTemplate.getTitle());
        String rowKey = DigestUtils.md5Hex(passInfo);

        log.info("GenPassTemplateRowKey: {}, {}", passInfo, rowKey);
        return rowKey;
    }

    /**
     * <h2>根据生成优惠券的请求来生成优惠券的 RowKey，只可以在领取优惠券的时候使用</h2>
     * @param request {@link: GainPassTemplateRequest}
     * @return String RowKey
     */
    public static String genPassRowKey(GainPassTemplateRequest request) {
        String reverseUserId = new StringBuilder(String.valueOf(request.getUserId())).reverse().toString();
        long timestamp = Long.MAX_VALUE - System.currentTimeMillis();
        String passTemplateRowKey = genPassTemplateRowKey(request.getPassTemplate());
        return reverseUserId + timestamp + passTemplateRowKey;
    }

    /**
     * <h2>根据传入的 Feedback 生成 RowKey</h2>
     * @param feedback {@link: Feedback}
     * @return String RowKey
     */
    public static String genFeedbackRowKey(Feedback feedback) {
        String reverseUserId = new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString();
        long timestamp = Long.MAX_VALUE - System.currentTimeMillis();
        return reverseUserId + timestamp;
    }
}
