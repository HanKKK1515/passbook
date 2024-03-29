package com.hll.passbook.constant;

/**
 * <h1>常量定义</h1>
 */
public class Constants {
    /** 商户优惠券Kafka Topic */
    public static final String TEMPLATE_TOPIC = "merchants-template";
    /** token文件存储路径 */
    public static final String TOKEN_DIR = "C:\\tmp\\token";
    /** 已使用的token文件名后缀 */
    public static final String USED_TOKEN_SUFFIX = "_";
    /** 用户数的redis key */
    public static final String USER_COUNT_REDIS_KEY = "HPassBook-user-count";
    /** 日期格式 */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * <h2>user hbase table</h2>
     */
    public class UserTable {
        /** user hbase 表名 */
        public static final String TABLE_NAME = "pb:user";
        /** 基本信息列簇 */
        public static final String FAMILY_B = "b";
        /** 用户名 */
        public static final String NAME = "name";
        /** 用户年龄 */
        public static final String AGE = "age";
        /** 用户性别 */
        public static final String SEX = "sex";

        /** 额外信息列簇 */
        public static final String FAMILY_O = "o";
        /** 电话号码 */
        public static final String PHONE = "phone";
        /** 住址 */
        public static final String ADDRESS = "address";
    }

    /**
     * <h2>passtemlate hbase table</h2>
     */
    public class PassTemplateTable {
        /** PassTemplate Hbase 表名 */
        public static final String TABLE_NAME = "pb:passtemplate";
        /** 基本信息列簇 */
        public static final String FAMILY_B = "b";
        /** 商户 id */
        public static final String ID = "id";
        /** 优惠券标题 */
        public static final String TITLE = "title";
        /** 优惠券摘要信息 */
        public static final String SUMMARY = "summary";
        /** 优惠券详细信息 */
        public static final String DESC = "desc";
        /** 优惠券是否有token */
        public static final String HAS_TOKEN = "hasToken";
        /** 优惠券背景色 */
        public static final String BACKGROUND = "background";

        /** 约束信息列簇 */
        public static final String FAMILY_C = "c";
        /** 最大个数限制 */
        public static final String LIMIT = "limit";
        /** 优惠券开始时间 */
        public static final String START = "start";
        /** 优惠券结束时间 */
        public static final String END = "end";
    }

    /**
     * <h2>pass hbase table</h2>
     */
    public class PassTable {
        /** pass hbase 表名 */
        public static final String TABLE_NAME = "pb:pass";
        /** 信息列簇 */
        public static final String FAMILY_I = "i";
        /** 用户id */
        public static final String USER_ID = "userId";
        /** 优惠券id */
        public static final String TEMPLATE_ID = "templateId";
        /** 优惠券识别码 */
        public static final String TOKEN = "token";
        /** 领取时间 */
        public static final String ASSIGNED_DATE = "assignedDate";
        /** 消费时间 */
        public static final String CON_DATE = "conDate";
    }

    /**
     * <h2>feedback hbase table</h2>
     */
    public class FeedbackTable {
        /** feedback hbase 表名 */
        public static final String TABLE_NAME = "pb:feedback";
        /** 信息列簇 */
        public static final String FAMILY_I = "i";
        /** 用户id */
        public static final String USER_ID = "userId";
        /** 评论/反馈类型 */
        public static final String TYPE = "type";
        /** passtemplate rowkey,如果是app评论，则是-1 */
        public static final String TEMPLATE_ID = "templateId";
        /** 评论内容 */
        public static final String COMMENT = "comment";
    }
}
