package com.hll.passbook.service;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.vo.Pass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户服务测试</h1>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPassServiceTest extends AbstractServiceTest {
    @Autowired
    private IUserPassService userPassService;

    @Test
    public void testGetUserPassInfo() throws Exception {
        System.out.println(JSON.toJSONString(userPassService.getUserPassInfo(userId)));
    }

    @Test
    public void testGetUserUsedPassInfo() throws Exception {
        System.out.println(JSON.toJSONString( userPassService.getUserUsedPassInfo(userId)));
    }

    @Test
    public void testGetUserAllPassInfo() throws Exception {
        System.out.println(JSON.toJSONString(
                userPassService.getUserAllPassInof(userId)
        ));
    }

    @Test
    public void testUserUsePass() throws Exception {
        Pass pass = new Pass();
        pass.setUserId(userId);
        pass.setTemplateId("6c4f4d10c4659d5f73e25ab2a944c2b9");

        System.out.println(JSON.toJSONString(
                userPassService.userUsePass(pass)
        ));
    }
}
