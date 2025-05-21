package com.yuxuan.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.yuxuan.netty.handler.SimpleClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;


public class TcpClient {
    static final Bootstrap bootstrap = new Bootstrap();
    static final EventLoopGroup workGroup = new NioEventLoopGroup();
    static ChannelFuture f = null;
    static final String host = "localhost";
    static final int port = 8080;

    static {
        bootstrap.group(workGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new SimpleClientHandler());
                socketChannel.pipeline().addLast(new StringEncoder());
            }
        });
        try {
            f = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static Response send(ClientRequest request) {
        f.channel().writeAndFlush(JSONObject.toJSONString(request));
        f.channel().writeAndFlush("\r\n");
        DefaultFuture df = new DefaultFuture(request);
        return df.get();
    }


}
