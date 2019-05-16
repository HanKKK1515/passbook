package com.hll.passbook.service.impl;

import com.hll.passbook.constant.Constants;
import com.hll.passbook.service.IHBasePassService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.PassTemplate;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <h1>Pass HBase 服务</h1>
 */
@Slf4j
@Service
public class HBasePassServiceImpl implements IHBasePassService {
    /** HBase 客户端 */
    private final HbaseTemplate hbaseTemplate;
    @Autowired
    public HBasePassServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Boolean dropPassTemplateToHBase(PassTemplate passTemplate) {
        byte[] rowKeyB = getRowKeyBytesIfUsable(passTemplate);
        if (rowKeyB == null) {
            return false;
        }

        dropPassTemplateToHBase(passTemplate, rowKeyB);
        return true;
    }

    private byte[] getRowKeyBytesIfUsable(PassTemplate passTemplate) {
        if (null == passTemplate) {
            return null;
        }

        String rowKey = RowKeyGenUtils.genPassTemplateRowKey(passTemplate);
        byte[] rowKeyB = Bytes.toBytes(rowKey);
        try {
            TableName tableName = TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME);
            Table table = hbaseTemplate.getConnection().getTable(tableName);
            Get get = new Get(rowKeyB);
            if (table.exists(get)) {
                log.info("RowKey {} is already exists!", rowKey);
                return null;
            }
        } catch (Exception ex) {
            log.error("DropPassTemplateToHBase Error: {}", ex.getMessage());
            return null;
        }

        return rowKeyB;
    }

    private void dropPassTemplateToHBase(PassTemplate passTemplate, byte[] rowKeyB) {
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

        Put put = new Put(rowKeyB);
        put.addColumn(FAMILY_B, ID, Bytes.toBytes(passTemplate.getId()));
        put.addColumn(FAMILY_B, TITLE, Bytes.toBytes(passTemplate.getTitle()));
        put.addColumn(FAMILY_B, SUMMARY, Bytes.toBytes(passTemplate.getSummary()));
        put.addColumn(FAMILY_B, DESC, Bytes.toBytes(passTemplate.getDesc()));
        put.addColumn(FAMILY_B, HAS_TOKEN, Bytes.toBytes(passTemplate.getHasToken()));
        put.addColumn(FAMILY_B, BACKGROUND, Bytes.toBytes(passTemplate.getBackground()));

        put.addColumn(FAMILY_C, LIMIT, Bytes.toBytes(passTemplate.getLimit()));
        put.addColumn(FAMILY_C, START, Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(passTemplate.getStart())));
        put.addColumn(FAMILY_C, END, Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(passTemplate.getEnd())));

        hbaseTemplate.saveOrUpdate(Constants.PassTemplateTable.TABLE_NAME, put);
    }
}
