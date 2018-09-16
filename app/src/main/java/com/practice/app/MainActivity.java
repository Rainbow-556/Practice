package com.practice.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.practice.app.android.AndroidActivity;
import com.practice.app.java.JavaActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_java).setOnClickListener(this);
        findViewById(R.id.btn_android).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_java:
                startActivity(new Intent(this, JavaActivity.class));
                break;
            case R.id.btn_android:
                startActivity(new Intent(this, AndroidActivity.class));
                break;
        }
    }
}
