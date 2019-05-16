package com.hll.passbook.service.impl;

import com.hll.passbook.service.IUserPassService;
import com.hll.passbook.vo.Pass;
import com.hll.passbook.vo.PassTemplate;
import com.hll.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <h1>用户优惠券相关功能实现</h1>
 */
@Slf4j
@Service
public class UserPassServiceImpl implements IUserPassService {


    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return null;
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return null;
    }

    @Override
    public Response getUserAllPassInof(Long userId) throws Exception {
        return null;
    }

    @Override
    public Response userUsePass(Pass pass) throws Exception {
        return null;
    }

    /**
     * <h2>通过 Passes 对象构建 Map</h2>
     * @param passes {@link: Pass}
     * @return Map {@link: PassTemplate}
     * @throws Exception
     */
    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passes) throws Exception {

        return null;
    }
}
