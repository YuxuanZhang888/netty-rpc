package com.yuxuan.user.remote;

import com.yuxuan.netty.client.Response;
import com.yuxuan.netty.util.ResponseUtil;
import com.yuxuan.user.bean.User;
import com.yuxuan.user.service.UserService;

import javax.annotation.Resource;

public class UserRemoteImpl implements UserRemote {
    @Resource
    private UserService userService;

    public Response saveUser(User user) {
        userService.save(user);
        return ResponseUtil.createSuccessResult(user);
    }
}
