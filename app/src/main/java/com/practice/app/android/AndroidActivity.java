package com.practice.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.practice.app.R;
import com.practice.app.android.bus.BusFirstActivity;
import com.practice.app.android.liststickyheader.ListStickyHeaderActivity;
import com.practice.app.android.looppager.LoopViewPagerActivity;
import com.practice.app.android.nestedscroll.NestedScrollActivity;
import com.practice.app.android.refresh.RefreshLayoutActivity;

/**
 * Created by lixiang on 2018/9/16.
 */
public final class AndroidActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);
        findViewById(R.id.btn_bus).setOnClickListener(this);
        findViewById(R.id.btn_pager).setOnClickListener(this);
        findViewById(R.id.btn_sticky_header).setOnClickListener(this);
        findViewById(R.id.btn_nested_scroll).setOnClickListener(this);
        findViewById(R.id.btn_refresh).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bus:
                startActivity(new Intent(this, BusFirstActivity.class));
                break;
            case R.id.btn_pager:
                startActivity(new Intent(this, LoopViewPagerActivity.class));
                break;
            case R.id.btn_sticky_header:
                startActivity(new Intent(this, ListStickyHeaderActivity.class));
                break;
            case R.id.btn_nested_scroll:
                startActivity(new Intent(this, NestedScrollActivity.class));
                break;
            case R.id.btn_refresh:
                startActivity(new Intent(this, RefreshLayoutActivity.class));
                break;
        }
    }
}
