package tw.com.taishinbank.ewallet.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.async.DownloadContactsAsyncTask;
import tw.com.taishinbank.ewallet.dbhelper.DatabaseHelper;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;

public class AddingContactActivity extends ActivityBase implements View.OnClickListener, DownloadContactsAsyncTask.OnDownloadStartFinishListener {

    private static final String TAG = "AddingContactActivity";

//    private SearchView searchView;
    private EditText editSearchText;
    private ImageButton buttonClearText;
    private Button buttonInvite;
    private TextView textSVAccount;
    private TextView textNotWallet;
    private TextView textLine1;
    private TextView textLine2;
    private LinearLayout layoutResult;
    private String searchedPhone;
    private boolean isWallet;
    private boolean isSV;
    private int currentInviteButtonTextResId;
    private ImageLoader imageLoader;
    private ImageView imagePhoto;

    private String memNO;

    private boolean isFromContact = false;

    public static final String EXTRA_SEARCH_PHONE = "EXTRA_SEARCH_NUMBER";
    private static final String PHONE_NUMBER_PREFIX = "09";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_contact);

        // 設定置中的標題與返回鈕
        setCenterTitle(R.string.invite_friends);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editSearchText = (EditText) findViewById(R.id.edit_search_text);
        buttonClearText = (ImageButton) findViewById(R.id.button_clear_text);

        buttonInvite = (Button) findViewById(R.id.button_invite);
        buttonInvite.setOnClickListener(this);
        ImageButton buttonSearch = (ImageButton) findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(this);

        textSVAccount = (TextView) findViewById(R.id.text_sv_account);
        textNotWallet = (TextView) findViewById(R.id.text_not_wallet);
        layoutResult = (LinearLayout) findViewById(R.id.layout_result);

        textLine1 = (TextView) findViewById(android.R.id.text1);
        textLine2 = (TextView) findViewById(android.R.id.text2);

        imagePhoto = (ImageView) findViewById(R.id.image_photo);

        imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.large_photo_size));

        if(getIntent() != null){
            searchedPhone = getIntent().getStringExtra(EXTRA_SEARCH_PHONE);
            if(!TextUtils.isEmpty(searchedPhone)) {
                isFromContact = true;
                if(searchedPhone.startsWith(PHONE_NUMBER_PREFIX) && searchedPhone.length() == getResources().getInteger(R.integer.cellphone_maxlength)){
                    searchedPhone = searchedPhone.substring(PHONE_NUMBER_PREFIX.length());
                }
                editSearchText.setText(searchedPhone);
                doSearch(searchedPhone);
            }
        }

        buttonClearText.setOnClickListener(this);
        editSearchText.addTextChangedListener(searchTextChangeListener);
        editSearchText.setOnEditorActionListener(editorActionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonClear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
        hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_search) {
            // 呼叫web service api查詢帳戶資訊
            doSearch(editSearchText.getText().toString());
        }else if(v.getId() == R.id.button_clear_text){
            editSearchText.setText("");
        }else if(v.getId() == R.id.button_invite){
            /* case2.是錢包會員，亦是儲值會員 || case1.是錢包會員，不是儲值會員：
               b.不在清單內：call api WLT030102 更新通訊錄/加入好友 (addFriends)
             */
            if(currentInviteButtonTextResId == R.string.add_friend){
                // 如果沒有網路連線，顯示提示對話框
                if(!NetworkUtil.isConnected(this)){
                    showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return ;
                }
                // 因為已為好友會disable按鈕，所以在這僅處理 b.不在local清單內的
                ArrayList<LocalContact> listToAdd = new ArrayList<>();
                LocalContact contactToAdd = new LocalContact();
                contactToAdd.setPhoneNumber(searchedPhone);
                listToAdd.add(contactToAdd);
                try {
                    GeneralHttpUtil.addFriends(listToAdd, responseListenerAddFriend, this, TAG);
                    showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO
                }
            }else{
                Uri smsUri = Uri.parse("smsto:" + searchedPhone);
                Intent smsIntent = new Intent (Intent.ACTION_SENDTO, smsUri);
                /* case1.是錢包會員，不是儲值會員：按下去則切到 簡訊發送，預設內容「快來成為儲值會員」*/
                if(currentInviteButtonTextResId == R.string.invite_to_join_sv){
                    //smsIntent.putExtra("sms_body", getString(R.string.sms_msg_join_sv));
                    //startActivity(smsIntent);
                    EventAnalyticsUtil.uploadHitRecordEvent(this, HitRecord.HitEvent.INVITE_JOIN_SV, HitRecord.HitType.PUSH);
                    sendInviteJoinSV();


                /* case3.不是錢包會員：按下去則切到 簡訊發送，預設內容「快來使用LetsPay行動錢包」*/
                } else if (currentInviteButtonTextResId == R.string.invite_to_join_wallet){
                    if(isFromContact) {
                        EventAnalyticsUtil.uploadHitRecordEvent(this, HitRecord.HitEvent.INVITE_JOIN_WALLET, HitRecord.HitType.CONTACTS);
                    } else {
                        EventAnalyticsUtil.uploadHitRecordEvent(this, HitRecord.HitEvent.INVITE_JOIN_WALLET, HitRecord.HitType.PHONENUMBER);
                    }

                    String inviteMsg = String.format(getString(R.string.invite_msg_join_wallet), PreferenceUtil.getNickname(this));
                    smsIntent.putExtra("sms_body", inviteMsg);
                    startActivity(smsIntent);
                }
            }
        }
    }

    private void updateButtonClear(){
        if(TextUtils.isEmpty(editSearchText.getText())){
            buttonClearText.setVisibility(View.GONE);
        }else{
            buttonClearText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 呼叫webservice做手機號碼認證
     * @param query 手機號碼後8碼
     */
    private void doSearch(String query){
        if(TextUtils.isEmpty(query) || query.length() != getResources().getInteger(R.integer.add_friend_cellphone_maxlength)){
            Toast.makeText(this, R.string.input_valid_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(this)){
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        hideKeyboard();
        // 隱藏搜尋結果
        layoutResult.setVisibility(View.INVISIBLE);
        searchedPhone = PHONE_NUMBER_PREFIX + query;

        // 呼叫api做手機號碼認證
        try {
            RedEnvelopeHttpUtil.getAccountCheckByPhone(searchedPhone, null, null, responseListener, this, TAG);
            showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    // 手機號碼驗證的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得會員序號
                memNO = GeneralResponseBodyUtil.getMemNo(result.getBody());
                searchedPhone = GeneralResponseBodyUtil.getPhone(result.getBody());
                String formattedPhone = FormatUtil.toCellPhoneNumberFormat(searchedPhone);
                String name = GeneralResponseBodyUtil.getName(result.getBody());
                // 讀取頭像
                imageLoader.loadImage(memNO, imagePhoto);

                // 取得是否為錢包或儲值會員
                isWallet = GeneralResponseBodyUtil.getWalletFlag(result.getBody());
                isSV = GeneralResponseBodyUtil.getSVFlag(result.getBody());

                // 跟iOS同步，直接用api回來的名字跟電話
                textLine1.setText(name);
                textLine2.setText(formattedPhone);

                showFriendInfoView();

            // 如果是查無此用戶，邀請使用錢包
            }else if(returnCode.equals(ResponseResult.RESULT_MEMBER_NOT_FOUND)) {
                // 清空頭像
                imagePhoto.setImageBitmap(null);
                // 當作非錢包也非儲值的case
                isWallet = false;
                isSV = false;

                // 清空名字，設定電話
                textLine1.setText("");
                String formattedPhone = FormatUtil.toCellPhoneNumberFormat(searchedPhone);
                textLine2.setText(formattedPhone);

                showFriendInfoView();

            }else{
                // 執行預設的錯誤處理
                handleResponseError(result, AddingContactActivity.this);
            }
        }
    };

    private void sendInviteJoinSV() {
        if(memNO == null)
            return;

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(this)){
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        try {
            ArrayList<String> memNOs = new ArrayList<>();
            memNOs.add(memNO);
            GeneralHttpUtil.sendInvitePush("3", memNOs, responseListener_sendInvite, this, TAG);
            showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private ResponseListener responseListener_sendInvite = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                Toast.makeText(AddingContactActivity.this, getString(R.string.send_invite_end), Toast.LENGTH_SHORT).show();
            } else {
                // 執行預設的錯誤處理
                handleResponseError(result, AddingContactActivity.this);
            }
        }
    };
    /**
     *
     */
    private void showFriendInfoView(){
        layoutResult.setVisibility(View.VISIBLE);
        // 用電話號碼搜尋本機資料庫
        DatabaseHelper dbHelper = new DatabaseHelper(AddingContactActivity.this);
        ArrayList<LocalContact> searchedContacts = dbHelper.searchByPhone(searchedPhone);
        // 初始為enable
        buttonInvite.setEnabled(true);

                /* case2.是錢包會員，亦是儲值會員：
                   檢查local 好友列表，
                 */
        if(isSV && isWallet){
            textSVAccount.setVisibility(View.VISIBLE);
            textNotWallet.setVisibility(View.INVISIBLE);

            // a.若已在清單內，顯示「已為好友清單」，按鈕無作用
            if(searchedContacts != null && searchedContacts.size() > 0){
                buttonInvite.setEnabled(false);
                currentInviteButtonTextResId = R.string.friend_already;

            // b.不在清單內：顯示加入錢包好友
            }else {
                currentInviteButtonTextResId = R.string.add_friend;
            }
        }else{
            textSVAccount.setVisibility(View.INVISIBLE);
            textNotWallet.setVisibility(View.VISIBLE);

            /* case1.是錢包會員，不是儲值會員：
               檢查local 好友列表
             */
            if(isWallet){
                textNotWallet.setText(R.string.not_sv_account);
                // a.若已在清單內：按鈕顯示「邀請成為儲值會員」，按下去則切到 簡訊發送，預設內容「快來成為儲值會員」
                if(searchedContacts != null && searchedContacts.size() > 0) {
                    currentInviteButtonTextResId = R.string.invite_to_join_sv;

                    // b.不在清單內：按鈕顯示「加入錢包好友」，按下去call api WLT030102 更新通訊錄/加入好友 (addFriends)
                }else{
                    currentInviteButtonTextResId = R.string.add_friend;
                }

            /* case3.不是錢包會員：
               按鈕顯示「邀請使用LetsPay行動錢包」
             */
            } else {
                currentInviteButtonTextResId = R.string.invite_to_join_wallet;
                textNotWallet.setText(R.string.not_wallet_account);
            }
        }

        buttonInvite.setText(currentInviteButtonTextResId);
    }

    // 加好友的response listener
    private ResponseListener responseListenerAddFriend = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 再次更新聯絡人資料
                new DownloadContactsAsyncTask(AddingContactActivity.this, AddingContactActivity.this).execute();
            }else{
                // 執行預設的錯誤處理
                handleResponseError(result, AddingContactActivity.this);
            }
        }
    };

    @Override
    public void onDownloadFinished(ResponseResult errorResult) {
        // 不管下載結果是否成功
        // 更新按鈕狀態（因為會重新下載就是加好友成功）

        // 如果原本已經是儲值帳戶會員，顯示已為好友，按鈕disable
        if (isSV) {
            buttonInvite.setEnabled(false);
            currentInviteButtonTextResId = R.string.friend_already;

        // 否則按鈕文字改為邀請加入儲值會員
        }else{
            currentInviteButtonTextResId = R.string.invite_to_join_sv;
        }
        buttonInvite.setText(currentInviteButtonTextResId);
    }

    @Override
    public void onTaskStarted() {
        showProgressLoading();
    }

    @Override
    public void onTaskFinished() {
        dismissProgressLoading();
    }

    private TextWatcher searchTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateButtonClear();
        }
    };

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(editSearchText.getText().toString());
                handled = true;
            }
            return handled;
        }
    };
}
