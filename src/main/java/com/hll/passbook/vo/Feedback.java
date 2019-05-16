package com.hll.passbook.vo;

import com.google.common.base.Enums;
import com.hll.passbook.constant.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户评论</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    /** 评论类型 */
    private Long userId;

    /** 评论类型 */
    private String type;

    /** passTemplate rowKey ,如果是 app 类型的评论，则没有 */
    private String templateId;

    /** 评论内容 */
    private String comment;

    public Boolean validate() {
        FeedbackType type = Enums.getIfPresent(FeedbackType.class, this.type.toUpperCase()).orNull();
        return null != type && null != comment;
    }
}
