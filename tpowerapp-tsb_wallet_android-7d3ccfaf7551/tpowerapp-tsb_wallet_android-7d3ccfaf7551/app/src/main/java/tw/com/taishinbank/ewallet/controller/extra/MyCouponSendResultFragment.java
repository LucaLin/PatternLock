package tw.com.taishinbank.ewallet.controller.extra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.ContactDetailActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.extra.CouponSend;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class MyCouponSendResultFragment extends Fragment {

    private static final String ARG_RESULT = "arg_result";

    // -- View hold --
    private TextView txtDate;
    private ImageView imgPhoto;
    private TextView txtTo;
    private TextView txtTitle1;
    private TextView txtTitle2;

    private Button btnNext;

    // -- Data model --
    private CouponSend result;

    public static MyCouponSendResultFragment newInstance(CouponSend result){
        MyCouponSendResultFragment fragment = new MyCouponSendResultFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULT, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setCenterTitle(R.string.extra_my_coupon_button_send_coupon);
        result = getArguments().getParcelable(ARG_RESULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra_send_result, container, false);

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
        imgPhoto  = (ImageView) view.findViewById(R.id.img_photo);
        txtTo     = (TextView ) view.findViewById(R.id.txt_to);
        txtTitle1 = (TextView ) view.findViewById(R.id.txt_title1);
        txtTitle2 = (TextView ) view.findViewById(R.id.txt_title2);
        btnNext   = (Button   ) view.findViewById(R.id.btn_next);
    }


    protected void setViewContent() {
        txtDate.setText(FormatUtil.toTimeFormatted(result.getSentDate(), false));
        //TODO Photo
        txtTo.setText(result.getReceiver().getNickname());
        txtTitle1.setText(result.getCoupon().getTitle());
        txtTitle2.setText(result.getCoupon().getSubTitle());

        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(String.valueOf(result.getReceiver().getMemNO()), imgPhoto);
    }

    protected void setListener() {
        //Go to SV_HOME
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((MyCouponActivity) getActivity()).IsFromFriendDetail)
                {
                    Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                    intent.putExtra(ContactDetailActivity.EXTRA_CONTACT_DATA, ((MyCouponActivity) getActivity()).getFriendContact());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                    ((MyCouponActivity) getActivity()).gotoMyUsedCoupon();
            }
        });
    }

    // ----
    // Class, Interface, enum
    // ----


}
