package com.example.r30_a.testlayout.cellBean;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.r30_a.testlayout.R;

public class CellSettingPageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    RadioButton radio33,radio44,radiohide,radioNOhide, radioISRepeat,radioNORepeat,radioIgnore,radioNoIgnore;
    RadioGroup radioGroup, radioGroup2, radioGroup3,radioGroup4;
    SharedPreferences sf;
    static int setCell = 1;
    static boolean ishide;
    static boolean isrepeat = true;
    static boolean isIgnore = true;
    Button btncomfirm;
    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_setting_page);
        init();

        btncomfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //選取要哪一種尺寸的球盤
                if(radio33.isChecked()){
                    setCell =1;
                  //  sf.edit().putInt("setCell",setCell).commit();
                }else if(radio44.isChecked()){
                    setCell =2;
                   // sf.edit().putInt("setCell",setCell).commit();
                }
                //選取要不要顯示線條
                if(radiohide.isChecked()){
                    ishide = true;
                  //  sf.edit().putBoolean("setLine",true).commit();
                }else if(radioNOhide.isChecked()){
                    ishide = false;
                   // sf.edit().putBoolean("setLine",false).commit();
                }
                //是否可重複選取
                if(radioISRepeat.isChecked()){
                    isrepeat  = true;

                   // sf.edit().putBoolean("setReapeat", isrepeat).commit();
                }else if (radioNORepeat.isChecked()){
                    isrepeat = false;
                    //sf.edit().putBoolean("setReapeat", isrepeat).commit();
                }


                //是否可略過圓點
                if(radioIgnore.isChecked()){
                    isIgnore = true;
                    radioISRepeat.setEnabled(true);
                    radioNORepeat.setEnabled(true);

                }else if(radioNoIgnore.isChecked()){
                    isIgnore = false;
                    isrepeat = false;
                    radioISRepeat.setEnabled(false);
                    radioNORepeat.setEnabled(false);
                }
                sf.edit().putInt("setCell",setCell).commit();
                sf.edit().putBoolean("setLine",ishide).commit();
                sf.edit().putBoolean("setReapeat", isrepeat).commit();
                sf.edit().putBoolean("setIgnore",isIgnore).commit();


                toast = Toast.makeText(CellSettingPageActivity.this,"儲存成功",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void init() {
        sf = getSharedPreferences("setting",MODE_PRIVATE);


        btncomfirm = (Button)findViewById(R.id.btncomfirm);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup2 = (RadioGroup)findViewById(R.id.radiogroup2);
        radioGroup3 = (RadioGroup)findViewById(R.id.radiogroup3);
        radioGroup4 = (RadioGroup)findViewById(R.id.radiogroup4);
        radioGroup4.setOnCheckedChangeListener(this);
        radioGroup3.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        radiohide = (RadioButton)findViewById(R.id.radiohide);
        radioNOhide = (RadioButton)findViewById(R.id.radionohide);
        radio33 = (RadioButton)findViewById(R.id.radio33);
        radio44 = (RadioButton)findViewById(R.id.radio44);
        radioISRepeat = (RadioButton)findViewById(R.id.radioISRepeat);
        radioNORepeat = (RadioButton)findViewById(R.id.radioNORepeat);
        radioIgnore  =(RadioButton)findViewById(R.id.radioIgnore);
        radioNoIgnore = (RadioButton)findViewById(R.id.radioNoIgnore);

        }

    @Override//畫面開啟的時候檢查一下先前radiobutton選取的狀況
    protected void onResume() {
        super.onResume();
        setCell = sf.getInt("setCell", 0);
        ishide = sf.getBoolean("setLine", false);
        isrepeat = sf.getBoolean("setReapeat", true);
        if (sf.getInt("setCell", 1) == 1) {
            radio33.setChecked(true);
        } else if (sf.getInt("setCell", 0) == 2) {
            radio44.setChecked(true);
        }

        if (ishide) {
            radiohide.setChecked(true);
        } else {
            radioNOhide.setChecked(true);
        }

        if(isrepeat){
            radioISRepeat.setChecked(true);
        }else {
            radioNORepeat.setChecked(true);
        }

        if (isIgnore){
            radioIgnore.setChecked(true);
            radioISRepeat.setEnabled(true);
            radioNORepeat.setEnabled(true);
        }
        else {
            radioNoIgnore.setChecked(true);
            radioISRepeat.setEnabled(false);
            radioNORepeat.setEnabled(false);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
    }

}
