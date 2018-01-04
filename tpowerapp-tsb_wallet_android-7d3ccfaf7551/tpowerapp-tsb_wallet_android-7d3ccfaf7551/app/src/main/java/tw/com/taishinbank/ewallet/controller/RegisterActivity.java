package tw.com.taishinbank.ewallet.controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.DiskCache;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;

public class RegisterActivity extends ActivityBase implements OnButtonNextClickedListener {
    private static final String TAG = "RegisterActivity";
    private LinearLayout linearLayoutProgressbar;
    private ImageView imageStep1, imageStep2, imageStep3, imageStep4;
    private TextView textStep1, textStep2, textStep3, textStep4;
    private ProgressBar progress1to2, progress2to3, progress3to4;

    private static int REGISTER_LEVEL_CHECKED;
    private static int REGISTER_LEVEL_CURRENT;
    private static int REGISTER_LEVEL_UNCHECK;
    public String memNo = null;
    public String phoneNumber = null;
    public String email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 設定上方客製的toolbar與置中的標題
        setCenterTitle(R.string.title_activity_welcome);

        Resources resources = getResources();
        REGISTER_LEVEL_UNCHECK = resources.getInteger(R.integer.register_level_uncheck);
        REGISTER_LEVEL_CURRENT = resources.getInteger(R.integer.register_level_current);
        REGISTER_LEVEL_CHECKED = resources.getInteger(R.integer.register_level_checked);

        linearLayoutProgressbar =(LinearLayout) findViewById(R.id.register_progressbar);

        imageStep1 = (ImageView) findViewById(R.id.image_register_step1);
        imageStep2 = (ImageView) findViewById(R.id.image_register_step2);
        imageStep3 = (ImageView) findViewById(R.id.image_register_step3);
        imageStep4 = (ImageView) findViewById(R.id.image_register_step4);

        textStep1 = (TextView) findViewById(R.id.text_register_step1);
        textStep2 = (TextView) findViewById(R.id.text_register_step2);
        textStep3 = (TextView) findViewById(R.id.text_register_step3);
        textStep4 = (TextView) findViewById(R.id.text_register_step4);

        progress1to2 = (ProgressBar) findViewById(R.id.progress_1to2);
        progress2to3 = (ProgressBar) findViewById(R.id.progress_2to3);
        progress3to4 = (ProgressBar) findViewById(R.id.progress_3to4);

        //註冊條約
        linearLayoutProgressbar.setVisibility(View.GONE);
        setCurrentPage(RegisterFragment_treaty.PAGE_INDEX);

        // 先確認是否有讀取外部儲存空間的權限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // 如果已經有權限
        if (!PermissionUtil.needGrantRuntimePermission(this, permissions,
                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
            // 砍光已經有的圖檔目錄
            DiskCache.clear();
        }

        if(PreferenceUtil.needCheckUpdate(this) && NetworkUtil.isConnected(this)){
            // 呼叫確認app版本更新api
            try {
                GeneralHttpUtil.checkAppUpdate(responseListenerCheckUpdate, this, TAG);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // 砍光已經有的圖檔目錄
                DiskCache.clear();
            }
        }
    }

    private void setCurrentPage(int page) {
        Fragment fragment = null;
        switch (page) {
            //step0:註冊條約確認
            case RegisterFragment_treaty.PAGE_INDEX:
                fragment = RegisterFragment_treaty.newInstance(RegisterFragment_treaty.PAGE_INDEX);
                break;

            //step0-2:註冊條約確認2
            case RegisterFragment_treaty.PAGE_INDEX2:
                fragment = RegisterFragment_treaty.newInstance(RegisterFragment_treaty.PAGE_INDEX2);
                break;

            // step1:輸入資料
            case RegisterFragment.PAGE_INDEX:
                linearLayoutProgressbar.setVisibility(View.VISIBLE);
                fragment = new RegisterFragment();
                imageStep1.getDrawable().setLevel(REGISTER_LEVEL_CURRENT);
                textStep1.setSelected(true);
                break;

            // step2-1:手機驗證
            case RegisterFragment2_1.PAGE_INDEX:
                fragment = new RegisterFragment2_1();
                setProgress(progress1to2, 1, 1);
                imageStep1.getDrawable().setLevel(REGISTER_LEVEL_CHECKED);
                imageStep2.getDrawable().setLevel(REGISTER_LEVEL_CURRENT);
                textStep1.setSelected(false);
                textStep2.setSelected(true);
                break;

            // step2-2:電子信箱驗證
            case RegisterFragment2_2.PAGE_INDEX:
                fragment = new RegisterFragment2_2();
                setProgress(progress2to3, 1, 2);
                break;

            // step3-1:設定登入密碼
            case RegisterFragment3_1.PAGE_INDEX:
                fragment = new RegisterFragment3_1();
                setProgress(progress2to3, 2, 2);
                imageStep2.getDrawable().setLevel(REGISTER_LEVEL_CHECKED);
                imageStep3.getDrawable().setLevel(REGISTER_LEVEL_CURRENT);
                textStep2.setSelected(false);
                textStep3.setSelected(true);
                break;

            // step3-2:設定個人資料
            case RegisterFragment3_2.PAGE_INDEX:
                fragment = new RegisterFragment3_2();
                setProgress(progress3to4, 1, 2);
                break;

            // step4:完成設定
            case RegisterFragment4.PAGE_INDEX:
                fragment = new RegisterFragment4();
                setProgress(progress3to4, 2, 2);
                imageStep3.getDrawable().setLevel(REGISTER_LEVEL_CHECKED);
                imageStep4.getDrawable().setLevel(REGISTER_LEVEL_CHECKED);
                textStep3.setSelected(false);
                textStep4.setSelected(true);
                break;

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // 如果不是第一頁，就搭配動畫切換
            if(page != RegisterFragment_treaty.PAGE_INDEX) {
                ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left);
            }
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
    }

    private void setProgress(ProgressBar progressBar, int progress, int max){
        progressBar.setMax(max);
        progressBar.setProgress(progress);
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
                        RegisterActivity.super.onBackPressed();
                    }
                    // 按下取消
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }

    // 確認app更新的api callback
    private ResponseListener responseListenerCheckUpdate = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                AppUpdateInfo appUpdateInfo = GeneralResponseBodyUtil.getAppUpdateInfo(result.getBody());
                showAlertIfNeedUpdate(appUpdateInfo);
                // 如果是Token失效，在此不處理，以免被登出後導到這頁無窮迴圈。
            }else if(returnCode.equals(ResponseResult.RESULT_TOKEN_EXPIRED)){
            // 如果不是共同error
            }else if(!handleCommonError(result, RegisterActivity.this)) {
                // 目前不做事
            }
        }
    };
}
