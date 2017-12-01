package com.geek.thread;

import com.geek.thread.task.GeekTask;

/**
 * Created by aotuman on 2017/11/23.
 */

public class GeekThreadPools {

    public static int executeWithGeekThreadPool(Runnable runnable){
        return executeWithGeekThreadPool(runnable,ThreadType.NORMAL_THREAD,ThreadPriority.NORMAL);
    }

    /**
     * 使用线程池执行Runnable
     * @param runnable 需要执行的Runnable
     * @param threadType Runnable 的任务类型 任务类型参考 {@link ThreadType}
     * @param threadPriority Runnable 的优先级 优先级参考 {@link ThreadPriority}
     * @return 当前Runnable的key,key可以用来取消当前还未执行的任务
     */
    public static int executeWithGeekThreadPool(final Runnable runnable, ThreadType threadType, ThreadPriority threadPriority){
        if(null == runnable ){
            return -1;
        }
        if(null == threadType){
            threadType = ThreadType.NORMAL_THREAD;
        }
        if(null == threadPriority){
            threadPriority = ThreadPriority.NORMAL;
        }
        GeekTask<Void> t =  new GeekTask<>(runnable, null, threadPriority);

        return GeekThreadManager.getInstance().submitCancelable(t,threadType);
    }

    /**
     * 取消正在队列中排队，还未执行的任务。
     * 如果任务已经开始执行，则此方法不起任何作用
     * 如果任务已经完成或者已经取消，返回ture
     * @param key 需要取消的任务
     * @return 是否取消成功
     */
    public static boolean cancelWork(int key){
        return key >= 0 && GeekThreadManager.getInstance().cancelWork(key);
    }
}
