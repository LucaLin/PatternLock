package com.example.r30_a.testlayout;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;
/*
* DrawLayout側滑選單的sample
* */

public class DrawLayoutActivity extends AppCompatActivity {
//使用drawlayout與toolbar做結合
//先鍵入compile 'com.android.support:design:26.1.0'
//drawLayout需搭配navigationView使用
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_layout);
        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawlayout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        //使用actionbar
        setSupportActionBar(toolbar);
        //整合drawlayout與toolbar，使左上角出現"三"圖示
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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


    }
}
