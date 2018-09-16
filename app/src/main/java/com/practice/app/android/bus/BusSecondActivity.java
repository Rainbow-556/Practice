package com.practice.app.android.bus;

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
public final class BusSecondActivity extends AppCompatActivity {
    private int data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        Button button = new Button(this);
        button.setText("update value");
        linearLayout.addView(button, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bus.get().with("addNumber").post(data++);
            }
        });
        setContentView(linearLayout);
        final ObservableData<Integer> observableData = Bus.get().with("addNumber");
        observableData.observe(this, new EventObserver<Integer>() {
            @Override
            public void onChanged(Integer data) {
                FLogger.msg("BusSecondActivity.onChanged: " + data);
            }
        });
    }
}
