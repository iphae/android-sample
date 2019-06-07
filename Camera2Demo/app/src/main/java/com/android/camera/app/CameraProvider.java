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

package com.android.camera.app;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;

import com.android.camera.device.CameraId;
import com.android.ex.camera2.portability.CameraAgent;
import com.android.ex.camera2.portability.CameraDeviceInfo.Characteristics;
import com.android.ex.camera2.portability.CameraExceptionHandler;
import com.android.ex.camera2.portability.CameraSettings;

/**
 * An interface which defines the camera provider.
 */
public interface CameraProvider {

    /**
     * Requests the camera device. If the camera device of the same ID is
     * already requested, then no-op here.
     *
     * @param id The ID of the requested camera device.
     */
    void requestCamera(int id);

    /**
     * Requests the camera device. If the camera device of the same ID is
     * already requested, then no-op here.
     *
     * @param id The ID of the requested camera device.
     * @param useNewApi Whether to use the new API if this platform provides it.
     */
    void requestCamera(int id, boolean useNewApi);

    boolean waitingForCamera();

    /**
     * Releases the camera device.
     *
     * @param id The camera ID.
     */
    void releaseCamera(int id);

    /**
     * Sets a callback for handling camera api runtime exceptions on
     * a handler.
     */
    void setCameraExceptionHandler(CameraExceptionHandler exceptionHandler);

    /**
     * Get the {@link Characteristics} of the given camera.
     *
     * @param cameraId Which camera.
     * @return The static characteristics of that camera.
     */
    Characteristics getCharacteristics(int cameraId);

    /**
     * @returns The current camera id.
     */
    CameraId getCurrentCameraId();

    /**
     * Returns the total number of cameras available on the device.
     */
    int getNumberOfCameras();

    /**
     * @returns The lowest ID of the back camera or -1 if not available.
     */
    int getFirstBackCameraId();

    /**
     * @return The lowest ID of the front camera or -1 if not available.
     */
    int getFirstFrontCameraId();

    /**
     * @returns Whether the camera is facing front.
     */
    boolean isFrontFacingCamera(int id);

    /**
     * @returns Whether the camera is facing back.
     */
    boolean isBackFacingCamera(int id);
}
