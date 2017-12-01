package com.geek.thread;

/**
 * Created by aotuman on 2017/11/23.
 * 线程池类型，用于防止各种类型的线程跑到一个池里面，相互影响
 */

public enum ThreadType {
    /**
     * 普通类型
     */
    NORMAL_THREAD,
    /**
     * 序列执行（上一个执行完才会执行下一个）
     */
    SERIAL_THREAD,
    /**
     * 实时响应类型
     */
    REAL_TIME_THREAD
}
