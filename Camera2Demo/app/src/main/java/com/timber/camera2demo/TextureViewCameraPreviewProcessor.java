package com.timber.camera2demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;


public class TextureViewCameraPreviewProcessor extends CameraPreviewProcessor implements TextureView.SurfaceTextureListener {
    private final static String TAG = "TextureViewCameraPreviewProcessor";

    private TextureView mPreviewView;

    public TextureViewCameraPreviewProcessor(Activity activity) {
        super(activity);

        Log.i(TAG, "timber.textureview preview onCreate begin.");
        mActivity.setContentView(R.layout.camera);
    }

    //可以通过TextureView或者SurfaceView
    public void initView() {
        mPreviewView = (TextureView) mActivity.findViewById(R.id.textureview);
        mPreviewView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        onSurfaceAvailable();
        //openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "timber.textureview preview onSurfaceTextureSizeChanged, width:" + width + " height:" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    // 这个方法要注意一下，因为每有一帧画面，都会回调一次此方法
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    protected void setupPreviewSurface() {
        SurfaceTexture texture = mPreviewView.getSurfaceTexture();

//      这里设置的就是预览大小
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        mPreviewSurface = new Surface(texture);

        mSurfaceReady = true;
    }
}
