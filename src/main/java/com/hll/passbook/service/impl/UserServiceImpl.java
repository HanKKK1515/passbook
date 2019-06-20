package com.hll.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.hll.passbook.constant.Constants;
import com.hll.passbook.hbase.HBaseService;
import com.hll.passbook.service.IUserService;
import com.hll.passbook.vo.Response;
import com.hll.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>创建用户服务实现</h2>
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    /** HBase 工具类 */
    private final HBaseService hBaseService;

    /** Redis 客户端 */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public UserServiceImpl(HBaseService hBaseService, StringRedisTemplate redisTemplate) {
        this.hBaseService = hBaseService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Response createUser(User user) throws Exception {
        Long curCount = redisTemplate.opsForValue().increment(Constants.USER_COUNT_REDIS_KEY, 1);
        Long userId = genUserId(curCount);

        Map<String, Object> familyData = new HashMap<>();
        familyData.put(Constants.UserTable.FAMILY_B, user.getBaseInfo());
        familyData.put(Constants.UserTable.FAMILY_O, user.getOtherInfo());
        if (hBaseService.createPro(familyData, Constants.UserTable.TABLE_NAME, String.valueOf(userId))) {
            user.setId(userId);
            return new Response(user);
        } else {
            log.error("CreateUser Error!: {}", JSON.toJSONString(user));
            return Response.failure("CreateUser Error!");
        }

    }

    /**
     * <h2>生成 UserId</h2>
     * @param prefix 前缀
     * @return userId
     */
    private Long genUserId(Long prefix) {
        String suffix = RandomStringUtils.randomNumeric(5);
        return Long.valueOf(prefix + suffix);
    }
}
