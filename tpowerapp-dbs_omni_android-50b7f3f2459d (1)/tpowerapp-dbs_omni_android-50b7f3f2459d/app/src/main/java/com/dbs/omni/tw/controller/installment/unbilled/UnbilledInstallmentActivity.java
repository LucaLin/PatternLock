package com.dbs.omni.tw.controller.installment.unbilled;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.result.ConfirmFragment;
import com.dbs.omni.tw.controller.result.ResultFailFragment;
import com.dbs.omni.tw.controller.result.ResultPassFragment;
import com.dbs.omni.tw.model.ShowTextData;

import java.util.ArrayList;

public class UnbilledInstallmentActivity extends ActivityBase {

    private static final String TAG = "UnbilledInstallmentActivity";
    private boolean isEndPage = false;
    private ArrayList<String> listDataFromChooseFragment;

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
        setContentView(R.layout.activity_unbilled_installment);

        setCenterTitleForCloseBar(R.string.installment_unbilled_main_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        UnbilledInstallmentListFragment unbilledInstallmentListFragment = new UnbilledInstallmentListFragment();

        unbilledInstallmentListFragment.setOnEventListener(new UnbilledInstallmentListFragment.OnEventListener() {
            @Override
            //前往確認頁面
            public void OnNextEvent() {
                // 修改 back按鈕圖示 第二頁無法判斷是否為第二頁 故只能這樣寫
                changeHeadBackClose(false);
                goToChoosePage();
            }
        });

        goToPage(unbilledInstallmentListFragment, false);
    }

    //前往頁面
    private void goToPage(Fragment fragment , boolean isAddBack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);

        if(isAddBack == true){
            ft.addToBackStack(TAG);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }

        ft.commit();
    }

    //前往分期選擇頁面
    private void goToChoosePage() {
        UnbilledInstallmentChooseFragment unbilledinstallmentChooseFragment = new UnbilledInstallmentChooseFragment();

        unbilledinstallmentChooseFragment.setOnEventListener(new UnbilledInstallmentChooseFragment.OnEventListener() {
            @Override
            //前往確認頁面
            public void OnNextEvent(ArrayList<String> list) {
                gotoConfirmPage(TAG , list);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, unbilledinstallmentChooseFragment);
        ft.addToBackStack(TAG);
        ft.commit();
    }


    //前往確認頁
    private void gotoConfirmPage(final String TAG, ArrayList<String> list){
        listDataFromChooseFragment = list;

        ConfirmFragment fragment = new ConfirmFragment();

        fragment = ConfirmFragment.newInstance(
                getString(R.string.installment_amount) + listDataFromChooseFragment.get(0),
                getString(R.string.installment_number_of_periods) + listDataFromChooseFragment.get(1),
                getMockData());

        fragment.setOnConfirmListener(new ConfirmFragment.OnConfirmListener() {
            @Override
            public void OnNext() {
                geToResultPass(TAG);
            }

//            @Override
//            public void OnFail() {
//                geToResultFail();
//            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);

        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment, ConfirmFragment.TAG);
        ft.commit();
    }

    private void geToResultPass(String TAG) {

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isEndPage = true;

        ResultPassFragment fragment = ResultPassFragment.newInstance(
                getString(R.string.installment_amount) + listDataFromChooseFragment.get(0),
                getString(R.string.installment_number_of_periods) + listDataFromChooseFragment.get(1),
                getString(R.string.finished),
                getMockData());

        fragment.setOnResultPassListener(new ResultPassFragment.OnResultPassListener() {
            @Override
            public void OnEnd() {
                finish();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    private void geToResultFail() {
        // Clear all previous pages
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        isEndPage = true;

        ResultFailFragment fragment = ResultFailFragment.newInstance(
                "分期",
                "未成功", "分期失敗");

        fragment.setOnResultFailListener(new ResultFailFragment.OnResultFailListener() {
            @Override
            public void OnFail() {
                finish();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);

        ft.replace(R.id.fragment_content, fragment);
        ft.commit();

    }

    //region Mock
    private ArrayList<ShowTextData> getMockData() {
        ArrayList<ShowTextData> list = new ArrayList<>();

        list.add(new ShowTextData(getString(R.string.installment_annual_interest_rate), listDataFromChooseFragment.get(2)));
        list.add(new ShowTextData(getString(R.string.installment_amount_per_period), listDataFromChooseFragment.get(3)));

        return list;
    }
    //endregion
}
