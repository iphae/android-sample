package com.android.camera.manager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.camera.ui.AbstractViewManager;
import com.android.camera2.R;

public class ShutterManager extends AbstractViewManager {
    private static final String TAG = "ShutterManager";

    public static final int SHUTTER_TYPE_PHOTO_VIDEO = 0;
    public static final int SHUTTER_TYPE_SINGLE_SHUTTER = 1;
    public static final int SHUTTER_TYPE_VIDEO_CAPTURE = 2;
    public static final int SHUTTER_TYPE_OK_CANCEL = 3;

    private int mShutterType = SHUTTER_TYPE_PHOTO_VIDEO;

    public ShutterManager(Context context) {
        super(context, null);
    }

    @Override
    protected View getView() {
        Log.i(TAG, "timber.getview");
        View view = null;
        int layoutId = R.layout.camera_shutter_photo_video;

        view = inflate(layoutId);
        return view;
    }
}
