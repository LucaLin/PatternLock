package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.ContactsAdapter;
import tw.com.taishinbank.ewallet.adapter.ContactsSeletedAdapter;
import tw.com.taishinbank.ewallet.async.DownloadContactsAsyncTask;
import tw.com.taishinbank.ewallet.controller.red.BlessingInputActivity;
import tw.com.taishinbank.ewallet.dbhelper.DatabaseHelper;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.InviteOption;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.Selectable;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class ContactListActivity extends ActivityBase implements SearchView.OnQueryTextListener, DownloadContactsAsyncTask.OnDownloadStartFinishListener/*, UploadContactsAsyncTask.OnUploadStartFinishListener*/ {

    private static final String TAG = "ContactListActivity";
    private ListView listView;
    private AlertDialog dialogInviteFriend;
    private ContactsAdapter adapter;
    private ContactsSeletedAdapter adapterSelected;
    private ArrayAdapter<InviteOption> inviteOptionArrayAdapter;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean showSvAccountOnly = false; // 是否只顯示儲值帳戶
    private boolean isListItemselectable = false; // 是否顯示可勾選的列表
    private boolean isSelectAll = false;

    public static final String EXTRA_SHOW_SV_ONLY = "extra_show_sv_only";
    public static final String EXTRA_IS_LISTITEM_SELECTABLE = "is_listitem_selectable";
    private final int RESULT_CONTACT_PICK = 0;

    private ImageLoader mImageLoader;

    // TODO 以後可能需要改成跟一般紅包共用的父類別
    private RedEnvelopeInputData inputData;

    private ArrayList<Selectable<LocalContact>> selectedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // 設定置中的標題與返回鈕
        setCenterTitle(R.string.title_activity_contact_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = (SearchView) findViewById(R.id.searchview);
        // 設定是否只顯示圖示
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        listView = (ListView) findViewById(android.R.id.list);
        final Intent intent = getIntent();
        // 嘗試從intent取得資料
        if(intent != null){
            showSvAccountOnly = intent.getBooleanExtra(EXTRA_SHOW_SV_ONLY, false);
            isListItemselectable = intent.getBooleanExtra(EXTRA_IS_LISTITEM_SELECTABLE, false);
            inputData = intent.getParcelableExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA);
        }

        // 只有從首頁進入好友清單，才可以有下拉更新的功能
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if(!isListItemselectable) {
            // TODO 設定主題顏色
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorRedPrimary));
            // 設定列表下拉事件處理
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // 如果沒有網路連線，顯示提示對話框
                    refreshContact();
                }
            });
        }else{
            // 否則disable下拉更新功能
            swipeRefreshLayout.setEnabled(false);
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mImageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));

        adapter = new ContactsAdapter(this, isListItemselectable, mImageLoader);

        // 如果list是可選的，則顯示按鈕，並設定按鈕的點擊事件處理、
        // 設定選中列表的adapter跟選中列表變更的listener
        if(isListItemselectable) {
            FrameLayout layoutButton = (FrameLayout) findViewById(R.id.layout_button);
            layoutButton.setVisibility(View.VISIBLE);
            final Button buttonSend = (Button) findViewById(R.id.button_send);
            buttonSend.setEnabled(false);
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 取出被選中的聯絡人memNO跟顯示名字
                    // TODO memNo可能要改成int[]
                    String[] selectedMemNos = new String[selectedContacts.size()];
                    String[] selectedMemNames = new String[selectedContacts.size()];
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        selectedMemNos[i] = selectedContacts.get(i).item.getMemNO();
                        selectedMemNames[i] = selectedContacts.get(i).item.getDisplayName();
                    }
                    inputData.setMemNOs(selectedMemNos);
                    inputData.setNames(selectedMemNames);
                    if(inputData.getType().equals(RedEnvelopeType.TYPE_GENERAL))
                        inputData.setTotalPeople(selectedContacts.size());

                    // 開啟輸入祝福語的頁面，並傳遞資料
                    Intent intent = new Intent(v.getContext(), BlessingInputActivity.class);
                    intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
                    startActivity(intent);
                }
            });
            buttonSend.setText(R.string.send_red_envelope_next);

            // 設定選中列表的adapter
            adapterSelected = new ContactsSeletedAdapter(mImageLoader);
            adapterSelected.setOnItemRemovedListener(new ContactsSeletedAdapter.OnItemRemovedListener() {
                @Override
                public void OnItemRemoved() {
                    // 通知聯絡人列表畫面更新與選中項目變更
                    adapter.notifySelectedItemChange();
                    adapter.notifyDataSetChanged();
                }
            });

            // 設定layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            // 加入onScrollListener，當列表快速滾動時，先暫停所有下載工作
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    // Pause image loader to ensure smoother scrolling when flinging
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        mImageLoader.setPauseWork(true);
                    } else {
                        mImageLoader.setPauseWork(false);
                    }
                }
            });
            recyclerView.setAdapter(adapterSelected);

            // 設定列表選中項目變更的事件
            adapter.setOnSelectedChangedListener(new ContactsAdapter.OnSelectedItemsChangedListener() {
                @Override
                public void OnSelectedItemsChanged(ArrayList<Selectable<LocalContact>> selectedContacts) {
                    ContactListActivity.this.selectedContacts = selectedContacts;
                    int selectedCount = selectedContacts.size();
                    adapterSelected.setContacts(selectedContacts);
                    // 根據選中人數設定發送按鈕的文字與狀態
                    if (selectedCount > 0) {
                        // 如果選中人數超過50筆就不顯示下方選中的聯絡人列表
                        if(selectedCount > 50) {
                            recyclerView.setVisibility(View.GONE);
                        }else{
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        buttonSend.setText(String.format(getString(R.string.send_red_envelope_with_count), selectedCount));
                        buttonSend.setEnabled(true);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        buttonSend.setText(R.string.send_red_envelope);
                        buttonSend.setEnabled(false);
                    }
                }
            });

        }

        listView.setAdapter(adapter);
        // 當聯絡人列表項目被點擊時，開啟聯絡人詳細頁面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //2015/12/30, TSB PM要求暫停
                Intent intentDetail = new Intent(ContactListActivity.this, ContactDetailActivity.class);
                intentDetail.putExtra(ContactDetailActivity.EXTRA_CONTACT_DATA, adapter.getItem(position).item);
                startActivity(intentDetail);
            }
        });
        // 加入onScrollListener，當列表快速滾動時，先暫停所有下載工作
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageLoader.setPauseWork(true);
                } else {
                    mImageLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        // 如果本機沒有資料，從遠端download
        if (!dbHelper.hasLocalContacts()) {
            // 如果沒有網路連線，顯示提示對話框
            if(!NetworkUtil.isConnected(ContactListActivity.this)){
                showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return ;
            }
            new DownloadContactsAsyncTask(this, this).execute();
        } else {
            //若有朋友是已經刪除帳戶，進行更新聯絡人資料
            if(PreferenceUtil.getAccountNotExist(this)) {
                refreshContact();
                PreferenceUtil.setAccountNotExist(this, false);
            }

        }

        //好友邀請Dialog 初始化, 將會在menu item開啟Dialog
        dialogInviteFriend = createInviteFriendDialog();

        checkIfShowFirstTimeUseDialog();
    }

    /**
     * 確認是否要顯示第一次使用的文字提示訊息
     */
    private void checkIfShowFirstTimeUseDialog(){
        PreferenceUtil.ENUM_USE_FRIEND enumUseFriend;
        if(isListItemselectable) {
            if(inputData.getType().equals(RedEnvelopeType.TYPE_MONEY_GOD)){
                enumUseFriend = PreferenceUtil.ENUM_USE_FRIEND.RED_MONEY_GOD;
            }else{
                enumUseFriend = PreferenceUtil.ENUM_USE_FRIEND.RED_GENERAL;
            }
        }
        else {
            enumUseFriend = PreferenceUtil.ENUM_USE_FRIEND.FRIEND;
        }

        if (PreferenceUtil.isFirstTimeUseFriend(this, enumUseFriend)) {
            showAlertDialog(getString(R.string.first_time_use_friend));
            PreferenceUtil.setFirstTimeUseFriend(this, false, enumUseFriend);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 從資料庫撈資料
        updateFromDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contact_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 根據是否顯示可勾選的列表，設定顯示於右上角的選單項目
        // 20160224依據需求調整成顯示加好友圖示、不顯示全選
//        menu.findItem(R.id.action_invite_friend).setVisible(!isListItemselectable);
//        menu.findItem(R.id.action_select_all).setVisible(isListItemselectable);
        menu.findItem(R.id.action_invite_friend).setVisible(true);
        menu.findItem(R.id.action_select_all).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 如果是全選
        if (id == R.id.action_select_all) {
            isSelectAll = !isSelectAll;
            adapter.setSelectAll(isSelectAll);
            return true;

        // 如果是邀請好友圖示
        } else if(id == R.id.action_invite_friend) {
            // 開啟邀請好友頁
//            startActivity(new Intent(this, AddingContactActivity.class));
//            return true;
            dialogInviteFriend.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // 傳入keyword搜尋聯絡人，並更新列表
        searchAndUpdate(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // 傳入keyword搜尋聯絡人，並更新列表
        searchAndUpdate(newText);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (RESULT_CONTACT_PICK):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    // 取得電話號碼
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.d(TAG, "original number = " + number);
                            // 去掉非0-9的字元
                            number = number.replaceAll("[^0-9]", "");
                            Intent intent = new Intent(this, AddingContactActivity.class);
                            intent.putExtra(AddingContactActivity.EXTRA_SEARCH_PHONE, number);
                            startActivity(intent);
                        }
                    }
                }

        }
    }

    private void searchAndUpdate(String keyword){
        adapter.getFilter().filter(keyword);
        isSelectAll = false;
    }

    @Override
    public void onTaskStarted() {
        // 顯示loading...對話框
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressLoading();
            }
        });
    }

    @Override
    public void onTaskFinished() {
        // 關閉loading...對話框

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressLoading();
            }
        });
    }

    private void refreshContact() {
        if(!NetworkUtil.isConnected(ContactListActivity.this)){
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            swipeRefreshLayout.setRefreshing(false);
            return ;
        }


        // TODO 暫先不上傳，直接做下載就好
        new DownloadContactsAsyncTask(ContactListActivity.this, ContactListActivity.this).execute();
        // 確認是否有權限存取聯絡人
//                    if (!PermissionUtil.needGrantRuntimePermission(ContactListActivity.this, Manifest.permission.READ_CONTACTS,
//                            PermissionUtil.PERMISSION_REQUEST_CODE_READ_CONTACTS)) {
//                        // 開始讀取並上傳聯絡人資料
//                        new UploadContactsAsyncTask(ContactListActivity.this, ContactListActivity.this).execute();
//                    }
    }


    protected void onInviteOptionSelected(InviteOption option) {
        if (option == InviteOption.Phone) {
            startActivity(new Intent(this, AddingContactActivity.class));

        } else if (option == InviteOption.Contact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, RESULT_CONTACT_PICK);

        } else if (option == InviteOption.FBMessenger) {
            EventAnalyticsUtil.uploadHitRecordEvent(this, HitRecord.HitEvent.INVITE_JOIN_WALLET, HitRecord.HitType.FACEBOOK);

            Intent inviteIntent = new Intent(Intent.ACTION_SEND);
            inviteIntent.setType("text/plain");
            inviteIntent.setPackage("com.facebook.orca");
            String inviteMsg = String.format(getString(R.string.invite_msg_join_wallet), PreferenceUtil.getNickname(this));
            inviteIntent.putExtra(Intent.EXTRA_TEXT, inviteMsg);
            inviteIntent.putExtra(Intent.EXTRA_SUBJECT, PreferenceUtil.getNickname(this) + "發送邀請");

            startActivity(Intent.createChooser(inviteIntent, getResources().getString(R.string.share)));

        } else if (option == InviteOption.Line) {
            EventAnalyticsUtil.uploadHitRecordEvent(this, HitRecord.HitEvent.INVITE_JOIN_WALLET, HitRecord.HitType.LINE);

            Intent inviteIntent = new Intent(Intent.ACTION_SEND);
            inviteIntent.setType("text/plain");
            inviteIntent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
            String inviteMsg = String.format(getString(R.string.invite_msg_join_wallet), PreferenceUtil.getNickname(this));
            inviteIntent.putExtra(Intent.EXTRA_TEXT, inviteMsg);
            inviteIntent.putExtra(Intent.EXTRA_SUBJECT, PreferenceUtil.getNickname(this) + "發送邀請");

            startActivity(Intent.createChooser(inviteIntent, getResources().getString(R.string.share)));
        }
    }

    protected AlertDialog createInviteFriendDialog() {
        inviteOptionArrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);

        inviteOptionArrayAdapter.add(InviteOption.Phone);
        inviteOptionArrayAdapter.add(InviteOption.Contact);
        inviteOptionArrayAdapter.add(InviteOption.Line);
        inviteOptionArrayAdapter.add(InviteOption.FBMessenger);

        // 客製Title View
        LayoutInflater inflater = getLayoutInflater();
        View customDialogTitleView = inflater.inflate(R.layout.dialog_invite_friend_title, null);
        // 建立對話框
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(customDialogTitleView)
                .setAdapter(inviteOptionArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InviteOption selectedOption = inviteOptionArrayAdapter.getItem(which);
                        onInviteOptionSelected(selectedOption);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

//    @Override
//    public void onUploadFinished() {
//        // 下載聯絡人
//        new DownloadContactsAsyncTask(this, this).execute();
//    }

    @Override
    public void onDownloadFinished(ResponseResult errorResult) {
        // 如果有錯誤，顯示錯誤提示框
        if(errorResult != null){
            // 執行預設的錯誤處理
            ResponseListener.handleResponseError(errorResult, this);
        }
        updateFromDB();
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_READ_CONTACTS) {
//            // 有權限存取
//            if (PermissionUtil.verifyPermissions(grantResults)) {
//                // 開始讀取並上傳聯絡人資料
//                new UploadContactsAsyncTask(this, this).execute();
//
//            } else {
//                // TODO 沒有權限時要做什麼
//                // 直接下載聯絡人
//                new DownloadContactsAsyncTask(this, this).execute();
//            }
//        }
//    }

    /**
     * 從資料庫撈資料出來更新列表
     */
    private void updateFromDB(){
        // 更新畫面資料
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        // 取得資料庫聯絡人資料
        ArrayList<LocalContact> list;
        if (showSvAccountOnly) {
            // 取得所有有儲值帳戶的聯絡人
            list = dbHelper.getAllSV();
        } else {
            // 取得所有聯絡人
            list = dbHelper.getAll();
        }
        // 設定adapter
        adapter.setContacts(list);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageLoader.setPauseWork(false);
        hideKeyboard();
    }
}
