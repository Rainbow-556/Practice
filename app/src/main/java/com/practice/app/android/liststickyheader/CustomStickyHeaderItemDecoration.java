package com.practice.app.android.liststickyheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.practice.app.R;

/**
 * Created by lixiang on 2019/1/19.
 */
public final class CustomStickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private View mHeaderView;
    private TextView tvHeader;
    private DataProvider mDataProvider;

    public CustomStickyHeaderItemDecoration(Context context, int layoutId) {
        mHeaderView = LayoutInflater.from(context).inflate(layoutId, null);
        tvHeader = mHeaderView.findViewById(R.id.tv_header);
    }

    public void setDataProvider(DataProvider dataProvider) {
        mDataProvider = dataProvider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
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
                if (stickyItem.isLastInGroup && itemView.getBottom() - parent.getPaddingTop() <= mHeaderView.getMeasuredHeight()) {
                    // 当前为group中的最后一个itemView，实现上个header被慢慢顶出去的效果
                    drawHeader(stickyItem, canvas, parent.getPaddingLeft(), itemView.getBottom() - mHeaderView.getMeasuredHeight());
                } else {
                    // 画吸顶的header
                    drawHeader(stickyItem, canvas, parent.getPaddingLeft(), parent.getPaddingTop());
                }
            } else {
                if (stickyItem.isFirstInGroup) {
                    // group中的第一个itemView
                    drawHeader(stickyItem, canvas, parent.getPaddingLeft(), itemView.getTop());
                }
            }
        }
    }

    private void drawHeader(StickyItem stickyItem, Canvas canvas, float left, float top) {
        // 使用RecyclerView的canvas把mHeaderView画出来
        canvas.save();
        canvas.translate(left, top);
        tvHeader.setText(String.valueOf(stickyItem.groupId % 2 == 0 ? stickyItem.groupId : stickyItem.groupId + 10));
        switch (stickyItem.groupId) {
            case 1:
                mHeaderView.setBackgroundColor(0xffffccdd);
                break;
            case 2:
                mHeaderView.setBackgroundColor(0xffddccdd);
                break;
            case 3:
                mHeaderView.setBackgroundColor(0xffffddcc);
                break;
            case 4:
                mHeaderView.setBackgroundColor(0xffdddddd);
                break;
        }
        // 由于更改了mHeaderView的子view属性，如果子View的宽高是wrap content的话，则需要手动重新测量一下
        // view.measure(0, 0)：0其实是SpecMode = 0(View.MeasureSpec.UNSPECIFIED)，SpecSize = 0的MeasureSpec
        mHeaderView.measure(0, 0);
        mHeaderView.layout(0, 0, mHeaderView.getMeasuredWidth(), mHeaderView.getMeasuredHeight());
        mHeaderView.draw(canvas);
        canvas.restore();
    }
}
