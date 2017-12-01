package com.geek.thread.executor;

import com.geek.thread.ThreadPoolParams;
import com.geek.thread.factory.BaseThreadFactory;
import com.geek.thread.factory.IOThreadFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by aotuman on 2017/11/23.
 */

public class IOExecutor extends BaseExecutor {

    protected IOExecutor(ThreadPoolParams params) {
        super(params);
    }

    @Override
    BaseThreadFactory getMJThreadFactory() {
        return new IOThreadFactory();
    }

    @Override
    RejectedExecutionHandler getMJRejectPolicy() {
        return new ThreadPoolExecutor.DiscardOldestPolicy();
    }
}
