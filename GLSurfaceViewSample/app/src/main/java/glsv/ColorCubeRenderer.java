package glsv;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.timber.glsurfaceviewsample.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import util.ResReadUtils;
import util.ShaderUtils;

public class ColorCubeRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer vertexBuffer, colorBuffer;
    private ShortBuffer indicesBuffer;
    private int mProgram;

    private int mPositionHandle;
    private int mColorHandle;

    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix=new float[16];
    private float[] mMVPMatrix=new float[16];

    private int mMatrixHandler;

    boolean b = false;
    private static final int VERTEX_POSITION_SIZE = 3;

    private static final int VERTEX_COLOR_SIZE = 4;
    /**
     * 点的坐标
     */
    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };

    /**
     * 定义索引
     */
    private short[] indices = {
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2,     //下面
    };

    //立方体的顶点颜色
    private float[] colors = {
            1.0f, 0.4f, 0.5f, 1f,   //V0
            0.3f, 1.0f, 0.5f, 1f,   //V1
            0.3f, 0.4f, 1.0f, 1f,   //V2
            1.0f, 1.0f, 1.0f, 1f,   //V3
            0.6f, 0.5f, 0.4f, 1f,   //V4
            0.6f, 1.0f, 0.4f, 1f,   //V5
            0.6f, 0.5f, 0.4f, 1f,   //V6
            0.6f, 0.5f, 0.4f, 1f    //V7
    };

    public ColorCubeRenderer() {

        //GLES10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("GLSurfaceDemo", "begin call openGL ES API");

        createCube();
    }

    private void createCube() {
        vertexBuffer = ByteBuffer.allocateDirect(cubePositions.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(cubePositions);
        vertexBuffer.position(0);

        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        indicesBuffer = ByteBuffer.allocateDirect(indices.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);


        final int vertexShaderId = ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.vertex_colorcube_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.fragment_colorcube_shader));

        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        if(b)
//            return;
//        b = true;
//        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
//        GLES30.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        //GLES30.glEnable(GLES30.GL_CULL_FACE);

        drawCube();

    }

    private void drawCube() {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT| GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES30.glUseProgram(mProgram);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");

        //下面是上传顶点数据，注意顶点数据是怎么跟 vertex shader的属性对应起来呢

        GLES30.glVertexAttribPointer(mPositionHandle, VERTEX_POSITION_SIZE,
                GLES30.GL_FLOAT, false,
                0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        //这个是顶点数据，怎么会出现在fragment shader的属性里面呢
        //片段着色器访问顶点数据怎么理解，可以访问顶点数据吗，如果可以，那么是不是顶点数据被光栅化后的值
        //假想的模型：所有顶点数据都会被光栅化然后传递到片段着色器，如何理解，所有片段都是由顶点光栅化而来，而光栅化不仅是针对位置信息插值计算出片段
        //同时其它顶点数据也一并被相应处理附加到片段的顶点属性里面去，所以说光栅化是对所有的顶点数据一并进行，所以光栅化得到的片段一并具有所有的顶点信息

        //顶点着色器的顶点属性或者没参与运算的顶点属性 在 传入片段着色器的in之前是被光栅化过的
        //vertex->out ======光栅化======> in -> fragment
        GLES30.glVertexAttribPointer(mColorHandle, VERTEX_COLOR_SIZE,
                GLES30.GL_FLOAT, false,
                0, colorBuffer);
        GLES30.glEnableVertexAttribArray(mColorHandle);

        mMatrixHandler= GLES30.glGetUniformLocation(mProgram,"vMatrix");
        GLES30.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);


        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_SHORT, indicesBuffer);
//        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}
