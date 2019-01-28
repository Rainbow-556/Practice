package com.practice.app.android.nestedscroll;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.app.R;

/**
 * Created by glennli on 2019/1/24.<br/>
 */
public final class NestedScrollLinearLayout extends LinearLayout implements NestedScrollingParent2 {
    private TextView tvTitle;
    private View mTabView, mHeaderView, mTitleView, mContentView, mListView;
    private ValueAnimator mAnimator;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private int mHeaderViewId, mTabViewId, mTitleViewId, mTitleTextViewId, mContentViewId;

    public NestedScrollLinearLayout(Context context) {
        this(context, null);
    }

    public NestedScrollLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NestedScrollLinearLayout);
        mHeaderViewId = array.getResourceId(R.styleable.NestedScrollLinearLayout_nsll_headerViewId, 0);
        mTitleViewId = array.getResourceId(R.styleable.NestedScrollLinearLayout_nsll_titleViewId, 0);
        mTitleTextViewId = array.getResourceId(R.styleable.NestedScrollLinearLayout_nsll_titleTextViewId, 0);
        mTabViewId = array.getResourceId(R.styleable.NestedScrollLinearLayout_nsll_tabViewId, 0);
        mContentViewId = array.getResourceId(R.styleable.NestedScrollLinearLayout_nsll_contentViewId, 0);
        array.recycle();
        init(context);
    }

    private void init(Context context) {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        post(new Runnable() {
            @Override
            public void run() {
                mListView = findViewById(R.id.list);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = findViewById(mHeaderViewId);
        mTitleView = findViewById(mTitleViewId);
        tvTitle = findViewById(mTitleTextViewId);
        mTabView = findViewById(mTabViewId);
        mContentView = findViewById(mContentViewId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int scrollMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        // 重新设置list的高度
        int pagerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                - mTabView.getMeasuredHeight() - mTitleView.getMeasuredHeight();
        int scrollMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                pagerHeight, MeasureSpec.EXACTLY);
        mContentView.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
    }

    @Override
    public void scrollTo(int x, int y) {
        int topViewHeight = mHeaderView.getMeasuredHeight();
        if (y > topViewHeight) {
            y = topViewHeight;
        } else if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        // 子view滚动之后调用，会把滑动时没有消费的值传过来
        // 在这里处理为消费的值，可以解决list与悬停View之间的断层
        handleNestedScroll(target, dyUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        handleNestedScroll(target, dy, consumed);
    }

    private void handleNestedScroll(@NonNull View target, int dy, int[] consumed) {
        // dy > 0：手指向上滑，dy < 0：手指向下滑
        boolean hiddenTop = dy > 0 && getScrollY() < mHeaderView.getMeasuredHeight();
        boolean showTop = dy < 0 && getScrollY() > 0
                && (!ViewCompat.canScrollVertically(mListView, -1) && !ViewCompat.canScrollVertically(target, -1));
        if (hiddenTop || showTop) {
            if (consumed != null) {
                consumed[1] = dy;
            }
            // 0 ~ 1
            float percent = 1f * (getScrollY() + dy) / mHeaderView.getMeasuredHeight();
            percent = Math.max(0, percent);
            percent = Math.min(1, percent);
            Log.e("lx", String.valueOf(percent));
            // 最小缩放到0.7
            percent = 1 - 0.3f * percent;
            tvTitle.setScaleX(percent);
            tvTitle.setScaleY(percent);
            scrollBy(0, dy);
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    private void animateScroll(float velocityY) {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, 1);
            mAnimator.setDuration(150);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    scrollTo(0, value);
                }
            });
        }
        if (mAnimator.isRunning()) {
            return;
        }
        int start = -1, end = -1;
        if (velocityY > 0) {
            start = getScrollY();
            end = mHeaderView.getMeasuredHeight();
        } else if (velocityY < 0) {
            start = getScrollY();
            end = 0;
        }
        if (start != end) {
            mAnimator.setIntValues(start, end);
            mAnimator.start();
        }
    }
}
