package com.geek.thread;

/**
 * Created by aotuman on 2017/11/23.
 */

public enum ThreadPriority {
    REAL_TIME(-1),
    /**
     * 高优先级
     */
    HIGH(0),
    /**
     * 普通优先级
     */
    NORMAL(10),
    /**
     * 低优先级
     */
    LOW(20),
    /**
     * 后台操作，不影响交互
     */
    BACKGROUND(30);

    private int mPriority;
    ThreadPriority(int priority){
        mPriority = priority;
    }

    public int getPriorityValue(){
        return mPriority;
    }
}
