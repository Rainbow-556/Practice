package com.practice.app.module.thread;

import com.practice.app.util.FLogger;

/**
 * Created by lixiang on 2018/7/30.<br/>
 */
public final class ThreadsOrderPrint {
    private Object mLock = new Object();
    private boolean isAPrinted;
    private int mMaxPrintCount = 5, mCurPrintCount;

    public void print() {
        mCurPrintCount = 0;
        isAPrinted = false;
        Runnable printA = new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    try {
                        while (true) {
                            while (isAPrinted) {
                                mLock.wait();
                            }
                            if (mCurPrintCount >= mMaxPrintCount) {
                                return;
                            }
                            FLogger.msg("A");
                            Thread.sleep(200);
                            isAPrinted = true;
                            mLock.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Runnable printB = new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    try {
                        while (true) {
                            while (!isAPrinted) {
                                mLock.wait();
                            }
                            if (mCurPrintCount >= mMaxPrintCount) {
                                return;
                            }
                            FLogger.msg("B---");
                            Thread.sleep(200);
                            isAPrinted = false;
                            mCurPrintCount++;
                            mLock.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(printA).start();
        new Thread(printB).start();
    }
}
