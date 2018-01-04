package tw.com.taishinbank.ewallet.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.WelcomePagerAdapter;

public class WelcomeActivity extends ActivityBase {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private WelcomePagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ArrayList<Integer> imageList = new ArrayList<>();
    private ArrayList<Integer> titleList = new ArrayList<>();
    private ArrayList<Integer> messageList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 設定上方客製的toolbar與置中的標題
        setCenterTitle(R.string.title_activity_welcome);
        // 設定標題與子標題
        setHeadline(null, R.string.welcome_title, R.string.welcome_subtitle);

        titleList.add(R.string.welcome_title_1);
        messageList.add(R.string.welcome_subtitle_1);
        titleList.add(R.string.welcome_title_2);
        messageList.add(R.string.welcome_subtitle_2);
        titleList.add(R.string.welcome_title_3);
        messageList.add(R.string.welcome_subtitle_3);

        imageList.add(R.drawable.img_welcome_page_01);
        imageList.add(R.drawable.img_welcome_page_03);
        imageList.add(R.drawable.img_welcome_page_05);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new WelcomePagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTitleString(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setTitleString(0);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        if(WelcomeFragment.MAX_PAGE_NUMBER <= 1)
            indicator.setVisibility(View.INVISIBLE);
    }

    public int getImage(int pageNumber)
    {
        return imageList.get(pageNumber);
    }

    public void setTitleString(int pageNumber){
        setHeadline(null, titleList.get(pageNumber), messageList.get(pageNumber));

    }

}
