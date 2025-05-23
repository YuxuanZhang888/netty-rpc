package com.yuxuan.client.core;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<>();
    static AtomicInteger position = new AtomicInteger(0);

    public static void removeChannel(ChannelFuture channelFuture) {
        channelFutures.remove(channelFuture);
    }

    public static void add(ChannelFuture channelFuture) {
        channelFutures.add(channelFuture);
    }

    public static void clear() {
        channelFutures.clear();
    }

    public static ChannelFuture get(AtomicInteger i) {
        int size = channelFutures.size();
        ChannelFuture channelFuture = null;
        if (i.get() > size) {
            channelFuture = channelFutures.get(0);
            ChannelManager.position = new AtomicInteger(1);
        } else {
            channelFuture = channelFutures.get(i.getAndIncrement());
        }
        return channelFuture;
    }

}
