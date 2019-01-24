package com.practice.app.android.nestedscroll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
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
public final class NestedScrollLinearLayout extends LinearLayout implements NestedScrollingParent {
    private TextView tvTitle;
    private View mTabView;

    public NestedScrollLinearLayout(Context context) {
        super(context);
    }

    public NestedScrollLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTitle = findViewById(R.id.tv_title);
        mTabView = findViewById(R.id.ll_tab_container);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewPager pager = findViewById(R.id.pager);
        int scrollMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        // 重新设置list的高度
        int pagerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom() -
                findViewById(R.id.v_top).getMeasuredHeight() - mTabView.getMeasuredHeight();
        int scrollMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                pagerHeight, MeasureSpec.EXACTLY);
        pager.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
//        Log.e("lx", "onNestedPreScroll: dy=" + String.valueOf(dy) + ", target=" + target.getClass().getSimpleName());
        if (true) {
            if (dy > 0) {
                // 手指向上滑
//                if (Math.abs(tvTitle.getTranslationY()) <= tvTitle.getMeasuredHeight()) {
//                    consumed[1] = dy;
//                    float y = tvTitle.getTranslationY() - dy;
//                    if (y > 0) {
//                        y = 0;
//                    } else if (Math.abs(y) > tvTitle.getMeasuredHeight()) {
//                        y = tvTitle.getMeasuredHeight();
//                    }
//                    tvTitle.setTranslationY(y);
//                    //
//                    if (y > 0) {
//                        y = 0;
//                    } else if (Math.abs(y) > mTabView.getMeasuredHeight()) {
//                        y = mTabView.getMeasuredHeight();
//                    }
//                    mTabView.setTranslationY(y);
//                } else {
//                    // title已经吸顶了
//                }
                if (mTabView.getScrollY() < mTabView.getMeasuredHeight()) {
                    consumed[1] = dy;
                    float y = mTabView.getScrollY() + dy;
                    if (y > mTabView.getMeasuredHeight()) {
                        y = mTabView.getMeasuredHeight();
                    } else if (y < 0) {
                        y = 0;
                    }
                    Log.e("lx", String.valueOf(y));
                    mTabView.scrollTo(0, (int) y);
                } else {

                }
            } else if (dy < 0) {
                // 手指向下滑
            }
        }
//        boolean hiddenTop = dy > 0 && getScrollY() < tvTitle.getMeasuredHeight() + mTabView.getMeasuredHeight();
//        boolean showTop = dy < 0 && getScrollY() > 0 && !ViewCompat.canScrollVertically(target, -1);
//        if (hiddenTop || showTop) {
//            scrollBy(0, dy);
//            consumed[1] = dy;
//        }
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }
}
