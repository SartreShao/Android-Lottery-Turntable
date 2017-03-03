package shaolizhi.mymusiclife;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/*
 * Created by 邵励治 on 2017/2/28.
 */

public class LotteryTurntable extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private boolean drawingSwitch;
    private Canvas canvas;

    //抽奖圆盘的直径
    private int diameter;
    //抽奖圆盘的中心
    private int circleCenter;
    //外接矩形RectF与SurfaceView的距离
    private int padding;

    //转盘上的文字
    private String[] stringsOfGifts;
    //存储图片用的Bitmap
    private Bitmap[] bitmapOfPictures;
    //盘块的个数
    private int itemCounts;
    //弧形的圆心角
    private float centralAngle;
    //抽奖转盘的外接矩形
    private RectF externalRectangle;
    //绘图的笔
    private Paint paintingPen;
    //写字的笔
    private Paint writingPen;

    //圆盘世界的速度
    private float v;
    //圆盘世界的加速度
    private float a;
    //圆盘世界的位移
    private float x;


    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (drawingSwitch) {
                //锁定画布，得到Canvas对象
                canvas = surfaceHolder.lockCanvas();
                //Start Drawing


                //绘制白色背景
                canvas.drawColor(Color.WHITE);
                //画抽奖转盘
                drawLotteryTurntable(x);

                //下面三行代码，是转盘世界的世界法则，我们的世界是不是也是由某个人写下的几行代码制定的呢？
                if (v <= 0) {
                    a = 0;
                    v = 0;
                }
                x += v;
                v += a;


                //Stop Drawing
                if (canvas != null) {
                    //解除锁定，并提交修改内容，更新屏幕
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    });


    public void controller(View button) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (a == 0) {
                    if (v == 0) {
                        //静止状态
                        //在静止状态点击按钮，我会给转盘一个初速度v=8让转盘转起来
                        v = 8;
                    } else {
                        //转动状态
                        //在转动状态点击按钮，我会给转盘一个摩擦力，导致a=-0.05，让转盘逐渐减速
                        a = (float) -0.05;
                    }
                }
            }
        });
    }

    private void drawLotteryTurntable(float angle) {
        for (int i = 0; i < itemCounts; i++) {
            drawSector(angle);
            drawPicture(angle, bitmapOfPictures[i]);
            drawText(angle, stringsOfGifts[i]);
            angle += centralAngle;
        }
    }

    private void drawSector(float angle) {
        canvas.drawArc(externalRectangle, angle, centralAngle, true, paintingPen);
    }

    private void drawText(float angle, String string) {
        Path path = new Path();
        //添加一个圆弧最为路径
        path.addArc(externalRectangle, angle, centralAngle);
        //测量传入字符串的宽度
        float textWidth = writingPen.measureText(string);
        float hOffset = (float) (diameter * Math.PI / 5 / 2 - textWidth / 2);// 水平偏移
        float vOffset = diameter / 12;// 垂直偏移
        canvas.drawTextOnPath(string, path, hOffset, vOffset, writingPen);
    }

    private void drawPicture(float angle, Bitmap bitmap) {
        //设置图片的宽度为半径的1/4
        int imageWidth = diameter / 8;
        float alpha = (float) ((180 / itemCounts + angle) * (Math.PI / 180));
        int x = (int) (circleCenter + diameter / 2 / 2 * Math.cos(alpha));
        int y = (int) (circleCenter + diameter / 2 / 2 * Math.sin(alpha));

        // 确定绘制图片的位置
        Rect rect = new Rect(x - imageWidth / 2, y - imageWidth / 2, x + imageWidth
                / 2, y + imageWidth / 2);

        canvas.drawBitmap(bitmap, null, rect, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //从系统测量出的SurfaceView的宽和高中取一个较小的作为SurfaceView这个正方形的边长
        int sideLength = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //中间变量，为了在surfaceCreated中初始化RectF——Android不建议在onMeasure中进行内存分配操作
        padding = getPaddingLeft();
        //获取圆形的直径
        diameter = sideLength - padding - padding;
        //获取原型的中心点
        circleCenter = sideLength / 2;

        setMeasuredDimension(sideLength, sideLength);
    }

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
        //屏幕常亮
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        v = (float) 0;
        a = (float) 0;
        x = (float) 0;

        //初始化礼品文字、礼品的图片资源
        stringsOfGifts = new String[]{"LG 34UC88-B", "iPhone 7", "Alienware R4 M17",
                "Dog", "Boot"};

        int[] picturesIdOfGifts = new int[]{R.drawable.lg_34uc88_b, R.drawable.iphone_7,
                R.drawable.alienware_r4_m17, R.drawable.dog, R.drawable.cat};

        //初始化盘块的个数itemCounts
        itemCounts = Math.min(stringsOfGifts.length, picturesIdOfGifts.length);

        //初始化圆心角centralAngle
        centralAngle = (float) 360 / itemCounts;

        //初始化绘图的笔
        paintingPen = new Paint();
        paintingPen.setColor(Color.BLACK);
        paintingPen.setStrokeWidth((float) 3.0);
        paintingPen.setStyle(Paint.Style.STROKE);
        paintingPen.setDither(true);
        paintingPen.setAntiAlias(true);

        //初始化写字的笔
        writingPen = new Paint();
        writingPen.setAntiAlias(true);
        writingPen.setDither(true);
        writingPen.setColor(Color.BLACK);
        writingPen.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));

        //初始化转盘的外接矩形externalRectangle
        externalRectangle = new RectF(padding, padding, diameter + padding, diameter + padding);


        //初始化Bitmap
        bitmapOfPictures = new Bitmap[itemCounts];

        for (int i = 0; i < itemCounts; i++) {
            bitmapOfPictures[i] = BitmapFactory.decodeResource(getResources(), picturesIdOfGifts[i]);
        }

        //打开线程开关，开启线程
        drawingSwitch = true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawingSwitch = false;
    }


}
