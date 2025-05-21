package com.yuxuan.netty.client;

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


public class NettyClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
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
            ChannelFuture f = bootstrap.connect(host, port).sync();
            f.channel().writeAndFlush("hello server");
            f.channel().writeAndFlush("\r\n");
            f.channel().closeFuture().sync();
            Object result = f.channel().attr(AttributeKey.valueOf("11")).get();
            System.out.println(result.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
