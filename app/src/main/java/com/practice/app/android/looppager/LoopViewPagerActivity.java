package com.practice.app.android.looppager;

import android.graphics.Color;
import android.os.Bundle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrameLayout = new FrameLayout(this);
        mFrameLayout.setBackgroundColor(0xffedcfae);
        mFrameLayout.setClipChildren(false);
        ViewPager pager = new ViewPager(this);
        pager.setClipChildren(false);
        int pagerHeight = 170 * 3, margin = 35 * 3;
        FrameLayout.LayoutParams lpPager = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pagerHeight);
        lpPager.leftMargin = margin;
        lpPager.rightMargin = margin;
        lpPager.gravity = Gravity.CENTER;
        mFrameLayout.addView(pager, lpPager);
        initPager(pager);
        setContentView(mFrameLayout);
    }

    private void initPager(final ViewPager pager) {
        pager.setOffscreenPageLimit(2);
        pager.setPageMargin(20 * 3);
        final ArrayList<String> list = new ArrayList<>();
        list.add("0");
        list.add("1");
        list.add("2");
        int size = list.size();
        final LinearLayout indicators = addIndicators(size);
        final MyAdapter adapter = new MyAdapter();
        adapter.setData(list);
        pager.setAdapter(adapter);
//        pager.setCurrentItem(4 * list.size());
        int currentItem = pager.getCurrentItem() % size;
        final float dotMaxScale = 0.5f;
        for (int i = 0; i < size; i++) {
            RoundRectIndicatorView view = (RoundRectIndicatorView) indicators.getChildAt(i);
            if (i != currentItem) {
//                view.setPivotX(20 * 3);
//                view.setScaleX(dotMaxScale);
//                view.setAlpha(dotMaxScale);
                view.update(false, 0);
            } else {
//                view.setPivotX(0);
                view.update(true, 1);
            }
        }
        // anim
        OnPageScrollListener onPageScrollListener = new OnPageScrollListener() {
            @Override
            public void onPageScroll(int enterPosition, int leavePosition, float percent) {
                enterPosition = adapter.getRealPosition(enterPosition);
                leavePosition = adapter.getRealPosition(leavePosition);
                FLogger.msg("onPageScrolled()"
                        + "    进入页面：" + enterPosition
                        + "    离开页面：" + leavePosition
                        + "    滑动百分比：" + percent);
                View enterIndicator = indicators.getChildAt(enterPosition);
                View leaveIndicator = indicators.getChildAt(leavePosition);
//                if (enterPosition > leavePosition) {
//                    enterIndicator.setPivotX(enterIndicator.getWidth());
//                    leaveIndicator.setPivotX(0);
//                } else {
//                    enterIndicator.setPivotX(enterIndicator.getWidth());
//                    leaveIndicator.setPivotX(0);
//                }
//                int childCount = indicators.getChildCount();
//                for (int i = 0; i < childCount; i++) {
//                    if (i != enterPosition && i != leavePosition) {
//                        View view = indicators.getChildAt(i);
//                        view.setPivotX(view.getWidth());
//                    }
//                }
                float scale = dotMaxScale + percent * dotMaxScale;
                if (enterIndicator.getScaleX() == dotMaxScale) {
                }
                enterIndicator.setScaleX(scale);
                enterIndicator.setAlpha(scale);
                //
                scale = 1 - percent * dotMaxScale;
                leaveIndicator.setScaleX(scale);
                leaveIndicator.setAlpha(scale);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        TransformPageScrollListener transformPageScrollListener = new TransformPageScrollListener(onPageScrollListener);
//        pager.addOnPageChangeListener(transformPageScrollListener);
        /*
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currPage, prePosition;
            float mLastPositionOffsetSum;  // 上一次滑动总的偏移量


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FLogger.msg("onPageScrolled --> position=" + position + ", positionOffset=" + positionOffset);
                if (prePosition == position) {
                    // 不改变缩放锚点
                    int realPosition = adapter.getRealPosition(position);
                    View currView = indicators.getChildAt(realPosition);
                    View nextView = indicators.getChildAt(realPosition + 1);
                    currView.setPivotX(0);
                    currView.setScaleX(1 - dotMaxScale * positionOffset);
                    if (nextView != null) {
                        nextView.setPivotX(nextView.getWidth());
                        nextView.setScaleX(dotMaxScale + dotMaxScale * positionOffset);
                    }
                } else {
                    // 改变缩放锚点
                    int realPosition = adapter.getRealPosition(position);
                    View currView = indicators.getChildAt(realPosition);
                    View nextView = indicators.getChildAt(realPosition + 1);
                    currView.setPivotX(currView.getWidth());
                    currView.setScaleX(1 - dotMaxScale * positionOffset);
                    if (nextView != null) {
                        if (prePosition < position) {
                            nextView.setPivotX(currView.getWidth());
                        } else {
                            nextView.setPivotX(0);
                        }
                        nextView.setScaleX(dotMaxScale + dotMaxScale * positionOffset);
                    }
                    prePosition = position;
                }
                if (true) {
                    return;
                }
                // 当前总的偏移量
                final float currentPositionOffsetSum = position + positionOffset;
                // 上次滑动的总偏移量大于此次滑动的总偏移量，页面从右向左进入(手指从右向左滑动)
                final boolean rightToLeft = mLastPositionOffsetSum <= currentPositionOffsetSum;
                if (currentPositionOffsetSum == mLastPositionOffsetSum) {
                    return;
                }
                mLastPositionOffsetSum = currentPositionOffsetSum;
                int realPosition = adapter.getRealPosition(position);
                if (rightToLeft) {
                    View currView = indicators.getChildAt(realPosition);
                    View nextView = indicators.getChildAt(realPosition + 1);
                    currView.setPivotX(0);
                    currView.setScaleX(1 - dotMaxScale * positionOffset);
                    if (nextView != null) {
                        nextView.setPivotX(nextView.getWidth());
                        nextView.setScaleX(dotMaxScale + dotMaxScale * positionOffset);
                    }
                } else {
                    View currView = indicators.getChildAt(realPosition);
                    View preView = indicators.getChildAt(realPosition + 1);
                    currView.setPivotX(currView.getWidth());
                    currView.setScaleX(1 - dotMaxScale * positionOffset);
                    if (preView != null) {
                        preView.setPivotX(0);
                        preView.setScaleX(dotMaxScale + dotMaxScale * positionOffset);
                    }
                }
                if (position == currPage) {
                    FLogger.msg("next, percent=" + positionOffset);
                    View currView = indicators.getChildAt(realPosition);
                    View nextView = indicators.getChildAt(realPosition + 1);
//                    currView.setPivotX(0);
                    currView.setScaleX(1 - dotMaxScale * positionOffset);
                    if (nextView != null) {
//                        nextView.setPivotX(nextView.getWidth());
                        nextView.setScaleX(dotMaxScale + dotMaxScale * positionOffset);
                    }
                } else {
                    // []
                    FLogger.msg("pre, percent=" + (1 - positionOffset));
                    View currView = indicators.getChildAt(realPosition);
                    View preView = indicators.getChildAt(realPosition + 1);
//                    currView.setPivotX(currView.getWidth());
                    currView.setScaleX(1 - dotMaxScale * positionOffset);
                    if (preView != null) {
//                        preView.setPivotX(0);
                        preView.setScaleX(dotMaxScale + dotMaxScale * positionOffset);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                FLogger.msg("onPageSelected(xxxx) --> position=" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    FLogger.msg("idle");
                    currPage = pager.getCurrentItem();
                }
            }
        });
        */
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                if (adapter.getRealPosition(position) == 0) {
//                    int childCount = indicators.getChildCount();
//                    for (int i = 1; i < childCount; i++) {
//                        View view = indicators.getChildAt(i);
//                        view.setPivotX(view.getWidth());
//                    }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pager.setPageTransformer(true, new ViewPager.PageTransformer() {
            float pageMaxScale = 0.2f;

            @Override
            public void transformPage(View page, float position) {
                int dotIndex = (int) page.getTag();
                FLogger.msg("transformPage() --> " + dotIndex + ", " + position);
                RoundRectIndicatorView dotView = (RoundRectIndicatorView) indicators.getChildAt(dotIndex);
                // page
                if (position >= -1 && position < 0) {
                    // [-1, 0]，左边的item -> 中间
                    page.setPivotX(page.getWidth());
                    page.setPivotY(page.getHeight() / 2);
                    float scale = 1 + pageMaxScale * position;
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                    //
//                    dotView.setPivotX(0);
//                    dotView.setScaleX(dotMaxScale + dotMaxScale * (1 + position));
                    dotView.update(true, 1 + position);
                } else if (position >= 0 && position <= 1) {
                    // [0, 1]，中间的item -> 右边
                    page.setPivotX(0);
                    page.setPivotY(page.getHeight() / 2);
                    float scale = 1 - pageMaxScale * position;
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                    //
//                    if (dotIndex == 0) {
//                        dotView.setPivotX(0);
//                    } else {
//                        dotView.setPivotX(dotView.getWidth());
//                    }
//                    dotView.setScaleX(1 - dotMaxScale * position);
                    dotView.update(false, 1 - position + 0.5f);
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
                    //
//                    dotView.setScaleX(dotMaxScale);
                }
            }
        });
    }

    private LinearLayout addIndicators(int size) {
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams lpParent = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpParent.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lpParent.bottomMargin = 170 * 3;
        for (int i = 0; i < size; i++) {
            RoundRectIndicatorView view = new RoundRectIndicatorView(this);
            LinearLayout.LayoutParams lpIndicator = new LinearLayout.LayoutParams(10 * 3,
                    15);
            if (i != 0) {
//                lpIndicator.leftMargin = 3;
            }
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
            textView.setText(data.get(realPosition));
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
}
