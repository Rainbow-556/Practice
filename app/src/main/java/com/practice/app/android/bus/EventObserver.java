package com.practice.app.android.bus;

/**
 * Created by lixiang on 2018/9/16.
 */
public interface EventObserver<T> {
    void onChanged(T data);
}
