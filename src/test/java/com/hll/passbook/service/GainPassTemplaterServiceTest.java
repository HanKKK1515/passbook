package com.hll.passbook.service;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.vo.GainPassTemplateRequest;
import com.hll.passbook.vo.PassTemplate;
import com.hll.passbook.vo.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户领取优惠券功能测试</h1>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GainPassTemplaterServiceTest extends AbstractServiceTest {
    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    @Test
    public void testGainPassTemplate() throws Exception {

        PassTemplate target = new PassTemplate();
        target.setId(38);
        target.setTitle("标题：测试38");
        target.setHasToken(true);

        GainPassTemplateRequest request = new GainPassTemplateRequest(userId, target);
        Response response = gainPassTemplateService.gainPassTemplate(request);
        System.out.println(JSON.toJSONString(response));
    }
}
