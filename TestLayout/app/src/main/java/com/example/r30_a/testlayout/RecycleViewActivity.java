package com.example.r30_a.testlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class RecycleViewActivity extends AppCompatActivity {
//放置RecycleView的主要地方

    RecyclerView recyclerView;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        recyclerView = (RecyclerView)findViewById(R.id.recycleview);
        list = new ArrayList<>();
        for(int i =0 ; i < 10 ; i++){
            list.add(i+"");
        }
        //呼叫自定的adapter物件,前面設定需要放入list物件
        RecycleAdapter recycleAdapter = new RecycleAdapter(list);
        //先使用預設的layoutmanager來排版
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //設定排版方向
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //設定分割線
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        //代入版型與資料
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleAdapter);


    }
}
