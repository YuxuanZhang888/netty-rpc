package com.yuxuan.pro_netty_rpc;

import com.yuxuan.netty.client.ClientRequest;
import com.yuxuan.netty.client.Response;
import com.yuxuan.netty.client.TcpClient;
import com.yuxuan.user.bean.User;
import org.junit.jupiter.api.Test;

public class TestTcp {
    @Test
    public void testGetResponse() {
        ClientRequest request = new ClientRequest();
        request.setContent("测试tcp连接请求");
        Response response = TcpClient.send(request);
        System.out.println(response.getResult());
    }

    @Test
    public void testSaveUser() {
        ClientRequest request = new ClientRequest();
        User user = new User();
        user.setId(1);
        user.setUsername("111");
        request.setCommand("com.yuxuan.user.controller.UserController.saveUser");
        request.setContent(user);
        Response response = TcpClient.send(request);
        System.out.println(response.getResult());
    }
}
