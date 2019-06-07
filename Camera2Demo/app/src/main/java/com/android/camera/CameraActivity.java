package com.android.camera;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.android.camera.module.ModuleController;
import com.android.camera.one.OneCameraOpener;
import com.android.camera.one.config.OneCameraFeatureConfig;
import com.android.camera.settings.ResolutionSetting;
import com.android.camera.settings.SettingsManager;
import com.android.camera.ui.MainActivityLayout;
import com.android.camera.ui.PreviewStatusListener;
import com.android.camera.util.QuickActivity;
import com.android.camera2.R;
import com.timber.camera2demo.CameraPreviewProcessor;
import com.timber.camera2demo.TextureViewCameraPreviewProcessor;


public class CameraActivity extends QuickActivity
        implements AppController
{
    private static final Log.Tag TAG = new Log.Tag("CameraActivity");

    /**
     * Should be used wherever a context is needed.
     */
    private Context mAppContext;

    private CameraController mCameraController;

    private CameraAppUI mCameraAppUI;

    private boolean mSecureCamera;

    private SettingsManager mSettingsManager;

    public boolean isSecureCamera()
    {
        return mSecureCamera;
    }

    @Override
    public void onCreateTasks(Bundle state) {

        mAppContext = getApplicationContext();

        mSettingsManager = getServices().getSettingsManager();

        setContentView(R.layout.activity_main);

        mCameraAppUI = new CameraAppUI(this,
                (MainActivityLayout) findViewById(R.id.activity_root_view), isCaptureIntent());
        mCameraAppUI.prepareModuleUI();
    }

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
}
