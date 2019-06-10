package com.android.camera.ui;

import android.view.View;

public abstract class AbstractViewManager implements IViewManager {
    private View mView;

    protected abstract View getView();

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
}
