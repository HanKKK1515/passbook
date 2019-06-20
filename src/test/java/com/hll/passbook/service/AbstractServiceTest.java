package com.hll.passbook.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbstractServiceTest {
    Long userId;

    @Before
    public void init() {
        userId = 3336484L;
    }
}
