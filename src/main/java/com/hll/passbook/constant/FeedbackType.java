package com.hll.passbook.constant;

/**
 * <h1>评论/反馈类型</h1>
 */
public enum FeedbackType {
    PASS("pass", "针对优惠券的评论/反馈"),
    APP("app", "针对卡包app的评论/反馈");

    /** 评论类型编码 */
    private String code;
    /** 评论类型描述 */
    private String desc;
    FeedbackType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
