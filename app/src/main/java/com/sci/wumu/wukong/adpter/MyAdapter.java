package com.sci.wumu.wukong.adpter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wumu on 17-4-11.
 */

public class MyAdapter extends FragmentPagerAdapter {
    private  List<Fragment> mFragmentList ;

    public MyAdapter(FragmentManager manager,List<Fragment> List) {

        super(manager);
        mFragmentList = List;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

}
