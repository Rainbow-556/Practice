package com.practice.app.android.bus;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by lixiang on 2018/9/16.
 */
public final class Bus {
    private HashMap<String, ObservableData<Object>> map;

    private Bus() {
        map = new HashMap<>();
    }

    private static class Holder {
        static final Bus INSTANCE = new Bus();
    }

    public static Bus get() {
        return Holder.INSTANCE;
    }

    @NonNull
    public synchronized <T> ObservableData<T> with(String eventType) {
        if (!map.containsKey(eventType)) {
            map.put(eventType, new ObservableData<>());
        }
        return (ObservableData<T>) map.get(eventType);
    }

    public synchronized void clear(String eventType) {
        ObservableData<Object> removed = map.remove(eventType);
        if (removed != null) {
            removed.release();
        }
    }
}
