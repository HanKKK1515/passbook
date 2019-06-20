package com.hll.passbook.service.impl;

import com.hll.passbook.constant.Constants;
import com.hll.passbook.hbase.HBaseService;
import com.hll.passbook.service.IHBasePassService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Pass HBase 服务</h1>
 */
@Slf4j
@Service
public class HBasePassServiceImpl implements IHBasePassService {
    /** HBase 工具类 */
    private final HBaseService hBaseService;
    @Autowired
    public HBasePassServiceImpl(HBaseService hBaseService) {
        this.hBaseService = hBaseService;
    }

    @Override
    public Boolean dropPassTemplateToHBase(PassTemplate passTemplate) {
        String rowKey = getRowKeyBytesIfUsable(passTemplate);
        if (rowKey == null) {
            return false;
        }

        return dropPassTemplateToHBase(passTemplate, rowKey);
    }

    private String getRowKeyBytesIfUsable(PassTemplate passTemplate) {
        if (null == passTemplate) {
            return null;
        }

        String rowKey = RowKeyGenUtils.genPassTemplateRowKey(passTemplate);
        Get get = new Get(Bytes.toBytes(rowKey));
        if (hBaseService.existsByGet(Constants.PassTemplateTable.TABLE_NAME, get)) {
            log.info("RowKey {} is already exists!", rowKey);
            return null;
        }

        return rowKey;
    }

    private Boolean dropPassTemplateToHBase(PassTemplate passTemplate, String rowKey) {
        Map<String, Object> familyData = new HashMap<>();

        PassTemplate passTemplateB = new PassTemplate();
        passTemplateB.setId(passTemplate.getId());
        passTemplateB.setTitle(passTemplate.getTitle());
        passTemplateB.setSummary(passTemplate.getSummary());
        passTemplateB.setDesc(passTemplate.getDesc());
        passTemplateB.setHasToken(passTemplate.getHasToken());
        passTemplateB.setBackground(passTemplate.getBackground());
        familyData.put(Constants.PassTemplateTable.FAMILY_B, passTemplateB);

        PassTemplate passTemplateC = new PassTemplate();
        passTemplateC.setLimit(passTemplate.getLimit());
        passTemplateC.setStart(passTemplate.getStart());
        passTemplateC.setEnd(passTemplate.getEnd());
        familyData.put(Constants.PassTemplateTable.FAMILY_C, passTemplateC);

        return hBaseService.createPro(familyData, Constants.PassTemplateTable.TABLE_NAME, rowKey);

        /*
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

        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(FAMILY_B, ID, Bytes.toBytes(passTemplate.getId()));
        put.addColumn(FAMILY_B, TITLE, Bytes.toBytes(passTemplate.getTitle()));
        put.addColumn(FAMILY_B, SUMMARY, Bytes.toBytes(passTemplate.getSummary()));
        put.addColumn(FAMILY_B, DESC, Bytes.toBytes(passTemplate.getDesc()));
        put.addColumn(FAMILY_B, HAS_TOKEN, Bytes.toBytes(passTemplate.getHasToken()));
        put.addColumn(FAMILY_B, BACKGROUND, Bytes.toBytes(passTemplate.getBackground()));

        put.addColumn(FAMILY_C, LIMIT, Bytes.toBytes(passTemplate.getLimit()));
        put.addColumn(FAMILY_C, START, Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(passTemplate.getStart())));
        put.addColumn(FAMILY_C, END, Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(passTemplate.getEnd())));

        hBaseService.saveOrUpdate(Constants.PassTemplateTable.TABLE_NAME, put);
        */
    }
}
