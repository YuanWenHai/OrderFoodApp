package com.will.orderfoodapp.menu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.will.orderfoodapp.bean.Headline;

import java.util.List;

/**
 * Created by Will on 2016/4/22.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private List<Headline> data;
    public PagerAdapter(FragmentManager fm,List<Headline> data){
        super(fm);
        this.data = data;
    }
    public Fragment getItem(int position){
        return PagerFragment.getInstance(data.get(position));
    }
    //两个页面
    public int getCount(){
        return 2;
    }
}
