package com.practice.app.module.thread;

import android.os.Handler;

import com.practice.app.util.FLogger;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lixiang on 2018/7/30.<br/>
 */
public final class ProducerAndConsumer {
    private static final int MAX_COUNT = 10;

    public void execute() {
        new SynchronizedWay().start();
//        new BlockingQueueWay().start();
//        reentrantLockWay();
    }

    /**
     * synchronized加Object.wait()、notifyAll()方式
     */
    private static class SynchronizedWay {
        private Object mLock = new Object();
        private ArrayList<String> mDataList = new ArrayList<>();
        private int mCurCount;

        public void start() {
            Runnable producer = new Runnable() {
                @Override
                public void run() {
                    synchronized (mLock) {
                        try {
                            while (true) {
                                while (mDataList.size() >= MAX_COUNT) {
                                    mLock.wait();
                                }
                                if (mCurCount >= MAX_COUNT) {
                                    mLock.notifyAll();
                                    return;
                                }
                                FLogger.msg(Thread.currentThread().getName() + " produce " + mCurCount);
                                mDataList.add(String.valueOf(mCurCount));
                                mCurCount++;
                                if (mCurCount % 2 == 0) {
                                    mLock.wait();
                                } else {
                                    mLock.notifyAll();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Runnable consumer = new Runnable() {
                @Override
                public void run() {
                    synchronized (mLock) {
                        try {
                            while (true) {
                                while (mDataList.isEmpty()) {
                                    if (mCurCount >= MAX_COUNT) {
                                        mLock.notifyAll();
                                        return;
                                    }
                                    mLock.wait();
                                }
                                String remove = mDataList.remove(0);
                                FLogger.msg(Thread.currentThread().getName() + " consume " + remove);
                                mLock.notifyAll();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            new Thread(null, producer, "producer_1").start();
            new Thread(null, producer, "producer_2").start();
            new Thread(null, consumer, "consumer_1").start();
            new Thread(null, consumer, "consumer_2").start();
        }
    }

    private static class BlockingQueueWay {
        private BlockingQueue<String> mBlockingQueue = new LinkedBlockingQueue(10);
        private volatile boolean isStop;
        private AtomicInteger mAtomicInteger = new AtomicInteger();

        public void start() {
            Runnable producer = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!isStop) {
                            String put = String.valueOf(mAtomicInteger.getAndAdd(1));
                            mBlockingQueue.put(put);
                            FLogger.msg(Thread.currentThread().getName() + " produce " + put);
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            Runnable consumer = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                        while (!mBlockingQueue.isEmpty()) {
                            String take = mBlockingQueue.take();
                            FLogger.msg(Thread.currentThread().getName() + " consume " + take);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(null, producer, "producer_1").start();
            new Thread(null, consumer, "consumer_1").start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isStop = true;
                }
            }, 3000);
        }
    }
}
