package com.timber.camera2demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;

import com.android.camera.CameraActivity;
import com.android.camera2.R;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public abstract class CameraPreviewProcessor {
    private final static String TAG = "CameraPreviewProcessor";
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraDevice mCameraDevice;

    protected boolean mCameraOpened = false;
    protected boolean mSurfaceReady = false;
    protected boolean mPreviewStarted = false;

    private boolean mBFirstFrame = false;

    protected Handler mHandler;
    private HandlerThread mThreadHandler;

    protected Size mPreviewSize;
    protected Surface mPreviewSurface;

    protected Context mContext;
    protected CameraActivity mCameraActivity;

    protected FrameLayout mCurSurfaceViewLayout;
    protected int mLayoutId;

    public CameraPreviewProcessor(CameraActivity activity, Context context) {
        mCameraActivity = activity;
        mContext = context;

        mPreviewSize = new Size(960, 720);
        initLooper();
    }

    public void initView() {
        FrameLayout surfaceViewRoot =
                mCameraActivity.findViewById(R.id.camera_surfaceview_root);

        mCurSurfaceViewLayout =
                (FrameLayout) mCameraActivity.getLayoutInflater().inflate(
                        mLayoutId, null);
        surfaceViewRoot.addView(mCurSurfaceViewLayout);
    }

    protected abstract void setupPreviewSurface();
    //protected abstract Surface getPreviewSurface();

    //很多过程都变成了异步的了，所以这里需要一个子线程的looper
    final public void initLooper() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }

    protected void onSurfaceAvailable() {
        setupPreviewSurface();
        startPreview();
        //openCamera();
    }

    protected void startPreview() {
        if (mPreviewStarted)
            return;
        if (mCameraOpened && mSurfaceReady) {
            try {
                createSession(mCameraDevice);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mPreviewStarted = true;
        }
    }

    protected Size getPreviewSize(int width, int height) {
        try {
            //获得所有摄像头的管理者CameraManager
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            //获得某个摄像头的特征，支持的参数
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics("0");
            //支持的STREAM CONFIGURATION
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            //摄像头支持的预览Size数组
            return Camera2Util.getMinPreSize(map.getOutputSizes(SurfaceTexture.class), width, height, 2000);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return new Size(720, 960);
    }

    final public void openCamera() {
        try {
            //获得所有摄像头的管理者CameraManager
            CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            Log.i(TAG, "timber.openCamera preview openCamera begin.");
            //打开相机
            cameraManager.openCamera("0", mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 开始预览，主要是camera.createCaptureSession这段代码很重要，创建会话
    private void createSession(CameraDevice camera) throws CameraAccessException {
        try {
            // 设置捕获请求为预览，这里还有拍照啊，录像等
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

//      就是在这里，通过这个set(key,value)方法，设置曝光啊，自动聚焦等参数！！ 如下举例：
//      mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);




        // 这里一定分别add两个surface，一个Textureview的，一个ImageReader的，如果没add，会造成没摄像头预览，或者没有ImageReader的那个回调！！
        mPreviewBuilder.addTarget(mPreviewSurface);

        List<Surface> surfaceList = new LinkedList<>();
        surfaceList.add(mPreviewSurface);

        /*
        这里处理额外的surface
         */
//        initImageReader();
//        mPreviewBuilder.addTarget(mImageReader.getSurface());
//        surfaceList.add(mImageReader.getSurface());
        /*
        这里处理额外的surface
         */

        Log.i(TAG, "timber.createSession createCaptureSession begin.");
        mCameraDevice.createCaptureSession(surfaceList,mSessionStateCallback, mHandler);
    }

    private void startPreviewRequest(CameraCaptureSession session) throws CameraAccessException {
        Log.i(TAG, "timber.startPreviewRequest setRepeatingRequest begin.");
        session.setRepeatingRequest(mPreviewBuilder.build(), mPreviewStartedCallback, mHandler);
    }

    public void setCameraOpened(CameraDevice camera) {
        mCameraOpened = true;
        mCameraDevice = camera;
        Log.i(TAG, "timber.setCameraOpened openCamera onOpened.");
        startPreview();
    }

    protected CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            setCameraOpened(camera);
            mCameraActivity.onCameraOpened(camera);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                startPreviewRequest(session);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };

    private CameraCaptureSession.CaptureCallback mPreviewStartedCallback
            = new CameraCaptureSession.CaptureCallback() {
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            // default empty implementation
            if (mBFirstFrame) {
                return;
            }
            Log.i(TAG, "timber.onCaptureCompleted setRepeatingRequest onCaptureCompleted.");
            mBFirstFrame = true;
            getBitmapFromPreviewSurface();
        }
    };

    protected void getBitmapFromPreviewSurface() {
        Log.i(TAG, "timber.cameraAnimation.getBitmap from previewsurface begin.");
        BitmapUtil.getBitmapFromSurfaceView(mPreviewSurface, mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                new BitmapUtil.SurfaceBitmapCallback() {
            @Override
            public void onSuccessCallback(Bitmap bitmap) {
                Log.d(TAG, "timber.cameraAnimation.getBitmap from previewsurface end.");
//                long date = System.currentTimeMillis();
//                String title = CameraUtil.instance().createJpegName(date);
//                getServices().getMediaSaver().addImage(
//                        bitmap, "timber_test_surfaceview_bitmap_" + title, date,
//                        mActivity.getLocationManager().getCurrentLocation(), mPreviewSize.getHeight(), mPreviewSize.getWidth(),
//                        0, null, null);
            }

            @Override
            public void onErrorCallback() {

            }
        },
                new Handler());
    }

    public boolean isSurfaceReady() {
        return mSurfaceReady;
    }

    public boolean isCameraOpened() {
        return mCameraOpened;
    }

    private ImageReader mImageReader;
    private void initImageReader() {
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), /*mSurfaceView.getHeight()*/mPreviewSize.getHeight(), ImageFormat.JPEG/*此处还有很多格式，比如我所用到YUV等*/, 2/*最大的图片数，mImageReader里能获取到图片数，但是实际中是2+1张图片，就是多一张*/);

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
//            Image img = reader.acquireNextImage();

//            Log.i(TAG, "timber.real preview size, width: " + img.getWidth() + ", height: " + img.getHeight());
//            img.close();
//            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
//            byte[] data = new byte[buffer.remaining()];
//            buffer.get(data);
//            img.close();
        }
    };
}
