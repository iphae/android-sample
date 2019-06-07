package glsv;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

public class MyGLSurfaceView extends GLSurfaceView {

    ColorCubeRenderer renderer;
    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        renderer = new ColorCubeRenderer();
        setRenderer(renderer);
        Log.i("GLSurfaceDemo", "new a MyGLSurface.");
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
