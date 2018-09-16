package com.practice.app.android.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.practice.app.util.FLogger;

/**
 * Created by lixiang on 2018/9/16.
 */
public final class BusFirstActivity extends AppCompatActivity {
    private int data;
    private EventObserver<Integer> mAnyLifecycleObserver = new EventObserver<Integer>() {
        @Override
        public void onChanged(Integer data) {
            FLogger.msg("observeOnAnyLifecycle onChanged lifecycleOwner=null: " + data);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        Button btnUpdate = new Button(this);
        btnUpdate.setText("update value");
        linearLayout.addView(btnUpdate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        final ObservableData<Integer> observableData = Bus.get().with("addNumber");
        observableData.observe(this, new EventObserver<Integer>() {
            @Override
            public void onChanged(Integer data) {
                FLogger.msg("BusFirstActivity.onChanged: " + data);
            }
        });
        observableData.observeOnAnyLifecycle(mAnyLifecycleObserver);
        observableData.observeOnAnyLifecycle(this, new EventObserver<Integer>() {
            @Override
            public void onChanged(Integer data) {
                FLogger.msg("observeOnAnyLifecycle.onChanged: " + data);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data++;
                observableData.post(data);
            }
        });
        //
        Button btnGo = new Button(this);
        btnGo.setText("open second page");
        linearLayout.addView(btnGo, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusFirstActivity.this, BusSecondActivity.class));
            }
        });
        setContentView(linearLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bus.get().with("addNumber").removeObserver(mAnyLifecycleObserver);
    }
}
