package com.hll.passbook.service;

import com.hll.passbook.vo.PassTemplate;

/**
 * <h1>pass hbase 服务</h1>
 */
public interface IHBasePassService {
    /**
     * <h2>将 PassTemplate 写入 HBase </h2>
     * @param  passTemplate {@link PassTemplate}
     * @return true/false
     */
    Boolean dropPassTemplateToHBase(PassTemplate passTemplate);
}
