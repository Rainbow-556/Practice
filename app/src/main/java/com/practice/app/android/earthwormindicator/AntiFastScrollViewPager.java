package com.practice.app.android.earthwormindicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by lixiang on 2019/2/26.
 */
public final class AntiFastScrollViewPager extends ViewPager {
    private boolean isPagerSettling;

    public AntiFastScrollViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public AntiFastScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 屏蔽快速滑动翻页，让手指抬起时，ViewPager滚动到指定位置时快一点，缩短屏蔽快速滚动的时间
        setSliderTransformDuration(this);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    isPagerSettling = true;
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    isPagerSettling = false;
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isPagerSettling) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private static void setSliderTransformDuration(ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new AccelerateDecelerateInterpolator());
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class FixedSpeedScroller extends Scroller {
        private float durationRatio = 1.9f;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
//            super.startScroll(startX, startY, dx, dy, duration);
            super.startScroll(startX, startY, dx, dy, (int) (duration / durationRatio));
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
//            super.startScroll(startX, startY, dx, dy, duration);
            super.startScroll(startX, startY, dx, dy, (int) (350 / durationRatio));
        }
    }
}
