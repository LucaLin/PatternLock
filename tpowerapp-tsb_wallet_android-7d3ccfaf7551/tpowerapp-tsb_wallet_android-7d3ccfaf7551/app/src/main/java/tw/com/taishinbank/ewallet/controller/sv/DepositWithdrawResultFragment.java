package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.DepositWithdrawResult;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;


public class DepositWithdrawResultFragment extends Fragment {

    private static final String TAG = "DepositWithdrawResultFragment";
    private static final String ARG_DEPOSIT_WITHDRAW_RESULT= "arg_deposit_withdraw_result";
    private static final String ARG_FROM_PAGE  = "arg_from_page";
    private static final String ARG_BACK_ORIGINAL_PROCESS  = "arg_back_original_process";

    public static final int FROM_DEPOSIT = 1;
    public static final int FROM_WITHDRAW = FROM_DEPOSIT + 1;

    private DepositWithdrawResult depositWithdrawResult;
    private int fromPage;
    private String originalProcess;

    private OnRetryClickedListener listener;

    /**
     * 用來建立Fragment
     */
    public static DepositWithdrawResultFragment newInstance(int fromPage, DepositWithdrawResult depositWithdrawResult) {
        DepositWithdrawResultFragment f = new DepositWithdrawResultFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_FROM_PAGE, fromPage);
        args.putParcelable(ARG_DEPOSIT_WITHDRAW_RESULT, depositWithdrawResult);
        f.setArguments(args);

        return f;
    }

    /**
     * 用來建立Fragment
     */
    public static DepositWithdrawResultFragment newInstance(int fromPage, DepositWithdrawResult depositWithdrawResult, String originalProcess) {
        DepositWithdrawResultFragment f = new DepositWithdrawResultFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_FROM_PAGE, fromPage);
        args.putParcelable(ARG_DEPOSIT_WITHDRAW_RESULT, depositWithdrawResult);
        args.putString(ARG_BACK_ORIGINAL_PROCESS, originalProcess);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromPage = getArguments().getInt(ARG_FROM_PAGE);
        depositWithdrawResult = getArguments().getParcelable(ARG_DEPOSIT_WITHDRAW_RESULT);
        originalProcess = getArguments().getString(ARG_BACK_ORIGINAL_PROCESS, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sv_result_deposit_withdraw, container, false);

        // Watch for button clicks.
        Button btnAction1 = (Button)view.findViewById(R.id.btn_action_1);
        Button btnAction2 = (Button)view.findViewById(R.id.btn_action_2);

        TextView textErrorMessage = (TextView) view.findViewById(R.id.txt_error_message);
        TextView textResultTitle= (TextView) view.findViewById(R.id.txt_result_title);
        ImageView imageResult = (ImageView) view.findViewById(R.id.img_sv_result);
        TextView textAmountTitle = (TextView) view.findViewById(R.id.txt_amount_title);
        TextView textAmount = (TextView) view.findViewById(R.id.txt_amount);

        TextView textLine1Title = (TextView) view.findViewById(R.id.txt_line1_title);
        TextView textLine1Amount = (TextView) view.findViewById(R.id.txt_line1_amount);
        TextView textLine2Title = (TextView) view.findViewById(R.id.txt_line2_title);
        TextView textLine2Amount = (TextView) view.findViewById(R.id.txt_line2_amount);
        View layoutResultAmount = view.findViewById(R.id.lyt_result_amount);

        String resultTitle;
        String formattedAmountLine1;
        String formattedAmountLine2;

        // 儲值
        if(fromPage == FROM_DEPOSIT){
            textAmountTitle.setText(R.string.sv_deposit);
            textLine1Title.setText(R.string.sv_original_amount);
            String titleLine2 = String.format(getString(R.string.sv_balance_after_action), getString(R.string.sv_deposit));
            textLine2Title.setText(titleLine2);
            double originalBalance = depositWithdrawResult.getBalance();
            if(depositWithdrawResult.isSuccess()){
                originalBalance -= depositWithdrawResult.getAmount();
            }
            formattedAmountLine1 = FormatUtil.toDecimalFormat(originalBalance, true);
            formattedAmountLine2 = FormatUtil.toDecimalFormat(depositWithdrawResult.getBalance(), true);
            resultTitle = getString(R.string.sv_deposit);

        // 提領
        }else{
            textAmountTitle.setText(R.string.sv_withdraw);
            String titleLine1 = String.format(getString(R.string.sv_balance_after_action), getString(R.string.sv_withdraw));
            textLine1Title.setText(titleLine1);
            textLine2Title.setText(R.string.sv_monthly_curr_after_withdraw);
            formattedAmountLine1 = FormatUtil.toDecimalFormat(depositWithdrawResult.getBalance(), true);
            formattedAmountLine2 = FormatUtil.toDecimalFormat(depositWithdrawResult.getMonthlyCurr(), true);
            resultTitle = getString(R.string.sv_withdraw);
        }
        // 設定金額文字
        String formattedAmount = FormatUtil.toDecimalFormat(depositWithdrawResult.getAmount(), true);
        textAmount.setText(formattedAmount);
        textLine1Amount.setText(formattedAmountLine1);
        textLine2Amount.setText(formattedAmountLine2);

        // 根據是否成功決定顯示內容
        if(depositWithdrawResult.isSuccess()){
            imageResult.setImageResource(R.drawable.ic_top_up_succeed);
            layoutResultAmount.setVisibility(View.VISIBLE);
            textErrorMessage.setVisibility(View.INVISIBLE);
            String formattedResultTitle = String.format(getString(R.string.sv_action_success), resultTitle);
            textResultTitle.setText(formattedResultTitle);
            textResultTitle.setTextColor(getResources().getColor(R.color.sv_result_title_success));

            // 如果要回原頁
            if(!TextUtils.isEmpty(originalProcess)) {
                btnAction2.setVisibility(View.GONE);
                btnAction1.setText(String.format(getString(R.string.sv_deposit_back_to_original_process), originalProcess));
                btnAction1.setOnClickListener(onBackOriginalProcessClickedListener);
            }else{
                btnAction1.setOnClickListener(onGoSVHomeClickedListener);
                btnAction2.setOnClickListener(onGoSVHistoryClickedListener);
            }

            // 更新儲值帳戶資訊
            if(getActivity() != null){
                // 更新儲值帳戶
                try {
                    RedEnvelopeHttpUtil.getSVAccountInfo(responseListenerSV, getActivity(), TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // DO NOTHING
                }
            }

        }else{
            imageResult.setImageResource(R.drawable.ic_top_up_failed);
            layoutResultAmount.setVisibility(View.INVISIBLE);
            textErrorMessage.setVisibility(View.VISIBLE);
            textErrorMessage.setText(depositWithdrawResult.getRtnMsg());
            String formattedResultTitle = String.format(getString(R.string.sv_action_fail), resultTitle);
            textResultTitle.setText(formattedResultTitle);
            textResultTitle.setTextColor(getResources().getColor(R.color.sv_result_title_failure));
            // 如果要回原頁
            if(!TextUtils.isEmpty(originalProcess)) {
                btnAction1.setText(String.format(getString(R.string.sv_deposit_back_to_original_process), originalProcess));
                btnAction1.setOnClickListener(onBackOriginalProcessClickedListener);
            }else {
                btnAction1.setOnClickListener(onGoSVHomeClickedListener);
            }
            btnAction2.setOnClickListener(onRetryClickedListener);
            btnAction2.setText(R.string.sv_try_again);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(fromPage == FROM_WITHDRAW) {
            ((ActivityBase) getActivity()).setCenterTitle(R.string.sv_withdraw);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private View.OnClickListener onGoSVHomeClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(depositWithdrawResult.isSuccess())
            {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_MONEY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
            {
                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.back_sv_main_alert_info), R.string.button_confirm, R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_MONEY);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                        ,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                        , true);

            }

        }
    };

    private View.OnClickListener onGoSVHistoryClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_GO_SV_HISTORY, "");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(fromPage == FROM_DEPOSIT){
                intent.putExtra(TransactionLogFragment.EXTRA_SWITCH_TO, TransactionLogFragment.TRX_IN);
            }
            else {
                intent.putExtra(TransactionLogFragment.EXTRA_SWITCH_TO, TransactionLogFragment.TRX_OUT);
            }
            startActivity(intent);
        }
    };

    private View.OnClickListener onBackOriginalProcessClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
        }
    };

    private View.OnClickListener onRetryClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onRetryClicked();
            }
        }
    };

    public void setListener(OnRetryClickedListener listener){
        this.listener = listener;
    }

    public interface OnRetryClickedListener{
        void onRetryClicked();
    }

    // 呼叫取得儲值帳戶資訊api的listener
    private ResponseListener responseListenerSV = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            // 成功的話，更新儲值帳戶資訊存sharedpreference
            // 失敗就算了
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                if(getActivity() != null) {
                    PreferenceUtil.setSVAccountInfo(getActivity(), result.getBody().toString());
                }
            }
        }
    };

}