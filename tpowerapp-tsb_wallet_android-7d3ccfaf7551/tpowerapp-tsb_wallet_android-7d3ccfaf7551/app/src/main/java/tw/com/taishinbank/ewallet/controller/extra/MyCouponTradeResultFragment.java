package tw.com.taishinbank.ewallet.controller.extra;

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
import android.widget.TextView;

import java.io.File;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class MyCouponTradeResultFragment extends Fragment {

    private static final String ARG_RESULT = "arg_result";

    // -- View hold --
    private TextView txtDate;
    private ImageView imgBanner;
    private TextView txtTitle1;
    private TextView txtTitle2;

    private Button btnNext;

    // -- Data model --
    private Coupon result;

    // -- Helper --
    private ImageLoader imageLoader;

    public static MyCouponTradeResultFragment newInstance(Coupon result){
        MyCouponTradeResultFragment fragment = new MyCouponTradeResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getArguments().getParcelable(ARG_RESULT);
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

    protected void setViewHold(View view) {
        txtDate   = (TextView ) view.findViewById(R.id.txt_date);
        imgBanner  = (ImageView) view.findViewById(R.id.img_coupon_banner);
        txtTitle1 = (TextView ) view.findViewById(R.id.txt_title1);
        txtTitle2 = (TextView ) view.findViewById(R.id.txt_title2);
        btnNext   = (Button   ) view.findViewById(R.id.btn_next);
    }


    protected void setViewContent() {
        txtDate.setText(FormatUtil.toTimeFormatted(result.getExchangeDate(), false));
        //TODO Photo
        txtTitle1.setText(result.getTitle());
        txtTitle2.setText(result.getSubTitle());


        String imgFilePath = null;
        switch (((MyCouponActivity) getActivity()).imageSize.toString())
        {
            case "LARGE":
                if(!TextUtils.isEmpty(result.getImagePathL())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + result.getImagePathL();
                }
                break;

            case "MEDIUM":
                if(!TextUtils.isEmpty(result.getImagePathM())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + result.getImagePathM();
                }
                break;

            case "SMALL":
                if(!TextUtils.isEmpty(result.getImagePathS())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + result.getImagePathS();
                }
                break;

        }
        if(imgFilePath != null) {
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
                ((MyCouponActivity) getActivity()).gotoMyUsedCoupon();
            }
        });
    }

    // ----
    // Class, Interface, enum
    // ----


}
