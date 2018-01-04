package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.PaymentResultDetailAdapter;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResult;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class PaymentResultDetailFragment extends DialogFragment {
    private RedEnvelopeSentResult sentResult;

    private static final String ARG_RESULT = "arg_result";

    /**
     * 用來建立Fragment
     */
    public static PaymentResultDetailFragment newInstance(RedEnvelopeSentResult sentResult) {
        PaymentResultDetailFragment f = new PaymentResultDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT, sentResult);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sentResult = getArguments().getParcelable(ARG_RESULT);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.RedEnvelopeDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_result_detail, container, false);

        TextView textTitle = (TextView) view.findViewById(android.R.id.title);
        textTitle.setText(R.string.transfer_detail);

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        // 製作用來顯示分割線跟轉帳付款人的資訊
        View headerView = inflater.inflate(R.layout.fragment_sent_result_detail_list_header, null);
        TextView textName = (TextView) headerView.findViewById(R.id.text_name);
        TextView textAccount = (TextView) headerView.findViewById(R.id.text_account);
        TextView textAmount = (TextView) headerView.findViewById(R.id.text_amount);
        TextView textAmountTitle = (TextView) headerView.findViewById(R.id.text_amount_title);
        // 設置發送者的名字、帳戶、總額
        textName.setText(sentResult.getSender());
        if(TextUtils.isEmpty(sentResult.getAccount())){
            View layoutAccount = headerView.findViewById(R.id.layout_account);
            layoutAccount.setVisibility(View.GONE);
        }else {
            textAccount.setText(FormatUtil.toAccountFormat(sentResult.getAccount()));
        }
        textAmount.setText(FormatUtil.toDecimalFormatFromString(sentResult.getAmount(), true));
        textAmountTitle.setText(R.string.transfer_amount_total);
        // 設定頭像
        ImageView imagePhoto = (ImageView) headerView.findViewById(R.id.image_photo);
        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(sentResult.getSenderMem(), imagePhoto);
        // 隱藏成功或失敗的圖示
        ImageView imageResult = (ImageView) headerView.findViewById(R.id.image_result);
        imageResult.setVisibility(View.INVISIBLE);
        // 以header的形式加入listview
        listView.addHeaderView(headerView);
        listView.setHeaderDividersEnabled(false);

        // 設定收款人的資訊
        PaymentResultDetailAdapter adapter = new PaymentResultDetailAdapter(sentResult.getTxResult(), getActivity(), imageLoader);
        listView.setAdapter(adapter);

        // Watch for button clicks.
        Button button = (Button)view.findViewById(R.id.button_ok);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PaymentResultDetailFragment.this.dismiss();
            }
        });

        // 設定右上角日期時間字串
        TextView textTime = (TextView) view.findViewById(R.id.text_time);
        textTime.setText(getFormattedTimeString(sentResult.getCreateDate()));

        return view;
    }

    /**
     * 取得格式化的日期時間字串
     * @param timeString yyyyMMddHHmmss格式的時間字串
     */
    private String getFormattedTimeString(String timeString){
        String formattedString = "";
        if(!TextUtils.isEmpty(timeString)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                // 將上次登入時間字串轉成date物件
                Date originalTime = sdf.parse(timeString);

                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd\nHH:mm");
                formattedString = df.format(originalTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return formattedString;
    }
}