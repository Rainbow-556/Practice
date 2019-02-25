package com.practice.app.android.earthwormindicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
}
