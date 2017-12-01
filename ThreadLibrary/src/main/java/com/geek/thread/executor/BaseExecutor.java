package com.geek.thread.executor;

import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.geek.thread.ThreadPoolParams;
import com.geek.thread.ThreadPriority;
import com.geek.thread.factory.BaseThreadFactory;
import com.geek.thread.task.GeekPriorityComparable;
import com.geek.thread.task.GeekTask;

import java.lang.reflect.Field;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by aotuman
 */
public abstract class BaseExecutor extends ThreadPoolExecutor {
    private static final String TAG = "BaseExecutor";

    public BaseExecutor(ThreadPoolParams params) {
        super(params.corePoolSize,
                params.maxPoolSize,
                params.keepAliveTimeSec,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>(params.poolQueueSize),
                new BaseThreadFactory(),
                new DiscardOldestPolicy());

        setThreadFactory(getMJThreadFactory());
        setRejectedExecutionHandler(getMJRejectPolicy());
    }

    abstract BaseThreadFactory getMJThreadFactory();

    abstract RejectedExecutionHandler getMJRejectPolicy();

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();

        if (task instanceof GeekTask) {
            execute(task);
            return (Future<?>) task;
        }
        GeekTask<Void> ftask = new GeekTask<Void>(task, null, ThreadPriority.NORMAL);
        execute(ftask);
        return ftask;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (null == r) {
            return;
        }
        ThreadPriority p = null;
        if (r instanceof GeekPriorityComparable) {
            p = ((GeekPriorityComparable) r).getGeekPriority();
        }
        if (null != p) {
            switch (p) {
                case REAL_TIME:
                    Process.setThreadPriority(Process.myTid(), 0);
                    break;
                case HIGH:
                    Process.setThreadPriority(Process.myTid(), 1);
                    break;
                case NORMAL:
                    Process.setThreadPriority(Process.myTid(), 5);
                    break;
                case LOW:
                    Process.setThreadPriority(Process.myTid(), 10);
                    break;
                case BACKGROUND:
                    Process.setThreadPriority(Process.myTid(), 11);
                    break;
                default:
                    Process.setThreadPriority(Process.myTid(), 5);
                    break;
            }
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        try {
            Class looperClazz = Looper.class;
            Field field = looperClazz.getDeclaredField("sThreadLocal");
            if (null != field) {
                field.setAccessible(true);
                Object o = field.get(null);
                if (o != null && o instanceof ThreadLocal) {
                    ThreadLocal l = ((ThreadLocal) o);
                    l.remove();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "afterExecute: ", e);
        }
    }
}
