package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class  ReceivePaymentResultFragment extends ResultFragmentBase {

    private SVTransactionIn svTransactionIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        svTransactionIn = getArguments().getParcelable(ReceivePaymentActivity.EXTRA_SV_TRX_IN);
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
        txtResultTitle.setText(R.string.reply_message_success);
        txtFromToWho.setText(getResources().getString(R.string.reply_message_to, getActivity()) + " " + svTransactionIn.getTxMemName());

        // Set Date
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        txtDate.setText(FormatUtil.toTimeFormatted(sdf.format(currentTime), false));

        txtDollarSign.setVisibility(View.GONE);
        txtAmount.setVisibility(View.GONE);

        {
            // 設定有底線的TextView Disapper
            lytSendingMsg.setVisibility(View.GONE);
            lytSvResult.setVisibility(View.VISIBLE);

            imgSvResult.setImageResource(R.drawable.ic_e_leave_message_succeed);
        }

        lytCautionArea.setVisibility(View.VISIBLE);
        txtCautionTitle.setVisibility(View.GONE);
        txtCautionContent.setVisibility(View.GONE);

        btnAction1.setText(R.string.sv_home);
        btnAction2.setText(R.string.sv_account_history);

        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(String.valueOf(svTransactionIn.getTxMemNO()), imgPhoto);
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
        //Go to pay
        btnAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_GO_SV_HISTORY, "");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                getActivity().finish();
            }
        });

        btnSvResultAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceivePaymentActivity act = (ReceivePaymentActivity) getActivity();
                act.replyMessage();
            }
        });
    }

    // ----
    // Class, Interface, enum
    // ----


}
