package sv;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //Surface对象由父类持有由父类创建，子类不可访问，
    //那Surface是个什么东西，什么时候被创建呢？
    //子类只可以获取SurfaceHolder，那子类通过SurfaceHolder操作Surface进行绘制吗？
    //通过定时器主动进行绘制（paint）
    SurfaceHolder holder=null;
    Paint paint;
    public MySurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        holder=getHolder();
        holder.addCallback(this);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);

        this.setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread t=new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning=false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas=holder.lockCanvas();
        //刷屏
        canvas.drawColor(Color.BLACK);

        canvas.drawCircle(x, y, 10, paint);

        holder.unlockCanvasAndPost(canvas);
    }

    private void paint(Paint paint) {
        Canvas canvas=holder.lockCanvas();
        //刷屏
        canvas.drawColor(Color.BLACK);

        canvas.drawCircle(x, y, 10, paint);

        holder.unlockCanvasAndPost(canvas);
    }

    boolean isRunning=true;
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isRunning) {
            paint(paint);
            move();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private int x,y;
    private void move(){
        x+=2;
        y+=2;
    }
}
