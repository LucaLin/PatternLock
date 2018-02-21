package com.example.r30_a.patternlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.patternlock.Controller.PatternHelper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnPlockSetting,btnPlockChecking,btnSetPage,btnClear;
    ImageView imgStatus;
    TextView txvStatus;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

    }

    private void init() {
        sp = getSharedPreferences(PatternHelper.key,MODE_PRIVATE);
        btnPlockSetting = (Button)findViewById(R.id.btnSetting);
        btnPlockChecking = (Button)findViewById(R.id.btnChecking);
        btnSetPage = (Button)findViewById(R.id.btnsetPage);
        btnClear = (Button)findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        imgStatus = (ImageView)findViewById(R.id.imgStatus);
        imgStatus.setBackgroundResource(R.drawable.icons8_cancel_24);
        txvStatus = (TextView)findViewById(R.id.txvStatus);

        btnPlockSetting.setOnClickListener(this);
        btnPlockChecking.setOnClickListener(this);
        btnSetPage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSetting:
                startActivity(new Intent(this,PatternLockSettingActivity.class));break;
            case R.id.btnChecking:
                if(txvStatus.getText().equals("未設定")){
                    Toast.makeText(this,"請先設定圖形密碼",Toast.LENGTH_SHORT).show();
                }else{
                startActivity(new Intent(this,PatternLockCheckingActivity.class))
                ;}break;
            case R.id.btnsetPage:
                startActivity(new Intent(this,SettingPageActivity.class));break;
            case R.id.btnClear:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示")
                        .setMessage("確定要清空密碼嗎？")
                        .setIcon(R.drawable.icons8_error_24)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PatternLockSettingActivity.savepwd = "";
                                imgStatus.setBackgroundResource(R.drawable.icons8_cancel_24);
                                txvStatus.setText("未設定");
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!(sp.getString(PatternHelper.key,"")).equals("")){

            imgStatus.setBackgroundResource(R.drawable.icons8_checkmark_24);
            txvStatus.setText("已設定");
        }

    }
}
