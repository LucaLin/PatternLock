package tw.com.taishinbank.ewallet.controller.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.LoginActivity;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.controller.creditcard.CreditCardManageActivity;
import tw.com.taishinbank.ewallet.controller.sv.DesignateAccountActivity;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.setting.PushSettingStatus;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.setting.PushData;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.SettingResponseBodyUtil;

public class ApplicationSettedActivity extends ActivityBase implements View.OnClickListener{

    private static final String TAG = "ApplicationSettedActivity";
    private Switch item_push_all, item_push_invoice_tickets, item_push_preferential_event,
            item_push_preferential_tickets, item_push_red_envelope, item_push_sv_account;
    private SVAccountInfo svAccountInfo;
    private List<PushData> pushSettinglist = new ArrayList<>();
    private boolean IsLockAll = false;

    @Override
    @SuppressWarnings("null")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_setted);

        setCenterTitle(R.string.drawer_item_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        View btn_promis_account = setItemContent(R.id.item_promise_account, getString(R.string.promise_account), null);
        btn_promis_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((WalletApplication)getApplication()).needSVLogin()){
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(ApplicationSettedActivity.this, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);

                    // 如果帳戶資訊都有了，開啟選擇紅包類型頁
                }else {
                    Intent intent = new Intent();
                    intent.setClass(ApplicationSettedActivity.this, DesignateAccountActivity.class);
                    intent.putExtra(DesignateAccountActivity.KEY_FROM_PAGE, DesignateAccountActivity.FROM_PROFILE);

                    startActivity(intent);
                }
            }
        });

        View item_sv_account_modify_mima = setItemContent(R.id.item_sv_account_modify_password, getString(R.string.sv_account_modify_password), null);
        item_sv_account_modify_mima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 如果儲值帳戶登入時間沒有過期，也有儲值帳戶資訊，開啟紅包類型選擇頁
                if(!needSVLogin()){
                    Intent intent = new Intent(ApplicationSettedActivity.this, ApplicationSettedSubActivity.class);
                    intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.CHANGE_SV_PASSWORD_PAGE.toString());
                    startActivity(intent);
                }else{
                    // 否則連到儲值帳戶登入頁
                    startActivityForResult(new Intent(ApplicationSettedActivity.this , SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                }
//
//                final Dialog dialog = new Dialog(ApplicationSettedActivity.this, R.style.RedEnvelopeDialog);//指定自定義樣式
//                dialog.setContentView(R.layout.dialog_red_envelope);//指定自定義layout
//                TextView textMessage = (TextView) dialog.findViewById(R.id.text_message);
//                textMessage.setText(R.string.text_change_sv_password);
//
//                //新增自定義按鈕點擊監聽
//                Button btn = (Button)dialog.findViewById(R.id.button_left);
//                btn.setVisibility(View.GONE);
//
//                btn = (Button)dialog.findViewById(R.id.button_right);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                btn.setText(android.R.string.ok);
//
//                // 顯示dialog
//                dialog.show();
            }
        });


        View item_create_remote_credit_card = setItemContent(R.id.item_create_remote_credit_card, getString(R.string.create_remote_credit_card), null);
        item_create_remote_credit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalConst.DISABLE_CREDIT_CARD) {
                    showAlertDialog(getString(R.string.msg_credit_card_is_disabled));
                    return;
                }
                Intent intent = new Intent(ApplicationSettedActivity.this, CreditCardManageActivity.class);
                startActivity(intent);
            }
        });

        View item_credit_card_modify_mima = setItemContent(R.id.item_credit_card_modify_password, getString(R.string.credit_card_modify_password), null);
        item_credit_card_modify_mima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalConst.DISABLE_CREDIT_CARD) {
                    showAlertDialog(getString(R.string.msg_credit_card_is_disabled));
                    return;
                }
                Intent intent = new Intent(ApplicationSettedActivity.this, ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.CHANGE_WALLET_PASSWORD_PAGE.toString());
                startActivity(intent);
            }
        });

        View item_use_provision = setItemContent(R.id.item_use_provision, getString(R.string.use_provision), null);
        item_use_provision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationSettedActivity.this, ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.USE_TREATY_PAGE.toString());
                startActivity(intent);
            }
        });

        View item_logout = setItemContent(R.id.item_logout, getString(R.string.logout), null);
        item_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ApplicationSettedActivity.this)
                        .setMessage(R.string.confirm_to_logout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                PreferenceUtil.manualLogout(ApplicationSettedActivity.this);
                                PreferenceUtil.clearAllPreferences(ApplicationSettedActivity.this);
                                dialog.dismiss();
                                Intent intent = new Intent(ApplicationSettedActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                // Call server logout API....不管結果
                                try {
                                    GeneralHttpUtil.logoutWallet(null, ApplicationSettedActivity.this);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });


        item_push_all = setItemContent_Switch(R.id.item_push_all, getString(R.string.all), null);
        item_push_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                onPushCheckboxClick(v);
            }
        });

        item_push_invoice_tickets = setItemContent_Switch( R.id.item_push_invoice_tickets, getString(R.string.invoice_tickets), null);
        item_push_invoice_tickets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                onPushCheckboxClick(v);
            }
        });

        item_push_preferential_event = setItemContent_Switch(R.id.item_push_preferential_event, getString(R.string.preferential_even_system), null);
        item_push_preferential_event.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                onPushCheckboxClick(v);
            }
        });

        item_push_preferential_tickets = setItemContent_Switch(R.id.item_push_preferential_tickets, getString(R.string.preferential_tickets), null);
        item_push_preferential_tickets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                onPushCheckboxClick(v);
            }
        });


        item_push_red_envelope = setItemContent_Switch(R.id.item_push_red_envelope, getString(R.string.red_envelope), null);
        item_push_red_envelope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                onPushCheckboxClick(v);
            }
        });


        item_push_sv_account = setItemContent_Switch(R.id.item_push_sv_account, getString(R.string.sv_account), null);
        item_push_sv_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                onPushCheckboxClick(v);
            }
        });

//
//        btn_save = (Button) view.findViewById(R.id.btn_save);
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        //Call HTTP Request after View is ready...
        loadSetting();
    }

    private boolean needSVLogin(){
        svAccountInfo = PreferenceUtil.getSVAccountInfo(this);
        return !(svAccountInfo != null && !isSVLoginTimeExpired());
    }

    /**
     * 回傳距離上次登入儲值帳戶是否超過指定時間（10分鐘）
     */
    private boolean isSVLoginTimeExpired(){
        String svLoginTime = PreferenceUtil.getSVLoginTime(this);
        if(!TextUtils.isEmpty(svLoginTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                // 將上次登入時間字串轉成date物件
                Date lastLoginTime = sdf.parse(svLoginTime);

                // 取得現在時間
                Calendar c = Calendar.getInstance();
                // 判斷是否已經超過上次登入時間10分鐘
                c.add(Calendar.MINUTE, -10);
                if(lastLoginTime.after(c.getTime())){
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
   
    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title

    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            saveSetting();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
     //   super.onBackPressed();
        saveSetting();
    }

    
    private View setItemContent(int layoutID, String content, String title)
    {

        View itemView = findViewById(layoutID);
        TextView text_title = (TextView) itemView.findViewById(android.R.id.title);
        text_title.setText(title);
        TextView text_content = (TextView) itemView.findViewById(android.R.id.content);
        if(title == null) {
            text_title.setVisibility(View.GONE);
            text_content.setGravity(text_title.getGravity());
        }
        else
            text_title.setText(content);
        if(content == null)
            text_content.setVisibility(View.GONE);
        else
            text_content.setText(content);

//        ImageView button = (ImageView) itemView.findViewById(android.R.id.button1);
        return itemView;
    }

    private Switch setItemContent_Switch(int layoutID, String content, String title)
    {

        View itemView = findViewById(layoutID);
        TextView text_title = (TextView) itemView.findViewById(android.R.id.title);
        text_title.setText(title);
        TextView text_content = (TextView) itemView.findViewById(android.R.id.content);
        if(title == null) {
            text_title.setVisibility(View.GONE);
            text_content.setGravity(text_title.getGravity());
        }
        else
            text_title.setText(content);
        if(content == null)
            text_content.setVisibility(View.GONE);
        else
            text_content.setText(content);

        Switch switchbox = (Switch) itemView.findViewById(R.id.switchbox);
        return switchbox;
    }

    // ----
    // Http
    // ----
    public void saveSetting() {
        hasChangeCheck();
      //  GeneralHttpUtil.saveSetting();



    }

    private void hasChangeCheck() {
        final ArrayList<PushData> changePushSettingList = new ArrayList<>();
        boolean isChangeSetting = false;

        for (PushData pushData: pushSettinglist) {
            boolean isOpne = false;

            PushSettingStatus status =  PushSettingStatus.CodeToEnum(pushData.getPushType());
            if(pushData.getPsSeq() == null) {
                isOpne = true;
            }

            switch (status) {
                case RED:
                    if(item_push_red_envelope.isChecked() != isOpne) {
                        isChangeSetting = true;
                        changePushSettingList.add(getChangedPushData(item_push_red_envelope, pushData));
                    }
                    else
                        changePushSettingList.add(pushData);
                    break;
                case SVACCOUNT:
                    if(item_push_sv_account.isChecked() != isOpne) {
                        isChangeSetting = true;
                        changePushSettingList.add(getChangedPushData(item_push_sv_account, pushData));
                    }
                    else
                        changePushSettingList.add(pushData);
                    break;
                case PERFER_TICKETS:
                    if(item_push_preferential_tickets.isChecked() != isOpne) {
                        isChangeSetting = true;
                        changePushSettingList.add(getChangedPushData(item_push_preferential_tickets, pushData));
                    }
                    else
                        changePushSettingList.add(pushData);
                    break;
                case SYSTEMINFO:
                    if(item_push_preferential_event.isChecked() != isOpne) {
                        isChangeSetting = true;
                        changePushSettingList.add(getChangedPushData(item_push_preferential_event, pushData));
                    }
                    else
                        changePushSettingList.add(pushData);
                    break;
                case INVOICE_TICKETS:
                    if(item_push_invoice_tickets.isChecked() != isOpne) {
                        isChangeSetting = true;
                        changePushSettingList.add(getChangedPushData(item_push_invoice_tickets, pushData));
                    }
                    else
                        changePushSettingList.add(pushData);
                    break;
            }
        }

        if(isChangeSetting)
        {
            this.showAlertDialog(getString(R.string.text_save_alter), R.string.button_save, R.string.button_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // 如果沒有網路連線，顯示提示對話框
                            if (!NetworkUtil.isConnected(ApplicationSettedActivity.this)) {
                                showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, true);
                            } else {
                                try {
                                    GeneralHttpUtil.savePushSetting(changePushSettingList, responseListener_save, ApplicationSettedActivity.this, TAG);
                                    showProgressLoading();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, false);
        }
        else
            finish();
    }

    private PushData getChangedPushData(Switch item ,PushData orginData)
    {
        PushData changePushData =  new PushData(orginData.getPsSeq(), orginData.getPushType(), orginData.getSwitchFlag());
        if(item.isChecked()) {
           // changePushData.setPsSeq();
            changePushData.setSwitchFlag("1");
        }
        else {
          //  changePushData.setPsSeq(null);
            changePushData.setSwitchFlag("0");
        }

        return changePushData;
    }



    private void loadSetting() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得收到的紅包
            try {
                GeneralHttpUtil.loadPushSetting(responseListener,this, TAG);
                this.showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

   //     GeneralHttpUtil.loadSetting();
    }

    private ResponseListener responseListener= new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                pushSettinglist = SettingResponseBodyUtil.parsePushSettingList(result.getBody());

                for (PushData pushData: pushSettinglist) {
                    boolean isOpne = false;
                    PushSettingStatus status =  PushSettingStatus.CodeToEnum(pushData.getPushType());
                    if(pushData.getPsSeq() == null) {
                        isOpne = true;
                    }

                    switch (status) {
                        case RED:
                            item_push_red_envelope.setChecked(isOpne);
                            break;
                        case SVACCOUNT:
                            item_push_sv_account.setChecked(isOpne);
                            break;
                        case PERFER_TICKETS:
                            item_push_preferential_tickets.setChecked(isOpne);
                            break;
                        case SYSTEMINFO:
                            item_push_preferential_event.setChecked(isOpne);
                            break;
                        case INVOICE_TICKETS:
                            item_push_invoice_tickets.setChecked(isOpne);
                            break;


                    }
                }
                checkAllPush();
                //Peter@2016/03/30:修正推播的「全部」，必須按第二次才有反應。
                IsLockAll = false;

            } else {
                // 執行預設的錯誤處理
                handleResponseError(result, ApplicationSettedActivity.this);
            }
        }
    };

    private ResponseListener responseListener_save = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                //pushSettinglist = SettingResponseBodyUtil.parsePushSettingList(result.getBody());
                showAlertDialog(getString(R.string.text_saved_complate_alter), R.string.button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        getFragmentManager().popBackStack();
                        finish();
                    }
                }, false);


            } else {
                // 如果不是共同error
                if(!handleCommonError(result, ApplicationSettedActivity.this)){
                    showAlertDialog(result.getReturnMessage(), R.string.button_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            getFragmentManager().popBackStack();
                            finish();
                        }
                    }, false);
                }
            }
        }
    };

    // ----
    // private
    // ----
    private void onPushCheckboxClick(CompoundButton view) {
//        item_push_all
//        item_push_invoice_tickets
//        item_push_preferential_event
//        item_push_preferential_tickets
//        item_push_red_envelope
//        item_push_sv_account
        if (((View) view.getParent()).getId() == R.id.item_push_all && !IsLockAll) {
            boolean isChecked = item_push_all.isChecked();
            item_push_invoice_tickets.setChecked(isChecked);
            item_push_preferential_event.setChecked(isChecked);
            item_push_preferential_tickets.setChecked(isChecked);
            item_push_red_envelope.setChecked(isChecked);
            item_push_sv_account.setChecked(isChecked);
        }
        else
        {
            checkAllPush();
        }
        IsLockAll = false;
        //TODO Call server API

    }

    private void checkAllPush()
    {
        IsLockAll = true;
        if(item_push_invoice_tickets.isChecked() &&
            item_push_preferential_event.isChecked() &&
            item_push_preferential_tickets.isChecked() &&
            item_push_red_envelope.isChecked() &&
            item_push_sv_account.isChecked())
        {
            item_push_all.setChecked(true);
        }
        else
        {
            item_push_all.setChecked(false);
        }
    }

    //
    //
    //

}
