package com.yuxuan.server;

import com.yuxuan.netty.constant.Constants;
import com.yuxuan.netty.factory.ZookeeperFactory;
import com.yuxuan.netty.handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class NettyServer {

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            serverBootstrap.group(parentGroup, childGroup);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                            ch.pipeline().addLast(new IdleStateHandler(60, 45, 20, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new SimpleServerHandler());
                        }
                    });
            InetAddress inetAddress = InetAddress.getLocalHost();
            ChannelFuture f = serverBootstrap.bind(8080).sync();
            CuratorFramework client = ZookeeperFactory.create();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(Constants.SERVER_PATH + inetAddress.getHostAddress());
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
