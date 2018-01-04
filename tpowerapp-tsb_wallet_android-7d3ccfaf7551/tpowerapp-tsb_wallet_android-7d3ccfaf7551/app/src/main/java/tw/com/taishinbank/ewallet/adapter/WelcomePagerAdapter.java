package tw.com.taishinbank.ewallet.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tw.com.taishinbank.ewallet.controller.WelcomeFragment;


public class WelcomePagerAdapter extends FragmentPagerAdapter {

    public WelcomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a WelcomeFragment (defined as a static inner class below).
        return WelcomeFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return WelcomeFragment.MAX_PAGE_NUMBER;
    }

}

