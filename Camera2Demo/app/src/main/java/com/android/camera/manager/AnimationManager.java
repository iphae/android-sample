/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.camera.manager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.android.camera.util.Gusterpolator;

/**
 * Class to handle animations.
 */

public class AnimationManager {

    public static final float FLASH_ALPHA_START = 0.3f;
    public static final float FLASH_ALPHA_END = 0f;
    public static final int FLASH_DURATION = 300;

    public static final int SHRINK_DURATION = 400;
    public static final int HOLD_DURATION = 2500;
    public static final int SLIDE_DURATION = 1100;

    //Begin add by xu.wei.hz for XR 5366306  on 17-10-9
    private static final float SCALE_LOWER=0.1f;
    private static final float SCALE_UPPER=1.0f;
    private static final int ANIMATE_BULB_DURATION=150;
    //End add by xu.wei.hz for XR 5366306 on 17-10-9

    private ObjectAnimator mFlashAnim;
    private AnimatorSet mCaptureAnimator;

    /**
     * Starts capture animation.
     * @param view a thumbnail view that shows a picture captured and gets animated
     */
    public void startCaptureAnimation(final View view) {
        if (mCaptureAnimator != null && mCaptureAnimator.isStarted()) {
            mCaptureAnimator.cancel();
        }
        View parentView = (View) view.getParent();
        float slideDistance = (float) (parentView.getWidth() - view.getLeft());

        float scaleX = ((float) parentView.getWidth()) / ((float) view.getWidth());
        float scaleY = ((float) parentView.getHeight()) / ((float) view.getHeight());
        float scale = scaleX > scaleY ? scaleX : scaleY;

        int centerX = view.getLeft() + view.getWidth() / 2;
        int centerY = view.getTop() + view.getHeight() / 2;

        ObjectAnimator slide = ObjectAnimator.ofFloat(view, "translationX", 0f, slideDistance)
                .setDuration(AnimationManager.SLIDE_DURATION);
        slide.setStartDelay(AnimationManager.SHRINK_DURATION + AnimationManager.HOLD_DURATION);

        ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY",
                parentView.getHeight() / 2 - centerY, 0f)
                .setDuration(AnimationManager.SHRINK_DURATION);
        translateY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Do nothing.
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Do nothing.
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Do nothing.
            }
        });

        mCaptureAnimator = new AnimatorSet();
        mCaptureAnimator.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", scale, 1f)
                        .setDuration(AnimationManager.SHRINK_DURATION),
                ObjectAnimator.ofFloat(view, "scaleY", scale, 1f)
                        .setDuration(AnimationManager.SHRINK_DURATION),
                ObjectAnimator.ofFloat(view, "translationX",
                        parentView.getWidth() / 2 - centerX, 0f)
                        .setDuration(AnimationManager.SHRINK_DURATION),
                translateY,
                slide);
        mCaptureAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setClickable(false);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setScaleX(1f);
                view.setScaleX(1f);
                view.setTranslationX(0f);
                view.setTranslationY(0f);
                view.setVisibility(View.INVISIBLE);
                mCaptureAnimator.removeAllListeners();
                mCaptureAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Do nothing.
            }
        });
        mCaptureAnimator.start();
    }

   /**
    * Starts flash animation.
    * @params flashOverlay the overlay that will animate on alpha to make the flash impression
    */
    public void startFlashAnimation(final View flashOverlay) {
        // End the previous animation if the previous one is still running
        if (mFlashAnim != null && mFlashAnim.isRunning()) {
            mFlashAnim.cancel();
        }
        // Start new flash animation.
        mFlashAnim = ObjectAnimator.ofFloat(flashOverlay, "alpha",
                AnimationManager.FLASH_ALPHA_START, AnimationManager.FLASH_ALPHA_END);
        mFlashAnim.setDuration(AnimationManager.FLASH_DURATION);
        mFlashAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                flashOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                flashOverlay.setAlpha(0f);
                flashOverlay.setVisibility(View.GONE);
                mFlashAnim.removeAllListeners();
                mFlashAnim = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Do nothing.
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Do nothing.
            }
        });
        mFlashAnim.start();
    }

    /**
     * Cancels on-going flash animation and capture animation, if any.
     */
    public void cancelAnimations() {
        // End the previous animation if the previous one is still running
        if (mFlashAnim != null && mFlashAnim.isRunning()) {
            mFlashAnim.cancel();
        }
        if (mCaptureAnimator != null && mCaptureAnimator.isStarted()) {
            mCaptureAnimator.cancel();
        }
    }


    private static final int CIRCLE_ANIM_DURATION_MS = 500;

    public static ValueAnimator buildShowingAnimator(final View viewToShow){
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(CIRCLE_ANIM_DURATION_MS);
        animator.setInterpolator(Gusterpolator.INSTANCE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (viewToShow.getVisibility() == View.INVISIBLE) {
                    viewToShow.setVisibility(View.VISIBLE);
                }
                float alpha = (Float) animation.getAnimatedValue();
                viewToShow.setAlpha(alpha);
            }
        });

        return animator;
    }

    public static ValueAnimator buildHidingAnimator(final View viewToHide) {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setDuration(CIRCLE_ANIM_DURATION_MS);
        animator.setInterpolator(Gusterpolator.INSTANCE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float) animation.getAnimatedValue();
                viewToHide.setAlpha(alpha);
                if (alpha == 0.0f) {
                    viewToHide.setVisibility(View.INVISIBLE);
                }
            }
        });

        return animator;
    }

    //Begin added by shile.wu for task 5091845 on 2017/09/06
    public static ValueAnimator buildShutterAnimator(final View view){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f,0.9f,1f);
            valueAnimator.setTarget(view);
            valueAnimator.setDuration(500);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.setScaleX((Float) animation.getAnimatedValue());
                    view.setScaleY((Float) animation.getAnimatedValue());
                }
            });
        return valueAnimator;
    }
    //End added by shile.wu for task 5091845 on 2017/09/06

    //Begin add by xu.wei.hz for XR 5366306  on 17-10-9
    public static ValueAnimator buildBulbAnimator(final View thumbnailView){ // MODIFIED by xuyang.liu, 2016-10-20,BUG-3178366

        ValueAnimator animator=ValueAnimator.ofFloat(SCALE_LOWER,SCALE_UPPER);
        animator.setDuration(ANIMATE_BULB_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float mAnimatorFraction = (float) valueAnimator.getAnimatedValue();
                thumbnailView.setAlpha(mAnimatorFraction);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                thumbnailView.setAlpha(1f);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                thumbnailView.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return animator;
    }
    //End add by xu.wei.hz for XR 5366306 on 17-10-9

    //Begin add by xu.wei.hz for XR 5762712 on 17-12-13
    public static ValueAnimator buildScalAnimator(final View view,float start, float end){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start,end);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(250);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setScaleX((Float) animation.getAnimatedValue());
                view.setScaleY((Float) animation.getAnimatedValue());
            }
        });
        return valueAnimator;
    }
    //End add by xu.wei.hz for XR 5762712 on 17-12-13
}
