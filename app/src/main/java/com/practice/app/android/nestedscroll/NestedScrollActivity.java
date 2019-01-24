package com.practice.app.android.nestedscroll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.practice.app.R;

/**
 * Created by glennli on 2019/1/24.<br/>
 */
public final class NestedScrollActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_scroll);
        findViewById(R.id.tv_left).setOnClickListener(this);
        findViewById(R.id.tv_middle).setOnClickListener(this);
        findViewById(R.id.tv_right).setOnClickListener(this);
        initViewPager();
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.tv_middle:
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.tv_right:
                mViewPager.setCurrentItem(2, true);
                break;
        }
    }
}
