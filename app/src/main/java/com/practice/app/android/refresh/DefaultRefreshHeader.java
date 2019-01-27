package com.practice.app.android.refresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.app.R;

/**
 * Created by lixiang on 2019/1/27.
 */
public final class DefaultRefreshHeader implements RefreshHeader {
    private View mHeaderView;

    @Override
    public void init(Context context, ViewGroup parent) {
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.default_refresh_header, parent, false);
    }

    @Override
    public View getView() {
        return mHeaderView;
    }

    @Override
    public void onPulling(float progress) {
    }
}
