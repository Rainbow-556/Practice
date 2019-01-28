package com.practice.app.android.nestedscroll;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.app.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;

/**
 * Created by glennli on 2019/1/24.<br/>
 */
public final class TestListFragment extends Fragment {
    public static TestListFragment newInstance(String tagText) {
        TestListFragment fragment = new TestListFragment();
        fragment.setTagText(tagText);
        return fragment;
    }

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private String mTagText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e("lx", String.valueOf("TestListFragment onCreateView"));
        if (mRefreshLayout == null) {
            mAdapter = new MyAdapter();
            mRefreshLayout = (SmartRefreshLayout) inflater.inflate(R.layout.fragment_test_list, container, false);
            mRecyclerView = mRefreshLayout.findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter);
        }
//        if (mRecyclerView == null) {
//            mAdapter = new MyAdapter();
//            mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_test_list, container, false);
//            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            mRecyclerView.setAdapter(mAdapter);
//        }
        mockData();
        return mRefreshLayout;
    }

    private void mockData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(String.valueOf(i));
        }
        mAdapter.setData(list);
    }

    private void setTagText(String tagText) {
        mTagText = tagText;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<String> mData = new ArrayList<>();

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nested_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bindData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void setData(ArrayList<String> mData) {
            this.mData = mData;
            notifyDataSetChanged();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvData;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tv_data);
        }

        void bindData(String text) {
            tvData.setText(String.format("%s-%s", mTagText, text));
        }
    }
}
