package com.example.raghu.recyclerviewtest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Raghu on 01-02-2015.
 */
public class SampleViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Song> songArrayList;
    SampleViewPagerAdapter(FragmentManager fm, ArrayList<Song> songArrayList) {
        super(fm);
        this.songArrayList = songArrayList;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        //String itemName = items[position];
        Fragment fragment = null;
        Log.e("SampleViewPagerAdapter", "getItem()" + position);
        switch (position) {
            case 0:
            fragment =  SongFragment.newInstance(songArrayList);
                break;
            case 1:
            fragment = (Fragment) SampleFragment.newInstance("sample", "fragment");
                break;
        }
        return fragment;
    }

    String[] items = {"Player", "Test"};
    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return items[position];
    }



}
