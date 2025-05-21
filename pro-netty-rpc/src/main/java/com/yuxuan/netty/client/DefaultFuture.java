package com.yuxuan.netty.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    public static ConcurrentHashMap<Long, DefaultFuture> allFuture = new ConcurrentHashMap<Long, DefaultFuture>();
    private Response response;
    public Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();

    public DefaultFuture(ClientRequest clientRequest) {
        allFuture.put(clientRequest.getId(), this);
    }

    public Response get() {
        lock.lock();
        try {
            while (!done()) {
                condition.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    public static void receive(Response response) {
        DefaultFuture df = allFuture.get(response.getId());
        if (df != null) {
            Lock lock = df.lock;
            lock.lock();
            try {
                df.setResponse(response);
                df.condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean done() {
        return this.response != null;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
