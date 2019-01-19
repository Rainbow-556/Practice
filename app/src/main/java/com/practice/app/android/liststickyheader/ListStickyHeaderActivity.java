package com.practice.app.android.liststickyheader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.app.R;

import java.util.ArrayList;

/**
 * Created by lixiang on 2019/1/19.
 */
public final class ListStickyHeaderActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sticky_header);
        mRecyclerView = findViewById(R.id.rv_sticky);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter();
//        StickyHeaderItemDecoration stickyHeaderItemDecoration = new StickyHeaderItemDecoration();
        CustomStickyHeaderItemDecoration stickyHeaderItemDecoration = new CustomStickyHeaderItemDecoration(this, R.layout.header_custom);
        stickyHeaderItemDecoration.setDataProvider(mAdapter);
        mRecyclerView.addItemDecoration(stickyHeaderItemDecoration);
        initData();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        ArrayList<StickyItem> list = new ArrayList<>();
        StickyItem stickyItem;
        for (int i = 0; i < 40; i++) {
            if (i <= 9) {
                stickyItem = new StickyItem(1, String.format("%s---%s", 1, i));
                if (i == 0) {
                    stickyItem.isFirstInGroup = true;
                } else if (i == 9) {
                    stickyItem.isLastInGroup = true;
                }
            } else if (i <= 19) {
                stickyItem = new StickyItem(2, String.format("%s---%s", 2, i));
                if (i == 10) {
                    stickyItem.isFirstInGroup = true;
                } else if (i == 19) {
                    stickyItem.isLastInGroup = true;
                }
            } else if (i <= 29) {
                stickyItem = new StickyItem(3, String.format("%s---%s", 3, i));
                if (i == 20) {
                    stickyItem.isFirstInGroup = true;
                } else if (i == 29) {
                    stickyItem.isLastInGroup = true;
                }
            } else {
                stickyItem = new StickyItem(4, String.format("%s---%s", 4, i));
                if (i == 30) {
                    stickyItem.isFirstInGroup = true;
                } else if (i == 39) {
                    stickyItem.isLastInGroup = true;
                }
            }
            list.add(stickyItem);
        }
        mAdapter.setData(list);
    }

    private static class MyAdapter extends RecyclerView.Adapter implements DataProvider {
        ArrayList<StickyItem> mData = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sticky_header, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyHolder) {
                ((MyHolder) holder).bindData(mData.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        void setData(ArrayList<StickyItem> list) {
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public StickyItem get(int position) {
            if (position >= 0 && position <= mData.size() - 1) {
                return mData.get(position);
            }
            return null;
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView tvData;

            public MyHolder(View itemView) {
                super(itemView);
                tvData = itemView.findViewById(R.id.tv_data);
            }

            void bindData(StickyItem stickyItem) {
                tvData.setText(stickyItem.data);
            }
        }
    }
}
