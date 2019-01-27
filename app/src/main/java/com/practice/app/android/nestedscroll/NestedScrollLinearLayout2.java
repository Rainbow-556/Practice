package com.practice.app.android.nestedscroll;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.app.R;

/**
 * Created by glennli on 2019/1/24.<br/>
 */
public final class NestedScrollLinearLayout2 extends LinearLayout implements NestedScrollingParent2 {
    private TextView tvTitle;
    private View mTabView, mTopView, mListView;
    private ValueAnimator mAnimator;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    public NestedScrollLinearLayout2(Context context) {
        super(context);
        init(context);
    }

    public NestedScrollLinearLayout2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestedScrollLinearLayout2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        tvTitle = findViewById(R.id.tv_title);
        mTabView = findViewById(R.id.ll_tab_container);
        mTopView = findViewById(R.id.v_top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewPager pager = findViewById(R.id.pager);
        int scrollMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        // 重新设置list的高度
        int pagerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                - mTabView.getMeasuredHeight() - tvTitle.getMeasuredHeight();
        int scrollMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                pagerHeight, MeasureSpec.EXACTLY);
        pager.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
    }

    @Override
    public void scrollTo(int x, int y) {
        int topViewHeight = mTopView.getMeasuredHeight();
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
        boolean hiddenTop = dy > 0 && getScrollY() < mTopView.getMeasuredHeight();
        boolean showTop = dy < 0 && getScrollY() > 0
                && (!ViewCompat.canScrollVertically(mListView, -1) && !ViewCompat.canScrollVertically(target, -1));
        if (hiddenTop || showTop) {
            if (consumed != null) {
                consumed[1] = dy;
            }
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
            end = mTopView.getMeasuredHeight();
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
