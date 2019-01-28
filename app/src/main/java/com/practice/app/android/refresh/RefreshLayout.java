package com.practice.app.android.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * Created by lixiang on 2019/1/27.
 */
public final class RefreshLayout extends LinearLayout implements NestedScrollingParent2 {
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private RefreshHeader mRefreshHeader;
    private float mDamping = 0.75f;
    private boolean isRefreshing;
    private ValueAnimator mScrollHeaderBackAnimator;

    public RefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mRefreshHeader = new DefaultRefreshHeader();
        mRefreshHeader.init(context, this);
        addView(mRefreshHeader.getView(), 0);
        mRefreshHeader.getView().post(new Runnable() {
            @Override
            public void run() {
                scrollTo(0, mRefreshHeader.getView().getMeasuredHeight());
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View contentView = getChildAt(1);
        int widthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        // 重新设置list的高度
        int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int heightSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        contentView.measure(widthSpec, heightSpec);
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
        handleNestedScroll(target, dyUnconsumed, null, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        handleNestedScroll(target, dy, consumed, type);
    }

    private void handleNestedScroll(@NonNull View target, int dy, int[] consumed, int type) {
        // dy > 0：手指向上滑，dy < 0：手指向下滑
        boolean hiddenTop = dy > 0 && getScrollY() < mRefreshHeader.getView().getMeasuredHeight();
        boolean showTop = dy < 0 && getScrollY() > 0
                && !ViewCompat.canScrollVertically(target, -1); // target已经滚动到了顶部
        if (isRefreshing) {
            if (hiddenTop || showTop) {
                if (consumed != null) {
                    consumed[1] = dy;
                }
                scrollBy(0, dy);
            }
            return;
        }
        if (showTop) {
            switch (type) {
                case ViewCompat.TYPE_TOUCH:
                    if (consumed != null) {
                        consumed[1] = dy;
                    }
                    // 下拉需要阻尼
                    scrollBy(0, (int) (dy * mDamping));
                    break;
                case ViewCompat.TYPE_NON_TOUCH:
                    // 滑动target的fling惯性滚动，不处理，也就是说target滚动到顶部时，不自动滑出header
                    if (consumed != null) {
                        consumed[1] = dy;
                    }
                    break;
                default:
                    break;
            }
        } else if (hiddenTop) {
            switch (type) {
                case ViewCompat.TYPE_TOUCH:
                case ViewCompat.TYPE_NON_TOUCH:
                    if (consumed != null) {
                        consumed[1] = dy;
                    }
                    scrollBy(0, dy);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isScrollBackAnimRunning()) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isRefreshing) {
                    if (getScrollY() == 0) {
                        isRefreshing = true;
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animateHeaderBack();
                            }
                        }, 3000);
                    } else if (getScrollY() > 0) {
                        animateHeaderBack();
                    }
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void animateHeaderBack() {
        if (mScrollHeaderBackAnimator == null) {
            mScrollHeaderBackAnimator = ValueAnimator.ofInt(0, 0);
            mScrollHeaderBackAnimator.setDuration(500);
            mScrollHeaderBackAnimator.setInterpolator(new LinearInterpolator());
            mScrollHeaderBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    scrollTo(0, value);
                }
            });
            mScrollHeaderBackAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isRefreshing = false;
                    scrollTo(0, mRefreshHeader.getView().getMeasuredHeight());
                }
            });
        }
        if (mScrollHeaderBackAnimator.isRunning()) {
            mScrollHeaderBackAnimator.cancel();
        }
        int start = getScrollY(), end = mRefreshHeader.getView().getMeasuredHeight();
        if (start == end) {
            mScrollHeaderBackAnimator.end();
            return;
        }
        mScrollHeaderBackAnimator.setIntValues(getScrollY(), mRefreshHeader.getView().getMeasuredHeight());
        mScrollHeaderBackAnimator.start();
    }

    private boolean isScrollBackAnimRunning() {
        return mScrollHeaderBackAnimator != null && mScrollHeaderBackAnimator.isRunning();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y > mRefreshHeader.getView().getMeasuredHeight()) {
            y = mRefreshHeader.getView().getMeasuredHeight();
        } else if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }
}
