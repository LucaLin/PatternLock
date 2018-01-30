package com.example.r30_a.testlayout;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;
/*
* DrawLayout側滑選單的sample
* */

public class DrawLayoutActivity extends FragmentActivity implements YouTubePlayer.OnInitializedListener{
//使用drawlayout與toolbar做結合
//先鍵入compile 'com.android.support:design:26.1.0'
//drawLayout需搭配navigationView使用
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    Toast toast;
    TabHost tabHost;
    private View headerView;
    private TextView headertxv;
    private ImageView headerimg;
    YouTubePlayerFragment youTubePlayerFragment;
    private ViewPager viewPager;
    private List<android.support.v4.app.Fragment> list;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_layout);
        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        list = new ArrayList<>();






        drawerLayout = (DrawerLayout)findViewById(R.id.drawlayout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        //使用actionbar
        //setSupportActionBar(toolbar);
        //整合drawlayout與toolbar，使左上角出現"三"圖示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        headerView = getLayoutInflater().inflate(R.layout.header_layout,navigationView,false);

        headerimg = (ImageView)headerView.findViewById(R.id.headerimg);
       // headertxv = (TextView)headerView.findViewById(R.id.)
        //updateHeaderInfo();
        headerimg.setImageResource(R.drawable.ic_person_black_24dp);

        //監聽drawlayout事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //按到選單後收起
                drawerLayout.closeDrawer(GravityCompat.START);

                int id = item.getItemId();
                //各按鈕事件example
                switch (id){
                    case R.id.homeshowing: toast.setText("home");toast.show();break;
                    case R.id.setting: toast.setText("setting");toast.show();break;
                    case R.id.QandA: toast.setText("Q & A");toast.show();break;
                    case R.id.about: toast.setText("about");toast.show();break;
                    case R.id.policy: toast.setText("policy");toast.show();break;


                }

                return false;
            }
        });

        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                changeTabFragment(tabId);


            }
        });
        //tab的按鈕要顯示選取顏色變化，需要做出一個selector
        addTabwidget("HOME","首頁",R.drawable.tab_home);
        addTabwidget("PAYMENT","付款",R.drawable.tab_payment);
        addTabwidget("BILL","帳單",R.drawable.tab_bill);
        addTabwidget("EXTRA","其它",R.drawable.tab_info);
        addTabwidget("YOUTUBE","形象廣告",R.drawable.youtube_tab);

    }


/*
    private void updateHeaderInfo() {
        loadImage();

    }

    private void loadImage(String data, ImageView imageView) {
        if(data == null){
            imageView.setImageBitmap();
        }

    }*/

    /*  private void changeTab(String tabId) {
          switch (tabId){
              case "HOME":break;
              case "PAYMENT":break;
              case "BILL":break;
              case "EXTRA":changeTabFragment(tabId);break;
          }
      }*/
    //增加tab按鈕的方法
    private void addTabwidget(String tag, String title, int iconId){
        TabHost.TabSpec spec = tabHost.newTabSpec(tag)
                .setIndicator(createTabItemView(title,iconId))
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String tag) {
                        return findViewById(android.R.id.tabcontent);
                    }
                });
        tabHost.addTab(spec);
    }



    //建立tab項目的view方法
    private View createTabItemView(String title, int id){
        View view = LayoutInflater.from(this).inflate(R.layout.tabwidget_layout,null);
        ImageView imageView = (ImageView)view.findViewById(android.R.id.icon);
        imageView.setImageResource(id);

        TextView txvtitle = (TextView)view.findViewById(android.R.id.title);
        txvtitle.setText(title);
        txvtitle.setTextSize(12);

        return view;
    }

    private void changeTabFragment(String tabId){
        Fragment fragment = null;
        youTubePlayerFragment = null;

        switch (tabId){
            case "HOME":break;
            case "PAYMENT":break;
            case "BILL":break;
            case "EXTRA": fragment = new Other_Fragment();
            break;
            case "YOUTUBE":
                //fragment = new FullScreenVideoActivity();
               // startActivity(new Intent(this,FullScreenVideoActivity.class));
                youTubePlayerFragment = new YouTubePlayerFragment();
                break;
        }

        if(fragment !=null){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.tabcontent,fragment);
            ft.commit();

        }
        if(youTubePlayerFragment != null){

            youTubePlayerFragment.initialize("api_key",this);
            android.app.FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.tabcontent,youTubePlayerFragment);

            fragmentTransaction.commit();

        }


    }



    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo("r-lNSEGkQAY");


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }



}
