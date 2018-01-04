package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.CouponSend;
import tw.com.taishinbank.ewallet.util.BrightnessUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.event.DownloadEvent;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

/**
 * 給店家掃描bar code / qr code 做交易
 */
public class MyCouponTradeFragment extends Fragment {

    private static final String TAG = "MyCouponTradeFragment";
    protected final static String ARG_COUPON = "ARG_COUPON";
    protected final int MARGIN_AUTOMATIC = -1;

    // -- View Hold --
    protected ImageView imgBRCode;
    protected ImageView imgQRCode;
    protected ImageView imgCouponAgency;
    protected Button btnTradeDone;

    // -- Data Model --
    protected Coupon coupon;
    private TextView txtCouponProd;
    private TextView txtValidDuration;
    private TextView txtCouponSerialNO;

    private BrightnessUtil brightnessUtil;

    public static MyCouponTradeFragment newInstance(Coupon coupon){
        MyCouponTradeFragment f = new MyCouponTradeFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_COUPON, coupon);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coupon = getArguments().getParcelable(ARG_COUPON);
        brightnessUtil = new BrightnessUtil(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra_coupon_barcode, container, false);

        // -- Set View hold --
        txtCouponProd = (TextView) view.findViewById(R.id.txt_coupon_prod);
        txtValidDuration = (TextView) view.findViewById(R.id.txt_valid_duration);
        imgBRCode = (ImageView) view.findViewById(R.id.img_bar_code);
        txtCouponSerialNO = (TextView) view.findViewById(R.id.txt_coupon_serialno);
        imgQRCode = (ImageView) view.findViewById(R.id.img_qr_code);
        btnTradeDone = (Button) view.findViewById(R.id.btn_trade_done);
        imgCouponAgency = (ImageView) view.findViewById(R.id.img_coupon_agency);

        // -- Set Coupon Agency Image --
        if(GlobalConst.DISABLE_DOWNLOAD_COUPON_AGENCY_IMAGE) {
            imgCouponAgency.setImageResource(R.drawable.ticket_express);
        } else {
            setCouponAgencyImage();
        }

        // -- Generate Barcode, QR code, SerialNo text --

        txtCouponSerialNO.setText(coupon.getSerialNO());

        GenerateQRCodeAsyncTask task = new GenerateQRCodeAsyncTask(
                coupon.getSerialNO(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getActivity().getResources().getDisplayMetrics()),  //Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getActivity().getResources().getDisplayMetrics()),  //Height
                MARGIN_AUTOMATIC,
                Color.BLACK, Color.TRANSPARENT, true);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        GenerateQRCodeAsyncTask task_2 = new GenerateQRCodeAsyncTask(
                coupon.getSerialNO(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getActivity().getResources().getDisplayMetrics()),  //Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56,  getActivity().getResources().getDisplayMetrics()),  //Height
                MARGIN_AUTOMATIC,
                Color.BLACK, Color.TRANSPARENT, false);
        task_2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // -- Set Listener --
        btnTradeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.extra_coupon_trade_done_message), android.R.string.ok, android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                couponTradeDone();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false);
            }
        });

        // -- Set View content --
        txtCouponProd.setText(coupon.getTitle());
        txtValidDuration.setText(FormatUtil.toDateFormatted(coupon.getStartDate().substring(0, 8)) + " - " + FormatUtil.toDateFormatted(coupon.getEndDate().substring(0, 8)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        brightnessUtil.toMaxBrightness();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onPause() {
        super.onPause();
        brightnessUtil.toOriginalBrightness();
    }

//    private void setCouponAgencyImage(){
//        String imagePath = ContactUtil.CouponAgencyFolderPath + File.separator + coupon.getTaSeq()  + ".jpg";
//
//        File file = new File(imagePath);
//        if(file.exists()) {
//            Calendar calendar = Calendar.getInstance();
//            long currentDate = calendar.getTime().getTime();
//            long createdDate = file.lastModified();
//            long diffDay = (currentDate - createdDate)/(1000*60*60*24);
//            if(diffDay >= 7 ) {
//                file.delete();
//                getCouponAgencyImage();
//            } else {
//                Bitmap imgBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                imgCouponAgency.setImageBitmap(imgBitmap);
//            }
//
//        } else {
//            getCouponAgencyImage();
//        }
//
//    }

    private void setCouponAgencyImage(){
        String imagePath = ExtraHttpUtil.getCouponAgenyImageSavePath(coupon.getTaSeq(), coupon.getIconUpdateTime());

        File file = new File(imagePath);
        if(file.exists()) {
            Bitmap imgBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgCouponAgency.setImageBitmap(imgBitmap);
        } else {

            File[] iconFiles = sharedMethods.findIconPaths(String.valueOf(coupon.getTaSeq()), ContactUtil.CouponAgencyFolderPath);
            if(iconFiles != null && iconFiles.length != 0) {
                for (File olderfile : iconFiles) {
                    if(olderfile.exists()) {
                        if(!olderfile.delete());
                        olderfile.delete();
                    }
                }
            }
            getCouponAgencyImage();
        }

    }

    private void couponTradeDone() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ExtraHttpUtil.tradedCoupon(coupon.getMsmSeq(), responseListener, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCouponAgencyImage() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ExtraHttpUtil.downloadCouponAgencyImage(coupon.getTaSeq(), coupon.getIconUpdateTime(), finishDownloadListener, getActivity());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // ----
    // Getter / Setter
    // ----


    // ----
    // ResponseListener
    // ----

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                coupon.setExchangeDate(ExtraResponseBodyUtil.getExchangeDate(result.getBody()));
                ((MyCouponActivity) getActivity()).gotoTradeResult(coupon);

            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private DownloadEvent.FinishDownloadListener finishDownloadListener = new DownloadEvent.FinishDownloadListener() {
        @Override
        public void onFinishDownload() {

            if(ExtraHttpUtil.isDownloadSuccess()) {
                ExtraHttpUtil.stopDownloadCoupon();
                setCouponAgencyImage();
            } else {
                ExtraHttpUtil.stopDownloadCoupon();
            }
        }

    };

    // ----
    // Class / Interface
    // ----
    public interface DetailConfirmListener{
        void onNextClicked(ArrayList<CouponSend> couponSendList);
    }

    public class GenerateQRCodeAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        String contentsToEncode;
        int imageWidth;
        int imageHeight;
        int marginSize;
        int color;
        int colorBack;
        boolean isQRCode = true;

        public GenerateQRCodeAsyncTask(String contentsToEncode, int imageWidth, int imageHeight, int marginSize, int color, int colorBack, boolean isQRCode) {
            this.contentsToEncode = contentsToEncode;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.marginSize = marginSize;
            this.color = color;
            this.colorBack = colorBack;
            this.isQRCode = isQRCode;
        }

        @Override
        protected void onPreExecute() {
            ((ActivityBase)getActivity()).showProgressLoading();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return generateBitmap(contentsToEncode, imageWidth, imageHeight, marginSize, color, colorBack);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(getActivity() == null) {
                return;
            }
            ((ActivityBase)getActivity()).dismissProgressLoading();
            if (bitmap == null) {
//                new AlertDialog.Builder(getActivity())
//                    .setMessage(R.string.error_qr_code_generate)
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .show();
                return;
            }

            if(isQRCode)
                imgQRCode.setImageBitmap(bitmap);
            else
                imgBRCode.setImageBitmap(bitmap);
        }

        protected Bitmap generateBitmap(@NonNull String contentsToEncode,
                                        int imageWidth, int imageHeight,
                                        int marginSize, int color, int colorBack)
                throws WriterException, IllegalStateException {

            Map<EncodeHintType, Object> hints = null;
            if (marginSize != MARGIN_AUTOMATIC) {
                hints = new EnumMap<>(EncodeHintType.class);
                // We want to generate with a custom margin size
                hints.put(EncodeHintType.MARGIN, marginSize);
            }

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix result;
            if(isQRCode)
                result = writer.encode(contentsToEncode, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);
            else
                result = writer.encode(contentsToEncode, BarcodeFormat.CODE_128, imageWidth, imageHeight, hints);

            final int width = result.getWidth();
            final int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? color : colorBack;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
    }
}
