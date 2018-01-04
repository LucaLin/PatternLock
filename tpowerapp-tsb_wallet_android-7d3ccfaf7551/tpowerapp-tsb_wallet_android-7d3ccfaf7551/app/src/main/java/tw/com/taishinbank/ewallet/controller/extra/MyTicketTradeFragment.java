package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.CouponSend;
import tw.com.taishinbank.ewallet.model.extra.TicketDetailData;
import tw.com.taishinbank.ewallet.util.BrightnessUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

/**
 * 給店家掃描bar code / qr code 做交易
 */
public class MyTicketTradeFragment extends Fragment {

    public interface OnEventListener
    {
        void OnTradedEvent(TicketDetailData ticketData);
    }


    private static final String TAG = "MyTicketTradeFragment";
    protected final static String ARG_TICKET = "ARG_TICKET";
    protected final int MARGIN_AUTOMATIC = -1;

    // -- View Hold --
    protected ImageView imgBRCode;
    protected ImageView imgQRCode;
    private TextView textTicketNumber;
    // -- Data Model --
    protected TicketDetailData ticket;

    private BrightnessUtil brightnessUtil;

    //
    private Handler handler=new Handler();
    //
    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener)
    {
        this.onEventListener = listener;
    }

    public static MyTicketTradeFragment newInstance(TicketDetailData ticketDetailData){
        MyTicketTradeFragment f = new MyTicketTradeFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_TICKET, ticketDetailData);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ticket = getArguments().getParcelable(ARG_TICKET);
        brightnessUtil = new BrightnessUtil(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra_ticket_barcode, container, false);

        // -- Set View hold --
        imgBRCode = (ImageView) view.findViewById(R.id.img_bar_code);
        imgQRCode = (ImageView) view.findViewById(R.id.img_qr_code);
        textTicketNumber = (TextView) view.findViewById(R.id.txt_ticket_id);
        textTicketNumber.setText(ticket.getSerialNo());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CreareBarcode();
        brightnessUtil.toMaxBrightness();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        brightnessUtil.toOriginalBrightness();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void CreareBarcode()
    {

        // -- Generate Barcode, QR code --
        GenerateQRCodeAsyncTask task = new GenerateQRCodeAsyncTask(
                ticket.getSerialNo(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getActivity().getResources().getDisplayMetrics()),  //Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getActivity().getResources().getDisplayMetrics()),  //Height
                MARGIN_AUTOMATIC,
                Color.BLACK, Color.TRANSPARENT, true);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        GenerateQRCodeAsyncTask task_2 = new GenerateQRCodeAsyncTask(
                ticket.getSerialNo(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getActivity().getResources().getDisplayMetrics()),  //Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56,  getActivity().getResources().getDisplayMetrics()),  //Height
                MARGIN_AUTOMATIC,
                Color.BLACK, Color.TRANSPARENT, false);
        task_2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        monitorPaymentResult();
    }

    private void monitorPaymentResult()
    {
        handler.postDelayed(runnable, 5000);
    }

    private Runnable runnable=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            getTradeStatus();
        }
    };
    // ----
    // Getter / Setter
    // ----


    // ----
    // ResponseListener
    // ----

    private void getTradeStatus() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            showRetryAlert(getString(R.string.msg_no_available_network));
        } else {
            try {
             //   ((ActivityBase) getActivity()).showProgressLoading();
                ExtraHttpUtil.queryTicketDetail(
                        ticket.getEtkSeq(),
                        responseListenerEntity, getActivity(), TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void showRetryAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.try_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTradeStatus();
                    }
                })
                .setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                }).show();
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerEntity = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

//            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                TicketDetailData checkTicketData = ExtraResponseBodyUtil.getTicketDetail(result.getBody());
                checkTradeStatus(checkTicketData);

            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showRetryAlert(result.getReturnMessage());
                }
            }
        }
    };

    private void checkTradeStatus(TicketDetailData checkTicketData) {
        if(checkTicketData.getStatus().equals("1")) {
            onEventListener.OnTradedEvent(checkTicketData);
        } else {
            monitorPaymentResult();
        }

    }

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
                new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.error_qr_code_generate)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
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
