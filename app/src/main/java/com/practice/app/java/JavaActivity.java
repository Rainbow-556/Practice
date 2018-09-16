package com.practice.app.java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.practice.app.R;
import com.practice.app.java.thread.MultiThreadActivity;

/**
 * Created by lixiang on 2018/9/16.
 */
public final class JavaActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        findViewById(R.id.btn_multi_thread).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_multi_thread:
                startActivity(new Intent(this, MultiThreadActivity.class));
                break;
        }
    }
}
