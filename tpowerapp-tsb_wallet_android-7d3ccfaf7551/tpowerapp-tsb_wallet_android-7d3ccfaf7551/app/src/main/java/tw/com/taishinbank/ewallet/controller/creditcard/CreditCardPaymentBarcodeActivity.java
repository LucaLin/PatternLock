package tw.com.taishinbank.ewallet.controller.creditcard;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;
import tw.com.taishinbank.ewallet.util.BrightnessUtil;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.DateTimeUtil;
import tw.com.taishinbank.ewallet.util.E7PayUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class CreditCardPaymentBarcodeActivity extends ActivityBase implements View.OnClickListener{

    public static final String EXTRA_CARD_DATA = "extra_card_data";
    //public static final String EXTRA_PAYMENT_DATA = "extra_payment_data";
    static public int MARGIN_AUTOMATIC = -1;

    private ImageView imgQRCode, imgBRCode;
    private TextView txtBacode;
    private E7PayUtil e7PayUtil;

//    private Timer timer = new Timer();
//    private TimerTask task;
    private Handler handler=new Handler();
    private CountDownTimer countdownTimer;
    public static final int TEN_MINUTES = 100*1000;
    public static final int ONE_SECOND = 1000;
    private TextView textCountdownTime;
    private Button buttonRefresh;

    private CreditCardTransaction inputCreditCardTransaction;
    private CreditCardData cardData;
    private String strBarcode;

    private BrightnessUtil brightnessUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_payment_barcode);
        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.payment_barcode_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        imgQRCode = (ImageView) findViewById(R.id.img_qr_code);
        imgBRCode = (ImageView) findViewById(R.id.img_br_code);
        txtBacode = (TextView) findViewById(R.id.txt_barcod);
        textCountdownTime = (TextView) findViewById(R.id.text_countdown_time);
        buttonRefresh =(Button) findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(this);

        brightnessUtil = new BrightnessUtil(this);
//        CreateCountDownTimer();
//        CreatePaymentBarcode();

       // monitorPaymentResult("1232121","123213");
    }



    @Override
    protected void onResume() {
        super.onResume();
        CreateCountDownTimer();
        CreatePaymentBarcode();

        brightnessUtil.toMaxBrightness();
    }

    private void CreatePaymentBarcode()
    {

        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);

        }

        showProgressLoading();

        // 停止timer
        countdownTimer.cancel();
        // 重設時間文字與按鈕
        String str = DateTimeUtil.convertMilliSecondsToMmSs(TEN_MINUTES);
        textCountdownTime.setText(str);

        //if(cardData == null)
        cardData = getIntent().getParcelableExtra(EXTRA_CARD_DATA);


        e7PayUtil = new E7PayUtil(this);
        e7PayUtil.setGetPaymentActionListener(onGetPaymentActionListener);
        e7PayUtil.GetPaymentBarcode(cardData.getToken(), PreferenceUtil.getMemNO(this));

    }

    E7PayUtil.OnGetPaymentActionListener onGetPaymentActionListener = new E7PayUtil.OnGetPaymentActionListener() {
        @Override
        public void SendPaymentBarcode(String Barcode, String CardToken) {
            dismissProgressLoading();
            txtBacode.setText(Barcode);
            //Generate QR Code
            //String stringToGen = "This is a code generate example....";
            GenerateQRCodeAsyncTask task = new GenerateQRCodeAsyncTask(
                    Barcode,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getApplication().getResources().getDisplayMetrics()),  //Width
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getApplication().getResources().getDisplayMetrics()),  //Height
                    MARGIN_AUTOMATIC,
                    Color.BLACK, Color.TRANSPARENT, true);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            GenerateQRCodeAsyncTask task_2 = new GenerateQRCodeAsyncTask(
                    Barcode,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 195, getApplication().getResources().getDisplayMetrics()),  //Width
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getApplication().getResources().getDisplayMetrics()),  //Height
                    MARGIN_AUTOMATIC,
                    Color.BLACK, Color.TRANSPARENT, false);
            task_2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            countdownTimer.start();
            buttonRefresh.setEnabled(true);
            monitorPaymentResult(Barcode);

        }

        @Override
        public void SendWalletPaymentResponse(String storeName, String merchantTradeDate, int tradeAmount, String cardNumberShelter, String cardName, int tradeStatus, String tradeStatusName) {
//            timer.cancel(); // 關閉監控


            inputCreditCardTransaction = new CreditCardTransaction();
            inputCreditCardTransaction.setStoreName(storeName);
            inputCreditCardTransaction.setMerchantTradeDate(merchantTradeDate);
            inputCreditCardTransaction.setTradeAmount(tradeAmount);
            inputCreditCardTransaction.setCardNumberShelter(cardNumberShelter);
            inputCreditCardTransaction.setCardName(cardName);
            inputCreditCardTransaction.setTradeStatus(tradeStatus);
            inputCreditCardTransaction.setTradeStatusName(tradeStatusName);
            ToPaymentResult(tradeStatus);
        }

        @Override
        public void GetBarcodeError(int errorCode, String message) {

            if(errorCode == 7106)
            {

                String alertMessage = cardData.getCardName() + FormatUtil.toHideCardNumberString(cardData.getCardNumber())
                        + "\n此卡片已經失效";

                showAlertDialog(alertMessage, R.string.result_appbar_delete, R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreditCardUtil.DB_delete(CreditCardPaymentBarcodeActivity.this, cardData);
                        Intent intent = new Intent(CreditCardPaymentBarcodeActivity.this, CreditCardResultPageActivity.class);
                        intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,
                                CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.DELETE_CREDIT_CARD_PAGE.toString());
                        intent.putExtra(CreditCardData.EXTRA_CREDIT_CARD_DATA, cardData);

                        if (intent != null) {
                            // TODO 確認什麼時候要改變這個flag
                            startActivity(intent);
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, false);
            }
            else {
                showAlertDialog(message, R.string.button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, false);
            }
        }

        @Override
        public void UpdateCardToken(String cardToken) {
            UpdateCardTokenToDB(cardToken);
        }

        @Override
        public void GetWalletPymentResponseFailed() {
            handler.postDelayed(runnable, 5000);
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        countdownTimer.cancel(); //停止時間
        brightnessUtil.toOriginalBrightness();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if(viewId == R.id.button_refresh)
        {

            buttonRefresh.setEnabled(false);
            handler.removeCallbacks(runnable);
            countdownTimer.cancel(); //停止時間

            CreatePaymentBarcode();

        }

    }

    private void CreateCountDownTimer()
    {
        if(countdownTimer == null) {
            // TODO refactor
            countdownTimer = new CountDownTimer(TEN_MINUTES, ONE_SECOND) {

                public void onTick(long millisUntilFinished) {
                    millisUntilFinished -= ONE_SECOND;
                    String str = DateTimeUtil.convertMilliSecondsToMmSs(millisUntilFinished);
                    textCountdownTime.setText(str);
                }

                @Override
                public void onFinish() {
                    handler.removeCallbacks(runnable);
                }

            };
        }
    }

    private void UpdateCardTokenToDB(String cardToken)
    {
        if (cardData == null)
            return;

        cardData.setToken(cardToken);
        CreditCardUtil.DB_Updata(this, cardData);

        //Refresh
        buttonRefresh.setEnabled(false);
        countdownTimer.cancel(); //停止時間
        handler.removeCallbacks(runnable);

        CreatePaymentBarcode();
    }

    private void monitorPaymentResult(String barcode)
    {
        strBarcode = barcode;
        handler.postDelayed(runnable, 5000);

//        task = new TimerTask() {
//            @Override
//            public void run() {
//                e7PayUtil.GetWalletPymentResponse(cardToken, barcode);
//                // TODO Auto-generated method stub
//            }
//        };
//        timer.schedule(task, 5000, 5000);
    }

    private void ToPaymentResult(int tradeStatus )
    {

        Intent intent = new Intent(getApplication(), CreditCardResultPageActivity.class);
        if(tradeStatus == 0) {
            handler.postDelayed(runnable, 5000);
            return;
        }else if(tradeStatus == 1) {
            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,
                    CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.CREDIT_CARD_PAY_SUCCESS.toString());
        }
        else {
            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,
                    CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.CREDIT_CARD_PAY_FAIL.toString());
        }

        intent.putExtra(CreditCardResultPageActivity.EXTRA_PAYMENT_DATA, inputCreditCardTransaction);


        countdownTimer.cancel(); //停止時間
        if (intent != null) {
            // TODO 確認什麼時候要改變這個flag
            startActivity(intent);
        }

    }

    private Runnable runnable=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            e7PayUtil.GetWalletPymentResponse(cardData.getToken(), strBarcode);
        }
    };

    // ----
    // Interface, class, implements and so on....
    // ----
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
            showProgressLoading();
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
            dismissProgressLoading();
            if (bitmap == null) {
                showAlertDialog(getString(R.string.error_qr_code_generate));
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