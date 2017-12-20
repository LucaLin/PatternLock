package com.example.r30_a.testlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class pileActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pile);
        ButterKnife.bind(this);
        setData();

    }
@OnClick(R.id.btnreset)
public void setData(){
    List<String> datas = new ArrayList<>();
    datas.add("Item1");
    datas.add("Item2");
    datas.add("Item3");
    datas.add("Item4");
    datas.add("Item5");
    datas.add("Item6");
    datas.add("Item7");
    datas.add("Item8");
    datas.add("Item9");
    datas.add("Item10");
    datas.add("Item11");

    Config config = new Config();
    config.secondaryScale = 0.8f;
    config.scaleRatio = 0.4f;
    config.maxStackCount = 4;
    config.initialStackCount = 2;
    config.space =getResources().getDimensionPixelOffset(R.dimen.item_space);

    recyclerView.setLayoutManager(new StackLayoutManager(config));
    recyclerView.setAdapter(new StackAdapter(datas));
}
}
