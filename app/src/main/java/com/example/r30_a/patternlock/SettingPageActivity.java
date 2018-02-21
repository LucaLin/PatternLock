package com.example.r30_a.patternlock;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingPageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    RadioButton radio33,radio44,
            radiohide,radioNOhide,
            radioShowRepeat,radioNOTshowRepeat,
            radioIgnore,radioNoIgnore
            ,radioIsRepeat,radioNoRepeat;
    RadioGroup GroupType, GroupHide, GroupRepeat,GroupShowRepeat,GroupIgnore;
    EditText edtRange;
    SharedPreferences sf;
    public static int setCell = 33;
    public static boolean ishide = false;
    public static boolean isRepeat = false;
    public static boolean showrepeat = true;
    public static boolean isIgnore = true;
    public static int RangeBall = 6;
    Button btncomfirm;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        init();

        btncomfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //選取要哪一種尺寸的球盤
                if(radio33.isChecked()){
                    setCell =33;
                }else if(radio44.isChecked()){
                    setCell =44;
                }
                //選取要不要顯示線條
                if(radiohide.isChecked()){
                    ishide = true;
                }else if(radioNOhide.isChecked()){
                    ishide = false;
                }
                //是否能重複選取
                if(radioIsRepeat.isChecked()){
                    isRepeat = true;
                }else if(radioNoRepeat.isChecked()){
                    isRepeat = false;
                }
                //是否標示重複選取的顏色
                if(radioShowRepeat.isChecked()){
                    showrepeat  = true;
                }else if (radioNOTshowRepeat.isChecked()){
                    showrepeat = false;
                }
                //是否可略過圓點
                if(radioIgnore.isChecked()){
                    isIgnore = true;
                }else if(radioNoIgnore.isChecked()){
                    isIgnore = false;
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

                        sf.edit().putInt("setCell",setCell).apply();
                        sf.edit().putBoolean("setLine",ishide).apply();
                        sf.edit().putBoolean("setReapeat", showrepeat).apply();
                        sf.edit().putBoolean("setIgnore",isIgnore).apply();
                        sf.edit().putBoolean("isRepeat", isRepeat).apply();
                        sf.edit().putInt("Rangeball",RangeBall).apply();

                        toast.setText("儲存成功");toast.show();
                    }
                }
            }
        });
    }
    private void init() {
        sf = getSharedPreferences("setting",MODE_PRIVATE);
        edtRange = (EditText)findViewById(R.id.edtrange);

        btncomfirm = (Button)findViewById(R.id.btncomfirm);
        GroupType = (RadioGroup)findViewById(R.id.GroupType);
        GroupHide = (RadioGroup)findViewById(R.id.GroupHide);
        GroupRepeat = (RadioGroup)findViewById(R.id.GroupRepeat);
        GroupShowRepeat = (RadioGroup)findViewById(R.id.GroupShowRepeat);
        GroupIgnore = (RadioGroup)findViewById(R.id.GroupIgnore);
        GroupType.setOnCheckedChangeListener(this);
        GroupHide.setOnCheckedChangeListener(this);
        GroupRepeat.setOnCheckedChangeListener(this);
        GroupShowRepeat.setOnCheckedChangeListener(this);
        GroupIgnore.setOnCheckedChangeListener(this);
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

        toast = Toast.makeText(SettingPageActivity.this,"",Toast.LENGTH_SHORT);
    }

    @Override//畫面開啟的時候檢查一下先前radiobutton選取的狀況
    protected void onResume() {
        super.onResume();
        setCell = sf.getInt("setCell", 0);
        ishide = sf.getBoolean("setLine", false);
        isRepeat = sf.getBoolean("isRepeat",false);
        showrepeat = sf.getBoolean("setReapeat", true);
        RangeBall = sf.getInt("Rangeball",6);
        edtRange.setText(String.valueOf(RangeBall));

        if (sf.getInt("setCell", 33) == 33) {
            radio33.setChecked(true);
        } else if (sf.getInt("setCell", 33) == 44) {
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

        }else if(id == R.id.radioNotRepeat){
            radioShowRepeat.setEnabled(false);
            radioNOTshowRepeat.setEnabled(false);
            radioNOTshowRepeat.setChecked(true);
            radioIgnore.setEnabled(true);
            radioNoIgnore.setEnabled(true);
        }

    }
}
