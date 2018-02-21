package com.example.r30_a.patternlock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.r30_a.patternlock.Controller.OnPatternChangeListener;
import com.example.r30_a.patternlock.Controller.PatternHelper;
import com.example.r30_a.patternlock.Controller.PatternIndicatorView;
import com.example.r30_a.patternlock.Controller.PatternLockView;
import com.example.r30_a.patternlock.Controller.ResultState;

import java.util.List;

public class PatternLockSettingActivity extends AppCompatActivity {

    SharedPreferences sf ;

    private PatternLockView patternLockView;
    private PatternIndicatorView patternIndicatorView;
    TextView txvlock;
    PatternHelper patternHelper;
    public static String savepwd;

    //任何activity要呼叫此頁的時候就可以呼叫此方法
    public static void startAction(Context context){
        Intent intent = new Intent(context, PatternLockSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock_setting);
        sf = getSharedPreferences(PatternHelper.key,MODE_PRIVATE);

        this.patternLockView = (PatternLockView)findViewById(R.id.CellBean);
        this.patternIndicatorView = (PatternIndicatorView)findViewById(R.id.indicatorView);
        txvlock = (TextView)findViewById(R.id.txvlock);

        //監聽圖形鎖盤的一舉一動，小盤與此連動
        patternLockView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(PatternLockView view) {
                patternIndicatorView.updateState(null, ResultState.OK);
            }
            @Override
            public void onChange(PatternLockView view, List<Integer> hitList) {
                patternIndicatorView.updateState(hitList,ResultState.OK);
            }
            @Override
            public void onComplete(PatternLockView view, List<Integer> okList) {

                ResultState resultState = isPatternOK(okList) ?
                        ResultState.OK : ResultState.ERROR;
                view.setResultState(resultState);
                patternIndicatorView.updateState(okList,resultState);
                updateMsg();
                sf.edit().putString(PatternHelper.key,savepwd).commit();
            }
            @Override
            public void onClear(PatternLockView view) {
                finishIfNeed();
            }
        });
        txvlock.setText("開始繪製解鎖圖案");
        patternHelper = new PatternHelper();

    }

    private boolean isPatternOK(List<Integer>okList){
        this.patternHelper.forsetting(okList);
        return this.patternHelper.isOK();
    }
    private void updateMsg(){
        this.txvlock.setText(this.patternHelper.getMessage());
        this.txvlock.setTextColor(this.patternHelper.isOK()?
                getResources().getColor(R.color.colorPrimary):
                getResources().getColor(R.color.colorAccent));
    }

    private void finishIfNeed(){
        if(this.patternHelper.isFinish()){
            finish();
        }

    }

}
