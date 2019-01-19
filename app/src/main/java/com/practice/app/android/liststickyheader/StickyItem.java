package com.practice.app.android.liststickyheader;

/**
 * Created by lixiang on 2019/1/19.
 */
public final class StickyItem {
    public String data;
    public int groupId;
    public boolean isFirstInGroup;
    public boolean isLastInGroup;

    public StickyItem(int groupId, String data) {
        this.groupId = groupId;
        this.data = data;
    }
}
