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

package com.android.camera.util;

import android.content.ContentResolver;

import com.android.ex.camera2.portability.CameraAgentFactory;

public class GservicesHelper {

    public static String getBlacklistedResolutionsBack(ContentResolver contentResolver) {
        return "";
    }

    public static String getBlacklistedResolutionsFront(ContentResolver contentResolver) {
        return "";
    }

    public static boolean isCaptureModuleDisabled(ContentResolver contentResolver) {
        return false;
    }

    public static boolean isJankStatisticsEnabled(ContentResolver contentResolver) {
        return false;
    }

    public static int getCaptureSupportLevelOverrideBack(ContentResolver contentResolver) {
        return -1;
    }

    public static int getCaptureSupportLevelOverrideFront(ContentResolver contentResolver) {
        return -1;
    }

    public static int getMaxAllowedNativeMemoryMb(ContentResolver contentResolver) {
        return -1;
    }

    public static int getMaxAllowedImageReaderCount(ContentResolver contentResolver) {
        return 15;
    }

    public static boolean useCamera2ApiThroughPortabilityLayer(ContentResolver contentResolver) {
        // Use the camera2 API by default. This only affects PhotoModule on L.
        return true;
    }

    public static boolean isGcamEnabled(ContentResolver contentResolver) {
        return false;
    }
    //Begin add by xu.wei.hz for surfaceView XR 6709333  on 18-8-3
    public static boolean useSureFaceViewToPreview() {
        return true;
    }

    public static CameraAgentFactory.CameraApi useWhichCameraApi() {
        return CameraAgentFactory.CameraApi.API_2;
    }
    //End add by xu.wei.hz for surfaceView XR 6709333 on 18-8-3
}
