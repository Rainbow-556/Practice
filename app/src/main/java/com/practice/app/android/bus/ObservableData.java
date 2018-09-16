package com.practice.app.android.bus;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Handler;
import android.os.Looper;

import com.practice.app.util.FLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lixiang on 2018/9/16.
 */
public final class ObservableData<T> {
    private T mData;
    private int mDataVersion;
    private HashMap<LifecycleOwner, ArrayList<EventObserverWrapper<T>>> mAtLeastResumeObserverMap = new HashMap<>();
    private ArrayList<EventObserver<T>> mAnyLifecycleObserverList = new ArrayList<>();

    public synchronized void observe(LifecycleOwner lifecycleOwner, EventObserver<T> observer) {
        if (lifecycleOwner == null || observer == null) {
            return;
        }
        if (mAtLeastResumeObserverMap.containsKey(lifecycleOwner)) {
            ArrayList<EventObserverWrapper<T>> wrappers = mAtLeastResumeObserverMap.get(lifecycleOwner);
            wrappers.add(new EventObserverWrapper<>(mDataVersion, observer));
            return;
        }
        ArrayList<EventObserverWrapper<T>> wrappers = new ArrayList<>();
        wrappers.add(new EventObserverWrapper<>(mDataVersion, observer));
        lifecycleOwner.getLifecycle().addObserver(new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // 页面销毁时自动移除监听
                    ArrayList<EventObserverWrapper<T>> removed = mAtLeastResumeObserverMap.remove(source);
                    if (removed != null) {
                        removed.clear();
                    }
                } else if (event == Lifecycle.Event.ON_RESUME) {
                    dispatchDataChanged(mAtLeastResumeObserverMap.get(source));
                }
            }
        });
        mAtLeastResumeObserverMap.put(lifecycleOwner, wrappers);
    }

    public synchronized void observeOnAnyLifecycle(EventObserver<T> observer) {
        observeOnAnyLifecycle(null, observer);
    }

    public synchronized void observeOnAnyLifecycle(LifecycleOwner lifecycleOwner, final EventObserver<T> observer) {
        if (observer == null || mAnyLifecycleObserverList.contains(observer)) {
            return;
        }
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().addObserver(new GenericLifecycleObserver() {
                @Override
                public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        // 页面销毁时自动移除监听
                        mAnyLifecycleObserverList.remove(observer);
                    }
                }
            });
        }
        mAnyLifecycleObserverList.add(observer);
    }

    public synchronized void removeObserver(EventObserver observer) {
        mAnyLifecycleObserverList.remove(observer);
    }

    public synchronized void post(final T newData) {
        if (Looper.getMainLooper() == Looper.getMainLooper()) {
            postInner(newData);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    postInner(newData);
                }
            });
        }
    }

    public synchronized void post() {
        post(null);
    }

    private void postInner(T newData) {
        mData = newData;
        mDataVersion++;
        Set<Map.Entry<LifecycleOwner, ArrayList<EventObserverWrapper<T>>>> entrySet = mAtLeastResumeObserverMap.entrySet();
        for (Map.Entry<LifecycleOwner, ArrayList<EventObserverWrapper<T>>> entry : entrySet) {
            if (entry.getKey().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                dispatchDataChanged(entry.getValue());
            }
        }
        for (EventObserver<T> observer : mAnyLifecycleObserverList) {
            observer.onChanged(mData);
        }
    }

    private void dispatchDataChanged(ArrayList<EventObserverWrapper<T>> list) {
        if (list == null) {
            return;
        }
        for (EventObserverWrapper<T> observer : list) {
            if (observer.dataVersion != mDataVersion) {
                observer.dataVersion = mDataVersion;
                observer.realObserver.onChanged(mData);
            }
        }
    }

    private static class EventObserverWrapper<T> {
        int dataVersion;
        EventObserver<T> realObserver;

        EventObserverWrapper(int dataVersion, EventObserver<T> realObserver) {
            this.dataVersion = dataVersion;
            this.realObserver = realObserver;
        }
    }

    void release() {
        mData = null;
        mAnyLifecycleObserverList.clear();
        mAtLeastResumeObserverMap.clear();
    }
}
