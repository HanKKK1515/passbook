package com.hll.passbook.hbase;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.Filter;

import java.util.List;
import java.util.Map;

/**
 * <h1>操作HBase  service</h1>
 */
public interface HBaseService {
    /**
     * <h2>在HBase上面创建表</h2>
     *
     * @param tableName 表名
     * @param family    列族名(可以同时传入多个列族名)
     * @return true/false
     */
    Boolean createTable(String tableName, String... family);

    /**
     * <h2>向表中插入数据</h2>
     *
     * @param c         数据模型对象
     * @param tableName 表名
     * @param family    列族
     * @param rowkey    rowkey
     * @return true/false
     */
    Boolean createPro(Object c, String tableName, String family, String rowkey);

    /**
     * <h2>向表中插入多个列簇数据</h2>
     *
     * @param familyData 列簇和对应的数据模型对象
     * @param tableName 表名
     * @param rowkey rowkey
     * @return true/false
     */
    Boolean createPro(Map<String, Object> familyData, String tableName, String rowkey);

    /**
     * <h2>Scan 查询所有的hbase数据</h2>
     *
     * @param tableName 表名
     * @param <T>       返回数据类型
     * @return 返回Class<T>类型的结果列表
     */
    <T> List<T> searchAll(String tableName, Class<T> c);

    /**
     * <h2>通过表名和rowkey获取一行数据</h2>
     *
     * @param <T>       数据类型
     * @param tableName 表名
     * @param rowkey    rewkey
     * @return T
     */
    <T> T getOne(String tableName, String rowkey, Class<T> c);

    /**
     * <h2>查询一条记录一个column的值</h2>
     *
     * @param tableName 表名
     * @param rowkey    rewkey
     * @param family    列族
     * @param column    列
     * @return String
     */
    String getColumn(String tableName, String rowkey, String family, String column);

    /**
     * <h2>把 Result 对象转换成 Object </h2>
     *
     * @param result {@link Result}
     * @param c 目标类型
     * @param <T> T
     * @return T
     */
    <T> T result2Object(Result result, Class<T> c);

    /**
     * <h2>查询开始row和结束row之间的数据</h2>
     *
     * @param <T>       数据类型
     * @param tableName 表名
     * @param startRow  开始row
     * @param endRow    结束row
     * @return List<T>
     */
    <T> List<T> findByRowRange(String tableName, String startRow, String endRow, Class<T> c);

    /**<h2>根据Filter列表条件查询</h2>
     * @param tableName 表名
     * @param filters 过滤条件集合
     * SingleColumnValueFilter scvf = new SingleColumnValueFilter(
     * Bytes.toBytes(family),  //搜索哪个列族
     * Bytes.toBytes(column),   //搜素哪一列
     * CompareFilter.CompareOp.EQUAL, //对比关系
     * Bytes.toBytes(Keywords)); //这里传入 SubstringComparator 比较器,搜索的结果为列值(value)包含关键字,传入bytes数组,则进行完全匹配
     * scvf.setLatestVersionOnly(true); //属性设置为true时,如果查询的列族下,没有colume这个列,则不返回这行数据,反之就返回这行数据
     * @param c c
     * @return List<T>
     */
    <T> List<T> searchAllByFilters(String tableName, List<Filter> filters, Class<T> c);

    /**
     * <h2>根据Filter条件查询</h2>
     *
     * @param tableName 表名
     * @param filter 过滤条件集合
     * @param c c
     * @param <T>
     * @return List<T>
     */
    <T> List<T> searchAllByFilter(String tableName, Filter filter, Class<T> c);

    /**
     * <h2>根据get获取结果</h2>
     *
     * @param tableName 表名
     * @param gets {@link Get}
     * @return {@link Result}
     */
    Result[] getByGets(String tableName, List<Get> gets);

    /**
     * <h2>判断get是否存在</h2>
     *
     * @param tableName 表名
     * @param get {@link Get}
     * @return true/false
     */
    Boolean existsByGet(String tableName, Get get);

    /**
     * <h2>根据Put集合更新</h2>
     *
     * @param tableName 表名
     * @param puts {@link Put}
     * @return true/false
     */
    Boolean saveOrUpdates(String tableName, List<Put> puts);

    /**
     * <h2>根据Put更新</h2>
     *
     * @param tableName 表名
     * @param put {@link Put}
     * @return true/false
     */
    Boolean saveOrUpdate(String tableName, Put put);
}