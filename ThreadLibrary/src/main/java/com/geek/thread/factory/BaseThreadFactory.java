package com.geek.thread.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aotuman
 */
public class BaseThreadFactory implements ThreadFactory{
    protected final AtomicInteger mCount = new AtomicInteger(1);
    protected String mThreadNamePrefix = "BaseThread";

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, mThreadNamePrefix + " #" + mCount.getAndIncrement());
    }
}
