package com.hll.passbook.service;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.vo.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InventoryServiceTest extends AbstractServiceTest {
    @Autowired
    private IInventoryService inventoryService;

    @Test
    public void testGetInventoryInfo() throws Exception {
        Response response = inventoryService.getInventoryInfo(userId);
        String jsonString = JSON.toJSONString(response);
        System.out.println(jsonString);
    }

}
