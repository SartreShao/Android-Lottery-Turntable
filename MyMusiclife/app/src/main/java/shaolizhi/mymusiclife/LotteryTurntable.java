package shaolizhi.mymusiclife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by shaol on 2017/2/28.
 */

public class LotteryTurntable extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * 这两个是唯一暴露给用户的两个内容
     */
    //转盘上的文字
    private String[] stringsOfGifts;
    //转盘上的图片资源
    private int[] picturesIdOfGifts;

    private SurfaceHolder surfaceHolder;
    private boolean drawingSwitch;
    private Canvas canvas;

    //盘块的个数
    private int itemCounts;
    //弧形的圆心角
    private float centralAngle;
    //抽奖转盘的外接矩形
    private RectF externalRectangle;
    //绘制扇形的画笔
    private Paint sectorPaint;
    //抽奖圆盘的直径
    private int diameter;
    //由于Android不建议在onMeasure中进行分配内存操作，所以只能将RectF的初始化工作从onMeasure中转移到surfaceCreated中去，padding是一个中间变量，记录了外接矩形RectF与SurfaceView的距离
    private int padding;


    /**
     * 绘制一个抽奖轮盘
     *
     * @param angle angle=0时，抽奖轮盘是完全水平的
     */
    private void drawLotteryTurntable(float angle) {
        for (int i = 0; i < itemCounts; i++) {
            drawSector(angle);
            drawPicture(angle);
            drawText(angle);
            angle += centralAngle;
        }
    }

    /**
     * 绘制扇形的方法
     *
     * @param angle angle=0时，该扇形是完全水平的
     */
    private void drawSector(float angle) {
        canvas.drawArc(externalRectangle, angle, centralAngle, true, sectorPaint);
    }

    /**
     * 绘制文字的方法
     *
     * @param angle angle=0时，文字水平
     */
    private void drawText(float angle) {

    }

    /**
     * 绘制图片的方法
     *
     * @param angle angle=0时，图片水平
     */
    private void drawPicture(float angle) {

    }

    /**
     *
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //从系统测量出的SurfaceView的宽和高中取一个较小的作为SurfaceView这个正方形的边长
        int sideLength = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //获取圆形的直径
        diameter = sideLength - getPaddingLeft() - getPaddingRight();
        //中间变量，为了在surfaceCreated中初始化RectF——Android不建议在onMeasure中进行内存分配操作
        padding = getPaddingLeft();

        setMeasuredDimension(sideLength, sideLength);
    }

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (drawingSwitch) {
                //锁定画布，得到Canvas对象
                canvas = surfaceHolder.lockCanvas();
                /**
                 * 开始绘画
                 */
                drawLotteryTurntable(0);
                /**
                 * 结束绘画
                 */
                if (canvas != null) {
                    //解除锁定，并提交修改内容，更新屏幕
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    });

    public LotteryTurntable(Context context) {
        this(context, null);
    }

    public LotteryTurntable(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LotteryTurntable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //通过SurfaceView获得SurfaceHolder对象
        surfaceHolder = this.getHolder();
        //为SurfaceHolder添加回调结构SurfaceHolder.Callback
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawingSwitch = true;
        thread.start();

        /**
         * 画扇形需要用的三个参数：externalRectangle、centralAngle、sectorPaint
         * 计算sectorPaint需要用的中间参数：itemCounts
         */
        //初始化转盘的外接矩形externalRectangle
        externalRectangle = new RectF(padding, padding, diameter + padding, diameter + padding);

        //初始化盘块的个数itemCounts
        itemCounts = Math.min(stringsOfGifts.length, picturesIdOfGifts.length);

        //初始化圆心角centralAngle
        centralAngle = (float) 360 / itemCounts;

        //初始化绘制扇形的画笔sectorPaint
        sectorPaint.setColor(Color.WHITE);
        sectorPaint.setStrokeWidth((float) 3.0);
        sectorPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawingSwitch = false;
    }
}
