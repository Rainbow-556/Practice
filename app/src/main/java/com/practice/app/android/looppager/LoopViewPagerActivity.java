package com.practice.app.android.looppager;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.app.util.FLogger;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by lixiang on 2018/9/22.
 */
public final class LoopViewPagerActivity extends AppCompatActivity {
    private FrameLayout mFrameLayout;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }
    };
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrameLayout = new FrameLayout(this);
        mFrameLayout.setBackgroundColor(0xffedcfae);
        mFrameLayout.setClipChildren(false);
        mViewPager = new ViewPager(this);
        mViewPager.setClipChildren(false);
        int pagerHeight = 140 * 3, margin = 20 * 3;
        FrameLayout.LayoutParams lpPager = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pagerHeight);
        lpPager.leftMargin = margin;
        lpPager.rightMargin = margin;
        lpPager.gravity = Gravity.CENTER;
        mFrameLayout.addView(mViewPager, lpPager);
        initPager(mViewPager);
        setContentView(mFrameLayout);
    }

    private void initPager(final ViewPager pager) {
        pager.setOffscreenPageLimit(2);
        pager.setPageMargin(6 * 3);
        final ArrayList<String> list = new ArrayList<>();
        list.add("0");
        list.add("1");
        list.add("2");
        final int size = list.size();
        final LinearLayout indicators = addIndicators(size);
        final MyAdapter adapter = new MyAdapter();
        adapter.setData(list);
        pager.setAdapter(adapter);
        final int currentItem = pager.getCurrentItem() % size;
        for (int i = 0; i < size; i++) {
            RoundRectIndicatorView view = (RoundRectIndicatorView) indicators.getChildAt(i);
            if (i != currentItem) {
                view.update(false, 0);
            } else {
                view.update(true, 1);
            }
        }
        // anim
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            int currPage, lastPage = -1, oldPage;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final boolean goingRight = position == oldPage;
                FLogger.msg("goingRight=" + goingRight + ", position=" + position + ", positionOffset=" + positionOffset);
                if (positionOffset < 0.005 || positionOffset > 0.995f) {
                    return;
                }
                int realLeftPosition = adapter.getRealPosition(position);
                int realRightPosition = adapter.getRealPosition(position + 1);
                RoundRectIndicatorView leftDotView = null, rightDotView = null;
                if (goingRight) {
                    leftDotView = (RoundRectIndicatorView) indicators.getChildAt(realLeftPosition);
                    if (leftDotView != null) {
                        leftDotView.update(true, 1 - positionOffset);
                    }
                    if (realRightPosition != 0) {
                        rightDotView = (RoundRectIndicatorView) indicators.getChildAt(realRightPosition);
                        if (rightDotView != null) {
                            rightDotView.update(false, positionOffset);
                        }
                    }
                } else {
                    if (realLeftPosition != adapter.getRealCount() - 1) {
                        leftDotView = (RoundRectIndicatorView) indicators.getChildAt(realLeftPosition);
                        if (leftDotView != null) {
                            leftDotView.update(true, 1 - positionOffset);
                        }
                    }
                    rightDotView = (RoundRectIndicatorView) indicators.getChildAt(realRightPosition);
                    if (rightDotView != null) {
                        rightDotView.update(false, positionOffset);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (lastPage == -1) {
                    lastPage = position;
                } else {
                    lastPage = currPage;
                }
                currPage = position;
                FLogger.w("onPageSelected=" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 从倒数第二页快速滑动到倒数第一页，再滑到第一页时，中间不会触发SCROLL_STATE_IDLE事件，
                // 导致oldPage没有更新，goingRight的值是错误的，待解决
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    FLogger.i("SCROLL_STATE_IDLE");
                    oldPage = pager.getCurrentItem();
                    int realOldPage = adapter.getRealPosition(lastPage);
                    int realCurrPage = adapter.getRealPosition(currPage);
                    if (realOldPage == adapter.getRealCount() - 1 && realCurrPage == 0) {
                        // 从最后一页滑到第一页
                        int duration = 150;
                        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                        animator.setDuration(duration);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                RoundRectIndicatorView firstDot = (RoundRectIndicatorView) indicators.getChildAt(0);
                                firstDot.update(true, value);
                            }
                        });
                        animator.start();
                        int childCount = indicators.getChildCount();
                        for (int i = 1; i < childCount; i++) {
                            RoundRectIndicatorView dot = (RoundRectIndicatorView) indicators.getChildAt(i);
                            dot.animateToOtherEnd(duration);
                        }
                    } else if (realOldPage == 0 && realCurrPage == adapter.getRealCount() - 1) {
                        // 从第一页滑到最后一页
                        final int childCount = indicators.getChildCount();
                        int duration = 150;
                        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                        animator.setDuration(duration);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (float) animation.getAnimatedValue();
                                RoundRectIndicatorView lastDot = (RoundRectIndicatorView) indicators.getChildAt(childCount - 1);
                                lastDot.update(false, value);
                            }
                        });
                        animator.start();
                        for (int i = 0; i < childCount - 1; i++) {
                            RoundRectIndicatorView dot = (RoundRectIndicatorView) indicators.getChildAt(i);
                            dot.animateToOtherEnd(duration);
                        }
                    }
                    lastPage = currPage;
                }
            }
        };
        pager.addOnPageChangeListener(onPageChangeListener);
        pager.setPageTransformer(true, new ViewPager.PageTransformer() {
            float pageMaxScale = 0.1f;

            @Override
            public void transformPage(View page, float position) {
                if (position >= -1 && position < 0) {
                    // [-1, 0]，左边的item -> 中间
                    page.setPivotX(page.getWidth());
                    page.setPivotY(page.getHeight() / 2);
                    float scale = 1 + pageMaxScale * position;
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                } else if (position >= 0 && position <= 1) {
                    // [0, 1]，中间的item -> 右边
                    page.setPivotX(0);
                    page.setPivotY(page.getHeight() / 2);
                    float scale = 1 - pageMaxScale * position;
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                } else {
                    if (position < -1) {
                        page.setPivotX(page.getWidth());
                        page.setPivotY(page.getHeight() / 2);
                    } else {
                        page.setPivotX(0);
                        page.setPivotY(page.getHeight() / 2);
                    }
                    page.setScaleX(1 - pageMaxScale);
                    page.setScaleY(1 - pageMaxScale);
                }
            }
        });
        pager.setCurrentItem(3 * list.size());
        onPageChangeListener.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        pager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessageDelayed(0, 3000);
            }
        }, 500);
    }

    private LinearLayout addIndicators(int size) {
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams lpParent = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpParent.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lpParent.bottomMargin = 170 * 3;
        int dotHeight = 5 * 3, dotWidth = dotHeight * 2;
        for (int i = 0; i < size; i++) {
            RoundRectIndicatorView view = new RoundRectIndicatorView(this);
            LinearLayout.LayoutParams lpIndicator = new LinearLayout.LayoutParams(dotWidth, dotHeight);
            parent.addView(view, lpIndicator);
        }
        mFrameLayout.addView(parent, lpParent);
        return parent;
    }

    private class MyAdapter extends PagerAdapter {
        ArrayList<String> data = new ArrayList<>();
        LinkedList<TextView> cacheViews = new LinkedList<>();

        @Override
        public int getCount() {
            return 10000;
        }

        int getRealCount() {
            return data.size();
        }

        int getRealPosition(int position) {
            return position % data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition = getRealPosition(position);
            TextView textView;
            if (cacheViews.isEmpty()) {
                textView = createItemView();
            } else {
                textView = cacheViews.remove();
            }
            textView.setText((data.get(realPosition) + ", " + position));
            textView.setTag(realPosition);
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            TextView textView = (TextView) object;
            if (textView.getParent() == container) {
                container.removeView(textView);
                cacheViews.add(textView);
            }
        }

        TextView createItemView() {
            TextView textView = new TextView(LoopViewPagerActivity.this);
            textView.setTextSize(30);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(0xffaaddcc);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(lp);
            return textView;
        }

        void setData(ArrayList<String> list) {
            data.addAll(list);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
