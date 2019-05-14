package com.hll.passbook.log;

/**
 * <h1>日志记录常量定义</h1>
 */
public class LogConstants {
    /** 用户动作名称 */
    public class ActionName {
        /** 用户查看优惠券信息 */
        public static final String user_pass_info = "UserPassInfo";

        /** 用户查看已使用的优惠券信息 */
        public static final String user_used_pass_info = "UserUsedPassInfo";

        /** 用户使用优惠券 */
        public static final String user_use_pass = "UserUsePass";

        /** 用户获取库存信息 */
        public static final String inventory_info = "InventoryInfo";

        /** 用户领取优惠券 */
        public static final String gain_pass_template = "GainPassTemplate";

        /** 用户创建评论 */
        public static final String create_feedback = "CreateFeedback";

        /** 用户获取评论 */
        public static final String get_feedback = "GetFeedback";
    }
}
