package com.hll.passbook.service;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.FeedbackType;
import com.hll.passbook.vo.Feedback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户反馈服务测试</h1>
 * Created by Qinyi.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackServiceTest extends AbstractServiceTest {
    @Autowired
    private IFeedbackService feedbackService;

    @Test
    public void testCreateFeedback() throws Exception {
        Feedback appFeedback = new Feedback();
        appFeedback.setUserId(userId);
        appFeedback.setType(FeedbackType.APP.getCode());
        appFeedback.setTemplateId("-1");
        appFeedback.setComment("慕课网学习分布式卡包应用！");

        System.out.println(JSON.toJSONString(
                feedbackService.createFeedback(appFeedback))
        );

        Feedback passFeedback = new Feedback();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.getCode());
        passFeedback.setTemplateId("75c02527fef0530caabeac3a2d4e9b62");
        passFeedback.setComment("优惠券评论");

        System.out.println(JSON.toJSONString(
                feedbackService.createFeedback(passFeedback)
        ));
    }

    @Test
    public void testGetFeedback() throws Exception {
        System.out.println(JSON.toJSONString(
                feedbackService.getFeedback(userId))
        );
    }
}
