package com.hll.passbook.service.impl;

import com.hll.passbook.constant.Constants;
import com.hll.passbook.service.IHBasePassService;
import com.hll.passbook.utils.RowKeyGenUtils;
import com.hll.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
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
        String rowKey = getRowKeyBytesIfUsable(passTemplate);
        if (rowKey == null) {
            return false;
        }

        dropPassTemplateToHBase(passTemplate, rowKey);
        return true;
    }

    private String getRowKeyBytesIfUsable(PassTemplate passTemplate) {
        if (null == passTemplate) {
            return null;
        }

        String rowKey = RowKeyGenUtils.genPassTemplateRowKey(passTemplate);
        try {
            TableName tableName = TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME);
            Connection connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
            Table table = connection.getTable(tableName);
//            Table table = hbaseTemplate.getConnection().getTable(tableName);
            Get get = new Get(Bytes.toBytes(rowKey));
            if (table.exists(get)) {
                log.info("RowKey {} is already exists!", rowKey);
                return null;
            }
        } catch (Exception ex) {
            log.error("DropPassTemplateToHBase Error: {}", ex.getMessage());
            return null;
        }

        return rowKey;
    }

    private void dropPassTemplateToHBase(PassTemplate passTemplate, String rowKey) {
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_B,
                Constants.PassTemplateTable.ID,
                Bytes.toBytes(passTemplate.getId())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_B,
                Constants.PassTemplateTable.TITLE,
                Bytes.toBytes(passTemplate.getTitle())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_B,
                Constants.PassTemplateTable.SUMMARY,
                Bytes.toBytes(passTemplate.getSummary())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_B,
                Constants.PassTemplateTable.DESC,
                Bytes.toBytes(passTemplate.getDesc())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_B,
                Constants.PassTemplateTable.HAS_TOKEN,
                Bytes.toBytes(passTemplate.getHasToken())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_B,
                Constants.PassTemplateTable.BACKGROUND,
                Bytes.toBytes(passTemplate.getBackground())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_C,
                Constants.PassTemplateTable.LIMIT,
                Bytes.toBytes(passTemplate.getLimit())
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_C,
                Constants.PassTemplateTable.START,
                Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(passTemplate.getStart()))
        );
        hbaseTemplate.put(
                Constants.PassTemplateTable.TABLE_NAME,
                rowKey,
                Constants.PassTemplateTable.FAMILY_C,
                Constants.PassTemplateTable.END,
                Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(passTemplate.getEnd()))
        );

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

        hbaseTemplate.saveOrUpdate(Constants.PassTemplateTable.TABLE_NAME, put);
        */
    }
}
