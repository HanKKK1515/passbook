package com.hll.passbook.hbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hll.passbook.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;

/**
 * HBase service实现类
 */
@Slf4j
@Service
public class HBaseServiceImpl implements HBaseService {

    /** hbase 客户端 */
    private final HbaseTemplate hbaseTemplate;
    @Autowired
    public HBaseServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public Boolean createTable(String tableName, String... family) {
        Admin admin;
        TableName tableNameObj;

        try {
            // 从hbaseTemplate 获取configuration对象,再获取Connection对象，用来获取admin
            Connection connection = ConnectionFactory.createConnection(hbaseTemplate.getConfiguration());
            admin = connection.getAdmin();
            tableNameObj = TableName.valueOf(tableName);
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableNameObj);
            for (String col : family) {
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(col);
                tableDescriptor.addFamily(columnDescriptor);
            }
            admin.createTable(tableDescriptor);
            return admin.tableExists(tableNameObj);
        } catch (IOException e) {
            log.error("CreateTable Error!: {}", tableName);
            return false;
        }
    }

    @Override
    public Boolean createPro(Object objc, String tableName, String family, String rowkey) {
        HashMap<String, Object> familyDatas = new HashMap<>();
        familyDatas.put(family, objc);
        return createPro(familyDatas, tableName, rowkey);
    }

    @Override
    public Boolean createPro(Map<String, Object> familyData, String tableName, String rowkey) {
        if (familyData == null || StringUtils.isBlank(tableName) || StringUtils.isBlank(rowkey)) {
            return false;
        }
        return hbaseTemplate.execute(tableName, table -> {
            Put put = new Put(Bytes.toBytes(rowkey));
            Set<String> families = familyData.keySet();
            for (String family : families) {
                PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(familyData.get(family).getClass());
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(familyData.get(family));
                for (PropertyDescriptor propertyDescriptor : pds) {
                    Class<?> propertyType = propertyDescriptor.getPropertyType();
                    if (!BeanUtils.isSimpleProperty(propertyType) || (Class.class == propertyType)) {
                        continue;
                    }
                    String properName = propertyDescriptor.getName();
                    Object propertyValue = beanWrapper.getPropertyValue(properName);

                    if (propertyValue != null) {
                        String value;
                        if (Date.class.isAssignableFrom(propertyType)) {
                            value = DateFormatUtils.format((Date) propertyValue, Constants.DATE_FORMAT);
                        } else {
                            value = propertyValue.toString();
                        }
                        put.addColumn(Bytes.toBytes(family), Bytes.toBytes(properName), Bytes.toBytes(value));
                    }
                }
            }
            table.put(put);
            return true;
        });
    }

    @Override
    public <T> List<T> searchAll(String tableName, Class<T> c) {
        return hbaseTemplate.find(tableName, new Scan(), getRowMapper(c));
    }

    @Override
    public <T> T getOne(String tableName, String rowkey, Class<T> c) {
        if (c == null || StringUtils.isBlank(tableName) || StringUtils.isBlank(rowkey)) {
            return null;
        }
        return hbaseTemplate.get(tableName, rowkey, getRowMapper(c));
    }

    @Override
    public String getColumn(String tableName, String rowkey, String family, String column) {
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(family) || StringUtils.isBlank(rowkey) || StringUtils.isBlank(column)) {
            return null;
        }

        return hbaseTemplate.get(tableName, rowkey, family, column, (result, rowNum) -> {
            List<Cell> ceList = result.listCells();
            String res = "";
            if (ceList != null && ceList.size() > 0) {
                for (Cell cell : ceList) {
                    res = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                }
            }
            return res;
        });
    }

    @Override
    public <T> List<T> findByRowRange(String tableName, String startRow, String endRow, Class<T> c) {
        if (c == null || StringUtils.isBlank(tableName) || StringUtils.isBlank(startRow) || StringUtils.isBlank(endRow)) {
            return null;
        }
        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(endRow));
        scan.setCacheBlocks(false);
        scan.setCaching(2000);
        return hbaseTemplate.find(tableName, scan, getRowMapper(c));
    }

    private <T> RowMapper<T> getRowMapper(Class<T> c) {
        return (result, rowNum) -> result2Object(result, c);

        /*return new RowMapper<T>() {
            @Override
            public T mapRow(Result result, int rowNum) throws Exception {
                T pojo = c.newInstance();
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(pojo);
                List<Cell> ceList = result.listCells();
                for (Cell cellItem : ceList) {
                    String cellName = new String(CellUtil.cloneQualifier(cellItem));
                    if (!"class".equals(cellName)) {
                        beanWrapper.setPropertyValue(cellName, new String(CellUtil.cloneValue(cellItem)));
                    }
                }
                return pojo;
            }
       };*/
    }

    @Override
    public <T> T result2Object(Result result, Class<T> c) {
        List<Cell> ceList = result.listCells();
        if (ceList == null || ceList.size() <= 0) {
            return null;
        }

        JSONObject obj = new JSONObject();
        for (Cell cell : ceList) {
            String key = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
            obj.put(key, value);
        }
        return JSON.parseObject(obj.toJSONString(), c);
    }

    @Override
    public <T> List<T> searchAllByFilters(String tableName, List<Filter> filters, Class<T> c) {
        Scan scan = new Scan();
        scan.setFilter(new FilterList(filters));
        return hbaseTemplate.find(tableName, scan, getRowMapper(c));
    }

    @Override
    public <T> List<T> searchAllByFilter(String tableName, Filter filter, Class<T> c) {
        Scan scan = new Scan();
        scan.setFilter(filter);
        return hbaseTemplate.find(tableName, scan, getRowMapper(c));
    }

    @Override
    public Result[] getByGets(String tableName, List<Get> gets) {
        return hbaseTemplate.execute(tableName, table -> table.get(gets));
    }

    @Override
    public Boolean existsByGet(String tableName, Get get) {
        return hbaseTemplate.execute(tableName, table -> table.exists(get));
    }

    @Override
    public Boolean saveOrUpdates(String tableName, List<Put> puts) {
        return hbaseTemplate.execute(tableName, table -> {
            table.put(puts);
            return true;
        });
    }

    @Override
    public Boolean saveOrUpdate(String tableName, Put put) {
        return hbaseTemplate.execute(tableName, table -> {
            table.put(put);
            return true;
        });
    }
}