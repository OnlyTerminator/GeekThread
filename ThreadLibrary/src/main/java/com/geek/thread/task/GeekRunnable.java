package com.geek.thread.task;

import com.geek.thread.ThreadPriority;

/**
 * Created by aotuman
 */
public abstract class GeekRunnable implements Runnable, GeekPriorityComparable{
    private ThreadPriority mPriority = ThreadPriority.LOW;

    public GeekRunnable(ThreadPriority priority) {
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
