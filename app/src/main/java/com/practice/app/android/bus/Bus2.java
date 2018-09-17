package com.practice.app.android.bus;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by lixiang on 2018/9/17.<br/>
 */
public class Bus2 {
    private static class Holder {
        static final Bus2 INSTANCE = new Bus2();
    }

    public static Bus2 get() {
        return Holder.INSTANCE;
    }

    private HashMap<LifecycleOwner, ArrayList<ObserverInfo>> map = new HashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Bus2() {}

    public synchronized void register(LifecycleOwner lifecycleOwner, int event, EventObserver2 observer) {
        ArrayList<Integer> list = new ArrayList<>(1);
        list.add(event);
        register(lifecycleOwner, list, observer);
    }

    public synchronized void register(LifecycleOwner lifecycleOwner, ArrayList<Integer> events, EventObserver2 observer) {
        if (events == null || events.isEmpty() || observer == null) {
            return;
        }
        if (map.containsKey(lifecycleOwner)) {
            ArrayList<ObserverInfo> observerInfoList = map.get(lifecycleOwner);
            ObserverInfo observerInfo = new ObserverInfo(events, observer);
            observerInfoList.add(observerInfo);
            return;
        }
        ArrayList<ObserverInfo> observerInfoList = new ArrayList<>(5);
        ObserverInfo observerInfo = new ObserverInfo(events, observer);
        observerInfoList.add(observerInfo);
        map.put(lifecycleOwner, observerInfoList);
        lifecycleOwner.getLifecycle().addObserver(new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // 页面销毁时自动移除监听
                    ArrayList<ObserverInfo> remove = map.remove(source);
                    if (remove != null) {
                        for (ObserverInfo observerInfo : remove) {
                            observerInfo.observer = null;
                            observerInfo.events = null;
                        }
                        remove.clear();
                    }
                }
            }
        });
    }

    public void post(final int event, final Object data) {
        if (Looper.getMainLooper() == Looper.getMainLooper()) {
            postInner(event, data);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    postInner(event, data);
                }
            });
        }
    }

    private void postInner(final int event, final Object data) {
        Set<Map.Entry<LifecycleOwner, ArrayList<ObserverInfo>>> set = map.entrySet();
        for (Map.Entry<LifecycleOwner, ArrayList<ObserverInfo>> entry : set) {
            ArrayList<ObserverInfo> observerInfoList = entry.getValue();
            for (ObserverInfo observerInfo : observerInfoList) {
                if (observerInfo.events.contains(event)) {
                    observerInfo.observer.onChanged(event, data);
                }
            }
        }
    }

    public synchronized void unregister(EventObserver2 observer) {
        if (observer == null) {
            return;
        }
        Set<Map.Entry<LifecycleOwner, ArrayList<ObserverInfo>>> set = map.entrySet();
        for (Map.Entry<LifecycleOwner, ArrayList<ObserverInfo>> entry : set) {
            ArrayList<ObserverInfo> observerInfoList = entry.getValue();
            Iterator<ObserverInfo> iterator = observerInfoList.iterator();
            while (iterator.hasNext()) {
                ObserverInfo observerInfo = iterator.next();
                if (observerInfo.observer == observer) {
                    iterator.remove();
                }
            }
        }
    }

    private static class ObserverInfo {
        ArrayList<Integer> events;
        EventObserver2 observer;

        ObserverInfo(ArrayList<Integer> events, EventObserver2 observer) {
            this.events = events;
            this.observer = observer;
        }
    }
}
