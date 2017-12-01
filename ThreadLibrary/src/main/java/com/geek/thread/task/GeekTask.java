package com.geek.thread.task;

import android.util.Log;

import com.geek.thread.GeekThreadManager;
import com.geek.thread.ThreadPriority;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by aotuman on 2017/11/23.
 * 用来执行和存储线程的Task
 */

public class GeekTask<T> extends FutureTask<T> implements GeekPriorityComparable{
    private static final String TAG = "GeekTask";
    private ThreadPriority mPriority;

    private int mKey = -1;

    public GeekTask(Runnable r, T result, ThreadPriority priority) {
        super(r, result);
        this.mPriority = priority;
    }

    public void setKey(int key) {
        if (key >= 0) {
            this.mKey = key;
        }
    }

    @Override
    public void setGeekPriority(ThreadPriority priority) {
        this.mPriority = priority;
    }

    @Override
    public ThreadPriority getGeekPriority() {
        return mPriority;
    }

    @Override
    public int compareTo(GeekPriorityComparable geekPriorityComparable) {
        if (null == geekPriorityComparable) {
            return 1;
        }
        return getGeekPriority().getPriorityValue() - geekPriorityComparable.getGeekPriority().getPriorityValue();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof GeekPriorityComparable && ((GeekPriorityComparable) obj).getGeekPriority() == getGeekPriority() && super.equals(obj);
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | CancellationException e) {
            Log.e(TAG, "done: ", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("An error occurred while executing MJFutureTask",
                    e.getCause());
        } finally {
            GeekThreadManager.getInstance().removeWork(mKey);
        }
    }
}
