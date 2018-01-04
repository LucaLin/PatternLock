package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import tw.com.taishinbank.ewallet.dbhelper.DatabaseHelper;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.InviteOption;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.Selectable;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class FriendListFragment extends Fragment implements SearchView.OnQueryTextListener, DownloadContactsAsyncTask.OnDownloadStartFinishListener/*, UploadContactsAsyncTask.OnUploadStartFinishListener*/ {

    private static final String TAG = "FriendListFragment";
    private static final int DEFAULT_MAX_FRIEND_SELECTION = 50;

    private ListView listView;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FrameLayout layoutButtonArea;
    private Button buttonNext;

    private boolean isSvAccountOnly = false; // 是否只顯示儲值帳戶
    private boolean isListItemselectable = false; // 是否顯示可勾選的列表
    private boolean isSelectAll = false;
    private int maxFriendSelection = DEFAULT_MAX_FRIEND_SELECTION;
    private PreferenceUtil.ENUM_USE_FRIEND useType;

    private ImageLoader mImageLoader;
    private ContactsAdapter adapter;
    private ContactsSeletedAdapter adapterSelected;

    private FriendListListener friendListListener;

    private AlertDialog dialogInviteFriend;
    private ArrayAdapter<InviteOption> inviteOptionArrayAdapter;
    private final int RESULT_CONTACT_PICK = 0;

    private static final String ARG_SV_ACCOUNT_ONLY = "arg_sv_account_only";
    private static final String ARG_ITEM_SELECTABLE = "arg_item_selectable";
    private static final String USE_TYPE = "use_type";

    /**
     * 用來建立Fragment，預設只顯示有儲值帳戶的好友
     */
    public static FriendListFragment newSelectableInstance(PreferenceUtil.ENUM_USE_FRIEND useType) {
        return newSelectableInstance(useType, true);
    }

    /**
     * 用來建立Fragment
     * @param useType 使用類型，用在存取是否第一次進入該功能好友選擇頁的preference
     * @param showSVOnly 是否只顯示有儲值帳戶的好友
     */
    public static FriendListFragment newSelectableInstance(PreferenceUtil.ENUM_USE_FRIEND useType, boolean showSVOnly) {
        FriendListFragment f = new FriendListFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_ITEM_SELECTABLE, true);
        args.putBoolean(ARG_SV_ACCOUNT_ONLY, showSVOnly);
        args.putString(USE_TYPE, useType.toString());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if(args != null){
            isSvAccountOnly = args.getBoolean(ARG_SV_ACCOUNT_ONLY);
            isListItemselectable = args.getBoolean(ARG_ITEM_SELECTABLE);
            useType = PreferenceUtil.ENUM_USE_FRIEND.valueOf(args.getString(USE_TYPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        // Set the view Hold
        setViewHold(view);

        // Set the view content, value, looks ....
        setViewContent();

        //Initial adapter, helper, imageLoader.....
        mImageLoader = new ImageLoader(getActivity(), getContext().getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        adapter = new ContactsAdapter(getActivity(), isListItemselectable, mImageLoader);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        listView.setAdapter(adapter);
        if (isListItemselectable) {
            adapterSelected = new ContactsSeletedAdapter(mImageLoader);
            recyclerView.setAdapter(adapterSelected);
        }

        // Set Listener
        setListener();

        // 如果本機沒有資料，從遠端download
        if (!dbHelper.hasLocalContacts()) {
            // 如果沒有網路連線，顯示提示對話框
            if(!NetworkUtil.isConnected(getContext())) {
                ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return view;
            }
            new DownloadContactsAsyncTask(getActivity(), this).execute();
        } else {
            if (PreferenceUtil.getAccountNotExist(getContext())) {
                refreshContact();
                PreferenceUtil.setAccountNotExist(getContext(), false);
            }
        }
        //好友邀請Dialog 初始化, 將會在menu item開啟Dialog
        dialogInviteFriend = createInviteFriendDialog();

        if (PreferenceUtil.isFirstTimeUseFriend(getContext(), useType)) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.first_time_use_friend));
            PreferenceUtil.setFirstTimeUseFriend(getContext(), false, useType);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityBase) getActivity()).setCenterTitle(R.string.extra_select_friend);

        // 從資料庫撈資料
        updateFromDB();
    }


    @Override
    public void onPause() {
        super.onPause();
        mImageLoader.setPauseWork(false);
        if(getActivity() != null){
            ((ActivityBase)getActivity()).hideKeyboard();
        }
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.activity_contact_list, menu);
//        return true;
//    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_contact_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // 根據是否顯示可勾選的列表，設定顯示於右上角的選單項目
        menu.findItem(R.id.action_invite_friend).setVisible(true);
        menu.findItem(R.id.action_select_all).setVisible(false);
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
        } else if(id == R.id.action_invite_friend){
            // 開啟邀請好友頁
            //startActivity(new Intent(getContext(), AddingContactActivity.class));
            dialogInviteFriend.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    protected void onInviteOptionSelected(InviteOption option) {
        if (option == InviteOption.Phone) {
            startActivity(new Intent(getActivity(), AddingContactActivity.class));

        } else if (option == InviteOption.Contact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, RESULT_CONTACT_PICK);

        } else if (option == InviteOption.FBMessenger) {
            EventAnalyticsUtil.uploadHitRecordEvent(getActivity(), HitRecord.HitEvent.INVITE_JOIN_WALLET, HitRecord.HitType.FACEBOOK);

            Intent inviteIntent = new Intent(Intent.ACTION_SEND);
            inviteIntent.setType("text/plain");
            inviteIntent.setPackage("com.facebook.orca");
            String inviteMsg = String.format(getString(R.string.invite_msg_join_wallet), PreferenceUtil.getNickname(getActivity()));
            inviteIntent.putExtra(Intent.EXTRA_TEXT, inviteMsg);
            inviteIntent.putExtra(Intent.EXTRA_SUBJECT, PreferenceUtil.getNickname(getActivity()) + "發送邀請");

            startActivity(Intent.createChooser(inviteIntent, getResources().getString(R.string.share)));

        } else if (option == InviteOption.Line) {
            EventAnalyticsUtil.uploadHitRecordEvent(getActivity(), HitRecord.HitEvent.INVITE_JOIN_WALLET, HitRecord.HitType.LINE);

            Intent inviteIntent = new Intent(Intent.ACTION_SEND);
            inviteIntent.setType("text/plain");
            inviteIntent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
            String inviteMsg = String.format(getString(R.string.invite_msg_join_wallet), PreferenceUtil.getNickname(getActivity()));
            inviteIntent.putExtra(Intent.EXTRA_TEXT, inviteMsg);
            inviteIntent.putExtra(Intent.EXTRA_SUBJECT, PreferenceUtil.getNickname(getActivity()) + "發送邀請");

            startActivity(Intent.createChooser(inviteIntent, getResources().getString(R.string.share)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (RESULT_CONTACT_PICK):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();

                    Activity activity = getActivity();
                    if(activity != null) {
                        // 取得電話號碼
                        Cursor cursor = activity.getContentResolver().query(contactData, null, null, null, null);
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Log.d(TAG, "original number = " + number);
                                // 去掉非0-9的字元
                                number = number.replaceAll("[^0-9]", "");
                                Intent intent = new Intent(activity, AddingContactActivity.class);
                                intent.putExtra(AddingContactActivity.EXTRA_SEARCH_PHONE, number);
                                startActivity(intent);
                            }
                        }
                    }
                }

        }
    }

    protected AlertDialog createInviteFriendDialog() {
        inviteOptionArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);

        inviteOptionArrayAdapter.add(InviteOption.Phone);
        inviteOptionArrayAdapter.add(InviteOption.Contact);
        inviteOptionArrayAdapter.add(InviteOption.Line);
        inviteOptionArrayAdapter.add(InviteOption.FBMessenger);

        // 客製Title View
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View customDialogTitleView = inflater.inflate(R.layout.dialog_invite_friend_title, null);
        // 建立對話框
        AlertDialog dialog = new AlertDialog.Builder(getContext())
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


    // ----
    // User interaction event
    // ----
    public void onFriendListRefresh() {
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getContext())){
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            swipeRefreshLayout.setRefreshing(false);
            return ;
        }

        // TODO 暫先不上傳，直接做下載就好
        // - Add DownloadContactsAsyncTask.OnDownloadStartFinishListener by this fragment
        new DownloadContactsAsyncTask(getActivity(), FriendListFragment.this).execute();
        // 確認是否有權限存取聯絡人
//                    if (!PermissionUtil.needGrantRuntimePermission(ContactListActivity.this, Manifest.permission.READ_CONTACTS,
//                            PermissionUtil.PERMISSION_REQUEST_CODE_READ_CONTACTS)) {
//                        // 開始讀取並上傳聯絡人資料
//                        new UploadContactsAsyncTask(ContactListActivity.this, ContactListActivity.this).execute();
//                    }
    }

    public void onFriendListSelectionChanged(ArrayList<Selectable<LocalContact>> selectedContacts) {
        // 根據選中人數設定發送按鈕的文字與狀態
        if (selectedContacts.size() > 0) {
            if(!friendListListener.shouldContinueUpdateList(selectedContacts)){
                return;
            }
            // 如果選中人數超過50筆就不顯示下方選中的聯絡人列表
            if (selectedContacts.size() > 50) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
            buttonNext.setText(String.format(getString(R.string.send_red_envelope_with_count), selectedContacts.size()));
            buttonNext.setEnabled(true);
        } else {
            recyclerView.setVisibility(View.GONE);
            buttonNext.setText(R.string.button_next_step);
            buttonNext.setEnabled(false);
        }
        adapterSelected.setContacts(selectedContacts);
    }

    // 當聯絡人列表項目被點擊時，開啟聯絡人詳細頁面
    public void onFriendListItemClick (int position) {
        //2015/12/30, TSB PM要求暫停
//        Intent intentDetail = new Intent(getActivity(), ContactDetailActivity.class);
//        intentDetail.putExtra(ContactDetailActivity.EXTRA_CONTACT_DATA, adapter.getItem(position).item);
//        startActivity(intentDetail);
    }

    // 先暫停所有載工作
    public void onFriendListScrollStateChange (AbsListView absListView, int scrollState) {
        // Pause image loader to ensure smoother scrolling when flinging
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            mImageLoader.setPauseWork(true);
        } else {
            mImageLoader.setPauseWork(false);
        }
    }

    //on Recycler View item remove
    public void onSelectedFriendListRemoved() {
        // 通知聯絡人列表畫面更新與選中項目變更
        adapter.notifySelectedItemChange();
        adapter.notifyDataSetChanged();
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

    public void onNextButtonClick() {
        if (friendListListener != null) {
            friendListListener.onNext(adapterSelected.getContacts());
        }

        // 取出被選中的聯絡人memNO跟顯示名字
//        // TODO memNo可能要改成int[]
//        List<Selectable<LocalContact>> selectedContacts = adapterSelected.getContacts();
//        String[] selectedMemNos = new String[selectedContacts.size()];
//        String[] selectedMemNames = new String[selectedContacts.size()];
//        for (int i = 0; i < selectedContacts.size(); i++) {
//            selectedMemNos[i] = selectedContacts.get(i).item.getMemNO();
//            selectedMemNames[i] = selectedContacts.get(i).item.getDisplayName();
//        }
    }

    // ----
    // Background Listener
    // ----
    @Override
    public void onTaskStarted() {
        // 顯示loading...對話框
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ActivityBase) getActivity()).showProgressLoading();
            }
        });
    }

    @Override
    public void onTaskFinished() {
        // 關閉loading...對話框

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ActivityBase) getActivity()).dismissProgressLoading();
            }
        });
    }

    public void onUploadFinished() {
        // 下載聯絡人
        new DownloadContactsAsyncTask(getActivity(), this).execute();
    }

    @Override
    public void onDownloadFinished(ResponseResult errorResult) {
        // 如果有錯誤，顯示錯誤提示框
        if(errorResult != null){
            // 執行預設的錯誤處理
            ResponseListener.handleResponseError(errorResult, (ActivityBase) getActivity());
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



    // ----
    // Private method
    // ----

    private void setViewHold(View pView) {
        searchView = (SearchView) pView.findViewById(R.id.searchview);
        listView = (ListView) pView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) pView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) pView.findViewById(R.id.recycler_view);
        layoutButtonArea = (FrameLayout) pView.findViewById(R.id.layout_button);
        buttonNext = (Button) pView.findViewById(R.id.button_send);
    }

    private void setViewContent() {
        // 設定是否只顯示圖示
        searchView.setIconifiedByDefault(false);
        //
        if (!isListItemselectable) {
            // TODO 設定主題顏色
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorRedPrimary));

        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            layoutButtonArea.setVisibility(View.VISIBLE);
            int selectedSize = (adapterSelected != null) ? adapterSelected.getItemCount() : 0;
            buttonNext.setEnabled(selectedSize > 0);
            buttonNext.setText(R.string.send_red_envelope_next);
            swipeRefreshLayout.setEnabled(false);
        }
    }

    private void setListener() {
        if (isListItemselectable) {

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNextButtonClick();
                }
            });


            // 設定選中列表的adapter
            adapterSelected.setOnItemRemovedListener(new ContactsSeletedAdapter.OnItemRemovedListener() {
                @Override
                public void OnItemRemoved() {
                    onSelectedFriendListRemoved();
                }
            });

            // 設定layout manager

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

            // 設定列表選中項目變更的事件
            adapter.setOnSelectedChangedListener(new ContactsAdapter.OnSelectedItemsChangedListener() {
                @Override
                public void OnSelectedItemsChanged(ArrayList<Selectable<LocalContact>> selectedContacts) {
                    onFriendListSelectionChanged(selectedContacts);
                }
            });
        }

        searchView.setOnQueryTextListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onFriendListRefresh();
            }
        });

        // 當聯絡人列表項目被點擊時，開啟聯絡人詳細頁面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onFriendListItemClick(position);
            }
        });
        // 加入onScrollListener，當列表快速滾動時，先暫停所有下載工作
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause image loader to ensure smoother scrolling when flinging
                onFriendListScrollStateChange(absListView, scrollState);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
    }

    private void searchAndUpdate(String keyword){
        adapter.getFilter().filter(keyword);
        isSelectAll = false;
    }

    /**
     * 從資料庫撈資料出來更新列表
     */
    private void updateFromDB(){
        // 更新畫面資料
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        // 取得資料庫聯絡人資料
        ArrayList<LocalContact> list;
        if (isSvAccountOnly) {
            // 取得所有有儲值帳戶的聯絡人
            list = dbHelper.getAllSV();
        } else {
            // 取得所有聯絡人
            list = dbHelper.getAll();
        }
        // 設定adapter
        adapter.setContacts(list);
    }

    public void resetLastSelection(){
        adapter.setSelection(adapter.getLastSelectedPosition(), false);
        adapter.notifyDataSetChanged();
    }

    private void refreshContact() {
        if(!NetworkUtil.isConnected(getContext())){
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            swipeRefreshLayout.setRefreshing(false);
            return ;
        }


        // TODO 暫先不上傳，直接做下載就好
        new DownloadContactsAsyncTask(getActivity(), this).execute();
    }


    // ----
    // Getter and Setter
    // ----

    public void setFriendListListener(FriendListListener pFriendListListener) {
        this.friendListListener = pFriendListListener;
    }

    // ----
    // Define interface or class
    // ----
    public interface FriendListListener {
        void onNext(ArrayList<LocalContact> list);

        /**
         * 當選中的聯絡人變更且數量大於0時，呼叫此方法
         * @return 如果可以繼續執行，return true; 否則return false
         */
        boolean shouldContinueUpdateList(ArrayList<Selectable<LocalContact>> selectedContacts);
    }

}
