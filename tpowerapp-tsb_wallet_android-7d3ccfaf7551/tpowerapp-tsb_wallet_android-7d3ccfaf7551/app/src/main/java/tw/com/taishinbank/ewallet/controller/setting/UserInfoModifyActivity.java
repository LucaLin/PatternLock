package tw.com.taishinbank.ewallet.controller.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;

public class UserInfoModifyActivity extends ActivityBase implements View.OnClickListener, OnButtonNextClickedListener {

    public static final int EMAIL_PAGE_CERTIFICATION = 2;
    public static final int PHONE_PAGE_CERTIFICATION = 3;
    public static final int TEN_MINUTES = 10*60*1000;
    public static final int ONE_SECOND = 1000;

    public String phoneNumber = null;
    public String email = null;
    private ImageButton button_cancel;
    private EditPersonalInfoActivity.ENUM_UPDATE_ITEM update_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_modify);
        button_cancel = (ImageButton) findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(this);
//        buttonConfirm.setEnabled(false);
//        buttonConfirm.setOnClickListener(this);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.drawer_item_settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        update_item = EditPersonalInfoActivity.ENUM_UPDATE_ITEM.valueOf(getIntent().getStringExtra(EditPersonalInfoActivity.EXTRA_CHANGE_ITEM));
        setCurrentPage(update_item);
    }


    private void setCurrentPage(EditPersonalInfoActivity.ENUM_UPDATE_ITEM page) {
        Fragment fragment = null;
        switch (page) {
            case PASSWORD:
                fragment = new ModifyPasswordFragment();
                break;

            case EMAIL:
                fragment = ModifyPhoneOrEmailFragment.newInstance(update_item);
                break;
            case PHONE:
                fragment = ModifyPhoneOrEmailFragment.newInstance(update_item);
                break;

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
    }

    private void setCurrentSubPage(int pageID) {
        Fragment fragment = null;
        switch (pageID) {
            case EMAIL_PAGE_CERTIFICATION:
                fragment = new CertificationEmailFragment();
                break;

            case PHONE_PAGE_CERTIFICATION:
                fragment = new CertificationPhoneFragment();
                break;

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onButtonNextClicked(int nextPage) {
        setCurrentSubPage(nextPage);
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if (viewId == R.id.button_cancel) {
            String title = "";
            String message = "";
            switch (update_item) {
                case PASSWORD:
                    title = String.format(getString(R.string.modify_userinfor_cancel_title), getString(R.string.login_password));
                    message = String.format(getString(R.string.modify_userinfor_cancel_message), getString(R.string.login_password));
                    break;

                case EMAIL:
                    title = String.format(getString(R.string.modify_userinfor_cancel_title), getString(R.string.edit_personal_email_title));
                    message = String.format(getString(R.string.modify_userinfor_cancel_message), getString(R.string.edit_personal_email_title));
                    break;
                case PHONE:
                    title = String.format(getString(R.string.modify_userinfor_cancel_title), getString(R.string.edit_personal_phone_title));
                    message = String.format(getString(R.string.modify_userinfor_cancel_message), getString(R.string.edit_personal_phone_title));
                    break;
            }
            showAlertDialog(title, message, R.string.modify_userinfor_cancel_button_ok, R.string.modify_userinfor_cancel_button_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GoBackPage();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, false);
        }
    }

    private void GoBackPage() {
        Intent intent = new Intent(this, EditPersonalInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
