package com.practice.app.android.earthwormindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by glennli on 2019/2/25.<br/>
 */
public final class EarthwormIndicatorView extends View {
    private RectF mRectF = new RectF();
    private Paint mPaint;

    public EarthwormIndicatorView(Context context) {
        this(context, null);
    }

    public EarthwormIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EarthwormIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.top = 0;
        mRectF.bottom = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mRectF, 0, 0, mPaint);
    }

    public void move(float offsetX, float width) {
        mRectF.left = offsetX;
        mRectF.right = mRectF.left + width;
        postInvalidate();
    }
}
