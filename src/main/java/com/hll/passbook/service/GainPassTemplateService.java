package com.hll.passbook.service;

import com.hll.passbook.vo.GainPassTemplateRequest;
import com.hll.passbook.vo.Response;

/**
 * <h1>用户领取优惠券功能实现</h1>
 */
public interface GainPassTemplateService {
    /**
     * <h2>用户领取优惠券</h2>
     * @param request {@link: GainPassTemplateRequest}
     * @return {@link: Response}
     * @throws Exception
     */
    Response gainPassTemplate(GainPassTemplateRequest request) throws Exception;
}
