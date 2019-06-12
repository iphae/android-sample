package com.android.camera.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.camera.CameraActivity;
import com.android.camera.util.Util;
import com.android.camera2.R;

public abstract class AbstractViewManager implements IViewManager {
    private Context mContext;
    protected final ViewGroup mParentView;
    private View mView;
    private int mOrientation;

    protected abstract View getView();


    public AbstractViewManager(Context context, ViewGroup layer) {
        mContext = context;
        mOrientation = getContext().getOrientationCompensation();
        if(layer == null) {
            mParentView = getContext().findViewById(R.id.camera_ui_layout);
        } else {
            mParentView = layer;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            show();
        } else if (visibility == View.INVISIBLE) {
            hide(View.INVISIBLE);
        } else if (visibility == View.GONE) {
            hide(View.GONE);
        }
    }

    @Override
    public int getVisibility() {
        if (mView != null) {
            return mView.getVisibility();
        }
        return -1;
    }

    private void show() {
        if (mView == null) {
            mView = getView();

            if (mView != null) {
                addView(mView, mParentView);
                Util.setOrientation(mView, mOrientation, false);
            }
        }
        if (mView != null) {
            mView.setVisibility(View.VISIBLE);
            mView.setClickable(true);
        }
    }

    private void hide(int visibility) {
        if (mView == null) {
            mView = getView();
        }
        if (mView != null) {
            mView.setVisibility(visibility);
        }
    }

    public final CameraActivity getContext() {
        return (CameraActivity)mContext;
    }

    public final View inflate(int layoutId) {
        return getContext().getLayoutInflater().inflate(layoutId, mParentView,false);
    }

    protected void addView(View view, ViewGroup layer) {
        layer.addView(view);
    }

    protected void removeView(View view, ViewGroup layer){
        layer.removeView(view);
    }
}
