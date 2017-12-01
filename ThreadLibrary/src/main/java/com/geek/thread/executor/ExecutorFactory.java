package com.geek.thread.executor;

import android.util.Log;

import com.geek.thread.ThreadPoolConst;
import com.geek.thread.ThreadPoolParams;
import com.geek.thread.ThreadType;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by aotuman on 2017/11/23.
 */

public class ExecutorFactory {
    private static AtomicBoolean mIsRunning = new AtomicBoolean(true);

    public static BaseExecutor getExecutor(ThreadType type) {
        if (!mIsRunning.get()) {
            return null;
        }
        BaseExecutor ret = null;
        switch (type){
            case REAL_TIME_THREAD:
                ret = IOExecutorInstanceHolder.getInstance();
                break;
            case SERIAL_THREAD:
                ret = IOExecutorInstanceHolder.getInstance();
                break;
            default:
                ret = IOExecutorInstanceHolder.getInstance();
                break;
        }

        return ret;
    }

    private static class IOExecutorInstanceHolder {
        private static final IOExecutor mExecutor = new IOExecutor(getIOExecutorParams());

        public static IOExecutor getInstance() {
            return mExecutor;
        }

        private static ThreadPoolParams getIOExecutorParams() {
            ThreadPoolParams params = new ThreadPoolParams();
            params.corePoolSize = ThreadPoolConst.IO_CORE_POOL_SIZE;
            params.keepAliveTimeSec = ThreadPoolConst.IO_KEEP_ALIVE_TIME;
            params.maxPoolSize = ThreadPoolConst.IO_MAXIMUM_POOL_SIZE;
            params.poolQueueSize = ThreadPoolConst.IO_POOL_QUEUE_SIZE;
            return params;
        }
    }

    public static void shutdownAll() {
        try {
            mIsRunning.set(false);
            IOExecutorInstanceHolder.getInstance().shutdown();
        } catch (Throwable e) {
            Log.e("ExecutorFactory", e.getMessage());
        }
    }
}
