package com.geek.thread;

import com.geek.thread.executor.ExecutorFactory;
import com.geek.thread.executor.SerialExecutor;
import com.geek.thread.task.GeekRunnable;
import com.geek.thread.task.GeekTask;
import com.geek.thread.task.GeekThread;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aotuman on 2017/11/23.
 */

public class GeekThreadManager {

    private static final Object mCacheLock = new Object();
    //用来保证多进线程之间的唯一性
    private static AtomicBoolean mInitialized = new AtomicBoolean(false);
    private SerialExecutor mSerialExecutor;
    private AtomicInteger mKeyIndex;
    private HashMap<Integer, Future> mFutureCache;

    private GeekThreadManager(){
        mInitialized.set(false);
        init();
    }

    public static GeekThreadManager getInstance(){
        return InstanceHolder.mInstance;
    }

    private static class InstanceHolder{
        private static final GeekThreadManager mInstance = new GeekThreadManager();
    }

    /**
     * 初始化信息
     */
    public void init(){
        mSerialExecutor = new SerialExecutor();
        mKeyIndex = new AtomicInteger(0);
        mFutureCache = new HashMap<>();
        mInitialized.set(true);
    }

    /**
     * 用来停止线程池
     */
    public void shutdown(){
        synchronized (mCacheLock) {
            mInitialized.set(false);
            if(null != mFutureCache){
                mFutureCache.clear();
                mFutureCache = null;
            }
            if (null != mSerialExecutor) {
                mSerialExecutor.shutdown();
            }
            ExecutorFactory.shutdownAll();
        }
    }

    public void execute(GeekThread thread, ThreadType threadType){
        if(!mInitialized.get()){
            throw new RuntimeException("Thread pool has been shutdown");
        }
        if(null == thread){
            throw new NullPointerException("GeekThread should not be null");
        }
        if(null == threadType){
            threadType = ThreadType.NORMAL_THREAD;
        }

        if(threadType == ThreadType.REAL_TIME_THREAD){
            thread.setGeekPriority(ThreadPriority.REAL_TIME);
        }

        if(threadType != ThreadType.SERIAL_THREAD) {
            executeThread(thread, threadType);
        }else {
            executeSerialThread(thread);
        }
    }

    public void execute(GeekRunnable runnable, ThreadType threadType){
        if(!mInitialized.get()){
            throw new RuntimeException("Thread pool has been shutdown");
        }
        if(null == runnable){
            throw new NullPointerException("GeekRunnable should not be null");
        }
        if(null == threadType){
            threadType = ThreadType.NORMAL_THREAD;
        }

        if(threadType == ThreadType.REAL_TIME_THREAD){
            runnable.setGeekPriority(ThreadPriority.REAL_TIME);
        }

        if(threadType != ThreadType.SERIAL_THREAD) {
            executeRunnable(runnable, threadType);
        }else {
            executeSerialRunnable(runnable);
        }
    }


    public void removeWork(int key){
        if(!mInitialized.get() || key < 0){
            return ;
        }
        synchronized (mCacheLock) {
            mFutureCache.remove(key);
        }
    }

    /**
     * 取消正在队列中排队，还未执行的任务。
     * 如果任务已经开始执行，则此方法不起任何作用
     * 如果任务已经完成或者已经取消，返回ture
     * @param key 需要取消的任务
     * @return 是否取消成功
     */
    public boolean cancelWork(int key){
        if(!mInitialized.get() || key < 0){
            return false;
        }
        boolean ret = false;
        synchronized (mCacheLock) {
            Future f = mFutureCache.get(key);
            if(null != f) {
                ret = f.isCancelled() || f.isDone()|| f.cancel(false);
            }
        }
        if(ret){
            removeWork(key);
        }

        return ret;
    }

    public int submitCancelable(GeekTask<?> task, ThreadType threadType){
        if(!mInitialized.get()){
            throw new RuntimeException("Thread pool has been shutdown");
        }
        if(null == task){
            throw new NullPointerException("MJFutureTask should not be null");
        }
        if(null == threadType){
            threadType = ThreadType.NORMAL_THREAD;
        }

        if(threadType == ThreadType.REAL_TIME_THREAD){
            task.setGeekPriority(ThreadPriority.REAL_TIME);
        }

        Future f = submit(task,threadType);
        int key = -1;
        if(null != f) {
            synchronized (mCacheLock) {
                if(mKeyIndex.get() < Integer.MAX_VALUE - 10) {
                    key = mKeyIndex.getAndIncrement();
                }else{
                    mKeyIndex.set(0);
                    key = 0;
                }
                mFutureCache.put(key, f);
                task.setKey(key);
            }
        }
        return key;
    }

    private Future<?> submit(GeekTask<?> task, ThreadType threadType){
        if(!mInitialized.get()){
            throw new RuntimeException("Thread pool has been shutdown");
        }
        if(null == task){
            throw new NullPointerException("FutureTask should not be null");
        }
        if(null == threadType){
            threadType = ThreadType.NORMAL_THREAD;
        }

        if(threadType == ThreadType.REAL_TIME_THREAD){
            task.setGeekPriority(ThreadPriority.REAL_TIME);
        }

        if(threadType == ThreadType.SERIAL_THREAD) {
            submitSerial(task);
            return null;
        }else {
            return submitThread(task, threadType);
        }
    }

    private Future<?> submitThread(FutureTask<?> task, ThreadType type){
        ExecutorService executor = ExecutorFactory.getExecutor(type);
        if(null == executor){
            return null;
        }
        return executor.submit(task);
    }

    private void submitSerial (GeekTask<?> task){
        mSerialExecutor.execute(task,task.getGeekPriority());
    }

    private void executeThread(GeekThread thread, ThreadType type){
        ExecutorService executor = ExecutorFactory.getExecutor(type);
        if(null == executor){
            return ;
        }
        executor.execute(thread);
    }

    private void executeRunnable(GeekRunnable runnable, ThreadType type){
        ExecutorService executor = ExecutorFactory.getExecutor(type);
        if(null == executor){
            return ;
        }
        executor.execute(runnable);
    }

    private void executeSerialThread(GeekThread thread){
        mSerialExecutor.execute(thread,thread.getGeekPriority());
    }

    private void executeSerialRunnable(GeekRunnable runnable){
        mSerialExecutor.execute(runnable,runnable.getGeekPriority());
    }
}
