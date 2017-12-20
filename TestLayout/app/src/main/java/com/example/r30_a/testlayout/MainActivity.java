package com.example.r30_a.testlayout;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Button btnsameFAB;
    private ImageView image;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    FloatingActionButton fapButton;
    //TextView txvAns;
    int r ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbarLayout);
        image = (ImageView)findViewById(R.id.image);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
       // txvAns = (TextView)findViewById(R.id.txvAns);

        btnsameFAB = (Button)findViewById(R.id.btnsameFAB);
        btnsameFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,EntryActivity.class));
            }
        });
        fapButton = (FloatingActionButton)findViewById(R.id.floatingbutton);
        fapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(MainActivity.this,EntryActivity.class));



            }
        });
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.btn_plus);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(it);
            }
        });
        collapsingToolbarLayout.setTitle("我的測試layout");
        //收縮後的字體顏色
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        //還沒收縮時的字體顏色
        //collapsingToolbarLayout.setExpandedTitleTextColor();
        image.setImageResource(R.drawable.sample2);



    }
}
