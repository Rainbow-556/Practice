package com.practice.app.android.nestedscroll;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by glennli on 2019/1/24.<br/>
 */
public final class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        TestListFragment fragment = null;
        switch (position) {
            case 0:
                fragment = TestListFragment.newInstance("Left");
                break;
            case 1:
                fragment = TestListFragment.newInstance("Middle");
                break;
            case 2:
                fragment = TestListFragment.newInstance("Right");
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
