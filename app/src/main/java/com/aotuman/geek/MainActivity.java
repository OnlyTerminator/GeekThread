package com.aotuman.geek;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

import com.geek.thread.GeekThreadManager;
import com.geek.thread.GeekThreadPools;
import com.geek.thread.ThreadPriority;
import com.geek.thread.ThreadType;
import com.geek.thread.task.GeekRunnable;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {
    private TextView tv1, tv2, tv3, tv4,tv5;
    private MyHandler myHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.text_1);
        tv2 = findViewById(R.id.text_2);
        tv3 = findViewById(R.id.text_3);
        tv4 = findViewById(R.id.text_4);
        tv5 = findViewById(R.id.text_5);
        initData();
    }
    private int id;
    private void initData() {
        myHandler = new MyHandler(new WeakReference<>(this));

        id = GeekThreadPools.executeWithGeekThreadPool(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Message message = myHandler.obtainMessage();
                    message.arg1 = i;
                    message.what = 4;
                    myHandler.sendMessage(message);
                    SystemClock.sleep(500);
                    if(i > 20){
                        GeekThreadManager.getInstance().cancelWork(id);
                    }
                }
            }
        });

        GeekThreadManager.getInstance().execute(new GeekRunnable(ThreadPriority.LOW) {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Message message = myHandler.obtainMessage();
                    message.arg1 = i;
                    message.what = 0;
                    myHandler.sendMessage(message);
                    SystemClock.sleep(500);
                }
            }
        }, ThreadType.NORMAL_THREAD);

        GeekThreadManager.getInstance().execute(new GeekRunnable(ThreadPriority.HIGH) {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Message message = myHandler.obtainMessage();
                    message.arg1 = i;
                    message.what = 1;
                    myHandler.sendMessage(message);
                    SystemClock.sleep(500);
                }
            }
        }, ThreadType.NORMAL_THREAD);

        GeekThreadManager.getInstance().execute(new GeekRunnable(ThreadPriority.HIGH) {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    Message message = myHandler.obtainMessage();
                    message.arg1 = i;
                    message.what = 2;
                    myHandler.sendMessage(message);
                    SystemClock.sleep(500);
                }
            }
        }, ThreadType.NORMAL_THREAD);

        GeekThreadManager.getInstance().execute(new GeekRunnable(ThreadPriority.HIGH) {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    Message message = myHandler.obtainMessage();
                    message.arg1 = i;
                    message.what = 3;
                    myHandler.sendMessage(message);
                    SystemClock.sleep(500);
                }
            }
        }, ThreadType.NORMAL_THREAD);
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public MyHandler(WeakReference<MainActivity> weakReference) {
            mActivity = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != mActivity.get()) {
                MainActivity activity = mActivity.get();
                String str = String.valueOf(msg.arg1);
                switch (msg.what) {
                    case 0:
                        activity.tv1.setText(str);
                        break;
                    case 1:
                        activity.tv2.setText(str);
                        break;
                    case 2:
                        activity.tv3.setText(str);
                        break;
                    case 3:
                        activity.tv4.setText(str);
                        break;
                    case 4:
                        activity.tv5.setText(str);
                        break;

                }
            }
        }
    }
}
