package com.yuxuan.client.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan.client.param.Response;
import com.yuxuan.client.core.DefaultFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ("ping".equals(msg.toString())) {
            ctx.channel().writeAndFlush("pong\r\n");
            return;
        }
        DefaultFuture.receive(JSONArray.parseObject(msg.toString(), Response.class));
        System.out.println("接收服务器返回数据" + JSONObject.toJSONString(msg));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
