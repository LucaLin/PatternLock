package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.interfaces.TicketOrderType;
import tw.com.taishinbank.ewallet.model.extra.TicketDetailData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class MyTicketResultFragment extends Fragment {

    public interface OnEventListener
    {
        void OnResultNextClickEvent(int switchTo);
    }

    public enum ENUM_MODE_TYPE {
        TRADE,
        RETURN
    }

    private static final String ARG_MODE = "arg_mode";
    private static final String ARG_RESULT = "arg_result";
    //private static final String ARG_STATUS = "arg_status";

    // -- View hold --
    private TextView txtDate;
    private ImageView imgBanner;
    private TextView txtTitle1;
    private TextView txtTitle2;
    private TextView txtAmount;
    private TextView txtResultTitle;
    private Button btnNext;

    private LinearLayout lytMoneyArea;

    // -- Data model --
    private TicketDetailData result;
//    private String tradeStatus;
    private ENUM_MODE_TYPE modeType;

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener)
    {
        this.onEventListener = listener;
    }


    public static MyTicketResultFragment newInstance(ENUM_MODE_TYPE modeType, TicketDetailData result){
        MyTicketResultFragment fragment = new MyTicketResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, modeType.toString());
        args.putParcelable(ARG_RESULT, result);
//        args.putInt(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        modeType = ENUM_MODE_TYPE.valueOf(getArguments().getString(ARG_MODE));
        result = getArguments().getParcelable(ARG_RESULT);
        //tradeStatus = getArguments().getParcelable(ARG_STATUS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra_trade_result, container, false);

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
    private void showDialog(String message) {
        ((ActivityBase) getActivity()).showAlertDialog(message, android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }


    protected void setViewHold(View view) {
        txtDate   = (TextView ) view.findViewById(R.id.txt_date);
        imgBanner  = (ImageView) view.findViewById(R.id.img_coupon_banner);
        txtTitle1 = (TextView ) view.findViewById(R.id.txt_title1);
        txtTitle2 = (TextView ) view.findViewById(R.id.txt_title2);
        txtAmount = (TextView ) view.findViewById(R.id.txt_amount);
        btnNext   = (Button   ) view.findViewById(R.id.btn_next);
        lytMoneyArea = (LinearLayout) view.findViewById(R.id.lyt_money_area);
        txtResultTitle = (TextView) view.findViewById(R.id.txt_result_title);
    }


    protected void setViewContent() {

        if (modeType.equals(ENUM_MODE_TYPE.RETURN)) {
            if(TicketOrderType.RETURN.code.equals(result.getStatus())) {
                txtResultTitle.setText(getString(R.string.extra_my_ticket_return_success));
            } else {
                txtResultTitle.setText(getString(R.string.extra_my_ticket_return_fail));
            }

            btnNext.setText(getString(R.string.extra_my_ticket_go_to_list));
        } else {
            btnNext.setText(getString(R.string.extra_my_ticket_back_to_list));
        }

        txtDate.setText(FormatUtil.toTimeFormatted(result.getLastUpdate()));
        //TODO Photo
        txtTitle1.setText(result.getStoreName());
        txtTitle2.setText(result.getTitle());

        //Amount
        lytMoneyArea.setVisibility(View.VISIBLE);
        txtAmount.setText(FormatUtil.toDecimalFormat(result.getPrice()));

        //txtDate.getText()

        if(!TextUtils.isEmpty(result.getIconUrl())) {
            File imgURL = new File(result.getIconUrl());
            String imgFilePath = ContactUtil.TicketFolderPath + File.separator + imgURL.getName();


            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBanner.setImageBitmap(imgBitmap);
            } else {
                imgBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            imgBanner.setImageResource(R.drawable.img_banner_default);
        }
    }

    protected void setListener() {
        //Go to SV_HOME
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 至票券夾
                if (modeType.equals(ENUM_MODE_TYPE.RETURN)) {
                    onEventListener.OnResultNextClickEvent(MyTicketFragment.TICKET_RETURN);
                } else {
                    onEventListener.OnResultNextClickEvent(MyTicketFragment.TICKET_USED);
                }
            }
        });
    }

    // ----
    // Class, Interface, enum
    // ----


}
