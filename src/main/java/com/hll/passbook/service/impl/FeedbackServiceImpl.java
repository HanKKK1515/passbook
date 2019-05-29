package com.hll.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.Constants;
import com.hll.passbook.mapper.FeedbackRowMapper;
import com.hll.passbook.service.IFeedbackService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.Feedback;
import com.hll.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
// import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h2>评论功能的实现</h2>
 */
@Slf4j
@Service
public class FeedbackServiceImpl implements IFeedbackService {
    /** HBase 客户端 */
    private final HbaseTemplate hbaseTemplate;
    @Autowired
    public FeedbackServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Response createFeedback(Feedback feedback) throws Exception {
        if (!feedback.validate()) {
            log.error("Feedback Error: {}", JSON.toJSONString(feedback));
            return new Response("Feedback error!");
        }

        String rowKey = RowKeyGenUtils.genFeedbackRowKey(feedback);
        hbaseTemplate.put(
                Constants.FeedbackTable.TABLE_NAME,
                rowKey,
                Constants.FeedbackTable.FAMILY_I,
                Constants.FeedbackTable.USER_ID,
                Bytes.toBytes(feedback.getUserId())
        );
        hbaseTemplate.put(
                Constants.FeedbackTable.TABLE_NAME,
                rowKey,
                Constants.FeedbackTable.FAMILY_I,
                Constants.FeedbackTable.USER_ID,
                Bytes.toBytes(feedback.getUserId())
        );
        hbaseTemplate.put(
                Constants.FeedbackTable.TABLE_NAME,
                rowKey,
                Constants.FeedbackTable.FAMILY_I,
                Constants.FeedbackTable.TYPE,
                Bytes.toBytes(feedback.getType())
        );
        hbaseTemplate.put(
                Constants.FeedbackTable.TABLE_NAME,
                rowKey,
                Constants.FeedbackTable.FAMILY_I,
                Constants.FeedbackTable.TEMPLATE_ID,
                Bytes.toBytes(feedback.getTemplateId())
        );
        hbaseTemplate.put(
                Constants.FeedbackTable.TABLE_NAME,
                rowKey,
                Constants.FeedbackTable.FAMILY_I,
                Constants.FeedbackTable.COMMENT,
                Bytes.toBytes(feedback.getComment())
        );

/*
        byte[] FAMILY_I = Bytes.toBytes(Constants.FeedbackTable.FAMILY_I);
        byte[] USER_ID = Bytes.toBytes(Constants.FeedbackTable.USER_ID);
        byte[] TYPE = Bytes.toBytes(Constants.FeedbackTable.TYPE);
        byte[] TEMPLATE_ID = Bytes.toBytes(Constants.FeedbackTable.TEMPLATE_ID);
        byte[] COMMENT = Bytes.toBytes(Constants.FeedbackTable.COMMENT);

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(feedback.getUserId()));
        put.addColumn(FAMILY_I, TYPE, Bytes.toBytes(feedback.getType()));
        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(feedback.getTemplateId()));
        put.addColumn(FAMILY_I, COMMENT, Bytes.toBytes(feedback.getComment()));
        hbaseTemplate.saveOrUpdate(Constants.FeedbackTable.TABLE_NAME, put);
*/
        return Response.success();
    }

    @Override
    public Response getFeedback(Long userId) {
        StringBuilder sbUserId = new StringBuilder(String.valueOf(userId));
        byte[] reverseUserId = sbUserId.reverse().toString().getBytes();

        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(reverseUserId));

        List<Feedback> feedbacks = hbaseTemplate.find(Constants.FeedbackTable.TABLE_NAME, scan, new FeedbackRowMapper());

        return new Response(feedbacks);
    }
}
