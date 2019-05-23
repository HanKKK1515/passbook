package com.hll.passbook.controller;

import com.hll.passbook.log.LogConstants;
import com.hll.passbook.log.LogGenerator;
import com.hll.passbook.service.IUserService;
import com.hll.passbook.vo.Response;
import com.hll.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>创建用户服务</h1>
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {
    /** 创建用户服务 */
    private final IUserService userService;
    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CreateUserController(IUserService userService, HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>创建用户</h2>
     * @param user {@link User}
     * @return {@link Response}
     */
    @PostMapping("/createuser")
    @ResponseBody
    public Response createUser(@RequestBody User user) throws Exception {
        LogGenerator.genLog(httpServletRequest, user.getId(), LogConstants.ActionName.CREATE_USER, user);
        return userService.createUser(user);
    }
}
