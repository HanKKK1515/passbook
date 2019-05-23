package com.hll.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 投放的优惠券模板对象的定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassTemplate {
    /** 优惠券所属商户id */
    private Integer id;
    /** 优惠券标题 */
    private String title;
    /** 优惠券摘要 */
    private String summary;
    /** 优惠券详细信息 */
    private String desc;
    /** 优惠券是否有token，用于商户核销。token存储于 redis set 中，每次领取从redis获取 */
    private Boolean hasToken;
    /** 优惠券背景色 */
    private Integer background;

    /** 优惠券最大个数限制，无限制为-1 */
    private Long limit;
    /** 优惠券开始时间 */
    private Date start;
    /** 优惠券结束时间 */
    private Date end;
}
