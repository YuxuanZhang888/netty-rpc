package com.yuxuan.pro_netty_rpc;

import com.yuxuan.netty.annotation.RemoteInvoke;
import com.yuxuan.user.bean.User;
import com.yuxuan.user.remote.UserRemote;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RemoteInvokeTest.class)
@ComponentScan("com.yuxuan")
public class RemoteInvokeTest {
    @RemoteInvoke
    private UserRemote userRemote;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("111");
        userRemote.saveUser(user);
    }
}
