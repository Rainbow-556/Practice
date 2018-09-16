package com.practice.app.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.practice.app.R;
import com.practice.app.android.bus.BusFirstActivity;

/**
 * Created by lixiang on 2018/9/16.
 */
public final class AndroidActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);
        findViewById(R.id.btn_bus).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bus:
                startActivity(new Intent(this, BusFirstActivity.class));
                break;
        }
    }
}
