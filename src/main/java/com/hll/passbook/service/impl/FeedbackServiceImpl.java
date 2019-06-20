package com.hll.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.Constants;
import com.hll.passbook.hbase.HBaseService;
import com.hll.passbook.service.IFeedbackService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.Feedback;
import com.hll.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h2>评论功能的实现</h2>
 */
@Slf4j
@Service
public class FeedbackServiceImpl implements IFeedbackService {
    /** HBase 工具类 */
    private final HBaseService hBaseService;
    @Autowired
    public FeedbackServiceImpl(HBaseService hBaseService) {
        this.hBaseService = hBaseService;
    }

    @Override
    public Response createFeedback(Feedback feedback) throws Exception {
        if (!feedback.validate()) {
            log.error("Feedback Error: {}", JSON.toJSONString(feedback));
            return new Response("CreateFeedback Error!");
        }

        String rowKey = RowKeyGenUtils.genFeedbackRowKey(feedback);
        if (hBaseService.createPro(feedback, Constants.FeedbackTable.TABLE_NAME, Constants.FeedbackTable.FAMILY_I, rowKey)) {
            return Response.success();
        } else {
            return new Response("CreateFeedback Error!");
        }
    }

    @Override
    public Response getFeedback(Long userId) {
        StringBuilder sbUserId = new StringBuilder(String.valueOf(userId));
        byte[] reverseUserId = sbUserId.reverse().toString().getBytes();
        PrefixFilter filter = new PrefixFilter(reverseUserId);

        List<Feedback> feedbacks = hBaseService.searchAllByFilter(Constants.FeedbackTable.TABLE_NAME, filter, Feedback.class);

        return new Response(feedbacks);
    }
}
