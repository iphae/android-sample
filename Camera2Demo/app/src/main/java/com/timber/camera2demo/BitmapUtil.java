package com.timber.camera2demo;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceView;


public class BitmapUtil {

    public interface SurfaceBitmapCallback {
        void onSuccessCallback(Bitmap bitmap);
        void onErrorCallback();
    }

    public static void getBitmapFromSurfaceView(Surface surface, int width, int height, final SurfaceBitmapCallback callback, Handler handler) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        android.util.Log.d("BitmapUtil", "timber.cameraAnimation.PixelCopy.request begin");
        PixelCopy.request(surface, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult){
                if (PixelCopy.SUCCESS == copyResult) {
                    callback.onSuccessCallback(bitmap);
                } else {
                    callback.onErrorCallback();
                }
            }
        }, handler);
    }
}
