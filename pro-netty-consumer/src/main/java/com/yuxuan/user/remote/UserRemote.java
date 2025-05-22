package com.yuxuan.user.remote;

import com.yuxuan.client.param.Response;
import com.yuxuan.user.bean.User;

public interface UserRemote {
    public Response saveUser(User user);
}
