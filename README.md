# GeekThread
Android ThreadPool的封装
## 使用方法
在项目的build.gradle里面加上引用：
```java
compile 'com.geek.thread:thread-pool:1.0.1'
```
## 方法说明
```java
GeekThreadManager.getInstance().execute(new GeekRunnable(ThreadPriority.LOW) {
    @Override
    public void run() {
        // do something
    }
}, ThreadType.NORMAL_THREAD);
```
通过传入Runable的方式来使用多线程，其中ThreadPriority.LOW表示线程级别，ThreadType.NORMAL_THREAD表示线程类型。
```java
GeekThreadManager.getInstance().execute(new GeekThread(ThreadPriority.NORMAL) {
    @Override
    public void run() {
        super.run();
          // do something
    }
},ThreadType.NORMAL_THREAD);
```
这个使用方法与上面的类似，只是传入的Runable换成了Thread
```java
GeekThreadPools.executeWithGeekThreadPool(new Runnable() {
    @Override
    public void run() {
        //do something
    }
});
```
直接扔进去一个普通的Runable，线程级别和类型都为普通的。
## 总结
这个框架主要的作用是内部帮我们实现了线程池，维护了多线程，我们只需要在使用的地方调用就可以了，无需担心因为多出调用多线程而导致的内存消耗等问题，也不需要每次都去实现一个线程池。
