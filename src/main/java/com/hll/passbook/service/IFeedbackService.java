package com.hll.passbook.service;

import com.hll.passbook.vo.Feedback;
import com.hll.passbook.vo.Response;

/**
 * <h1>评论功能：即用户评论功能服务接口</h1>
 */
public interface IFeedbackService {
    /**
     * <h2>创建评论</h2>
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    Response createFeedback(Feedback feedback) throws Exception;

    /**
     * <h2>根据 UserId 获取用户评论</h2>
     * @param userId 用户id
     * @return {@link Response}
     */
    Response getFeedback(Long userId) throws Exception;
}
