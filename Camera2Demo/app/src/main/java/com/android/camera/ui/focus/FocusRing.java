/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.camera.ui.focus;

import android.graphics.RectF;

/**
 * Primary interface for interacting with the focus ring UI.
 */
public interface FocusRing {
    /**
     * Check the state of the passive focus ring animation.
     *
     * @return whether the passive focus animation is running.
     */
    boolean isPassiveFocusRunning();
    /**
     * Check the state of the active focus ring animation.
     *
     * @return whether the active focus animation is running.
     */
    boolean isActiveFocusRunning();
    /**
     * Start a passive focus animation.
     */
    void startPassiveFocus();
    /**
     * Start an active focus animation.
     */
    void startActiveFocus();
    /**
     * Stop any currently running focus animations.
     */
    void stopFocusAnimations();
    /**
     * Set the location of the focus ring animation center.
     */
    void setFocusLocation(float viewX, float viewY);

    /**
     * Set the location of the focus ring animation center.
     */
    void centerFocusLocation();

    /**
     * Set the target radius as a ratio of min to max visible radius
     * which will internally convert and clamp the value to the
     * correct pixel radius.
     */
    void setRadiusRatio(float ratio);

    /**
     * The physical size of preview can vary and does not map directly
     * to the size of the view. This allows for conversions between view
     * and preview space for values that are provided in preview space.
     */
    void configurePreviewDimensions(RectF previewArea);

    //Begin add by xu.wei.hz for focus&AE/AF XR *6709714  on 18-8-14
    void showNormal(boolean isActive);
    void showSuccess(boolean isActive);
    void showFail(boolean isActive);
    void onFocusAeAfLocked();
    void setValueBound(int min, int max);
    void resetAE();
    void clearFocus();
    boolean isShow();
    boolean onScroll(float x, float y);
    interface EVOChangedListener{
        void onEVOChanged(int value);
    }
    void setEvoListener(EVOChangedListener listener);
    void setNeedShowEVO(boolean state);
    //End add by xu.wei.hz for focus&AE/AF XR *6709714 on 18-8-14
    //Begin add by xu.wei.hz for orientation XR *6709714  on 18-8-28
    void setOrientation(int orientation);
    //End add by xu.wei.hz for orientation XR *6709714 on 18-8-28

    /*Begin add by xiongbo.huang for XR 7135157 on 2018/11/01*/
    boolean isEVOSliderShow();
    /*End add by xiongbo.huang for XR 7135157 on 2018/11/01*/
    //Begin Modified by fumeng.gao on 19-05-08 for touch_to_capture
    void showCapture();
    //End Modified by fumeng.gao on 19-05-08 for touch_to_capture
}