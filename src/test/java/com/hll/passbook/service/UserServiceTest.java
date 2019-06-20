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
        user.setBaseInfo(new User.BaseInfo("jack2", 162, "m2"));
        user.setOtherInfo(new User.OtherInfo("13355555552", "深圳市南山区2"));

        System.out.println(userService.createUser(user));
    }
}
