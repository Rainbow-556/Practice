package com.practice.app.android.nestedscroll;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
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
public final class NestedScrollLinearLayout extends LinearLayout implements NestedScrollingParent {
    private TextView tvTitle;
    private View mTabView, mTopView, mListView;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    public NestedScrollLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public NestedScrollLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestedScrollLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 子view滚动之后调用，会把滑动时没有消费的值传过来
        // 在这里处理为消费的值，可以解决list与悬停View之间的断层
//        Log.w("lx", String.valueOf("onNestedScroll(): dyUnconsumed=" + dyUnconsumed));
        boolean hiddenTop = dyUnconsumed > 0 && getScrollY() < mTopView.getMeasuredHeight();
        boolean showTop = dyUnconsumed < 0 && getScrollY() > 0
                && (!ViewCompat.canScrollVertically(mListView, -1) && !ViewCompat.canScrollVertically(target, -1));
        if (hiddenTop || showTop) {
            scrollBy(0, dyUnconsumed);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
//        Log.e("lx", "onNestedPreScroll: dy=" + String.valueOf(dy) + ", target=" + target.getClass().getSimpleName());
        // dy > 0：手指向上滑，dy < 0：手指向下滑
        boolean hiddenTop = dy > 0 && getScrollY() < mTopView.getMeasuredHeight();
        boolean showTop = dy < 0 && getScrollY() > 0
                && (!ViewCompat.canScrollVertically(mListView, -1) && !ViewCompat.canScrollVertically(target, -1));
        if (hiddenTop || showTop) {
            consumed[1] = dy;
            scrollBy(0, dy);
        }
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        // velocityY > 0：手指向上滑，velocityY < 0：手指向下滑
//        Log.e("lx", String.valueOf("onNestedFling(): velocityY=" + velocityY));
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        boolean hiddenTop = velocityY > 0 && getScrollY() < mTopView.getMeasuredHeight();
        boolean showTop = velocityY < 0 && getScrollY() > 0
                && (!ViewCompat.canScrollVertically(mListView, -1) && !ViewCompat.canScrollVertically(target, -1));
        if (hiddenTop || showTop) {
            animateScroll(velocityY, false);
        }
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    private ValueAnimator mAnimator;

    private void animateScroll(float velocityY, boolean consumed) {
//        Log.e("lx", String.valueOf(consumed));
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
