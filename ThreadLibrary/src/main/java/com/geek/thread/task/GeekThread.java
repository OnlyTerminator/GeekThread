package com.geek.thread.task;

import com.geek.thread.ThreadPriority;

/**
 * Created by aotuman
 * the thread that can set priority
 */
public abstract class GeekThread extends Thread implements GeekPriorityComparable{

    private ThreadPriority mPriority = ThreadPriority.LOW;

    public GeekThread(ThreadPriority priority) {
        this.mPriority = priority;
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
    public int compareTo(GeekPriorityComparable another) {
        if (null == another) {
            return 1;
        }
        return getGeekPriority().getPriorityValue() - another.getGeekPriority().getPriorityValue();
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof GeekPriorityComparable && ((GeekPriorityComparable) obj).getGeekPriority() == getGeekPriority() && super.equals(obj);
    }
}
