package com.hll.passbook.service;

import com.hll.passbook.vo.User;
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
public class UserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    public void testCreateUser() throws Exception {
        User user = new User();
        user.setBaseInfo(new User.BaseInfo("jack", 16, "m"));
        user.setOtherInfo(new User.OtherInfo("13333333333", "深圳市南山区"));

        System.out.println(userService.createUser(user));
    }
}
