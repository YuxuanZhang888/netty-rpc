package com.yuxuan.client.core;

import com.yuxuan.client.param.ClientRequest;
import com.yuxuan.client.param.Response;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    public static ConcurrentHashMap<Long, DefaultFuture> allFuture = new ConcurrentHashMap<Long, DefaultFuture>();
    private Response response;
    public Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();
    private long startTime = System.currentTimeMillis();
    private long timrout = 2 * 60 * 1000l;

    public DefaultFuture(ClientRequest clientRequest) {
        allFuture.put(clientRequest.getId(), this);
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public long getTimrout() {
        return timrout;
    }

    public void setTimrout(long timrout) {
        this.timrout = timrout;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public Response get(long time) {
        lock.lock();
        try {
            while (!done()) {
                condition.await(time, TimeUnit.SECONDS);
                if (System.currentTimeMillis() - startTime > time) {
                    System.out.println("Time out");
                    break;
                }
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

    static class FutureThread extends Thread {

        @Override
        public void run() {
            Set<Long> ids = allFuture.keySet();
            for (Long id : ids) {
                DefaultFuture df = allFuture.get(id);
                if (df == null) {
                    allFuture.remove(df);
                } else {
                    if (df.getTimrout() < (System.currentTimeMillis() - df.getStartTime())) {
                        Response response = new Response();
                        response.setId(id);
                        response.setCode("33333");
                        response.setMsg("Time out");
                        receive(response);
                    }
                }
            }
        }
    }

    static {
        FutureThread futureThread = new FutureThread();
        futureThread.setDaemon(true);
        futureThread.start();
    }
}
