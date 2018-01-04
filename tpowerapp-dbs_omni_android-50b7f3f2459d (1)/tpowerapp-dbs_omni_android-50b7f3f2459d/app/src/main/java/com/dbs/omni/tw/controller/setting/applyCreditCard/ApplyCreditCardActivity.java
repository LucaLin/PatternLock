package com.dbs.omni.tw.controller.setting.applyCreditCard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.controller.setting.editProfile.EditProfileActivity;
import com.dbs.omni.tw.controller.setting.editProfile.changeUserData.ChangeUserDataActivity;
import com.dbs.omni.tw.element.CreditCardItemView;
import com.dbs.omni.tw.model.ShowTextData;
import com.dbs.omni.tw.model.setting.ApplyCreditCardData;

import java.util.ArrayList;

public class ApplyCreditCardActivity extends ActivityBase {

    private static final String TAG = "ApplyCreditCardActivity";
    public ArrayList<String> mSelectCardList = new ArrayList<>();
    public ArrayList<ApplyCreditCardData> mCardList;
    public ArrayList<CreditCardItemView> mCardViewList;
//    private String titleArray[];
//    private String contentArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_credit_card);

        setCenterTitle(R.string.personal_service_list_apply_credit_card);

        ChooseCreditCardFragment chooseCreditCardFragment = new ChooseCreditCardFragment();

        chooseCreditCardFragment.setOnEventListener(new ChooseCreditCardFragment.OnEventListener() {
            @Override
            public void OnNextEvent() {
                gotoConfirmPage();
            }
        });

        goToPage(chooseCreditCardFragment , false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        changeBackBarAction();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        changeBackBarAction();
    }

    //前往頁面
    private void goToPage(Fragment fragment , boolean isAddBack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.applyCreditCardFrameLayout, fragment);

        if(isAddBack == true){
            ft.addToBackStack(TAG);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }

        ft.commit();
    }

    //前往辦卡確認頁面
    private void gotoConfirmPage() {
//        // 修改 back按鈕圖示 第二頁無法判斷是否為第二頁 故只能這樣寫
//        changeHeadBackClose(false);

        CreditCardConfirmFragment creditCardConfirmFragment = CreditCardConfirmFragment.newInstance(mSelectCardList, mCardList);

        //前往完成頁面
        creditCardConfirmFragment.setOnEventListener(new CreditCardConfirmFragment.OnEventListener() {
            @Override
            public void OnNextEvent() {
                goToResultPass();
            }
        });

        goToPage(creditCardConfirmFragment , true);
    }

    private void goToResultPass() {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ResultPassFragment fragment = ResultPassFragment.newInstance(
                getString(R.string.result_payment_textup_pass),
                getString(R.string.result_personal_service_apply_credit_card_textdown_pass),
                getString(R.string.finished),
                getMockData(),
                ResultPassFragment.IS_APPLY_CREDIT_CARD);

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                goToHomePage();
            }
        });

        fragment.setOnModifyListener(new ResultPassFragment.OnModifyListener() {
            @Override
            public void OnModifyPhone() {
                goToPhoneModify();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.applyCreditCardFrameLayout, fragment);
        ft.commit();
    }

    private void goToResultFail() {
        // Clear all previous pages
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ResultFailFragment fragment = ResultFailFragment.newInstance(
                getString(R.string.result_personal_service_apply_credit_card_textup),
                getString(R.string.result_personal_service_apply_credit_card_textdown_fail),
                "錯誤訊息錯誤訊息錯誤訊息");

        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                goToHomePage();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.replace(R.id.applyCreditCardFrameLayout, fragment);
        ft.commit();
    }

    //結束結果頁後前往首頁
    private void goToHomePage() {
        finish();
    }


    private void goToPhoneModify() {
        Intent intent = new Intent();
        intent.setClass(ApplyCreditCardActivity.this , ChangeUserDataActivity.class);
        intent.putExtra(EditProfileActivity.FUNCTION＿NAME , R.string.personal_service_list_phone_modify);
        startActivity(intent);
        //由於不能允許回到結果頁 , 所以要下finish()
        finish();
    }


    //region Mock
    private ArrayList<ShowTextData> getMockData() {
        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.result_personal_service_apply_credit_card_name), "AAAAA卡\nBBBB卡\nCCCC卡\nDDDD卡\n"));
        list.add(new ShowTextData(getString(R.string.result_personal_service_phone_number), "0911111111"));

        return list;
    }

}
