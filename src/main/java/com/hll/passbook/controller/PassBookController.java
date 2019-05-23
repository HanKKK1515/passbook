package com.hll.passbook.controller;

import com.hll.passbook.log.LogConstants;
import com.hll.passbook.log.LogGenerator;
import com.hll.passbook.service.IFeedbackService;
import com.hll.passbook.service.IGainPassTemplateService;
import com.hll.passbook.service.IInventoryService;
import com.hll.passbook.service.IUserPassService;
import com.hll.passbook.vo.Feedback;
import com.hll.passbook.vo.GainPassTemplateRequest;
import com.hll.passbook.vo.Pass;
import com.hll.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1> PassBook Rest Controller </h1>
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassBookController {
    /** 用户优惠券服务 */
    private final IUserPassService userPassService;
    /** 优惠券库存服务 */
    private final IInventoryService inventoryService;
    /** 领取优惠券服务 */
    private final IGainPassTemplateService gainPassTemplateService;
    /** 用户反馈服务 */
    private final IFeedbackService feedbackService;
    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PassBookController(IUserPassService userPassService,
                              IInventoryService inventoryService,
                              IGainPassTemplateService gainPassTemplateService,
                              IFeedbackService feedbackService,
                              HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.inventoryService = inventoryService;
        this.gainPassTemplateService = gainPassTemplateService;
        this.feedbackService = feedbackService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>获取用户个人的优惠券信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     */
    @GetMapping("/userpassinfo")
    @ResponseBody
    public Response userPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(httpServletRequest, userId, LogConstants.ActionName.USER_PASS_INFO, null);
        return userPassService.getUserPassInfo(userId);
    }

    /**
     * <h2>获取用户已经使用了的优惠券</h2>
     * @param userId 用户 id
     * @return {@link Response}
     */
    @GetMapping("/userusedpassinfo")
    @ResponseBody
    public Response userUsedPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(httpServletRequest,userId, LogConstants.ActionName.USER_USED_PASS_INFO, null);
        return userPassService.getUserUsedPassInfo(userId);
    }

    /**
     * <h2>用户使用优惠券</h2>
     * @param pass {@link Pass}
     * @return {@link Response}
     */
    @PostMapping("/userusepass")
    @ResponseBody
    public Response userUsePass(@RequestBody Pass pass) throws Exception {
        LogGenerator.genLog(httpServletRequest, pass.getUserId(), LogConstants.ActionName.USER_USE_PASS, pass);
        return userPassService.userUsePass(pass);
    }

    /**
     * <h2>获取库存信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     */
    @GetMapping("/inventoryinfo")
    @ResponseBody
    public Response inventoryInfo(Long userId) throws Exception {
        LogGenerator.genLog(httpServletRequest, userId, LogConstants.ActionName.INVENTORY_INFO, null);
        return inventoryService.getInventoryInfo(userId);
    }

    /**
     * <h2>用户领取优惠券</h2>
     * @param request {@link GainPassTemplateRequest}
     * @return {@link Response}
     */
    @PostMapping("/gainpasstemplate")
    @ResponseBody
    public Response gainPassTemplate(@RequestBody GainPassTemplateRequest request) throws  Exception {
        LogGenerator.genLog(httpServletRequest, request.getUserId(), LogConstants.ActionName.GAIN_PASS_TEMPLATE, request);
        return gainPassTemplateService.gainPassTemplate(request);
    }

    /**
     * <h2>用户创建评论</h2>
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    @PostMapping("/createfeedback")
    @ResponseBody
    public Response createFeedback(@RequestBody Feedback feedback) throws Exception {
        LogGenerator.genLog(httpServletRequest, feedback.getUserId(), LogConstants.ActionName.CREATE_FEEDBACK, feedback);
        return feedbackService.createFeedback(feedback);
    }

    /**
     * <h2>用户获取评论信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     */
    @GetMapping("/getfeedback")
    @ResponseBody
    public Response getFeedback(Long userId) throws Exception {
        LogGenerator.genLog(httpServletRequest, userId, LogConstants.ActionName.GET_FEEDBACK, null);
        return feedbackService.getFeedback(userId);
    }

    /**
     * <h2>异常演示接口</h2>
     * @return {@link Response}
     */
    @GetMapping("/exception")
    @ResponseBody
    public Response exception() throws  Exception {
        throw new Exception("This's test exception!");
    }
}
