package com.example.r30_a.testlayout.cellBean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.r30_a.testlayout.R;

public class CellPageActivity extends AppCompatActivity {
    Button toCellSet33, CellSetting,toCellCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_page);

        toCellSet33 = (Button)findViewById(R.id.btn33);
        toCellSet33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CellBeanSettingActivity.startAction(CellPageActivity.this);
            }
        });
        CellSetting = (Button)findViewById(R.id.cellset);
        CellSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CellPageActivity.this,CellSettingPageActivity.class))
                ;
            }
        });

        toCellCheck = (Button)findViewById(R.id.toCellCheck);
        toCellCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CellBeanCheckingActivity.startAction(CellPageActivity.this);
            }
        });
    }
}
