package shaolizhi.mymusiclife;

import android.app.Activity;
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
import android.widget.ImageView;

public class WheelOfFortune extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Boolean threadIsRunning;
    private Canvas canvas;
    private ImageView imageView;
    //抽奖的文字
    private String[] stringsOfGift = new String[]{"LG 34UC88-B", "iPhone 7", "Alienware R4 M17",
            "Dog", "Boot"};
    //每个盘块的颜色
    private int[] colorsOfGiftBackground = new int[]{0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000,
            0xFF000000};
    //与文字对应的图片
    private int[] imageOfGift = new int[]{R.drawable.lg_34uc88_b, R.drawable.iphone_7,
            R.drawable.alienware_r4_m17, R.drawable.dog, R.drawable.cat};
    //与文字对应图片的bitmap数组
    private Bitmap[] imageOfGiftBitmap;
    //盘块的个数
    private int itemCount = 5;
    //绘制盘块的范围
    private RectF drawRange = new RectF();
    //圆的直径
    private int radius;
    //绘制盘块的画笔
    private Paint backgroundPaint;
    //绘制文字的画笔
    private Paint textPaint;
    //滚动的速度
    private double speed = 0;
    private volatile float mStartAngle = 0;
    //是否点击了停止
    private boolean isFuckerClickedEnd = false;
    //转盘加速度
    private double a_speed = 0;
    //View的中心位置
    private int centerOfView;
    //View的padding,这里我们认为4个padding的值一致，以paddingLeft为标准
    private int padding;
    //背景图的bitmap
    private Bitmap imgageOfBackgroudBitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.background);
    //文字的大小
    private float textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    private Activity activity;

    public void getActivity(Activity activity) {
        this.activity = activity;
        imageView = (ImageView) activity.findViewById(R.id.imageButton);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a_speed<0){
                    //do-nothing
                }else {
                    if (speed == 0){
                        //静止
                        speed = 8;
                        a_speed = 0;
                    }else {
                        //开始
                        a_speed = -0.05;
                    }
                }
            }
        });
    }

    private Thread threadToDraw = new Thread(new Runnable() {
        @Override
        public void run() {
            while (threadIsRunning) {
                draw();
            }
        }
    });

    private void draw() {
        canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            /**
             * 画你希望画的东西
             */

            /**
             * 绘制背景颜色为白色,并画上背景图
             */
            canvas.drawColor(0xFFFFFFFF);
//            canvas.drawBitmap(imgageOfBackgroudBitmap, null, new Rect(padding / 2, padding / 2,
//                    getMeasuredWidth() - padding / 2, getMeasuredWidth() - padding / 2), null);

            /**
             * 设置初始角度，并且设置每次转动的角度
             */
            float tmpAngle = mStartAngle;
            float sweepAngle = (float) (360 / itemCount);

            for (int i = 0; i < itemCount; i++) {
                /**
                 * 利用drawArc绘制五个颜色不同的扇形
                 * ——这五个扇形作为抽奖转盘中的五个区域，平分了整个原型
                 */
                backgroundPaint.setColor(colorsOfGiftBackground[i]);
                backgroundPaint.setStrokeWidth((float) 3.0);
                backgroundPaint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(drawRange, tmpAngle, sweepAngle, true, backgroundPaint);
                /**
                 * 绘制文本
                 */
                drawText(tmpAngle, sweepAngle, stringsOfGift[i]);
                /**
                 * 绘制图标
                 */
                drawIcon(tmpAngle, i);
                /**
                 * 让整个图像转动起来
                 */
                tmpAngle += sweepAngle;

            }
            speed += a_speed;
            mStartAngle += speed;
            if (speed<=0){
                speed = 0;
                a_speed =0;
            }
        }
        if (canvas != null) {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawText(float startAngle, float sweepAngle, String string) {
        Path path = new Path();
        //添加一个圆弧最为路径
        path.addArc(drawRange, startAngle, sweepAngle);
        //测量传入字符串的宽度
        float textWidth = textPaint.measureText(string);
        float hOffset = (float) (radius * Math.PI / 5 / 2 - textWidth / 2);// 水平偏移
        float vOffset = radius / 12;// 垂直偏移
        canvas.drawTextOnPath(string, path, hOffset, vOffset, textPaint);
    }

    private void drawIcon(float startAngle, int i) {
        //设置图片的宽度为半径的1/4
        int imageWidth = radius / 8;
        float angle = (float) ((180 / itemCount + startAngle) * (Math.PI / 180));
        int x = (int) (centerOfView + radius / 2 / 2 * Math.cos(angle));
        int y = (int) (centerOfView + radius / 2 / 2 * Math.sin(angle));

        // 确定绘制图片的位置
        Rect rect = new Rect(x - imageWidth / 2, y - imageWidth / 2, x + imageWidth
                / 2, y + imageWidth / 2);

        canvas.drawBitmap(imageOfGiftBitmap[i], null, rect, null);
    }

    public WheelOfFortune(Context context) {
        this(context, null);
    }

    public WheelOfFortune(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelOfFortune(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        /**
         * 说句实话，这三个有什么用我还是没看懂
         */
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    /**
     * 设置控件为正方形,并对以下变量进行赋值
     * radius 转盘的直径
     * padding Padding值
     * centOfView View的中心点（View是个正方形，正方形有中心点）
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //从系统测量出的SurfaceView的宽和高中取一个较小的作为width
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //获取圆形的直径
        radius = width - getPaddingLeft() - getPaddingRight();
        //设定padding值
        padding = getPaddingLeft();
        //设定中心点
        centerOfView = width / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /**
         * 初始化绘制圆弧的画笔
         */
        backgroundPaint = new Paint();
        //设置画笔的抗锯齿
        backgroundPaint.setAntiAlias(true);
        //设定是否使用图像抖动处理，会使绘制出来的图片更加平滑和饱满，图像更加清晰
        backgroundPaint.setDither(true);

        /**
         * 初始化绘制文字的画笔
         */
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);

        /**
         * 设定一个RectF类，也就是矩形，代表了圆弧的绘制范围
         */
        drawRange = new RectF(padding, padding, radius + padding,
                radius + padding);

        /**
         * 初始化与文字对应的图片的Bitmap（就是LG显示器和外星人电脑它们）
         */
        imageOfGiftBitmap = new Bitmap[itemCount];
        for (int i = 0; i < itemCount; i++) {
            imageOfGiftBitmap[i] = BitmapFactory.decodeResource(getResources(), imageOfGift[i]);
        }

        /**
         * 启动线程，并设置线程开关为开（threadIsRunning是线程开关，它代替了threadToDraw.stop()的功能）
         */
        threadIsRunning = true;
        threadToDraw.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        threadIsRunning = false;
    }
}
