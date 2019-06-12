package com.android.camera;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.camera.app.AppController;
import com.android.camera.app.CameraAppUI;
import com.android.camera.app.CameraController;
import com.android.camera.app.CameraProvider;
import com.android.camera.app.CameraServices;
import com.android.camera.app.CameraServicesImpl;
import com.android.camera.app.ModuleManager;
import com.android.camera.app.OrientationManager;
import com.android.camera.debug.Log;
import com.android.camera.device.ActiveCameraDeviceTracker;
import com.android.camera.module.ModuleController;
import com.android.camera.one.OneCameraOpener;
import com.android.camera.one.config.OneCameraFeatureConfig;
import com.android.camera.settings.ResolutionSetting;
import com.android.camera.settings.SettingsManager;
import com.android.camera.stats.UsageStatistics;
import com.android.camera.ui.MainActivityLayout;
import com.android.camera.ui.PreviewStatusListener;
import com.android.camera.util.CameraUtil;
import com.android.camera.util.QuickActivity;
import com.android.camera2.R;
import com.android.ex.camera2.portability.CameraAgent;
import com.android.ex.camera2.portability.CameraAgentFactory;
import com.android.ex.camera2.portability.CameraExceptionHandler;
import com.timber.camera2demo.CameraPreviewProcessor;
import com.timber.camera2demo.SurfaceViewCameraPreviewProcessor;
import com.timber.camera2demo.TextureViewCameraPreviewProcessor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CameraActivity extends QuickActivity
        implements AppController, OrientationManager.OnOrientationChangeListener
{
    private static final Log.Tag TAG = new Log.Tag("CameraActivity");

    private static final int MSG_CLEAR_SCREEN_ON_FLAG = 2;

    private ActiveCameraDeviceTracker mActiveCameraDeviceTracker;

    /**
     * Should be used wherever a context is needed.
     */
    private Context mAppContext;

    private boolean mCameraFatalError = false;

    private HandlerThread mThreadHandler;
    private Handler mMainHandler;
    private CameraPreviewProcessor mCameraPreviewer;

    private FatalErrorHandler mFatalErrorHandler;
    private CameraController mCameraController;
    private boolean mPaused;

    private CameraAppUI mCameraAppUI;

    private boolean mSecureCamera;

    private SettingsManager mSettingsManager;

    public boolean isSecureCamera()
    {
        return mSecureCamera;
    }

    private int mOrientationCompensation = 0;
    public int getOrientationCompensation() {
        return mOrientationCompensation;
    }
    private List<OnOrientationListener> mOrientationListeners =
            new CopyOnWriteArrayList<OnOrientationListener>();


    public interface OnOrientationListener {
        void onOrientationChanged(int orientation);
    }

    @Override
    public void onOrientationChanged(OrientationManager orientationManager, OrientationManager.DeviceOrientation orientation) {
        int orientationCompensation = (orientation.getDegrees() + CameraUtil.getDisplayRotation()) % 360;
        if (mOrientationCompensation != orientationCompensation) {
            Log.d(TAG, "[updateCompensation] mCompensation:"
                    + mOrientationCompensation + ", compensation:"
                    + orientationCompensation);
            mOrientationCompensation = orientationCompensation;
            for (OnOrientationListener listener : mOrientationListeners) {
                if (listener != null) {
                    listener.onOrientationChanged(mOrientationCompensation);
                }
            }
        }
    }

    public boolean addOnOrientationListener(OnOrientationListener l) {
        if (!mOrientationListeners.contains(l)) {
            return mOrientationListeners.add(l);
        }
        return false;
    }

    public boolean removeOnOrientationListener(OnOrientationListener l) {
        return mOrientationListeners.remove(l);
    }

    public boolean addViewManager(ViewManager viewManager) {
        return true;//mCameraAppUI.addViewManager(viewManager);
    }

    public boolean removeViewManager(ViewManager viewManager) {
        return true;//mCameraAppUI.removeViewManager(viewManager);
    }

    @Override
    public void onCreateTasks(Bundle state) {

        mAppContext = getApplicationContext();

        mSettingsManager = getServices().getSettingsManager();

        setContentView(R.layout.activity_main);

        mThreadHandler = new HandlerThread("Camera2-Open");
        mThreadHandler.start();
        mMainHandler = new MainHandler(this, mThreadHandler.getLooper());
        mCameraPreviewer = new TextureViewCameraPreviewProcessor(this, getAndroidContext());
        //mCameraPreviewer = new SurfaceViewCameraPreviewProcessor(this, getAndroidContext());
        mCameraPreviewer.initView();
        mCameraPreviewer.openCamera();

        mCameraAppUI = new CameraAppUI(this,
                (MainActivityLayout) findViewById(R.id.activity_root_view), isCaptureIntent());

        mCameraAppUI.prepareModuleUI();

    }

    /**
     * Note: Make sure this callback is unregistered properly when the activity
     * is destroyed since we're otherwise leaking the Activity reference.
     */
    private final CameraExceptionHandler.CameraExceptionCallback mCameraExceptionCallback = new
            CameraExceptionHandler.CameraExceptionCallback()
            {
                @Override
                public void onCameraError(int errorCode)
                {
                    // Not a fatal error. only do Log.e().
                    Log.e(TAG, "Camera error callback. error=" + errorCode);
                }

                @Override
                public void onCameraException(RuntimeException ex, String commandHistory, int action, int state)
                {
                    Log.e(TAG, "Camera Exception", ex);
                    onFatalError();
                }

                @Override
                public void onDispatchThreadException(RuntimeException ex)
                {
                    Log.e(TAG, "DispatchThread Exception", ex);
                    onFatalError();
                }

                private void onFatalError()
                {
                    if (mCameraFatalError)
                    {
                        return;
                    }
                    mCameraFatalError = true;

                    // If the activity receives exception during onPause, just exit the app.
                    if (mPaused && !isFinishing())
                    {
                        Log.e(TAG, "Fatal error during onPause, call Activity.finish()");
                        finish();
                    } else
                    {
                        mFatalErrorHandler.handleFatalError(FatalErrorHandler.Reason.CANNOT_CONNECT_TO_CAMERA);
                    }
                }
            };

    @Override
    public boolean isCaptureIntent() {
        return MediaStore.ACTION_VIDEO_CAPTURE.equals(getIntent().getAction())
                || MediaStore.ACTION_IMAGE_CAPTURE.equals(getIntent().getAction())
                || MediaStore.ACTION_IMAGE_CAPTURE_SECURE.equals(getIntent().getAction());
    }

    @Override
    public Context getAndroidContext() {
        return mAppContext;
    }

    @Override
    public OneCameraFeatureConfig getCameraFeatureConfig() {
        return null;
    }

    @Override
    public Dialog createDialog() {
        return null;
    }

    @Override
    public String getModuleScope() {
        return null;
    }

    @Override
    public String getCameraScope() {
        return null;
    }

    @Override
    public void launchActivityByIntent(Intent intent) {

    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public ModuleController getCurrentModuleController() {
        return null;
    }

    @Override
    public int getCurrentModuleIndex() {
        return 0;
    }

    @Override
    public int getModuleId(int modeIndex) {
        return 0;
    }

    @Override
    public int getQuickSwitchToModuleId(int currentModuleIndex) {
        return 0;
    }

    @Override
    public int getPreferredChildModeIndex(int modeIndex) {
        return 0;
    }

    @Override
    public void onModeSelected(int moduleIndex) {

    }

    @Override
    public void onSettingsSelected() {

    }

    @Override
    public void freezeScreenUntilPreviewReady() {

    }

    @Override
    public SurfaceTexture getPreviewBuffer() {
        return null;
    }

    @Override
    public void onPreviewReadyToStart() {

    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void addPreviewAreaSizeChangedListener(PreviewStatusListener.PreviewAreaChangedListener listener) {

    }

    @Override
    public void removePreviewAreaSizeChangedListener(PreviewStatusListener.PreviewAreaChangedListener listener) {

    }

    @Override
    public void setupOneShotPreviewListener() {

    }

    @Override
    public void updatePreviewAspectRatio(float aspectRatio) {

    }

    @Override
    public void updatePreviewTransformFullscreen(Matrix matrix, float aspectRatio) {

    }

    @Override
    public RectF getFullscreenRect() {
        return null;
    }

    @Override
    public void updatePreviewTransform(Matrix matrix) {

    }

    @Override
    public void setPreviewStatusListener(PreviewStatusListener previewStatusListener) {

    }

    @Override
    public FrameLayout getModuleLayoutRoot() {
        return null;
    }

    @Override
    public void lockOrientation() {

    }

    @Override
    public void unlockOrientation() {

    }

    @Override
    public void setShutterEventsListener(ShutterEventsListener listener) {

    }

    @Override
    public void setShutterEnabled(boolean enabled) {

    }

    @Override
    public boolean isShutterEnabled() {
        return false;
    }

    @Override
    public void startFlashAnimation(boolean shortFlash) {

    }

    @Override
    public void startPreCaptureAnimation() {

    }

    @Override
    public void cancelPreCaptureAnimation() {

    }

    @Override
    public void startPostCaptureAnimation() {

    }

    @Override
    public void startPostCaptureAnimation(Bitmap thumbnail) {

    }

    @Override
    public void cancelPostCaptureAnimation() {

    }

    @Override
    public void notifyNewMedia(Uri uri) {

    }

    @Override
    public void enableKeepScreenOn(boolean enabled) {

    }

    @Override
    public CameraProvider getCameraProvider() {
        return mCameraController;
    }

    @Override
    public OneCameraOpener getCameraOpener() {
        return null;
    }

    @Override
    public OrientationManager getOrientationManager() {
        return null;
    }

    @Override
    public LocationManager getLocationManager() {
        return null;
    }

    @Override
    public SettingsManager getSettingsManager() {
        return mSettingsManager;
    }

    @Override
    public ResolutionSetting getResolutionSetting() {
        return null;
    }

    @Override
    public CameraServices getServices() {
        return CameraServicesImpl.instance();
    }

    @Override
    public FatalErrorHandler getFatalErrorHandler() {
        return null;
    }

    @Override
    public CameraAppUI getCameraAppUI() {
        return null;
    }

    @Override
    public ModuleManager getModuleManager() {
        return null;
    }

    @Override
    public SoundPlayer getSoundPlayer() {
        return null;
    }

    @Override
    public boolean isAutoRotateScreen() {
        return false;
    }

    @Override
    public void finishActivityWithIntentCompleted(Intent resultIntent) {

    }

    @Override
    public void finishActivityWithIntentCanceled() {

    }

    private static class MainHandler extends Handler
    {
        final WeakReference<CameraActivity> mActivity;

        public MainHandler(CameraActivity activity, Looper looper)
        {
            super(looper);
            mActivity = new WeakReference<CameraActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            CameraActivity activity = mActivity.get();
            if (activity == null)
            {
                return;
            }
            switch (msg.what)
            {

                case MSG_CLEAR_SCREEN_ON_FLAG:
                {
                    if (!activity.mPaused)
                    {
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void doInitMainUI() {
        if (!mCameraPreviewer.isSurfaceReady())
            return;
        if (!mCameraPreviewer.isCameraOpened())
            return;
        mCameraAppUI.initMainUI();
    }

    public void onCameraOpened(CameraDevice camera) {
        doInitMainUI();
    }

    public void onPreviewSurfaceReady() {
        doInitMainUI();
    }
}
