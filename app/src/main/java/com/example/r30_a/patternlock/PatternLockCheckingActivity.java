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

public class PatternLockCheckingActivity extends AppCompatActivity {

    private PatternLockView patternLockView;
    private PatternIndicatorView patternIndicatorView;
    private TextView txvmsg;
    private PatternHelper patternHelper;
    SharedPreferences sf;
    public static String checkpwd;

    //任何activity要呼叫此頁的時候就可以呼叫此方法
    public static void startAction(Context context ){
        Intent intent = new Intent(context, PatternLockCheckingActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock_checking);
        sf = getSharedPreferences(PatternHelper.key,MODE_PRIVATE);

        patternIndicatorView = (PatternIndicatorView) findViewById(R.id.checkindicatorView);
        patternLockView = (PatternLockView) findViewById(R.id.checkpatternlockview);
        this.txvmsg = (TextView) findViewById(R.id.txvcheckmsg);
        checkpwd = sf.getString(PatternHelper.key,"");

        //監聽圖形鎖盤的一舉一動，小盤與此連動
        this.patternLockView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(PatternLockView view) {
                patternIndicatorView.updateState(null, ResultState.OK);
            }
            @Override
            public void onChange(PatternLockView view, List<Integer> hitList) {
                patternIndicatorView.updateState(hitList, ResultState.OK);
            }
            @Override
            public void onComplete(PatternLockView view, List<Integer> hitList) {
                //繪製完成時的檢核動作，此步最為重要，請小心設定
                ResultState resultState = isPatternOk(hitList) ?
                        ResultState.OK : ResultState.ERROR;
                view.setResultState(resultState);
                patternIndicatorView.updateState(hitList, resultState);
                updateMsg();
            }
            @Override
            public void onClear(PatternLockView view) {
                finishIfNeeded();
            }
        });
        txvmsg.setText("請繪製解鎖圖案以供辨識");
        this.patternHelper = new PatternHelper();
    }

    //檢查驗證圖形的地方
    private boolean isPatternOk(List<Integer> hitList) {
        this.patternHelper.forChecking(hitList);
        return this.patternHelper.isOK();
    }

    private void updateMsg() {
        this.txvmsg.setText(this.patternHelper.getMessage());
        this.txvmsg.setTextColor(this.patternHelper.isOK() ?
                getResources().getColor(R.color.colorPrimary) :
                getResources().getColor(R.color.colorAccent));
    }

    private void finishIfNeeded() {
        if (this.patternHelper.isFinish()) {
            finish();
        }
    }
    public String getSavepwd(){
        String checkpwd =  sf.getString(PatternHelper.key,"");
        return checkpwd;
    }
}
