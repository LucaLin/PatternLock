package com.example.r30_a.testlayout.cellBean;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.r30_a.testlayout.R;

public class CellSettingPageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    RadioButton radio33,radio44,radiohide,radioNOhide, radioShowRepeat,radioNOTshowRepeat,radioIgnore,radioNoIgnore
            ,radioIsRepeat,radioNoRepeat;
    RadioGroup radioGroup, radioGroup2, radioGroup3,radioGroup4,radioGroup5;
    EditText edtRange;
    SharedPreferences sf;
    static int setCell = 1;
    static boolean ishide;
    static boolean isRepeat = false;
    static boolean showrepeat = true;
    static boolean isIgnore = true;
    static int RangeBall;
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
                    isIgnore=true;
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

                //是否能重複選取
                if(radioIsRepeat.isChecked()){
                    isRepeat = true;
                  //  isIgnore = true;
                }else if(radioNoRepeat.isChecked()){
                    isRepeat = false;
                }

                //是否標示重複選取的顏色
                if(radioShowRepeat.isChecked()){
                    showrepeat  = true;

                   // sf.edit().putBoolean("setReapeat", isrepeat).commit();
                }else if (radioNOTshowRepeat.isChecked()){
                    showrepeat = false;
                    //sf.edit().putBoolean("setReapeat", isrepeat).commit();
                }


                //是否可略過圓點
                if(radioIgnore.isChecked()){
                    isIgnore = true;
                    radioShowRepeat.setEnabled(false);
                    radioNOTshowRepeat.setEnabled(false);

                }else if(radioNoIgnore.isChecked()){
                    isIgnore = false;
                    //showrepeat = false;
                    //isRepeat = false;
                    radioShowRepeat.setEnabled(false);
                    radioNOTshowRepeat.setEnabled(false);
                }
                if("".equals(edtRange.getText().toString().trim())){
                    toast.setText("沒有輸入任何選球數喔！請重新輸入");
                    toast.show();
                }
                else {
                RangeBall = Integer.parseInt(edtRange.getText().toString());

                if(RangeBall < 5 || RangeBall > 16 ){
                    toast.setText("選球數需在6~16之間喔！請重新輸入");
                    toast.show();
                }else {

                sf.edit().putInt("setCell",setCell).commit();
                sf.edit().putBoolean("setLine",ishide).commit();
                sf.edit().putBoolean("setReapeat", showrepeat).commit();
                sf.edit().putBoolean("setIgnore",isIgnore).commit();
                sf.edit().putBoolean("isRepeat", isRepeat).commit();
                sf.edit().putInt("Rangeball",RangeBall).commit();

                toast.setText("儲存成功");
                toast.show();
                }
                }
            }
        });
    }

    private void init() {
        sf = getSharedPreferences("setting",MODE_PRIVATE);
        edtRange = (EditText)findViewById(R.id.edtrange);





        btncomfirm = (Button)findViewById(R.id.btncomfirm);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup2 = (RadioGroup)findViewById(R.id.radiogroup2);
        radioGroup3 = (RadioGroup)findViewById(R.id.radiogroup3);
        radioGroup4 = (RadioGroup)findViewById(R.id.radiogroup4);
        radioGroup5 = (RadioGroup)findViewById(R.id.radiogroup5);
        radioGroup5.setOnCheckedChangeListener(this);
        radioGroup4.setOnCheckedChangeListener(this);
        radioGroup3.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        radiohide = (RadioButton)findViewById(R.id.radiohide);
        radioNOhide = (RadioButton)findViewById(R.id.radionohide);
        radio33 = (RadioButton)findViewById(R.id.radio33);
        radio44 = (RadioButton)findViewById(R.id.radio44);
        radioShowRepeat = (RadioButton)findViewById(R.id.radioShowRepeat);
        radioNOTshowRepeat = (RadioButton)findViewById(R.id.radioNotShowRepeat);
        radioIgnore  =(RadioButton)findViewById(R.id.radioIgnore);
        radioNoIgnore = (RadioButton)findViewById(R.id.radioNoIgnore);
        radioIsRepeat = (RadioButton)findViewById(R.id.radioRepeat);
        radioNoRepeat = (RadioButton)findViewById(R.id.radioNotRepeat);


        toast = Toast.makeText(CellSettingPageActivity.this,"",Toast.LENGTH_SHORT);
        }

    @Override//畫面開啟的時候檢查一下先前radiobutton選取的狀況
    protected void onResume() {
        super.onResume();
        setCell = sf.getInt("setCell", 0);
        ishide = sf.getBoolean("setLine", false);
        isRepeat = sf.getBoolean("isRepeat",false);
        showrepeat = sf.getBoolean("setReapeat", true);
        RangeBall = sf.getInt("Rangeball",0);
        edtRange.setText(String.valueOf(RangeBall));
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
        if(isRepeat){
            radioIsRepeat.setChecked(true);
        }else {
            radioNoRepeat.setChecked(true);
        }

        if(showrepeat){
            radioShowRepeat.setChecked(true);
        }else {
            radioNOTshowRepeat.setChecked(true);
        }

        if (isIgnore){
            radioIgnore.setChecked(true);
            radioShowRepeat.setEnabled(true);
            radioNOTshowRepeat.setEnabled(true);
        }
        else {
            radioNoIgnore.setChecked(true);
            radioShowRepeat.setEnabled(false);
            radioNOTshowRepeat.setEnabled(false);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int id) {
        if (id == R.id.radioRepeat){
            radioShowRepeat.setEnabled(true);
            radioNOTshowRepeat.setEnabled(true);
            radioIgnore.setEnabled(false);
            radioNoIgnore.setEnabled(false);
        }else if(id == R.id.radioNotRepeat){
            radioShowRepeat.setEnabled(false);
            radioNOTshowRepeat.setEnabled(false);
            radioIgnore.setEnabled(true);
            radioNoIgnore.setEnabled(true);
        }else if(id == R.id.radio44){
            radioIgnore.setEnabled(false);
            radioNoIgnore.setEnabled(false);
        }



    }

}
