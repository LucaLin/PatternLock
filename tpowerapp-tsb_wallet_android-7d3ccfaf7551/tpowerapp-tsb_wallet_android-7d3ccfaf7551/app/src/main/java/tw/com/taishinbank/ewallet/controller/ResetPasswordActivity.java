package tw.com.taishinbank.ewallet.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;

public class ResetPasswordActivity extends ActivityBase implements OnButtonNextClickedListener {

    public String memNo = null;
    public String verifyID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // 設定上方客製的toolbar與置中的標題
        setCenterTitle(R.string.title_activity_welcome);

        setCurrentPage(ResetPasswordFragment1.PAGE_INDEX);
    }

    private void setCurrentPage(int page) {
        Fragment fragment = null;
        switch (page) {
            // step1:輸入資料
            case ResetPasswordFragment1.PAGE_INDEX:
                fragment = new ResetPasswordFragment1();
                break;

            // step2:email系統預設碼驗證
            case ResetPasswordFragment2.PAGE_INDEX:
                fragment = new ResetPasswordFragment2();
                break;

            // step3:修改密碼
            case ResetPasswordFragment3.PAGE_INDEX:
                fragment = new ResetPasswordFragment3();
                break;

            // step4:完成設定
            case ResetPasswordFragment4.PAGE_INDEX:
                fragment = new ResetPasswordFragment4();
                break;

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // 如果不是第一頁，就搭配動畫切換
            if(page != ResetPasswordFragment1.PAGE_INDEX) {
                ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left);
            }
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
    }

    @Override
    public void onButtonNextClicked(int nextPage) {
        setCurrentPage(nextPage);
    }

    @Override
    public void onBackPressed() {
        // 顯示提示對話框，確認使用者是否要離開註冊流程
        showAlertDialog(getString(R.string.register_back_pressed_warning), android.R.string.ok, android.R.string.cancel,
                // 按下確定
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ResetPasswordActivity.super.onBackPressed();
                    }
                    // 按下取消
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }
}
