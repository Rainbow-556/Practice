package com.practice.app.android.looppager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lixiang on 2018/9/25.<br/>
 */
public final class RoundRectIndicatorView extends View {
    private float mProgress;
    private boolean isLeftAnchor;
    private RectF mDrawRect = new RectF();
    private int mSelectedColor = Color.BLACK, mUnSelectedColor = Color.GRAY;
    private Paint mPaint;

    public RoundRectIndicatorView(Context context) {
        super(context);
        init(context);
    }

    public RoundRectIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundRectIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(mSelectedColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(getHeight());
        float rectWidth = Math.max(getHeight(), mProgress * getWidth());
        if (isLeftAnchor) {
            mDrawRect.set(0, 0, rectWidth, getHeight());
        } else {
            mDrawRect.set(getWidth() - rectWidth, 0, getWidth(), getHeight());
        }
        float r = getHeight() / 2;
        canvas.drawRoundRect(mDrawRect, r, r, mPaint);
    }

    public void update(boolean isLeftAnchor, float progress) {
        this.isLeftAnchor = isLeftAnchor;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        this.mProgress = progress;
        postInvalidate();
    }
}
