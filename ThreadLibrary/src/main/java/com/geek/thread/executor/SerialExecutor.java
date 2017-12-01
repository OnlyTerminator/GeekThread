package com.geek.thread.executor;

import android.util.Log;

import com.geek.thread.ThreadPoolConst;
import com.geek.thread.ThreadPriority;
import com.geek.thread.ThreadType;
import com.geek.thread.task.GeekTask;

import java.util.ArrayDeque;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by aotuman on 2017/11/28.
 */

public class SerialExecutor {
    private final ArrayDeque<GeekTask<Void>> mTasks;
    private GeekTask<Void> mActive;
    private Future<?> mCurrentFuture;
    private final Executor mWaitingExecutor;
    private WaitRunnable mWaiting;
    private volatile boolean isRunning = true;

    public SerialExecutor() {
        mTasks = new ArrayDeque<>();
        mWaitingExecutor = ExecutorFactory.getExecutor(ThreadType.NORMAL_THREAD);
        mWaiting = new WaitRunnable();
    }

    public synchronized void execute(final Runnable r, ThreadPriority priority) {
        mTasks.offer(new GeekTask<Void>(r, null, priority));
        if (mActive == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if (isRunning && (mActive = mTasks.poll()) != null) {
            BaseExecutor executor = ExecutorFactory.getExecutor(ThreadType.NORMAL_THREAD);
            if (null == executor) {
                shutdown();
                return;
            }
            mCurrentFuture = executor.submit(mActive);
            mWaitingExecutor.execute(mWaiting);
        }
    }

    public synchronized void shutdown() {
        try {
            isRunning = false;
            mTasks.clear();
            if (null != mActive) {
                mActive.cancel(true);
            }
        } catch (Throwable e) {
            Log.e("SerialExecutor", "",e);
        }
    }

    private class WaitRunnable implements Runnable {
        @Override
        public void run() {
            try {
                if (null != mCurrentFuture) {
                    mCurrentFuture.get(ThreadPoolConst.SERIAL_EXECUTOR_TIMEOUT, ThreadPoolConst.SERIAL_EXECUTOR_TIMEOUT_UNIT);
                }
            } catch (CancellationException | InterruptedException  e) {
                Log.e("SerialExecutor", e.getMessage());
            } catch (ExecutionException e) {
                throw new RuntimeException("An error occurred while executing SerialExecutor",
                        e.getCause());
            } catch (TimeoutException e){
                Log.e("SerialExecutor", e.getMessage());
                Log.w("SerialExecutor", "task timeout force stop and scheduleNext");
            }finally {
                try {
                    if (null != mCurrentFuture) {
                        mCurrentFuture.cancel(true);
                    }
                } catch (Exception e1) {
                    e1.getMessage();
                }
                scheduleNext();
            }
        }
    }
}
