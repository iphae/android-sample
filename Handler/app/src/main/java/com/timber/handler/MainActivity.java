package com.timber.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    public TextView mTextView;
    private static final int MSG_ONE = 1;
    private static final int MSG_CAMERA_OPEN_DONE = 16;
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ONE:
                    Log.i("Handler", "mMainHandler handle msg:" + msg.what + " Thread:" + Thread.currentThread().getName());
                    mTextView.setText(msg.getTarget() + " handle msg:" + msg.what);
                    break;

                case MSG_CAMERA_OPEN_DONE:
                    break;
            }
        }
    };

    private static final int MSG_TWO = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TWO:
                    Log.i("Handler", "mHandler handle msg:" + msg.what + " Thread:" + Thread.currentThread().getName());
                    mTextView.setText(msg.getTarget() + " handle msg:" + msg.what);
                    break;
            }
        }
    };

    private static final int MSG_THREE = 3;
    private Handler mThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.show);
        Button buttonOpen = findViewById(R.id.button_open);
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraStartUpThread.openCamera();
            }
        });

        Button buttonWait = findViewById(R.id.button_wait);
        buttonWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitCameraStartUpThread(false);
                mTextView.setText("wait done");
            }
        });


        initThreadWithHandler();


        //测试openCamera
        /*mCameraStartUpThread = new CameraStartUpThread();
        mCameraStartUpThread.start();*/
    }

    public boolean waitDone() {
        final Object waitDoneLock = new Object();
        final Runnable unlockRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (waitDoneLock) {
                    waitDoneLock.notifyAll();
                }
            }
        };

        //object not locked by thread before wait()
        synchronized (waitDoneLock) {
            mThreadHandler.post(unlockRunnable);
            try {
                waitDoneLock.wait();
            } catch (InterruptedException ex) {
                Log.v(TAG, "waitDone interrupted");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void doWorkForTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendToHandler(int what, Object obj, Handler handler, long delay) {
        doWorkForTime(delay);

        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;

        handler.sendMessage(msg);

    }

    private void initThread() {
        //开启新线程
        new Thread() {
            @Override
            public void run() {
                sendToHandler(1, "A", mMainHandler, 3000);
            }

        }.start();

        new Thread() {
            @Override
            public void run() {
                sendToHandler(2, "B", mHandler, 6000);
            }

        }.start();

        new Thread() {
            @Override
            public void run() {
                doWorkForTime(9000);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("执行了mHandler的post Runnable");
                    }
                });
            }

        }.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWorkForTime(12000);

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("执行了mMainHandler的post Runnable");
                    }
                });
            }
        }).start();
    }

    private void initThreadWithHandler() {
        HandlerThread handlerThread = new HandlerThread("Thread for Handler");
        handlerThread.start();

        mThreadHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_THREE:
                        Log.i("lalala", "非主线程执行消息:" + Thread.currentThread().getName());
                        doWorkForTime(10000);
                        break;
                }
            }
        };


        Button buttonWait = findViewById(R.id.button_wait_3);
        buttonWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThreadHandler.sendEmptyMessage(MSG_THREE);
                mTextView.setText("send MSG_THREE");
                waitDone();
                //mTextView.setText("wait done MSG_THREE");
            }
        });
    }

    //------------------------------------------------------------------------
    private void cameraStartUpThreadDone() {
        Log.d(TAG, "[cameraStartUpThreadDone]");
        mIsWaitForStartUpThread = false;
        mWaitCameraStartUpThread.open();
    }

    public void waitCameraStartUpThread(boolean cancel) {
        Log.i(TAG, "waitCameraStartUpThread(" + cancel + ") begin mCameraStartUpThread="
                + mCameraStartUpThread);

        //先把它关闭，防止在我们等待之前就已經open
        //我们等待的是在我们发出等待请求之后的open
        mWaitCameraStartUpThread.close();
        mCameraStartUpThread.resumeThread();

        mIsWaitForStartUpThread = true;
        mWaitCameraStartUpThread.block();

        Log.i(TAG, "waitCameraStartUpThread() end");
    }
    //-------------------------------------------------------------------------

    private final String TAG = "CameraDeviceCtrl";
    private CameraStartUpThread mCameraStartUpThread;
    private boolean mIsWaitForStartUpThread = false;
    private final ConditionVariable mWaitCameraStartUpThread = new ConditionVariable();

    private class CameraStartUpThread extends Thread {
        private volatile boolean mOpenCamera = false;
        private volatile boolean mIsActive = true;

        private ConditionVariable mConditionVariable = new ConditionVariable();
        public void resumeThread() {
            Log.d(TAG, "resume CameraStartUpThread");
            mConditionVariable.open();
        }

        public CameraStartUpThread() {
            super("CameraStartUpThread");
        }

        private void waitWithoutInterrupt(Object object) {
            try {
                object.wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "unexpected interrupt: " + object);
            }
        }

        public synchronized void openCamera() {
            mOpenCamera = true;
            notifyAll();
        }

        @Override
        public void run() {
            while (mIsActive) {
                synchronized (this) {
                    if (!mOpenCamera) {
                        //上次调用已经完成，给等待的线程进行通知，然后继续等待新的指令
                        cameraStartUpThreadDone();
                        //等待指令，下一次的执行周期由这里开始
                        waitWithoutInterrupt(this);
                        continue;
                    }
                }

                //open动作将会在下面执行一次，本周期内将执行完
                //继续等待主线程发打开指令
                mOpenCamera = false;


                //begin 进行打开操作
                //..耗时..
                doWorkForTime(10000);
                //end 进行打开操作
                mMainHandler.sendEmptyMessage(MSG_CAMERA_OPEN_DONE);
            }

        }
    }
}
