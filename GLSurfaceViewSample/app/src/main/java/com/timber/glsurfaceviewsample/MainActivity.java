package com.timber.glsurfaceviewsample;

import android.app.Activity;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import javax.microedition.khronos.opengles.GL10;

import glsv.ColorCubeRenderer;
import glsv.DemoRenderer;
import glsv.MyGLSurfaceView;
import sv.MySurfaceView;
import util.AppCore;

public class MainActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(new MySurfaceView(this));
//    }

    private GLSurfaceView mGLView;
    private SurfaceHolder mHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCore.getInstance().init(getApplication());
        mGLView = new MyGLSurfaceView(this);
//        ViewGroup.LayoutParams layoutParams=mGLView.getLayoutParams();
//        layoutParams.width=480;
//        layoutParams.height=480;
//        mGLView.setLayoutParams(layoutParams);

        mHolder = mGLView.getHolder();

//        ViewGroup v=new ViewGroup(this) {
//            @Override
//            protected void onLayout(boolean changed, int l, int t, int r, int b) {
//
//            }
//        };
//        v.addView(mGLView);
//        v.setVisibility(View.GONE);
        setContentView(mGLView);
/*        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final Handler handler = new Handler(Looper.myLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mGLView.requestRender();
                        handler.postDelayed(this, 100);
                    }
                });
                Looper.loop();
            }
        });
        thread.start();*/
    }


    public void onPause(){
        super.onPause();
        mGLView.onPause(); //当Activity暂停时，告诉GLSurfaceView也停止渲染，并释放资源。
    }

    public void onResume(){
        super.onResume();
        mGLView.onResume(); //当Activity恢复时，告诉GLSurfaceView加载资源，继续渲染。
    }
}
