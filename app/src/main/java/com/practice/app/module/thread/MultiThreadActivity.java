package com.practice.app.module.thread;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.practice.app.R;

/**
 * Created by lixiang on 2018/7/30.<br/>
 */
public final class MultiThreadActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_multi_thread);
        findViewById(R.id.btn_order_print).setOnClickListener(this);
        findViewById(R.id.btn_producer_consumer).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order_print: // 两个线程依次打印A，B
                ThreadsOrderPrint orderPrint = new ThreadsOrderPrint();
                orderPrint.print();
                break;
            case R.id.btn_producer_consumer: // 生产者和消费者
                ProducerAndConsumer producerAndConsumer = new ProducerAndConsumer();
                producerAndConsumer.execute();
                break;
        }
    }
}
