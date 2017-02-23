package shaolizhi.mymusiclife;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Random;

/**
 * Created by shaol on 2017/2/17.
 */

public class SuckMyDick extends View {

    private String text;
    private int textColor;
    private int textSize;
    private Paint paint;
    private Rect rect;

    public SuckMyDick(Context context) {
        this(context, null);
    }

    public SuckMyDick(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 获取我们自定义的属性：Text,TextColor,TextSize
     */
    public SuckMyDick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SuckMyDick, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.SuckMyDick_text:
                    text = typedArray.getString(attr);
                    break;
                case R.styleable.SuckMyDick_textColor:
                    textColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.SuckMyDick_textSize:
                    textSize = typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();

        /**
         * 设置画笔进行文字绘制相关计算时，文字大小为用户输入大小
         * 使用getTextBounds给rect赋值，此时rect的大小是text的大小（也就是说text多大rect多大【动态的？】）
         */
        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                text = randomText();
                postInvalidate();
            }
        });

    }

    @Override
    protected void onMeasure(int width, int height) {
        int mWidth = 0;
        int mHeight = 0;
        int specMode = MeasureSpec.getMode(width);
        int specSize = MeasureSpec.getSize(width);
        switch (specMode) {
            /**
             * match_parent或直接指定
             */
            case MeasureSpec.EXACTLY:
                mWidth = getPaddingLeft() + getPaddingRight() + specSize;
                break;
            /**
             * wrap_content
             */
            case MeasureSpec.AT_MOST:
                mWidth = getPaddingLeft() + getPaddingRight() + rect.width();
                break;
        }
        specMode = MeasureSpec.getMode(height);
        specSize = MeasureSpec.getSize(height);
        switch (specMode) {
            /**
             * match_parent或直接指定
             */
            case MeasureSpec.EXACTLY:
                mHeight = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            /**
             * wrap_content
             */
            case MeasureSpec.AT_MOST:
                mHeight = getPaddingTop() + getPaddingBottom() + rect.height();
                break;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        /**
         * drawRect函数详解：
         * 画一个长方形，传入两个参数，长方形的左上角坐标和长方形的右下角坐标，
         * 长方形左上角为(0,0)
         * 长方形右下角是由xml中定义的android:weight和android:height决定的
         */
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        paint.setColor(Color.WHITE);
        /**
         * 这里需要详解一下getMeasuredWidth和getWidth的区别
         * getMeasuredWidth获取的是View原始的大小，也就是这个View在XML文件中配置或代码中设置的大小
         * getWidth获取的是这个View最终显示的大小，这个大小有可能等于getMeasuredWidth也有可能不等于
         *
         * drawText#Canvas详解
         * 用画笔（Paint）在坐标（x,y）处写一个字符串（text），字符串的属性由画笔设定。
         */
        canvas.drawText(text, getWidth() / 2 - rect.right / 2 - rect.left / 2, getHeight() / 2 + rect.height() / 2, paint);

        //  System.out.println(" ");
        System.out.println("fuck");
        System.out.println("Rect.Left = " + rect.left);
        System.out.println("Rect.Right = " + rect.right);
        System.out.println("Rect.Width = " + rect.width());
        System.out.println("View.Width = " + getWidth());
        System.out.println("View.MeasureWidth = " + getMeasuredWidth());
    }

    /**
     * 本方法用于生成一个随机的四位数（String）
     */
    private String randomText(){
        Random random = new Random();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0;i<4;i++){
            stringBuffer.append(random.nextInt(10));
        }
        return stringBuffer.toString();

    }
}
