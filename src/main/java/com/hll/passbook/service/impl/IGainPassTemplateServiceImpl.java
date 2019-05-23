package com.hll.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.Constants;
import com.hll.passbook.mapper.PassTemplateRowMapper;
import com.hll.passbook.service.IGainPassTemplateService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.GainPassTemplateRequest;
import com.hll.passbook.vo.PassTemplate;
import com.hll.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>用户领取优惠券的功能实现</h1>
 */
@Slf4j
@Service
public class IGainPassTemplateServiceImpl implements IGainPassTemplateService {
    /** HBase 客户端 */
    private final HbaseTemplate hbaseTemplate;
    /** redis 客户端 */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public IGainPassTemplateServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {
        String passTemplateId = RowKeyGenUtils.genPassTemplateRowKey(request.getPassTemplate());

        PassTemplate passTemplate;
        try {
            passTemplate = hbaseTemplate.get(Constants.PassTemplateTable.TABLE_NAME, passTemplateId, new PassTemplateRowMapper());
        } catch (Exception e) {
            log.error("Gain PassTemplate error: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("Gain PassTemplate error!");
        }

        if (passTemplate.getLimit() <= 1 && passTemplate.getLimit() != -1) {
            log.error("PassTemplate Limit Max: {}", JSON.toJSONString(passTemplate));
            return Response.failure("PassTemplate Limit Max");
        }

        Date cur = new Date();
        if (cur.getTime() < passTemplate.getStart().getTime() || cur.getTime() > passTemplate.getEnd().getTime()) {
            log.error("PassTemplate ValideDate Error: {}", JSON.toJSONString(passTemplate));
            return Response.failure("PassTemplate ValideDate Error");
        }

        if (passTemplate.getLimit() != -1) {
            byte[] FAMILY_I = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
            List<Mutation> datas = new ArrayList<>();
            Put put = new Put(Bytes.toBytes(passTemplateId));
            put.addColumn(FAMILY_I, LIMIT, Bytes.toBytes(passTemplate.getLimit() - 1));
            datas.add(put);
            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME, datas);
        }

        if (!addPassForUser(request, passTemplate.getId(), passTemplateId)) {
            return Response.failure("Gain PassTemplate Error!");
        }

        return Response.success();
    }

    /**
     * <h2>给用户添加优惠券</h2>
     * @param request {@link GainPassTemplateRequest}
     * @param merchantsId 商户 id
     * @param passTemplateId 优惠券 id
     * @return true/false
     */
    private boolean addPassForUser(GainPassTemplateRequest request, Integer merchantsId, String passTemplateId) throws IOException {
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
        byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> datas = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(RowKeyGenUtils.genPassRowKey(request)));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(request.getUserId()));
        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(passTemplateId));

        if (request.getPassTemplate().getHasToken()) {
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if (token == null) {
                log.error("Token not exists!: {}", passTemplateId);
                return false;
            }
            recordTokenToFile(merchantsId, passTemplateId, token);
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes(token));
        } else {
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes("-1"));
        }

        put.addColumn(FAMILY_I, ASSIGNED_DATE, Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date())));
        put.addColumn(FAMILY_I, CON_DATE, Bytes.toBytes("-1"));

        datas.add(put);
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, datas);

        return true;
    }

    /**
     * <h2>将已使用的 token 记录到文件中</h2>
     * @param merchantsId 商户 id
     * @param passTemplateId 优惠券 id
     * @param token 分配的优惠券 token
     */
    private void recordTokenToFile(Integer merchantsId, String passTemplateId, String token) throws IOException {
        Path path = Paths.get(Constants.TOKEN_DIR, String.valueOf(merchantsId), passTemplateId + Constants.USED_TOKEN_SUFFIX);
        Files.write(path, (token + "\n").getBytes(), StandardOpenOption.APPEND);
    }
}
