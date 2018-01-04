package tw.com.taishinbank.ewallet.controller.extra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;

public class EarnResultFragment extends Fragment {

    public final static String EXTRA_COUPON_PROD = "EXTRA_COUPON_PROD";
    public final static String EXTRA_COUPON_CONTENT = "EXTRA_COUPON_CONTENT";
    public final static String EXTRA_NEXT_BUTTON_TEXT = "EXTRA_NEXT_BUTTON_TEXT";

    // -- View Hold --
    protected TextView txtMainMessage;
    protected TextView txtSecondMessage;

    protected ImageView imgCouponBanner;
    protected TextView txtCouponProd;
    protected TextView txtCouponContent;

    protected Button btnNext;

    public EarnResultFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_earn_result, container, false);

        // Set view hold
        txtMainMessage = (TextView) view.findViewById(R.id.txt_main_message);
        txtSecondMessage = (TextView) view.findViewById(R.id.txt_second_message);

        imgCouponBanner = (ImageView) view.findViewById(R.id.img_coupon_banner);
        txtCouponProd = (TextView) view.findViewById(R.id.txt_coupon_prod);
        txtCouponContent = (TextView) view.findViewById(R.id.txt_coupon_content);
        btnNext = (Button) view.findViewById(R.id.btn_next);

        // Set listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNext();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ---
    // My methods
    // ---

    // ----
    // User interaction
    // ----
    protected void clickNext() {
        //((EarnActivity) getActivity()).takeCoupon(couponEnter);
    }
}
