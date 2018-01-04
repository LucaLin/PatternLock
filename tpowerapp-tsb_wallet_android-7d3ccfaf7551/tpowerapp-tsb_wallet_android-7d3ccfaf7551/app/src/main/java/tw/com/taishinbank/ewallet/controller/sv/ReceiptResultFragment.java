package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.sv.ReceiptResult;
import tw.com.taishinbank.ewallet.model.sv.TxResult;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class ReceiptResultFragment extends ResultFragmentBase {

    private static final String ARG_RESULT = "arg_result";

    public static ReceiptResultFragment newInstance(ReceiptResult result){
        ReceiptResultFragment fragment = new ReceiptResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT, result);
        fragment.setArguments(args);
        return fragment;
    }

    private ReceiptResult result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getArguments().getParcelable(ARG_RESULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setViewHold(view);

        setViewContent();

        setListener();

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
        List<TxResult> results = result.getTxResult();
        if(result.getResult().equalsIgnoreCase("Y")) {
            txtResultTitle.setText(R.string.receipt_success);
            if(results.size() > 1) {
                imgSvResult.setImageResource(R.drawable.ic_main_invite_people_succeed);
            }else{
                imgSvResult.setImageResource(R.drawable.ic_main_invite_person_succeed);
            }
        }else{
            txtResultTitle.setText(R.string.receipt_fail);
            imgSvResult.setImageResource(R.drawable.ic_main_pay_failed);
        }

        // TODO
        String toMemName = "";
        if(results != null){
            toMemName = concatNames(results);
            // 如果是均分收款（多人）
            if(results.size() > 1){
                imgPhoto.setImageResource(R.drawable.img_taishin_photo_dark);

            // 如果是收款（單人）
            }else {
                ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
                imageLoader.loadImage(String.valueOf(results.get(0).getTxMemNO()), imgPhoto);

            }
        }
        txtFromToWho.setText(getResources().getString(R.string.pay_to, getActivity()) + " " + toMemName);
        txtFromToWhoCount.setText(getNamesNumberString(results));

        // Set Date
        txtDate.setText(FormatUtil.toTimeFormatted(result.getCreateDate(), false));

        txtDollarSign.setVisibility(View.VISIBLE);
        txtAmount.setVisibility(View.VISIBLE);
        txtAmount.setText(FormatUtil.toDecimalFormat(result.getAmount()));

        // 設定有底線的TextView Disappear
        {
            lytSendingMsg.setVisibility(View.GONE);
            lytSvResult.setVisibility(View.VISIBLE);
        }

        lytSvResultActionArea.setVisibility(View.GONE);
        lytCautionArea.setVisibility(View.GONE);
        txtCautionTitle.setVisibility(View.GONE);
        txtCautionContent.setVisibility(View.GONE);

        btnAction1.setText(R.string.sv_home);
        btnAction2.setText(R.string.sv_account_history);
    }

    protected void setListener() {
        //Go to SV_HOME
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_MONEY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                getActivity().finish();
            }
        });
        //Go to record
        btnAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GO_SV_HISTORY, "");
                intent.putExtra(TransactionLogFragment.EXTRA_SWITCH_TO, TransactionLogFragment.TRX_IN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                getActivity().finish();
            }
        });

        //Go to SV_HOME
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_MONEY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public static String concatNames(List<TxResult> list) {
        StringBuilder sb = new StringBuilder("");

        boolean isFirst = true;
        for (TxResult c : list) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }
            sb.append(c.getName());
        }

        return sb.toString();
    }

    public static String getNamesNumberString(List<TxResult> list){
        if(list != null && list.size() > 1) {
            return "(" + list.size() + ")";
        }
        return "";
    }
    // ----
    // Class, Interface, enum
    // ----

}
