package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.extra.MyCouponActivity;
import tw.com.taishinbank.ewallet.controller.red.BlessingInputActivity;
import tw.com.taishinbank.ewallet.controller.sv.PaymentActivity;
import tw.com.taishinbank.ewallet.controller.sv.ReceiptActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ContactDetailMenu;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class ContactDetailActivity extends ActivityBase{

    private static final String TAG = "ContactDetailActivity";

    public static final String EXTRA_CONTACT_DATA = "extra_contact_data";
    private LocalContact localContact;

    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // 設定上方客製的toolbar與置中的標題
        setCenterTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        TextView textName = (TextView) findViewById(R.id.text_name);
        TextView textPhone = (TextView) findViewById(R.id.text_phone);
        TextView textSvAccount = (TextView) findViewById(R.id.text_sv_account);

        localContact = getIntent().getParcelableExtra(EXTRA_CONTACT_DATA);

        // 設定頭像
        ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.large_photo_size));
        imageLoader.loadImage(localContact.getMemNO(), imagePhoto);

        // 設定名稱
        textName.setText(localContact.getDisplayName());
        // 設定電話號碼
        textPhone.setText(FormatUtil.toCellPhoneNumberFormat(localContact.getPhoneNumber()));

        ListView listView = (ListView)findViewById(android.R.id.list);
      //  String[] itemNames = getResources().getStringArray(R.array.contact_detail_menu);
        int[] iconResourceIds = {R.drawable.ic_friend_item_red, R.drawable.ic_friend_item_share_cost,
                R.drawable.ic_friend_item_transfer_money, R.drawable.ic_friend_item_ticket_, R.drawable.ic_friend_item_history};


        // 設定列表的資料
        // 是儲值帳戶
        if(localContact.isSVAccount()) {
            textSvAccount.setVisibility(View.VISIBLE);
            for (int i = 0; i < iconResourceIds.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("image", iconResourceIds[i]);
                item.put("text", ContactDetailMenu.CodeToEnum(i).getDescription());

                items.add(item);
            }
        // 非儲值帳戶
        }else{
            textSvAccount.setVisibility(View.INVISIBLE);
            // 只顯示最後兩筆
            for (int i = 3; i < iconResourceIds.length; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("image", iconResourceIds[i]);
                item.put("text", ContactDetailMenu.CodeToEnum(i).getDescription());
                items.add(item);
            }

            Button btnInvite =(Button) findViewById(R.id.button_invite);
            btnInvite.setVisibility(View.VISIBLE);
            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventAnalyticsUtil.uploadHitRecordEvent(ContactDetailActivity.this, HitRecord.HitEvent.INVITE_JOIN_SV, HitRecord.HitType.PUSH);
                    sendInviteJoinSV();
                }
            });
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                items, R.layout.activity_contact_detail_list_item, new String[]{"image", "text"},
                new int[]{R.id.image, R.id.text});
        listView.setAdapter(simpleAdapter);

        // 設定選單點擊事件處理
        listView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果儲值帳戶登入成功，再呼叫api取儲值帳戶資訊
        if(requestCode == SVLoginActivity.REQUEST_LOGIN_SV && resultCode == Activity.RESULT_OK){

        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactDetailMenu selectMeun = ContactDetailMenu.DescToEnum(items.get(position).get("text").toString());

            if(selectMeun.equals(ContactDetailMenu.SENDRED))
            {
                // 如果還沒登入過儲值帳戶
                if(((WalletApplication)getApplication()).needSVLogin()){
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(ContactDetailActivity.this, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);

                    // 如果帳戶資訊都有了，開啟選擇紅包類型頁
                }else {
                    // 產生紅包需要的物件並放到intent
                    RedEnvelopeInputData inputData = new RedEnvelopeInputData(RedEnvelopeType.TYPE_GENERAL);
                    String[] selectedMemNos = { localContact.getMemNO() };
                    inputData.setMemNOs(selectedMemNos);
                    String[] selectedMemNames = { localContact.getDisplayName() };
                    inputData.setNames(selectedMemNames);
                    inputData.setTotalPeople(selectedMemNos.length);

                    Intent intent = new Intent(ContactDetailActivity.this, BlessingInputActivity.class);
                    intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
                    startActivity(intent);
                }
            }
            else if(selectMeun.equals(ContactDetailMenu.PAYMENT)) {
                // 如果還沒登入過儲值帳戶
                if(((WalletApplication)getApplication()).needSVLogin()){
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(ContactDetailActivity.this, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);

                    // 如果帳戶資訊都有了，開啟選擇紅包類型頁
                }else {

                    Intent intent = new Intent();
                    intent.setClass(ContactDetailActivity.this, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.EXTRA_RECEIVER, localContact);
                    startActivity(intent);
                }
            }
            else if(selectMeun.equals(ContactDetailMenu.RECEIPT)) {
                // 如果還沒登入過儲值帳戶
                if(((WalletApplication)getApplication()).needSVLogin()){
                    // 連到儲值帳戶登入頁
                    startActivityForResult(new Intent(ContactDetailActivity.this, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);

                    // 如果帳戶資訊都有了，開啟選擇紅包類型頁
                }else {
                    Intent intentGoReceipt = new Intent();
                    intentGoReceipt.setClass(ContactDetailActivity.this, ReceiptActivity.class);
                    intentGoReceipt.putExtra(PaymentActivity.EXTRA_RECEIVER, localContact);
                    startActivity(intentGoReceipt);
                }
            }
            else if(selectMeun.equals(ContactDetailMenu.SENDCOUPON)) {
                Intent intent = new Intent(ContactDetailActivity.this , MyCouponActivity.class);
                intent.putExtra(MyCouponActivity.EXTRA_FRIEND, localContact);
                startActivity(intent);
            }
            else if(selectMeun.equals(ContactDetailMenu.MESSAGELOG)) {
                Intent intent = new Intent(ContactDetailActivity.this , FriendHistoryActivity.class);
                intent.putExtra(FriendHistoryActivity.EXTRA_FRIEND, localContact);
                startActivity(intent);
            }
        }
    };


    /***
     * Http
     */
    private void sendInviteJoinSV() {

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
            memNOs.add(localContact.getMemNO());
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
                Toast.makeText(ContactDetailActivity.this, getString(R.string.send_invite_end), Toast.LENGTH_SHORT).show();
            } else {
                // 執行預設的錯誤處理
                handleResponseError(result, ContactDetailActivity.this);
            }
        }
    };
}
