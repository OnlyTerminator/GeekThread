package com.geek.thread.factory;

/**
 * Created by di.duan on 2016/4/26.
 */
public class IOThreadFactory extends BaseThreadFactory {
    public IOThreadFactory(){
        mThreadNamePrefix = "IOThread";
    }
}
