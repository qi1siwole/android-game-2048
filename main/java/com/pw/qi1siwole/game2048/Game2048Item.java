package com.pw.qi1siwole.game2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by user on 2017/3/29.
 */

public class Game2048Item extends View {

    private int mNumber;
    private String mNumberVal;

    private Paint mPaint;
    private Rect mBound;

    public Game2048Item(Context context) {
        this(context, null);
    }

    public Game2048Item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Game2048Item(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mNumber = 0;
        mPaint = new Paint();
        mBound = new Rect();
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
        mPaint.setTextSize(50.0f);
        mNumberVal = String.valueOf(number);
        mPaint.getTextBounds(mNumberVal, 0, mNumberVal.length(), mBound);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String bkgdColor = null;
        switch (mNumber) {
            case 0:
                bkgdColor = "#CCC0B3";
                break;
            case 2:
                bkgdColor = "#EEE4DA";
                break;
            case 4:
                bkgdColor = "#EDE0C8";
                break;
            case 8:
                bkgdColor = "#F2B179";// #F2B179
                break;
            case 16:
                bkgdColor = "#F49563";
                break;
            case 32:
                bkgdColor = "#F5794D";
                break;
            case 64:
                bkgdColor = "#F55D37";
                break;
            case 128:
                bkgdColor = "#EEE863";
                break;
            case 256:
                bkgdColor = "#EDB04D";
                break;
            case 512:
                bkgdColor = "#ECB04D";
                break;
            case 1024:
                bkgdColor = "#EB9437";
                break;
            case 2048:
                bkgdColor = "#EA7821";
                break;
            default:
                bkgdColor = "#EA7821";
                break;
        }

        mPaint.setColor(Color.parseColor(bkgdColor));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

        if (0 != mNumber) {
            mPaint.setColor(Color.BLACK);
            float x = (getWidth() - mBound.width()) / 2;
            float y = (getHeight() + mBound.height()) / 2;

            canvas.drawText(mNumberVal, x, y, mPaint);
        }
    }
}
