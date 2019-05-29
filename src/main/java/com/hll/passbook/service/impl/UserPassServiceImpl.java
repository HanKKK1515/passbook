package com.hll.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.Constants;
import com.hll.passbook.constant.PassStatus;
import com.hll.passbook.dao.MerchantsDao;
import com.hll.passbook.entity.Merchants;
import com.hll.passbook.mapper.PassRowMapper;
import com.hll.passbook.service.IUserPassService;
import com.hll.passbook.vo.Pass;
import com.hll.passbook.vo.PassInfo;
import com.hll.passbook.vo.PassTemplate;
import com.hll.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>用户优惠券相关功能实现</h1>
 */
@Slf4j
@Service
public class UserPassServiceImpl implements IUserPassService {
    /** HBase 客户端 */
    private final HbaseTemplate hbaseTemplate;
    /** 商户服务 */
    private final MerchantsDao merchantsDao;

    @Autowired
    public UserPassServiceImpl(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
    }

    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInof(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) throws Exception {
        List<Filter> filters = new ArrayList<>();
        String reverseUserId = new StringBuilder(String.valueOf(pass.getUserId())).reverse().toString();
        byte[] rowPrefix = Bytes.toBytes(reverseUserId);
        filters.add(new PrefixFilter(rowPrefix));
        filters.add(new SingleColumnValueFilter(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.CON_DATE.getBytes(),
                CompareOperator.EQUAL,
                Bytes.toBytes("-1")
        ));
        filters.add(new SingleColumnValueFilter(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.TEMPLATE_ID.getBytes(),
                CompareOperator.EQUAL,
                Bytes.toBytes(pass.getTemplateId())
        ));

        Scan scan = new Scan();
        scan.setFilter(new FilterList(filters));

        List<Pass> passes = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());
        if (passes == null || passes.size() != 1) {
            log.error("UserUsePass Error: {}", JSON.toJSONString(passes));
            return Response.failure("UserUsePass Error");
        }

        hbaseTemplate.put(
                Constants.PassTable.TABLE_NAME,
                passes.get(0).getRowKey(),
                Constants.PassTable.FAMILY_I,
                Constants.PassTable.CON_DATE,
                Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()))
        );

        /*
        Put put = new Put(passes.get(0).getRowKey().getBytes());
        put.addColumn(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.CON_DATE.getBytes(),
                Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()))
        );
        List<Mutation> datas = new ArrayList<>();
        datas.add(put);

        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, datas);
        */

        return Response.success();
    }

    /**
     * <h2>通过 Passes 对象构建 Map</h2>
     * @param passes {@link Pass}
     * @return Map {@link PassTemplate}
     */
    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passes) throws Exception {
        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);

        Stream<String> templateIdsStream = passes.stream().map(Pass::getTemplateId);
        List<String> templateIds = templateIdsStream.collect(Collectors.toList());
        
        List<Get> templateGets = new ArrayList<>(templateIds.size());
        templateIds.forEach(t -> templateGets.add(new Get(Bytes.toBytes(t))));

        TableName templateTableName = TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME);
        Connection connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
        Table templateTable = connection.getTable(templateTableName);
//        Table templateTable = hbaseTemplate.getConnection().getTable(templateTableName);
        Result[] templateResults = templateTable.get(templateGets);

        // PassTemplate Id -> PassTemplate Object 的 Map,用于构造 PassInfo
        Map<String, PassTemplate> templateId2Object = new HashMap<>();
        for (Result result : templateResults) {
            PassTemplate passTemplate = new PassTemplate();
            passTemplate.setId(Bytes.toInt(result.getValue(FAMILY_B, ID)));
            passTemplate.setTitle(Bytes.toString(result.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(result.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(result.getValue(FAMILY_B, DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(result.getValue(FAMILY_B, HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(result.getValue(FAMILY_B, BACKGROUND)));

            String[] pattern = new String[] {"yyyy-mm-dd"};
            passTemplate.setLimit(Bytes.toLong(result.getValue(FAMILY_C, LIMIT)));
            passTemplate.setStart(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_C, START)), pattern));
            passTemplate.setEnd(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_C, END)), pattern));

            templateId2Object.put(Bytes.toString(result.getRow()), passTemplate);
        }

        return templateId2Object;
    }

    /**
     * <h2>通过 PassTemplate 获取 Merchants Map </h2>
     * @param passTemplates {@link PassTemplate}
     * @return Map {@link Merchants}
     */
    private Map<Integer, Merchants> buildMerchantsMap(List<PassTemplate> passTemplates) throws Exception {
        Map<Integer, Merchants> merchantsMap = new HashMap<>();
        Stream<Integer> merchantsIdStream = passTemplates.stream().map(PassTemplate::getId);
        List<Integer> merchantsIds = merchantsIdStream.collect(Collectors.toList());
        List<Merchants> merchants = merchantsDao.findAllByIdIn(merchantsIds);

        merchants.forEach(m -> merchantsMap.put(m.getId(), m));
        return merchantsMap;
    }

    /**
     * <h2>根据优惠券状态来获取优惠券信息</h2>
     * @param userId 用户 id
     * @param status 优惠券状态 {@link PassStatus}
     * @return {@link Response}
     */
    private Response getPassInfoByStatus(Long userId, PassStatus status) throws Exception {
        String reverseUserId = new StringBuilder(String.valueOf(userId)).reverse().toString();
        byte[] rowPrefix = Bytes.toBytes(reverseUserId);

        List<Filter> filters = new ArrayList<>();
        filters.add(new PrefixFilter(rowPrefix));
        if (status != PassStatus.ALL) {
            filters.add(new SingleColumnValueFilter(
                    Constants.PassTable.FAMILY_I.getBytes(),
                    Constants.PassTable.CON_DATE.getBytes(),
                    status == PassStatus.UNUSED ? CompareOperator.EQUAL : CompareOperator.NOT_EQUAL,
                    Bytes.toBytes("-1")
            ));
        }
        Scan scan = new Scan();
        scan.setFilter(new FilterList(filters));


        List<Pass> passes = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());
        Map<String, PassTemplate> templateMap = buildPassTemplateMap(passes);
        Map<Integer, Merchants> merchantsMap = buildMerchantsMap(new ArrayList<>(templateMap.values()));

        List<PassInfo> passInfos = new ArrayList<>();
        for (Pass pass : passes) {
            PassTemplate passTemplate = templateMap.getOrDefault(pass.getTemplateId(), null);
            if (null == passTemplate) {
                log.error("PassTemplate null: {}", pass.getTemplateId());
                continue;
            }
            Merchants merchants = merchantsMap.getOrDefault(passTemplate.getId(), null);
            if (null == merchants) {
                log.error("Merchants null: {}", pass.getUserId());
                continue;
            }

            passInfos.add(new PassInfo(pass, passTemplate, merchants));
        }
        return new Response(passInfos);
    }
}
