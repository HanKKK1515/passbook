package com.hll.passbook.vo;

import com.hll.passbook.constant.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h1>用户评论</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Feedback {
    /** 用户 id */
    private Long userId;

    /** 评论类型 */
    private String type;

    /** passTemplate rowKey ,如果是 app 类型的评论，则没有 */
    private String templateId;

    /** 评论内容 */
    private String comment;

    public Boolean validate() {
        try {
            Enum.valueOf(FeedbackType.class, this.type.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return false;
        }
        return null != comment;
    }
}
