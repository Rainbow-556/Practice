package com.practice.app.android.earthwormindicator;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.app.R;

/**
 * Created by glennli on 2019/2/25.<br/>
 */
public final class EarthwormIndicatorActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private EarthwormIndicatorView mEarthwormIndicatorView;
    private LinearLayout llTabContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthworm_indicator);
        mViewPager = findViewById(R.id.pager);
        llTabContainer = findViewById(R.id.ll_tab_container);
        mEarthwormIndicatorView = findViewById(R.id.v_indicator);
        initPager();
    }

    private void initPager() {
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                TextView itemView = createItemView();
                itemView.setText(String.valueOf(position));
                itemView.setBackgroundColor(position % 2 == 0 ? Color.RED : Color.BLUE);
                container.addView(itemView);
                return itemView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view = (View) object;
                if (view.getParent() == container) {
                    container.removeView(view);
                }
            }

            TextView createItemView() {
                TextView textView = new TextView(EarthwormIndicatorActivity.this);
                textView.setTextSize(30);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(lp);
                return textView;
            }
        };
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(createOnPageChangeListener());
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                View view = llTabContainer.findViewWithTag("0");
                mEarthwormIndicatorView.move(view.getLeft(), view.getMeasuredWidth());
            }
        });
    }

    private ViewPager.OnPageChangeListener createOnPageChangeListener() {
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            int currPage, lastPage = -1, oldPage, lastPosition;
            boolean goingRight, isDrag;
            float oldIndicatorWidth, oldOffset;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // position: 当前ViewPager显示的第一个item索引
                // positionOffset: 滑向下一页时，是从0 -> 1；滑向上一页时，是从1 -> 0
                // 快速滑动时，positionOffset不会到0
                goingRight = position == oldPage;
                int leftItemPosition = position, rightItemPosition = position + 1;
                Log.e("lx", "position=" + position + ", positionOffset=" + positionOffset + ", goingRight=" + goingRight);
                View leftTabView = llTabContainer.findViewWithTag(String.valueOf(leftItemPosition));
                View rightTabView = llTabContainer.findViewWithTag(String.valueOf(rightItemPosition));
                if (positionOffset == 0) {
                    View oldView = llTabContainer.findViewWithTag(String.valueOf(position));
                    oldIndicatorWidth = oldView.getMeasuredWidth();
                    oldOffset = oldView.getLeft();
                    mEarthwormIndicatorView.move(oldOffset, oldIndicatorWidth);
                    return;
                }
                float widthDiff, width, offsetDiff, offsetX;
                if (goingRight) {
                    widthDiff = rightTabView.getMeasuredWidth() - leftTabView.getMeasuredWidth();
                    width = oldIndicatorWidth + widthDiff * positionOffset;
                    offsetDiff = rightTabView.getLeft() - leftTabView.getLeft();
                    offsetX = oldOffset + offsetDiff * positionOffset;
                } else {
                    widthDiff = leftTabView.getMeasuredWidth() - rightTabView.getMeasuredWidth();
                    positionOffset = 1 - positionOffset;
                    width = oldIndicatorWidth + widthDiff * positionOffset;
                    offsetDiff = leftTabView.getLeft() - rightTabView.getLeft();
                    offsetX = oldOffset + offsetDiff * positionOffset;
                }
                mEarthwormIndicatorView.move(offsetX, width);
            }

            @Override
            public void onPageSelected(int position) {
                if (lastPage == -1) {
                    lastPage = position;
                } else {
                    lastPage = currPage;
                }
                currPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    isDrag = false;
                    oldPage = mViewPager.getCurrentItem();
                    lastPosition = mViewPager.getCurrentItem();
                    Log.i("lx", "SCROLL_STATE_IDLE");
                } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    // SCROLL_STATE_SETTLING状态会在onPageSelected()之前回调，所以在这里保存oldPage
                    Log.i("lx", "SCROLL_STATE_SETTLING");
//                    if (isDrag) {
//                        oldPage = mViewPager.getCurrentItem();
//                    }
                } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    Log.i("lx", "SCROLL_STATE_DRAGGING");
                    isDrag = true;
                }
            }
        };
        return listener;
    }

    private ViewPager.OnPageChangeListener createOnPageChangeListener_2() {
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            float mLastPositionOffsetSum;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 当前总的偏移量
                float currentPositionOffsetSum = position + positionOffset;
                // 上次滑动的总偏移量大于此次滑动的总偏移量，页面从右向左进入(手指从右向左滑动)
                boolean rightToLeft = mLastPositionOffsetSum <= currentPositionOffsetSum;
                if (currentPositionOffsetSum == mLastPositionOffsetSum) return;
                int enterPosition;
                int leavePosition;
                float percent;
                if (rightToLeft) {  // 从右向左滑
                    enterPosition = (positionOffset == 0.0f) ? position : position + 1;
                    leavePosition = enterPosition - 1;
                    percent = (positionOffset == 0.0f) ? 1.0f : positionOffset;
                } else {            // 从左向右滑
                    enterPosition = position;
                    leavePosition = position + 1;
                    percent = 1 - positionOffset;
                }
                Log.e("lx", "enterPosition=" + enterPosition + ", leavePosition=" + leavePosition + ", percent" + percent);
//                if (mOnPageScrollListener != null) {
//                    mOnPageScrollListener.onPageScroll(enterPosition, leavePosition, percent);
//                }
                mLastPositionOffsetSum = currentPositionOffsetSum;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        return listener;
    }
}
