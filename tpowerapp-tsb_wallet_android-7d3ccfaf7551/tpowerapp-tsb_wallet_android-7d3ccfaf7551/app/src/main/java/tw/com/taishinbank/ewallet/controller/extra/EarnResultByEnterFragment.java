package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONException;

import java.util.Calendar;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.CouponEnter;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class EarnResultByEnterFragment extends EarnResultFragment {

    public enum ENUM_IMAGE_SIZE
    {
        LARGE,
        MEDIUM,
        SMALL,
    }

    private static final String TAG = "EarnResultByEnterFragment";
    public final static String EXTRA_EARNED_COUPON = "EXTRA_EARNED_COUPON";

    private CouponEnter couponEnter;
    private Coupon coupon;

    private String nextButtonText;

    public ENUM_IMAGE_SIZE imageSize = ENUM_IMAGE_SIZE.MEDIUM;

    public EarnResultByEnterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null)
            return;

        couponEnter = getArguments().getParcelable(EXTRA_EARNED_COUPON);

        nextButtonText = getArguments().getString(EXTRA_NEXT_BUTTON_TEXT);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        if(width < 640)
            imageSize = ENUM_IMAGE_SIZE.SMALL;
        else if(width > 720)
            imageSize = ENUM_IMAGE_SIZE.LARGE;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);

        txtMainMessage.setText(getResources().getString(R.string.extra_earn_enter_take_success));
        txtSecondMessage.setText(FormatUtil.toTimeFormatted(Calendar.getInstance().getTime().toString(), false));

        txtCouponProd.setText("");
        txtCouponContent.setText("");
        btnNext.setText(nextButtonText);

        inquiryEntity();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ----
    // Public
    // ----
    public static EarnResultByEnterFragment newInstance(CouponEnter couponEnter, String nextButtonText) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_EARNED_COUPON, couponEnter);
        args.putString(EXTRA_NEXT_BUTTON_TEXT, nextButtonText);

        EarnResultByEnterFragment fragment = new EarnResultByEnterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ----
    // Http
    // ----
    private void inquiryEntity() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            showRetryAlert(getString(R.string.msg_no_available_network));
        } else {
            try {
                ExtraHttpUtil.queryCouponDetail(
                        String.valueOf(couponEnter.getCpSeq()),
                        String.valueOf(couponEnter.getMsmSeq()),
                        responseListenerEntity, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerEntity = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                coupon = new Gson().fromJson(result.getBody().toString(), Coupon.class);
                txtCouponProd.setText(coupon.getTitle());
                txtCouponContent.setText(coupon.getSubTitle());
                txtSecondMessage.setText(FormatUtil.toTimeFormatted(coupon.getCreateDate(), false));
//                setBannerImage();

            } else {
                // 如果不是共同error
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showRetryAlert(result.getReturnMessage());
                }
            }
        }
    };
//
//    private void imageDownload(String imageUrl)
//    {
//        // 如果沒有網路連線，顯示提示對話框
//        if (!NetworkUtil.isConnected(getActivity())) {
//            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }, true);
//        } else {
//            try {
//                //  fileName = "WLTCouponUpload/wltcoupon_20160130145138/Image/wltcoupon_20160130145138_image_L.jpg";
//                // ((ActivityBase) getActivity()).showProgressLoading();
//                //GeneralHttpUtil generalHttpUtil = new GeneralHttpUtil();
//                GeneralHttpUtil.downloadCoupon(imageUrl, finishDownloadListener, getActivity());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private DownloadEvent.FinishDownloadListener finishDownloadListener = new DownloadEvent.FinishDownloadListener() {
//        @Override
//        public void onFinishDownload() {
//            GeneralHttpUtil.stopDownloadCoupon();
//            setBannerImage();
//        }
//    };

    // ----
    // private method
    // ----
    protected void showRetryAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.try_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inquiryEntity();
                    }
                })
                .setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                }).show();
    }
//
//    private void startImageDownload(String imageUrl) {
//        File imgFile = new File(ContactUtil.FolderPath + File.separator + imageUrl);
//        if (!imgFile.exists()) {
//            imageDownload(imageUrl);
//        }
//    }
//
//
//    private void setBannerImage() {
//
//        File imgFile;
//        switch (imageSize.toString())
//        {
//            case "LARGE":
//                imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathL());
//                if(imgFile.exists()) {
//                    Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imgCouponBanner.setImageBitmap(imgBitmap);
//                } else {
//                    startImageDownload(coupon.getImagePathL());
//                }
//                break;
//
//            case "MEDIUM":
//                imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathM());
//                if(imgFile.exists()) {
//                    Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imgCouponBanner.setImageBitmap(imgBitmap);
//                } else {
//                    startImageDownload(coupon.getImagePathM());
//                }
//                break;
//
//            case "SMALL":
//                imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathS());
//                if(imgFile.exists()) {
//                    Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imgCouponBanner.setImageBitmap(imgBitmap);
//                } else {
//                    startImageDownload(coupon.getImagePathS());
//                }
//                break;
//        }
//
//    }

    // ----
    // User interaction
    // ----
    @Override
    protected void clickNext() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), MyCouponActivity.class);
        startActivity(intent);

        getActivity().finish();
    }
}
