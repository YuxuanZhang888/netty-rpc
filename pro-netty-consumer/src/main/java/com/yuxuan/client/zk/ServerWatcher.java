package com.yuxuan.client.zk;

import com.yuxuan.client.core.ChannelManager;
import com.yuxuan.client.core.TcpClient;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerWatcher implements CuratorWatcher {

    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        CuratorFramework client = ZookeeperFactory.create();
        String path = watchedEvent.getPath();
        client.getChildren().usingWatcher(this).forPath(path);

        List<String> serverPath = client.getChildren().forPath(path);
        ChannelManager.realServerPath = new CopyOnWriteArrayList<>();

        ChannelManager.realServerPath.clear();
        for (String serverPathItem : serverPath) {
            String[] strs = serverPathItem.split("#");
            int weight = Integer.parseInt(strs[2]);
            if (weight > 0) {
                for (int w = 0; w <= weight; w++) {
                    ChannelManager.realServerPath.add(strs[0] + "#" + strs[1]);
                }
            }
            ChannelManager.realServerPath.add(strs[0] + "#" + strs[1]);

        }
        ChannelManager.clear();
        for (String realServer : ChannelManager.realServerPath) {
            String[] str = realServer.split("#");
            try {
                int weight = Integer.parseInt(str[2]);
                if (weight > 0) {
                    for (int w = 0; w <= weight; w++) {
                        ChannelFuture channelFuture = TcpClient.bootstrap.connect(str[0], Integer.valueOf(str[1]));
                        ChannelManager.add(channelFuture);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
