package com.practice.app.android.refresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lixiang on 2019/1/27.
 */
public interface RefreshHeader {
    void init(Context context, ViewGroup parent);

    View getView();

    void onPulling(float progress);
}
