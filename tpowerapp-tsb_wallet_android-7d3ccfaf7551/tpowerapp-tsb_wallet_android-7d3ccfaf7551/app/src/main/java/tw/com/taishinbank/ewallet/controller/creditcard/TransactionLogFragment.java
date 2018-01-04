package tw.com.taishinbank.ewallet.controller.creditcard;

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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Calendar;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.creditcard.TransactionLogAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.creditcard.TransactionStatus;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.CreditCardHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.CreditCardResponseBodyUtil;

public class TransactionLogFragment extends Fragment {

    private static final String TAG = "TransactionLogFragment";
    public static final String EXTRA_SWITCH_TO = "EXTRA_SWITCH_TO";

    public static final int TRX_CURR = 0;
    public static final int TRX_LAST = 1;

    public final static int SECTION_SEPERATOR_AWAITING_SEQ = -1;
    public final static int SECTION_SEPERATOR_FINISHED_SEQ = -2;

    // -- View Hold --
    private ListView lstTransactionLog;
    private FrameLayout btnTransactionCurrent;
    private FrameLayout btnTransactionLast;
    private TextView txtTransactionCurrent;
    private TextView txtTransactionLast;

    private AlertDialog dlgMonthCriteria;
    private TextView btnMonthCriteria;

    // -- List View Adapter --
    int totalCount_CurrMonth = 0;
    int totalCount_LastMonth = 0;
    private TransactionLogAdapter adapterTransactionLastLog;
    private TransactionLogAdapter adapterTransactionCurrentLog;
    private ArrayAdapter<String> adpaterMonthCriteria;

    // -- Data Model --
    private int switchList = TRX_CURR;
    private int idxMonthMenuItem = 0;
    private String queryStatus = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getArguments() != null)
            switchList = getArguments().getInt(EXTRA_SWITCH_TO, TRX_CURR);

        createMonthCriteriaAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_transaction_log, container, false);

        // Set View Hold
        lstTransactionLog = (ListView) view.findViewById(R.id.lst_transaction_log);
        btnTransactionCurrent = (FrameLayout) view.findViewById(R.id.btn_transaction_out);
        btnTransactionLast = (FrameLayout) view.findViewById(R.id.btn_transaction_in);
        txtTransactionCurrent = (TextView) view.findViewById(R.id.txt_transaction_out);
        txtTransactionLast = (TextView) view.findViewById(R.id.txt_transaction_in);
        txtTransactionCurrent.setText(getString(R.string.current_month_trans_history));
        txtTransactionLast.setText(getString(R.string.last_month_trans_history));

        // List Adpater
        adapterTransactionCurrentLog = new TransactionLogAdapter(getContext());
        adapterTransactionLastLog = new TransactionLogAdapter(getContext());

        // Set Listener
        btnTransactionCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTransactionCurr();
            }
        });
        btnTransactionLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTransactionLast();
            }
        });
        lstTransactionLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (switchList == TRX_CURR)
                    onTransactionCurrItemClick(position);
                else
                    onTransactionLastItemClick(position);
            }
        });

        // Default: show the expend transaction...
//        switchToTransactionCurr();
//        updateViewBySwitch();


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
        activityBase.setCenterTitle(R.string.credit_trans_history);
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


        // Load the Data
        inquiryTransactionList();

    }

    @Override
    public void onPause() {
        if(btnMonthCriteria != null)
            btnMonthCriteria.setVisibility(View.GONE);

        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
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

    // ----
    // User interaction
    // ----
    public void switchToTransactionCurr() {
        switchList = TRX_CURR;
        updateViewBySwitch();
//        btnTransactionCurrent.setBackgroundResource(R.drawable.tab_history_p);
//        btnTransactionLast.setBackgroundDrawable(null);
//        switchList = TRX_CURR;
//        lstTransactionLog.setAdapter(adapterTransactionLastLog);
//        txtTransactionCurrent.setTextColor(getResources().getColor(android.R.color.white));
//        txtTransactionLast.setTextColor(getResources().getColor(R.color.radio_tab_text));
    }

    public void switchToTransactionLast() {
        switchList = TRX_LAST;
        updateViewBySwitch();
//        btnTransactionCurrent.setBackgroundDrawable(null);
//        btnTransactionLast.setBackgroundResource(R.drawable.tab_history_p_right);
//        switchList = TRX_LAST;
//        lstTransactionLog.setAdapter(adapterTransactionCurrentLog);
//        txtTransactionCurrent.setTextColor(getResources().getColor(R.color.radio_tab_text));
//        txtTransactionLast.setTextColor(getResources().getColor(android.R.color.white));
    }

    public void onTransactionCurrItemClick(int position) {
        CreditCardTransaction currentTransactionCurr = adapterTransactionCurrentLog.getTransactionList().get(position);

        tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogDetailFragment detailFragment = new tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogDetailFragment.EXTRA_TRX, currentTransactionCurr);
        detailFragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(android.R.id.tabcontent, detailFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void onTransactionLastItemClick(int position) {
        CreditCardTransaction lastTransactionLast = adapterTransactionLastLog.getTransactionList().get(position);

        tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogDetailFragment detailFragment = new tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogDetailFragment.EXTRA_TRX, lastTransactionLast);
        detailFragment.setArguments(bundle);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(android.R.id.tabcontent, detailFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    // ----
    // Http
    // ----
    private void inquiryTransactionList() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得收到的紅包
            try {
                //本月
                Calendar calendar = Calendar.getInstance();


                String currentDate = CreditCardUtil.getToday(calendar);
                String monthOneDate = CreditCardUtil.getFirstMonthDay(calendar);
                CreditCardHttpUtil.queryCreditCardTransactionLog(monthOneDate, currentDate, queryStatus, responseListenerTransactionCurrent, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerTransactionCurrent = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            // ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                totalCount_CurrMonth = CreditCardResponseBodyUtil.getTotalCount(result.getBody());
                adapterTransactionCurrentLog.setTransactionList(CreditCardResponseBodyUtil.parseTransactionList(result.getBody()));

            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if(handleCommonError(result, (ActivityBase) getActivity())){
                    return;
                } else {
                    showAlert((ActivityBase) getActivity(), result.getReturnMessage());
                }
            }


            try {
                //上個月
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);
                String monthEndDate = CreditCardUtil.getLastMonthDay(calendar);
                String monthOneDate = CreditCardUtil.getFirstMonthDay(calendar);
                CreditCardHttpUtil.queryCreditCardTransactionLog(monthOneDate, monthEndDate, queryStatus, responseListenerTransactionLast, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private ResponseListener responseListenerTransactionLast = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                totalCount_LastMonth = CreditCardResponseBodyUtil.getTotalCount(result.getBody());
                adapterTransactionLastLog.setTransactionList(CreditCardResponseBodyUtil.parseTransactionList(result.getBody()));
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

    private void updateListView() {
        //clarifyList();

        adapterTransactionCurrentLog.notifyDataSetChanged();
        adapterTransactionLastLog.notifyDataSetChanged();
        updateViewBySwitch();
    }

    private void updateViewBySwitch() {
        if (switchList == TRX_CURR) {
            btnTransactionCurrent.setBackgroundResource(R.drawable.tab_history_p);
            btnTransactionLast.setBackgroundDrawable(null);
            switchList = TRX_CURR;
            lstTransactionLog.setAdapter(adapterTransactionCurrentLog);
            txtTransactionCurrent.setTextColor(getResources().getColor(android.R.color.white));
            txtTransactionLast.setTextColor(getResources().getColor(R.color.radio_tab_text));
        } else {
            btnTransactionCurrent.setBackgroundDrawable(null);
            btnTransactionLast.setBackgroundResource(R.drawable.tab_history_p_right);
            switchList = TRX_LAST;
            lstTransactionLog.setAdapter(adapterTransactionLastLog);
            txtTransactionCurrent.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtTransactionLast.setTextColor(getResources().getColor(android.R.color.white));
        }
    }


    public void onChangeMonthCriteria(int position) {
        idxMonthMenuItem = position;
        updateMonthView();
        queryStatus = GetQueryStatus(position);
        inquiryTransactionList();
    }

    private String GetQueryStatus(int position) {
        if(position == 0)
        {
            return "";
        }
        else
        {
            String statusName = adpaterMonthCriteria.getItem(idxMonthMenuItem).toString();
            return TransactionStatus.DescToEnum(statusName).getCode();
        }
    }


    private void createMonthCriteriaAdapter() {
        adpaterMonthCriteria = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item);

        adpaterMonthCriteria.add(getString(R.string.trx_latest_all));
        adpaterMonthCriteria.add(TransactionStatus.SUCCESS.getDescription());
        adpaterMonthCriteria.add(TransactionStatus.FAILURE.getDescription());
        adpaterMonthCriteria.add(TransactionStatus.RETURN.getDescription());
        adpaterMonthCriteria.add(TransactionStatus.READY.getDescription());
    }

    private void updateMonthView() {
        if(adpaterMonthCriteria != null)
            btnMonthCriteria.setText(adpaterMonthCriteria.getItem(idxMonthMenuItem).toString());
    }
    // ----
    // Class, Interface, enum
    // ----


}
