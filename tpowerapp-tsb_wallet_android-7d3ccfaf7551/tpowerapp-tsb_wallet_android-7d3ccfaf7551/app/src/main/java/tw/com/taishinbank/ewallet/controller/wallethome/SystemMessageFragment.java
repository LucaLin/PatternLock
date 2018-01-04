package tw.com.taishinbank.ewallet.controller.wallethome;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Set;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.wallethome.WalletSystemMessageAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.wallethome.WalletSystemMsg;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;

public class SystemMessageFragment extends Fragment implements WalletSystemMessageAdapter.OnItemClickedListener {

    private static final String TAG = "SystemMessageFragment";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ImageLoader imageLoader;
    private ArrayList<WalletSystemMsg> systemMsgsList = new ArrayList<>();
    private WalletSystemMessageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_system_message, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 設定adapter
        adapter = new WalletSystemMessageAdapter(getActivity());
        adapter.setOnItemClickedListener(this);
        recyclerView.setAdapter(adapter);
        // 設定layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    imageLoader.setPauseWork(true);
                } else {
                    imageLoader.setPauseWork(false);
                }
            }
        });

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        }else {
            // 呼叫api取得系統訊息列表
            try {
                GeneralHttpUtil.querySystemMessage(null, responseListener, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.drawer_item_system_message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            // 返回上一頁
            if (menuItem.getItemId() == android.R.id.home) {
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);
            }
        }
    }

    // 呼叫取得系統訊息列表api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null){
                return ;
            }

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                systemMsgsList = GeneralResponseBodyUtil.parseSystemMessageList(result.getBody());
                // 檢查並存入Local列表
                if(systemMsgsList.size() > 0) {
                    Set<String> readSet = PreferenceUtil.getSystemMessageReadList(getActivity());
                    for (WalletSystemMsg item : systemMsgsList) {
                        if(!TextUtils.isEmpty(item.getCreateDate()) && !readSet.contains(item.getCreateDate())){
                            readSet.add(item.getCreateDate());
                        }
                    }
                    PreferenceUtil.setSystemMessageReadList(getActivity(), readSet);
                }
                ((ActivityBase) getActivity()).dismissProgressLoading();
                adapter.setList(systemMsgsList);
            } else {
                ((ActivityBase) getActivity()).dismissProgressLoading();
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        imageLoader.setPauseWork(false);
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onItemClicked(WalletSystemMsg item) {
        Fragment fragment = SystemMessageDetailFragment.newInstance(item);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(android.R.id.tabcontent, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
