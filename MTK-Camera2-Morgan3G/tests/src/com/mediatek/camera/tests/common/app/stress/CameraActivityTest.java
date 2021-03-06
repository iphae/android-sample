/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2016. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */
package com.mediatek.camera.tests.common.app.stress;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import com.mediatek.camera.CameraActivity;
import com.mediatek.camera.tests.CameraInstrumentationTestCaseBase;
import com.mediatek.camera.tests.annotation.StressAnnotation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Stress tests for CameraActivity.
 */
public class CameraActivityTest extends CameraInstrumentationTestCaseBase {
    /**
     * Launch and finish camera activity many times, check these activity are reachable.
     * If application are leaking activity, every reference is reachable.
     *
     * @throws Exception test with exception.
     */
    @StressAnnotation
    public void testActivityLeak() throws Exception {
        checkActivityLeak(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        checkActivityLeak(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
    }

    private void checkActivityLeak(String action) throws Exception {
        int testCount = 10;
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext, CameraActivity.class);
        ArrayList<WeakReference<Activity>> refs =
                new ArrayList<>();
        for (int i = 0; i < testCount; i++) {
            Activity activity = mInstrumentation.startActivitySync(intent);
            refs.add(new WeakReference<>(activity));
            activity.finish();
            mInstrumentation.waitForIdleSync();
            activity = null;
        }
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();
        int refCount = 0;
        for (WeakReference<Activity> c : refs) {
            if (c.get() != null) {
                refCount++;
            }
        }
        // if application are leaking activity, every reference is reachable.
        assertTrue("Activity is leaked!!!!!!", refCount != testCount);
    }
}