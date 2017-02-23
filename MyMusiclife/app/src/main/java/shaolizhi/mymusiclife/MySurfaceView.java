package shaolizhi.mymusiclife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by shaol on 2017/2/23.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private String TAG = "Suck";

    private SurfaceHolder surfaceHolder;
    private boolean threadFlag;
    private int counter;
    private Canvas canvas;

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (threadFlag) {
                //锁定画布，得到Canvas对象
                canvas = surfaceHolder.lockCanvas();
                //设定Canvas对象的背景颜色
                canvas.drawColor(Color.BLACK);
                //创建画笔并设置参数
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setTextSize(40);
                //画一个白色的长方形在屏幕上
                canvas.drawRect(100, 100, 500, 500, paint);
                //画一个白色的Text在屏幕上，并使用该Text展示SurfaceView的存在时间
                canvas.drawText("时间 = " + (counter++) + "秒", 300, 600, paint);
                if (canvas != null) {
                    //解除锁定，并提交修改内容，更新屏幕
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                //进程暂停1秒钟
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //通过SurfaceView获得SurfaceHolder对象
        surfaceHolder = this.getHolder();
        //为SurfaceHolder添加回调结构SurfaceHolder.Callback
        surfaceHolder.addCallback(this);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public MySurfaceView(Context context) {
        this(context,null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        counter = 0;
        threadFlag = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        threadFlag = false;
    }
}
