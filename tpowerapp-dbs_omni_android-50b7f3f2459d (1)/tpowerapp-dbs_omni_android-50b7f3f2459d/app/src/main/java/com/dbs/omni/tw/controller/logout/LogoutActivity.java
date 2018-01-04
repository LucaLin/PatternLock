package com.dbs.omni.tw.controller.logout;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.login.LoginActivity;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.HttpUtilBase;
import com.dbs.omni.tw.util.http.mode.register.LogoutDate;

import java.util.ArrayList;

public class LogoutActivity extends ActivityBase {

    public static final String EXTRA_DATA = "extra_data";

    private Boolean isEndPage = true;
    private LogoutDate mLogoutDate;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        changeBackBarAction();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isEndPage) {
            return;
        }

        super.onBackPressed();

        changeBackBarAction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        setCenterTitleForCloseBar(R.string.logout);

        if(getIntent().hasExtra(EXTRA_DATA)) {
            mLogoutDate = getIntent().getParcelableExtra(EXTRA_DATA);
        }

        showLogoutPage();
    }

    private void showLogoutPage(){
        ArrayList<ShowTextData> showTextDatas;
//        if(GlobalConst.UseLocalMock) {
//            showTextDatas = getMockData();
//        } else {
            showTextDatas = toShowDataList();
//        }


        ResultPassFragment fragment = ResultPassFragment.newInstance(
                getString(R.string.logout_text_up),
                getString(R.string.logout_text_down),
                getString(R.string.login),
                showTextDatas,
                ResultPassFragment.IS_LOG_OUT);

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                gotoLoginPage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

    }

    //region Go to pager function

    private void gotoLoginPage() {
        PreferenceUtil.setIsLogin(this, false);
        HttpUtilBase.sessionID = "";
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

// endregion

    //region 整理成顯示的資料清單
    private ArrayList<ShowTextData> toShowDataList() {
        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.login_time), FormatUtil.toTimeFormattedForChinese(this, mLogoutDate.getLoginTime())));
        list.add(new ShowTextData(getString(R.string.logout_time), FormatUtil.toTimeFormattedForChinese(this, mLogoutDate.getLogoutTime())));
        list.add(new ShowTextData(getString(R.string.using_time), FormatUtil.toTimeFormattedForChinese(this, mLogoutDate.getStayTime(), true)));

        return list;
    }
    //endregion


    //region Mock
    private ArrayList<ShowTextData> getMockData() {
        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.login_time), "2017年2月14日, 上午9:00"));
        list.add(new ShowTextData(getString(R.string.logout_time), "2017年2月14日, 上午9:35"));
        list.add(new ShowTextData(getString(R.string.using_time), "0小時35分"));

        return list;
    }
    //endregion
}
