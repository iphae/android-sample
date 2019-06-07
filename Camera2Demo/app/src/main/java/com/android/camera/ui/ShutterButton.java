/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.android.camera.manager.AnimationManager;


//Begin added by shile.wu for task 5091845 on 2017/09/06
//End added by shile.wu for task 5091845 on 2017/09/06

/**
 * A button designed to be used for the on-screen shutter button. It's currently
 * an {@code ImageView} that can call a delegate when the pressed state changes.
 */
public class ShutterButton extends RotateImageView implements View.OnLongClickListener {
    private static final String TAG = "ShutterButton";

    private OnShutterButtonListener mListener;
    private boolean mOldPressed;

    // M: this variable to avoid needless onClick after onLongPressed;
    private boolean mLongPressed;
    //Begin add by xu.wei.hz for XR 5104589  on 17-8-2
    private boolean mTouchEnabled = true;
    //End add by xu.wei.hz for XR 5104589 on 17-8-2
    //Begin add by xu.wei.hz for XR 5762712  on 17-12-15
    private boolean isAnimationEnable = true;
    //End add by xu.wei.hz for XR 5762712 on 17-12-13
    private TouchCoordinate mTouchCoordinate;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown() keyCode = " + keyCode+",event:"+event);
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyUp() keyCode = " + keyCode+",event:"+event);
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * A callback to be invoked when a ShutterButton's pressed state changes.
     */
    public interface OnShutterButtonListener {
        /**
         * Called when a ShutterButton has been pressed.
         *
         * @param pressed
         *            The ShutterButton that was pressed.
         */
        void onShutterButtonFocus(boolean pressed);

        void onShutterButtonClick();

        void onShutterButtonLongPressed();
        void onShutterCoordinate(TouchCoordinate coord);
    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(this);
    }

    public void setOnShutterButtonListener(OnShutterButtonListener listener) {
        mListener = listener;
    }

    /**
     * Hook into the drawable state changing to get changes to isPressed -- the
     * onPressed listener doesn't always get called when the pressed state
     * changes.
     */
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final boolean pressed = isPressed();
        //Begin added by shile.wu for task 5091845 on 2017/09/06
        boolean enable = isEnabled();
        Log.i(TAG, "drawableStateChanged() pressed = " + pressed+",enable:"+enable);

        //Begin add by xu.wei.hz for XR 5762712  on 17-12-15
        if(pressed && enable && isAnimationEnable){
        //End add by xu.wei.hz for XR 5762712 on 17-12-15
            //Begin add by xu.wei.hz for XR 5230805  on 17-9-6
            startShutterAnimator();
            //End add by xu.wei.hz for XR 5230805 on 17-9-6
        }
        //End added by shile.wu for task 5091845 on 2017/09/06
        if (pressed != mOldPressed) {
            if (!pressed) {
                // When pressing the physical camera button the sequence of
                // events is:
                // focus pressed, optional camera pressed, focus released.
                // We want to emulate this sequence of events with the shutter
                // button. When clicking using a trackball button, the view
                // system changes the drawable state before posting click
                // notification, so the sequence of events is:
                // pressed(true), optional click, pressed(false)
                // When clicking using touch events, the view system changes the
                // drawable state after posting click notification, so the
                // sequence of events is:
                // pressed(true), pressed(false), optional click
                // Since we're emulating the physical camera button, we want to
                // have the same order of events. So we want the optional click
                // callback to be delivered before the pressed(false) callback.
                //
                // To do this, we delay the posting of the pressed(false) event
                // slightly by pushing it on the event queue. This moves it
                // after the optional click notification, so our client always
                // sees events in this sequence:
                // pressed(true), optional click, pressed(false)
                post(new Runnable() {
                    @Override
                    public void run() {
                        callShutterButtonFocus(pressed);
                    }
                });
            } else {
                callShutterButtonFocus(pressed);
            }
            mOldPressed = pressed;
        }
    }
    private ValueAnimator animator;//Added by shile.wu for task 5091845 on 2017/09/06

    //Begin add by xu.wei.hz for XR 5230805  on 17-9-6
    public void startShutterAnimator() {
        if(animator == null){
            animator = AnimationManager.buildShutterAnimator(this);
        }
        if(animator.isRunning()){
            animator.cancel();
            animator.start();
        }else {
            animator.start();
        }
    }
    //End add by xu.wei.hz for XR 5230805 on 17-9-6

    private void callShutterButtonFocus(boolean pressed) {
        if (mListener != null) {
            mListener.onShutterButtonFocus(pressed);
        }
        mLongPressed = false;
    }


    @Override
    public boolean performClick() {
        boolean result = super.performClick();
        if (mListener != null && isEnabled() && isClickable() && !mLongPressed) {
            mListener.onShutterCoordinate(mTouchCoordinate);
            mTouchCoordinate = null;
            mListener.onShutterButtonClick();
        }
        return result;
    }

    public boolean onLongClick(View v) {
        if (mListener != null && isEnabled() && isClickable()) {
            mListener.onShutterButtonLongPressed();
            mLongPressed = true;
        }
        return false;
    }
    //Begin add by xu.wei.hz for XR 5104589  on 17-8-2
    @Override
    public boolean dispatchTouchEvent(MotionEvent m) {
        if (mTouchEnabled) {
            if (m.getActionMasked() == MotionEvent.ACTION_UP) {
                mTouchCoordinate = new TouchCoordinate(m.getX(), m.getY(), this.getMeasuredWidth(),
                        this.getMeasuredHeight());
            }
            return super.dispatchTouchEvent(m);
        } else {
            return false;
        }
    }
    public void enableTouch(boolean enable) {
        mTouchEnabled = enable;
        setLongClickable(enable);
    }
    //End add by xu.wei.hz for XR 5104589 on 17-8-2
    //Begin add by xu.wei.hz for XR 5762712  on 17-12-15
    public void setAnimationEnable(boolean state) {
        isAnimationEnable = state;
    }
    public void startScalAnimator(float start, float end) {
        if(animator == null){
            animator = AnimationManager.buildScalAnimator(this, start, end);
        }
        if(animator.isRunning()){
            animator.cancel();
            animator.start();
        }else {
            animator.start();
        }
    }
    //End add by xu.wei.hz for XR 5762712 on 17-12-15
}
