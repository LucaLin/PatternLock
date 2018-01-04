package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResult;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResultEach;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;

public class PaymentResultFragment extends ResultFragmentBase {

    private static final String TAG = "PaymentResultFragment";
    private static final String ARG_RESULT = "arg_result";
    private static final String ARG_ERROR_MESSAGE = "arg_error_message";
    private static final String TAG_DIALOG_FRAGMENT = "dialog_fragment";

    private OnRetryClickedListener listener;

    public static PaymentResultFragment newInstance(RedEnvelopeSentResult result){
        PaymentResultFragment fragment = new PaymentResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT, result);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentResultFragment newInstance(String result){
        PaymentResultFragment fragment = new PaymentResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, result);
        fragment.setArguments(args);
        return fragment;
    }

    private RedEnvelopeSentResult result;
    private String errorMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            result = getArguments().getParcelable(ARG_RESULT);
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setViewHold(view);

        setViewContent();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ----
    // Private method
    // ----

    protected void setViewHold(View view) {
        super.setViewHold(view);

    }


    protected void setViewContent() {
        RedEnvelopeSentResultEach[] results = null;
        if(result != null){
            results = result.getTxResult();
        }
        btnAction1.setText(R.string.sv_home);
        btnAction1.setOnClickListener(onGoSVHomeClickedListener);

        // 如果成功
        if(results != null && results.length > 0 && results[0].getResult().equalsIgnoreCase("Y")) {
            txtResultTitle.setText(R.string.pay_success);
            imgSvResult.setImageResource(R.drawable.ic_main_pay_succeed);
            txtResultTitle.setTextColor(getResources().getColor(R.color.sv_result_title_success));
            btnAction2.setText(R.string.sv_account_history);
            btnAction2.setOnClickListener(onGoSVHistoryClickedListener);
            lytSvResultActionArea.setVisibility(View.VISIBLE);
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
        // 如果失敗
        }else{
            lytSvResultActionArea.setVisibility(View.GONE);
            txtResultTitle.setText(R.string.pay_fail);
            txtResultTitle.setTextColor(getResources().getColor(R.color.sv_result_title_failure));
            imgSvResult.setImageResource(R.drawable.ic_main_pay_failed);
            btnAction2.setText(R.string.sv_try_again);
            btnAction2.setOnClickListener(onRetryClickedListener);
            txtErrorMessage.setVisibility(View.VISIBLE);
            if(results != null && results.length > 0) {
                txtErrorMessage.setText(results[0].getBancsMsg());
            }else{
                txtErrorMessage.setText(errorMessage);
            }
        }

        // 有詳情資料，就用來顯示收到的人明與頭像，否則隱藏人名跟頭像
        if(results != null && results.length > 0) {
            String toMemName = result.getTxResult()[0].getName();
            txtFromToWho.setText(getResources().getString(R.string.pay_to) + " " + toMemName);
            ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(results[0].getToMem(), imgPhoto);
        }else{
            lytNameArea.setVisibility(View.GONE);
            imgPhoto.setVisibility(View.INVISIBLE);
        }

        if(result != null) {
            // Set Date
            txtDate.setText(FormatUtil.toTimeFormatted(result.getCreateDate(), false));

            txtDollarSign.setVisibility(View.VISIBLE);
            txtAmount.setVisibility(View.VISIBLE);
            txtAmount.setText(FormatUtil.toDecimalFormatFromString(result.getAmount()));

            btnSvResultAction.setText(R.string.check_detail);
            btnSvResultAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_main_card_more, 0);
            btnSvResultAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    // Create and show the dialog.
                    DialogFragment newFragment = PaymentResultDetailFragment.newInstance(result);
                    newFragment.show(ft, TAG_DIALOG_FRAGMENT);
                }
            });
        }else{
            txtDate.setVisibility(View.INVISIBLE);
            lytMoneyArea.setVisibility(View.GONE);
        }

        // 設定有底線的TextView Disappear
        {
            lytSendingMsg.setVisibility(View.GONE);
            lytSvResult.setVisibility(View.VISIBLE);
        }

        lytCautionArea.setVisibility(View.GONE);
        txtCautionTitle.setVisibility(View.GONE);
        txtCautionContent.setVisibility(View.GONE);

    }

    private View.OnClickListener onGoSVHomeClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RedEnvelopeSentResultEach[] results = null;
            if(result != null){
                results = result.getTxResult();
            }
            if(results != null && results.length > 0 && results[0].getResult().equalsIgnoreCase("Y")) {
                goSVHome();
            }else {
                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.back_sv_main_alert_info), R.string.button_confirm, R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                goSVHome();
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
            startActivity(intent);
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

    private void goSVHome(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_MONEY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    // ----
    // Class, Interface, enum
    // ----

}
