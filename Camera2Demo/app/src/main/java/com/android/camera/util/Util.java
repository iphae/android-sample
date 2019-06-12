/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.camera.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.android.camera.ui.Rotatable;

/**
 * Collection of utility functions used in this package.
 */
public class Util {
    private static final String TAG = "Util";
    private static final float ALPHA_ENABLE = 1.0F;
    private static final float ALPHA_DISABLE = 0.3F;
    //Begin add by jinhui.chen for Task 7218482  on 2018-12-11
    private static int[] mScreenSize = new int[2];
    //End add by jinhui.chen for Task 7218482  on 2018-12-11
    // / @}
    private Util() {
    }

//    public static void initialize(Context context) {
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        wm.getDefaultDisplay().getMetrics(metrics);
//        sPixelDensity = metrics.density;
//        sImageFileNamer = new ImageFileNamer(context.getString(R.string.image_file_name_format));
//    }
//






    public static void fadeIn(View view, float startAlpha, float endAlpha, long duration) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(startAlpha, endAlpha);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public static void fadeIn(View view) {
        fadeIn(view, 0F, 1F, 400);

        // We disabled the button in fadeOut(), so enable it here.
        view.setEnabled(true);
    }

    public static void fadeOut(View view) {
        //Begin add by xu.wei.hz for monkey XR 5393468  on 17-10-10
        if (view == null || view.getVisibility() != View.VISIBLE) {
            return;
        }
        //End add by xu.wei.hz for monkey XR 5393468 on 17-10-10

        // Since the button is still clickable before fade-out animation
        // ends, we disable the button first to block click.
        view.setEnabled(false);
        Animation animation = new AlphaAnimation(1F, 0F);
        animation.setDuration(200);//Modified by shile.wu for ergo task5091845 on 2017/07/24
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
    }



    // / M: set view orientation @{
    public static void setOrientation(View view, int orientation, boolean animation) {
        if (view == null) {
            return;
        }
        if (view instanceof Rotatable) {
            ((Rotatable) view).setOrientation(orientation, animation);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                setOrientation(group.getChildAt(i), orientation, animation);
            }
        }
    }

    public static void setEnabledState(View view, boolean enabled) {
        if (view != null) {
            float alpha = enabled ? ALPHA_ENABLE : ALPHA_DISABLE;
            view.setAlpha(alpha);
        }
    }

    //Begin add by jinhui.chen for Task 7218482  on 2018-12-11
    public static int[] getScreenSize(Context context) {
        if(mScreenSize == null || mScreenSize[0] == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            int[] size = new int[2];
            mScreenSize[0] = Math.min(point.x, point.y);
            mScreenSize[1] = Math.max(point.x, point.y);
        }
        return mScreenSize;
    }

    public static boolean isOnUpperHalfScreen(Context context, MotionEvent ev) {
        int[] screenSize = Util.getScreenSize(context);
        if (ev.getRawY() <= screenSize[1] / 2.0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOnPaddingScreen(float padding, MotionEvent ev) {
        if (ev.getRawX() <= padding || ev.getRawX() >= mScreenSize[0] - padding) {
            return true;
        } else {
            return false;
        }
    }
    //End add by jinhui.chen for Task 7218482  on 2018-12-11
}


