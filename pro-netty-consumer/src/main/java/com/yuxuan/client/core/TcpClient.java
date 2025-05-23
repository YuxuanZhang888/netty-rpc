package com.yuxuan.client.core;

import com.alibaba.fastjson.JSONObject;
import com.yuxuan.client.constant.Constants;
import com.yuxuan.client.param.ClientRequest;
import com.yuxuan.client.param.Response;
import com.yuxuan.client.handler.SimpleClientHandler;
import com.yuxuan.client.zk.ServerWatcher;
import com.yuxuan.client.zk.ZookeeperFactory;
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
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import java.util.List;


public class TcpClient {
    public static final Bootstrap bootstrap = new Bootstrap();
    static final EventLoopGroup workGroup = new NioEventLoopGroup();
    static ChannelFuture f = null;


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
        CuratorFramework client = ZookeeperFactory.create();
        try {
            List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);

            CuratorWatcher watcher = new ServerWatcher();
            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);

            for (String serverPath : serverPaths) {
                String[] strs = serverPath.split("#");
                int weight = Integer.parseInt(strs[2]);
                if (weight > 0) {
                    for (int w = 0; w <= weight; w++) {
                        ChannelManager.realServerPath.add(strs[0] + "#" + strs[1]);
                        ChannelFuture channelFuture = bootstrap.connect(strs[0], Integer.parseInt(strs[1]));
                        ChannelManager.add(channelFuture);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public static Response send(ClientRequest request) {
        f = ChannelManager.get(ChannelManager.position);
        f.channel().writeAndFlush(JSONObject.toJSONString(request));
        f.channel().writeAndFlush("\r\n");
        DefaultFuture df = new DefaultFuture(request);
        return df.get();
    }


}
