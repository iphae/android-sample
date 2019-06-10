package com.timber.camera2demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.android.camera.CameraActivity;
import com.android.camera2.R;


public class SurfaceViewCameraPreviewProcessor extends CameraPreviewProcessor implements SurfaceHolder.Callback {
    private final static String TAG = "SurfaceViewCameraPreviewProcessor";
    private SurfaceView mPreviewView;

    public SurfaceViewCameraPreviewProcessor(CameraActivity activity, Context context) {
        super(activity, context);

        mLayoutId = R.layout.camera_preview_layout;
        Log.i(TAG, "timber.surfaceview onCreate begin.");
        //mActivity.setContentView(R.layout.camera_sv);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "timber.surfaceview surfaceCreated.");
        onSurfaceAvailable();
        //openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "timber.surfaceview surfaceChanged, width:" + width + " height:" + height);
        if (width == mPreviewSize.getWidth() && height == mPreviewSize.getHeight()) {
            mSurfaceReady = true;
            startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void initView() {
        super.initView();

        mPreviewView = (SurfaceView) mCurSurfaceViewLayout.findViewById(R.id.camera_preview);
        mPreviewView.getHolder().addCallback(this);
        //mPreviewView.getHolder().setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
    }

    protected void setupPreviewSurface() {
        mPreviewSurface = mPreviewView.getHolder().getSurface();

        mPreviewView.getHolder().setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//        mPreviewView.setVisibility(View.INVISIBLE);
//        mPreviewView.setVisibility(View.VISIBLE);
        Log.i(TAG, "timber.surfaceview initPreviewSurface done.");
    }
}
