package com.hll.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.Constants;
import com.hll.passbook.hbase.HBaseService;
import com.hll.passbook.service.IGainPassTemplateService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.GainPassTemplateRequest;
import com.hll.passbook.vo.Pass;
import com.hll.passbook.vo.PassTemplate;
import com.hll.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Date;

/**
 * <h1>用户领取优惠券的功能实现</h1>
 */
@Slf4j
@Service
public class GainPassTemplateServiceImpl implements IGainPassTemplateService {
    /** HBase 工具类 */
    private final HBaseService hBaseService;
    /** redis 客户端 */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public GainPassTemplateServiceImpl(HBaseService hBaseService, StringRedisTemplate redisTemplate) {
        this.hBaseService = hBaseService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {
        String passTemplateId = RowKeyGenUtils.genPassTemplateRowKey(request.getPassTemplate());

        PassTemplate passTemplate;
        try {
            passTemplate = hBaseService.getOne(Constants.PassTemplateTable.TABLE_NAME, passTemplateId, PassTemplate.class);
        } catch (Exception e) {
            log.error("Not Exists PassTemplate: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("Not Exists PassTemplate!");
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

        if (!addPassForUser(request, passTemplate.getId(), passTemplateId)) {
            return Response.failure("Gain PassTemplate Error!");
        }

        if (passTemplate.getLimit() != -1) {
            byte[] FAMILY_I = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
            Put put = new Put(Bytes.toBytes(passTemplateId));
            long limit = passTemplate.getLimit() - 1L;
            put.addColumn(FAMILY_I, LIMIT, Bytes.toBytes(String.valueOf(limit)));
            hBaseService.saveOrUpdate(Constants.PassTemplateTable.TABLE_NAME, put);
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
    private boolean addPassForUser(GainPassTemplateRequest request, Integer merchantsId, String passTemplateId) throws Exception {
        String rowKey = RowKeyGenUtils.genPassRowKey(request);

        Pass pass = new Pass();
        pass.setUserId(request.getUserId());
        pass.setRowKey(rowKey);
        pass.setTemplateId(passTemplateId);

        if (request.getPassTemplate().getHasToken()) {
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if (token == null) {
                log.error("Token not exists!: {}", passTemplateId);
                return false;
            }
            recordTokenToFile(merchantsId, passTemplateId, token);
            pass.setToken(token);
        } else {
            pass.setToken("-1");
        }

        pass.setAssignedDate(new Date());
        pass.setConDate(null);

        return hBaseService.createPro(pass, Constants.PassTable.TABLE_NAME, Constants.PassTable.FAMILY_I, rowKey);
    }

    /**
     * <h2>将已使用的 token 记录到文件中</h2>
     * @param merchantsId 商户 id
     * @param passTemplateId 优惠券 id
     * @param token 分配的优惠券 token
     */
    private void recordTokenToFile(Integer merchantsId, String passTemplateId, String token) throws IOException {
        Path path = Paths.get(Constants.TOKEN_DIR, String.valueOf(merchantsId), passTemplateId + Constants.USED_TOKEN_SUFFIX);
        Files.write(path, (token + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
