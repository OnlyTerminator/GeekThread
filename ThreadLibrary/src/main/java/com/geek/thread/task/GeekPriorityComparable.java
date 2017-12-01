package com.geek.thread.task;

import com.geek.thread.ThreadPriority;

/**
 * Created by Anthony on 2016/9/8.
 */
public interface GeekPriorityComparable extends Comparable<GeekPriorityComparable>{
    void setGeekPriority(ThreadPriority priority);
    ThreadPriority getGeekPriority();
}
