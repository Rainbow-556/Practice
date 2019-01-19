package com.practice.app.android.liststickyheader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lixiang on 2019/1/19.
 */
public final class StickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int mHeaderHeight = 120;
    private DataProvider mDataProvider;

    public StickyHeaderItemDecoration() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mHeaderHeight / 2);
    }

    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDataProvider == null) {
            return;
        }
        int position = parent.getChildAdapterPosition(view);
        StickyItem stickyItem = mDataProvider.get(position);
        if (stickyItem == null) {
            return;
        }
        if (stickyItem.isFirstInGroup) {
            outRect.top = mHeaderHeight;
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        // canvas是RecyclerView的整个canvas
        int childCount = parent.getChildCount();
        StickyItem stickyItem;
        int position;
        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            position = parent.getChildAdapterPosition(itemView);
            if (mDataProvider == null || (stickyItem = mDataProvider.get(position)) == null) {
                continue;
            }
            if (i == 0) {
                // 屏幕中第一个可见的itemView
                if (stickyItem.isLastInGroup && itemView.getBottom() - parent.getPaddingTop() <= mHeaderHeight) {
                    // 当前为group中的最后一个itemView，实现上个header被慢慢顶出去的效果
                    drawHeader(stickyItem, canvas, parent.getPaddingLeft(), itemView.getBottom() - mHeaderHeight,
                            itemView.getMeasuredWidth(), itemView.getBottom());
                } else {
                    // 画吸顶的header
                    drawHeader(stickyItem, canvas, parent.getPaddingLeft(), parent.getPaddingTop(),
                            itemView.getMeasuredWidth(), parent.getPaddingTop() + mHeaderHeight);
                }
            } else {
                if (stickyItem.isFirstInGroup) {
                    // group中的第一个itemView
                    drawHeader(stickyItem, canvas, parent.getPaddingLeft(), itemView.getTop() - mHeaderHeight,
                            itemView.getMeasuredWidth(), itemView.getTop());
                }
            }
        }
    }

    private void drawHeader(StickyItem stickyItem, Canvas canvas, float left, float top, float right, float bottom) {
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(left, top, right, bottom, mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawText(String.valueOf(stickyItem.groupId), left, top + mHeaderHeight / 1.5f, mPaint);
    }
}
