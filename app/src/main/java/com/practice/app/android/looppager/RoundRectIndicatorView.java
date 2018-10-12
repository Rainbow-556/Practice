package com.practice.app.android.looppager;

import android.animation.ValueAnimator;
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
        final int viewHeight = getHeight();
        if (isDrawByProgress) {
            final int viewWidth = getWidth();
            mPaint.setStrokeWidth(viewHeight);
            float rectWidth = viewHeight + mProgress * (viewWidth - viewHeight);
            if (isLeftAnchor) {
                mDrawRect.set(0, 0, rectWidth, viewHeight);
            } else {
                mDrawRect.set(viewWidth - rectWidth, 0, viewWidth, viewHeight);
            }
        }
        final float r = viewHeight / 2;
        canvas.drawRoundRect(mDrawRect, r, r, mPaint);
    }

    private boolean isDrawByProgress = true;

    public void update(boolean isLeftAnchor, float progress) {
        isDrawByProgress = true;
        this.isLeftAnchor = isLeftAnchor;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        this.mProgress = progress;
        postInvalidate();
    }

    public void animateToOtherEnd(int duration) {
        isDrawByProgress = false;
        final boolean atLeft = mDrawRect.left == 0;
        final int totalSpace = getWidth() - getHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (atLeft) {
                    mDrawRect.offsetTo(totalSpace * value, 0);
                } else {
                    mDrawRect.offsetTo(totalSpace - totalSpace * value, 0);
                }
                postInvalidate();
            }
        });
        animator.start();
    }
}
