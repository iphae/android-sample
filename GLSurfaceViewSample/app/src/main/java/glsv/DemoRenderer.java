package glsv;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DemoRenderer implements GLSurfaceView.Renderer {
    static final String TAG = "DemoRenderer";
    @Override
    public void onDrawFrame(GL10 gl) {
        //每帧都需要调用该方法进行绘制。绘制时通常先调用glClear来清空framebuffer。
        //然后调用OpenGL ES其他接口进行绘制
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        Log.i(TAG, "onDrawFrame.");

    }
    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        //当surface的尺寸发生改变时，该方法被调用，。往往在这里设置ViewPort。或者Camara等。
        gl.glViewport(0, 0, w, h);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 该方法在渲染开始前调用，OpenGL ES的绘制上下文被重建时也会调用。
        //当Activity暂停时，绘制上下文会丢失，当Activity恢复时，绘制上下文会重建。
        Log.i(TAG, "onSurfaceCreated.");
        //do nothing special
    }
}
