package com.timber.camera2demo;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;

import com.android.camera.CameraActivity;

import java.nio.ByteBuffer;

public class Camera2Demo extends CameraActivity {


    private ImageReader mImageReader;

    CameraPreviewProcessor mCameraPrevier;

    @Override
    public void onCreateTasks(Bundle savedInstanceState) {
        //mCameraPrevier = new TextureViewCameraPreviewProcessor(this);
        //mCameraPrevier = new SurfaceViewCameraPreviewProcessor(this);

        mCameraPrevier.initView();
        mCameraPrevier.openCamera();
    }

    private void initImageReader() {
        mImageReader = ImageReader.newInstance(/*mSurfaceView.getWidth()*/720, /*mSurfaceView.getHeight()*/960, ImageFormat.JPEG/*此处还有很多格式，比如我所用到YUV等*/, 2/*最大的图片数，mImageReader里能获取到图片数，但是实际中是2+1张图片，就是多一张*/);

        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        /**
         *  当有一张图片可用时会回调此方法，但有一点一定要注意：
         *  一定要调用 reader.acquireNextImage()和close()方法，否则画面就会卡住！！！！！我被这个坑坑了好久！！！
         *    很多人可能写Demo就在这里打一个Log，结果卡住了，或者方法不能一直被回调。
         **/
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireNextImage();

            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            img.close();
        }
    };
}
