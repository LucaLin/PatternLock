package tw.com.taishinbank.ewallet.controller.sv;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class ReceivePaymentFragment extends ResultFragmentBase {

    private SVTransactionIn svTransactionIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        svTransactionIn = getArguments().getParcelable(ReceivePaymentActivity.EXTRA_SV_TRX_IN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 改inflate自己的View
        View view = inflater.inflate(R.layout.fragment_sv_result_scrollable, container, false);

        setViewHold(view);

        setViewContent();

        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // Private method
    // ----

    protected void setViewHold(View view) {
        super.setViewHold(view);

    }


    protected void setViewContent() {
        txtResultTitle.setText(R.string.sv_receive_payment);
        txtFromToWho.setText(svTransactionIn.getTxMemName());

        txtDate.setText(FormatUtil.toTimeFormatted(svTransactionIn.getCreateDate(), false));

        txtAmount.setText(FormatUtil.toDecimalFormatFromString(svTransactionIn.getAmount()));

        {//將圖片改成文字格式訊息，且有底線
            // 設定有底線的TextView行數、底線顏色、底線寬度
            lytSendingMsg.setVisibility(View.VISIBLE);
            lytSvResult.setVisibility(View.GONE);

            Resources resources = getResources();
            txtSendingMessageLine.setNumberOfLines(resources.getInteger(R.integer.red_envelope_sent_result_blessing_lines));
            txtSendingMessageLine.setLineColor(resources.getColor(R.color.red_envelope_divider));
            txtSendingMessageLine.setLineWidth(resources.getDimensionPixelSize(R.dimen.red_envelope_sent_result_divider_height));

            txtSendingMessage.setText(svTransactionIn.getSenderMessage());
        }

        imgSvResult.setVisibility(View.GONE);

        lytCautionArea.setVisibility(View.GONE);

        // 如果尚未回覆過訊息，顯示回覆訊息
        if(TextUtils.isEmpty(svTransactionIn.getReplyMessage())) {
            lytSvResultActionArea.setVisibility(View.VISIBLE);
        }
        btnAction1.setText(R.string.sv_home);
        btnAction2.setText(R.string.sv_account_history);

        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(String.valueOf(svTransactionIn.getTxMemNO()), imgPhoto);
    }

    protected void setListener() {
        //Go to home
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
        //Go to history
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
