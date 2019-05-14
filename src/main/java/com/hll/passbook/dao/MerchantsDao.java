package com.hll.passbook.dao;

import com.hll.passbook.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * merchants dao 接口
 */
public interface MerchantsDao extends JpaRepository<Merchants, Integer> {
    Merchants findAllByName(String name);
    List<Merchants> findAllByIdIn(List<Integer> ids);
}
