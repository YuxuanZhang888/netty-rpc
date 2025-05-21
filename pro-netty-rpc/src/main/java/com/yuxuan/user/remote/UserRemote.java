package com.yuxuan.user.remote;

import com.yuxuan.netty.client.Response;
import com.yuxuan.user.bean.User;

public interface UserRemote {
    public Response saveUser(User user);
}
