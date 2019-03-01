package com.timber.threadinterrupt;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start();
    }

    public void start() {
        MyThread myThread = new MyThread();
        myThread.start();

        try {
            Thread.sleep(3000);
            myThread.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    System.out.println("timber:test in MyThread.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("timber:interrupt");
                    Thread.currentThread().interrupt();//继续中断,恢复中断标记，while循环才能退出
                }
            }

            System.out.println("timber:stop");
        }

        public void cancel() {
            interrupt();
        }
    }
}
