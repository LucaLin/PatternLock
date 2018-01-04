package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.TransactionInLogAdapter;
import tw.com.taishinbank.ewallet.adapter.sv.TransactionOutLogAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.TransactionStatus;
import tw.com.taishinbank.ewallet.interfaces.TransactionType;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

public class TransactionLogFragment extends Fragment {

    private static final String TAG = "TransactionLogFragment";
    public static final String EXTRA_SWITCH_TO = "EXTRA_SWITCH_TO";

    public static final int TRX_OUT = 0;
    public static final int TRX_IN = 1;

    public final static int SECTION_SEPERATOR_AWAITING_SEQ = -1;
    public final static int SECTION_SEPERATOR_FINISHED_SEQ = -2;

    // -- View Hold --
    private Menu menu;
    private ListView lstTransactionLog;
    private FrameLayout btnTransactionOut;
    private FrameLayout btnTransactionIn;
    private TextView txtTransactionOut;
    private TextView txtTransactionIn;
    private ImageView imgTransactionOutNew;
    private ImageView imgTransactionInNew;

    private AlertDialog dlgMonthCriteria;
    private TextView btnMonthCriteria;

    // -- List View Adapter --
    private TransactionOutLogAdapter adapterTransactionOutLog;
    private TransactionInLogAdapter adapterTransactionInLog;
    private ArrayAdapter<String> adpaterMonthCriteria;

    // -- Data Model --
    private int idxSwitchList = TRX_OUT;
    private int idxMonthMenuItem = 0;

    public static TransactionLogFragment newInstance(int switchTo) {
        Bundle args = new Bundle();
        args.putInt(TransactionLogFragment.EXTRA_SWITCH_TO, switchTo);
        TransactionLogFragment fragment = new TransactionLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getArguments() != null)
            idxSwitchList = getArguments().getInt(EXTRA_SWITCH_TO, TRX_OUT);

        createMonthCriteriaAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_transaction_log, container, false);

        // Set View Hold
        lstTransactionLog = (ListView) view.findViewById(R.id.lst_transaction_log);
        btnTransactionOut = (FrameLayout) view.findViewById(R.id.btn_transaction_out);
        btnTransactionIn = (FrameLayout) view.findViewById(R.id.btn_transaction_in);
        txtTransactionOut = (TextView) view.findViewById(R.id.txt_transaction_out);
        txtTransactionIn = (TextView) view.findViewById(R.id.txt_transaction_in);
        imgTransactionInNew =(ImageView) view.findViewById(R.id.img_in_new);
        imgTransactionOutNew =(ImageView) view.findViewById(R.id.img_out_new);

        // List Adpater
        adapterTransactionInLog = new TransactionInLogAdapter(getContext());
        adapterTransactionOutLog = new TransactionOutLogAdapter(getContext());

        // Set Listener
        btnTransactionOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTransactionOut();
            }
        });
        btnTransactionIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTransactionIn();
            }
        });
        lstTransactionLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (idxSwitchList == TRX_IN)
                    onTransactionInItemClick(position);
                else
                    onTransactionOutItemClick(position);
            }
        });

        // Default: show the expend transaction...
        updateViewBySwitch();

        // Load the Data
        inquiryTransactionOutList();

        // Create Dialog
        dlgMonthCriteria =  new AlertDialog.Builder(getContext())
            .setAdapter(adpaterMonthCriteria, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onChangeMonthCriteria(which);
                    dialog.dismiss();
                }
            })
            .create();
        dlgMonthCriteria.setCancelable(true);
        dlgMonthCriteria.setCanceledOnTouchOutside(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.sv_account_history);
        Toolbar toolbar = (Toolbar) activityBase.findViewById(R.id.toolbar);
        if (toolbar != null) {
            btnMonthCriteria = (TextView) toolbar.findViewById(R.id.custom_button_right);
            btnMonthCriteria.setVisibility(View.VISIBLE);
            btnMonthCriteria.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_header_triangle, 0);
            btnMonthCriteria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgMonthCriteria.show();
                }
            });
            updateMonthView();
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if (item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // User interaction
    // ----
    public void switchToTransactionOut() {
        idxSwitchList = TRX_OUT;
        updateViewBySwitch();
//        btnTransactionOut.setBackgroundResource(R.drawable.tab_history_p);
//        btnTransactionIn.setBackgroundDrawable(null);
//        idxSwitchList = TRX_OUT;
//        lstTransactionLog.setAdapter(adapterTransactionOutLog);
//        txtTransactionOut.setTextColor(getResources().getColor(android.R.color.white));
//        txtTransactionIn.setTextColor(getResources().getColor(R.color.radio_tab_text));
    }

    public void switchToTransactionIn() {
        idxSwitchList = TRX_IN;
        updateViewBySwitch();
//        btnTransactionOut.setBackgroundDrawable(null);
//        btnTransactionIn.setBackgroundResource(R.drawable.tab_history_p_right);
//        idxSwitchList = TRX_IN;
//        lstTransactionLog.setAdapter(adapterTransactionInLog);
//        txtTransactionOut.setTextColor(getResources().getColor(R.color.radio_tab_text));
//        txtTransactionIn.setTextColor(getResources().getColor(android.R.color.white));
    }

    public void onTransactionOutItemClick(int position) {
        SVTransactionOut svTransactionOut = adapterTransactionOutLog.getTransactionOutList().get(position);

        TransactionLogDetailFragment detailFragment = TransactionLogDetailFragment.newInstance(svTransactionOut);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(android.R.id.tabcontent, detailFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void onTransactionInItemClick(int position) {
        SVTransactionIn svTransactionIn = adapterTransactionInLog.getTransactionInList().get(position);
        // 儲值不需詳情
        if (!TransactionType.DEPOSIT.code.equals(svTransactionIn.getTxType())) {
            TransactionLogDetailFragment detailFragment = TransactionLogDetailFragment.newInstance(svTransactionIn);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
            ft.replace(android.R.id.tabcontent, detailFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void onChangeMonthCriteria(int position) {
        idxMonthMenuItem = position;
        updateMonthView();
        inquiryTransactionOutList();
    }

    // ----
    // Http
    // ----
    private void inquiryTransactionOutList() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {

            HttpUtilBase.MonthOption monthOption = HttpUtilBase.MonthOption.LATEST_1_MONTH;
            if(idxMonthMenuItem == HttpUtilBase.MonthOption.LATEST_2_MONTH.ordinal()){
                monthOption = HttpUtilBase.MonthOption.LATEST_2_MONTH;
            }

            // 呼叫api取得收到的紅包
            try {
                SVHttpUtil.queryExpendTxLog(monthOption, null, null, responseListenerTransactionOut, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerTransactionOut = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                adapterTransactionOutLog.setTransactionOutList(SVResponseBodyUtil.parseTransactionOutList(result.getBody()));

            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if(handleCommonError(result, (ActivityBase) getActivity())){
                    return;
                } else {
                    showAlert((ActivityBase) getActivity(), result.getReturnMessage());
                }
            }

            HttpUtilBase.MonthOption monthOption = HttpUtilBase.MonthOption.LATEST_1_MONTH;
            if(idxMonthMenuItem == HttpUtilBase.MonthOption.LATEST_2_MONTH.ordinal()){
                monthOption = HttpUtilBase.MonthOption.LATEST_2_MONTH;
            }

            // 呼叫api取得發送的紅包清單
            try {
                SVHttpUtil.queryIncomeTxLog(monthOption, null, null, responseListenerTransactionIn, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 呼叫發出紅包api的listener
    private ResponseListener responseListenerTransactionIn = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                adapterTransactionInLog.setTransactionInList(SVResponseBodyUtil.parseTransactionInList(result.getBody()));
                updateListView();

            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    // ----
    // Private method
    // ----

    /**
     * 將Out, In 查詢結果加入分section 以及加入 section header
     */
    private void clarifyList() {
        boolean IsNewIn = false;
        boolean IsNewOut = false;

        // -- Transaction out list --
        List<SVTransactionOut> unfinishedOutList = new ArrayList<>();
        List<SVTransactionOut> finishedOutList = new ArrayList<>();
        for (SVTransactionOut o : adapterTransactionOutLog.getTransactionOutList()) {
            if(o.getReadFlag().equals("0"))
                IsNewOut = true;

            if (TransactionStatus.AWAITING.getCode().equals(o.getTxStatus())) {
                unfinishedOutList.add(o);
            } else {
                finishedOutList.add(o);
            }
        }

        //塞入空的DataModel當成section seperator
        if (unfinishedOutList.size() > 0) {
            unfinishedOutList.add(0, new SVTransactionOut(SECTION_SEPERATOR_AWAITING_SEQ));
        }
        if (finishedOutList.size() > 0) {
            finishedOutList.add(0, new SVTransactionOut(SECTION_SEPERATOR_FINISHED_SEQ));
        }

        List<SVTransactionOut> displayOutList = new ArrayList<>();
        displayOutList.addAll(unfinishedOutList);
        displayOutList.addAll(finishedOutList);
        adapterTransactionOutLog.setTransactionOutList(displayOutList);

        if(IsNewOut)
            imgTransactionOutNew.setVisibility(View.VISIBLE);

        // -- Transaction In list --
        List<SVTransactionIn> unfinishedInList = new ArrayList<>();
        List<SVTransactionIn> finishedInList = new ArrayList<>();
        for (SVTransactionIn in : adapterTransactionInLog.getTransactionInList()) {
            if(in.getReadFlag().equals("0"))
                IsNewIn = true;

            if (TransactionStatus.AWAITING.getCode().equals(in.getTxStatus())) {
                unfinishedInList.add(in);
            } else {
                finishedInList.add(in);
            }
        }

        //塞入空的DataModel當成section seperator
        if (unfinishedInList.size() > 0) {
            unfinishedInList.add(0, new SVTransactionIn(SECTION_SEPERATOR_AWAITING_SEQ));
        }
        if (finishedInList.size() > 0) {
            finishedInList.add(0, new SVTransactionIn(SECTION_SEPERATOR_FINISHED_SEQ));
        }

        List<SVTransactionIn> displayInList = new ArrayList<>();
        displayInList.addAll(unfinishedInList);
        displayInList.addAll(finishedInList);
        adapterTransactionInLog.setTransactionInList(displayInList);

        if(IsNewIn)
            imgTransactionInNew.setVisibility(View.VISIBLE);
    }

    private void updateListView() {
        clarifyList();
        adapterTransactionOutLog.notifyDataSetChanged();
        adapterTransactionInLog.notifyDataSetChanged();
    }

    private void updateViewBySwitch() {
        if (idxSwitchList == TRX_OUT) {
            btnTransactionOut.setBackgroundResource(R.drawable.tab_history_p);
            btnTransactionIn.setBackgroundDrawable(null);
            idxSwitchList = TRX_OUT;
            lstTransactionLog.setAdapter(adapterTransactionOutLog);
            txtTransactionOut.setTextColor(getResources().getColor(android.R.color.white));
            txtTransactionIn.setTextColor(getResources().getColor(R.color.radio_tab_text));
        } else {
            btnTransactionOut.setBackgroundDrawable(null);
            btnTransactionIn.setBackgroundResource(R.drawable.tab_history_p_right);
            idxSwitchList = TRX_IN;
            lstTransactionLog.setAdapter(adapterTransactionInLog);
            txtTransactionOut.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtTransactionIn.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void createMonthCriteriaAdapter() {
        adpaterMonthCriteria = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item);
        adpaterMonthCriteria.add(getString(R.string.sv_trx_latest_1_month));
        adpaterMonthCriteria.add(getString(R.string.sv_trx_latest_2_month));
    }

    private void updateMonthView() {
        if(adpaterMonthCriteria != null)
            btnMonthCriteria.setText(adpaterMonthCriteria.getItem(idxMonthMenuItem).toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        btnMonthCriteria.setVisibility(View.GONE);
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ----
    // Class, Interface, enum
    // ----


}
