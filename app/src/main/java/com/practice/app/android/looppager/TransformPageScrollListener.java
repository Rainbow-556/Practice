package com.practice.app.android.looppager;

import android.support.v4.view.ViewPager;

/**
 * Created by lixiang on 2018/9/23.
 */
public final class TransformPageScrollListener implements ViewPager.OnPageChangeListener {
    private OnPageScrollListener mOnPageScrollListener;
    private double mLastPositionOffsetSum;  // 上一次滑动总的偏移量

    public TransformPageScrollListener(OnPageScrollListener listener) {
        mOnPageScrollListener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // 当前总的偏移量
        final float currentPositionOffsetSum = position + positionOffset;
        // 上次滑动的总偏移量大于此次滑动的总偏移量，页面从右向左进入(手指从右向左滑动)
        final boolean rightToLeft = mLastPositionOffsetSum <= currentPositionOffsetSum;
        if (currentPositionOffsetSum == mLastPositionOffsetSum) {
            return;
        }
        int enterPosition;
        int leavePosition;
        float percent;
        if (rightToLeft) {
            // 从右向左滑
            enterPosition = (positionOffset == 0) ? position : position + 1;
            leavePosition = enterPosition - 1;
            percent = (positionOffset == 0) ? 1 : positionOffset;
        } else {
            // 从左向右滑
            enterPosition = position;
            leavePosition = position + 1;
            percent = 1 - positionOffset;
        }
        mLastPositionOffsetSum = currentPositionOffsetSum;
        if (mOnPageScrollListener != null) {
            mOnPageScrollListener.onPageScroll(enterPosition, leavePosition, percent);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageScrollListener != null) {
            mOnPageScrollListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageScrollListener != null) {
            mOnPageScrollListener.onPageScrollStateChanged(state);
        }
    }
}
