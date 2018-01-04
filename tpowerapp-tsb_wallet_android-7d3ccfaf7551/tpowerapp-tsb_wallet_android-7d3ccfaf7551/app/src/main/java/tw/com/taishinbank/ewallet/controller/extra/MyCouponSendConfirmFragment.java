package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.extra.MyCouponSendConfirmAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.DetailConfirmFragmentBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.CouponSend;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

/**
 * 畫面繼承自"DetailConfirmFragmentBase"，只要顯示訊息以及發送列表就好。
 * 列表部份”金額”變成”優惠券標題” -  Adapter 另外設定
 *
 * @see
 * DetailConfirmFragmentBase
 */
public class MyCouponSendConfirmFragment extends DetailConfirmFragmentBase {

    private static final String TAG = "MyCouponSendConfirmFragment";
    protected final static String ARG_COUPON = "ARG_COUPON";

    // -- Data Model --
    protected Coupon coupon;
    protected ArrayList<CouponSend> couponSendList;

    // -- Helper --
    protected MyCouponSendConfirmAdapter adapter;

    // -- Callback listener --
    protected DetailConfirmListener detailConfirmListener;

    public static MyCouponSendConfirmFragment newInstance(String message, Coupon coupon, ArrayList<LocalContact> friendList){
        MyCouponSendConfirmFragment f = new MyCouponSendConfirmFragment();

        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putParcelable(ARG_COUPON, coupon);
        args.putParcelableArrayList(ARG_FRIEND_LIST, friendList);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setCenterTitle(R.string.extra_my_coupon_button_send_coupon);

        //Coupon is one of the necessary data....
        //If throw null pointer exception, there is no need to go further process.
        coupon = getArguments().getParcelable(ARG_COUPON);
        CouponSend couponSend = new CouponSend(friendList.get(0), coupon);

        couponSendList = new ArrayList<>();
        couponSendList.add(couponSend);

        adapter = new MyCouponSendConfirmAdapter(getActivity());
        adapter.setList(couponSendList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setViewContent();
        return view;
    }

    @Override
    protected void setViewContent() {
        super.setViewContent();
        textPeopleCount.setVisibility(View.GONE);
        textTotalMoney.setVisibility(View.GONE);
        textLegend.setText(R.string.extra_my_coupon_give_detail);
        buttonNext.setText(R.string.extra_my_coupon_button_send_coupon);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("CouponList", "onDestroyView()");

        if (getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ----
    // User interaction
    // ----
    @Override
    protected void onNextClick() {
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ExtraHttpUtil.sendMessage(coupon.getCpSeq(), coupon.getMsmSeq(), message, friendList.get(0).getMemNO(), messageEnterResponseListener, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                Log.w("MyCoupon", e.getMessage(), e);
                return;
            }
        }
    }


    // ----
    // Http
    // ----
    private ResponseListener messageEnterResponseListener = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 往下一步走
                for (CouponSend couponSend : couponSendList) {
                    couponSend.setSentDate(ExtraResponseBodyUtil.getSenderDate(result.getBody()));
                }
                if (detailConfirmListener != null)
                    detailConfirmListener.onNext(couponSendList);

            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    // ----
    // Getter / Setter
    // ----
    public void setListener(DetailConfirmListener listener) {
        this.detailConfirmListener = listener;
    }

    // ----
    // Class / Interface
    // ----
    public interface DetailConfirmListener{
        void onNext(ArrayList<CouponSend> couponSendList);
    }

}
