package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.creditcard.CreditCardTradeType;
import tw.com.taishinbank.ewallet.interfaces.creditcard.TransactionStatus;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;
import tw.com.taishinbank.ewallet.util.BrightnessUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class TransactionLogDetailFragment extends Fragment {

    public static final String EXTRA_TRX = "EXTRA_TRX";

    // -- View Hold --
    //  - Common -
    private TextView txtSubtitle;
    private ImageView imgPhoto;
    private TextView textName;
    private TextView textAmount;
    private TextView textCardName;
    private TextView textStoreNameDetail;
    private TextView textTime;
    private TextView textReturnMessage;
    private TextView textProblemMessage;
    private LinearLayout buttonCustomerService;

    private TextView textOrderID;
    private TextView textBuyDate, textTransDate, textReturnDate;

    private TextView textAuthID;
    private TextView textTradeNo;

    private TextView textButtonReturn;

    private LinearLayout linearLayoutOrderId, linearLayoutBuyDate, linearLayoutTransDate, linearLayoutReturnDate,
                         linearLayoutCreditCard, linearLayoutStoreDetail, linearLayoutMessage, linearLayoutMessageTicket,
                         linearLayoutAuthID, linearLayoutTradeNo;


    // -- Return --
    static public int MARGIN_AUTOMATIC = -1;
    private ImageView imgBRCode;

    // -- Data Model --
    private CreditCardTransaction transaction;

    // -- View helper --
    private ImageLoader imageLoader;

    private BrightnessUtil brightnessUtil;
    private boolean isClickRetrun = false;

    public TransactionLogDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        transaction = getArguments().getParcelable(EXTRA_TRX);
        brightnessUtil = new BrightnessUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_creditcard_transaction_log_detail, container, false);

        // Set View Hold
        setViewHold(view);
        setViewContent();

        // Helper
        imageLoader = new ImageLoader(getActivity(), getActivity().getResources().getDimensionPixelSize(R.dimen.list_photo_size));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityBase) getActivity()).setCenterTitle(R.string.credit_trans_history);
        if(isClickRetrun) {
            brightnessUtil.toMaxBrightness();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isClickRetrun) {
            brightnessUtil.toOriginalBrightness();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ----
    // Private method
    // ----
    private void setViewHold(View view) {
        txtSubtitle = (TextView)    view.findViewById(R.id.txt_subtitle);
        imgPhoto   = (ImageView)    view.findViewById(R.id.image_photo);
        textName   = (TextView)     view.findViewById(R.id.text_name);
        textAmount = (TextView)     view.findViewById(R.id.text_amount);
        textTime   = (TextView)     view.findViewById(R.id.text_time);
        textCardName = (TextView)   view.findViewById(R.id.text_cardname);
        textOrderID = (TextView)   view.findViewById(R.id.text_order_id);
        textBuyDate = (TextView)   view.findViewById(R.id.text_buy_date);
        textTransDate = (TextView)   view.findViewById(R.id.text_trans_date);
        textReturnDate = (TextView)   view.findViewById(R.id.text_return_date);
        textReturnMessage = (TextView)   view.findViewById(R.id.text_message_return);
        textProblemMessage = (TextView)   view.findViewById(R.id.text_message_problem);
        textAuthID = (TextView)   view.findViewById(R.id.text_authid);
        textTradeNo = (TextView)   view.findViewById(R.id.text_tradeno);
        buttonCustomerService = (LinearLayout) view.findViewById(R.id.button_customer_service);
        textButtonReturn = (TextView) view.findViewById(R.id.text_go_return);

        textStoreNameDetail = (TextView)   view.findViewById(R.id.text_storename_detail);
        linearLayoutBuyDate = (LinearLayout) view.findViewById(R.id.linearlayout_buydate_info);
        linearLayoutOrderId = (LinearLayout) view.findViewById(R.id.linearlayout_orderid_info);
        linearLayoutTransDate = (LinearLayout) view.findViewById(R.id.linearlayout_transdate_info);
        linearLayoutReturnDate = (LinearLayout) view.findViewById(R.id.linearlayout_returndate_info);
        linearLayoutCreditCard = (LinearLayout) view.findViewById(R.id.linearlayout_returndate_info);
        linearLayoutStoreDetail = (LinearLayout) view.findViewById(R.id.linearlayout_store_detail);
        linearLayoutMessage = (LinearLayout) view.findViewById(R.id.linearLayout_message);
        linearLayoutMessageTicket = (LinearLayout) view.findViewById(R.id.linearLayout_message_ticket);
        linearLayoutAuthID = (LinearLayout) view.findViewById(R.id.linearlayout_authid);
        linearLayoutTradeNo = (LinearLayout) view.findViewById(R.id.linearlayout_tradeno);
    }

    private void setViewContent() {

        if(CreditCardTradeType.TICKET.getCode().equals(transaction.getPlatform())) {
            linearLayoutMessageTicket.setVisibility(View.VISIBLE);
            linearLayoutMessage.setVisibility(View.GONE);

            linearLayoutStoreDetail.setVisibility(View.GONE);
            linearLayoutOrderId.setVisibility(View.VISIBLE);
            textOrderID.setText(transaction.getOrderNo());

            if(TransactionStatus.SUCCESS.getCode().equals(String.valueOf(transaction.getTradeStatus()))) {
                linearLayoutBuyDate.setVisibility(View.VISIBLE);
                textBuyDate.setText(toTimeFormatted(transaction.getServiceTradeDate(), false));

                toMessageFormattedCallStyle(textReturnMessage, getString(R.string.return_message_trans_history), transaction.getStoreTel());
                toMessageFormattedCallStyle(textProblemMessage, getString(R.string.info_message_for_ticket_trans_history), "02-26553355");

            } else if(TransactionStatus.RETURN.getCode().equals(String.valueOf(transaction.getTradeStatus()))) {
                textReturnMessage.setVisibility(View.GONE);
                linearLayoutTransDate.setVisibility(View.VISIBLE);
                linearLayoutReturnDate.setVisibility(View.VISIBLE);

                String strTime = transaction.getMerchantTradeDate();
                try {
                    strTime = strTime.replace(" ", "");
                    strTime = strTime.substring(0, 14);
                    strTime = FormatUtil.toTimeFormatted(strTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    strTime = transaction.getMerchantTradeDate();
                }
                textTransDate.setText(toTimeFormatted(transaction.getMerchantTradeDate(), false));
                textReturnDate.setText(toTimeFormatted(transaction.getRtnTradeDate(), false));

                toMessageFormattedCallStyle(textProblemMessage, getString(R.string.info_message_for_ticket_trans_history), "02-26553355");

            } else {
                textReturnMessage.setVisibility(View.GONE);
                linearLayoutTransDate.setVisibility(View.VISIBLE);
                textTransDate.setText(toTimeFormatted(transaction.getMerchantTradeDate(), false));

                toMessageFormattedCallStyle(textProblemMessage, getString(R.string.info_message_for_ticket_trans_history), "02-26553355");
            }



        } else {
            linearLayoutTradeNo.setVisibility(View.VISIBLE);
            linearLayoutAuthID.setVisibility(View.VISIBLE);
            textAuthID.setText(transaction.getAuthIdResp());
            textTradeNo.setText(transaction.getServiceTradeNO());
            
            buttonCustomerService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + "02-26553355"));
                    startActivity(callIntent);
                }
            });

            if(TransactionStatus.SUCCESS.getCode().equals(String.valueOf(transaction.getTradeStatus()))) {
                if (!TextUtils.isEmpty(transaction.getServiceTradeNO())) {
                    textButtonReturn.setVisibility(View.VISIBLE);
                    textButtonReturn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isClickRetrun = true;
                            brightnessUtil.toMaxBrightness();
                            showReturnAlertDialog(String.format(getString(R.string.extra_my_ticket_return_for_serviceTradeNo_alert_info), transaction.getServiceTradeNO()), android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isClickRetrun = false;
                                    brightnessUtil.toOriginalBrightness();
                                    dialog.dismiss();
                                }
                            }, true);
                        }
                    });
                } else {
                    textButtonReturn.setVisibility(View.GONE);
                }
            }
        }


        // Set txtStatus -- Text & Background color
        if(TransactionStatus.SUCCESS.getCode().equals(String.valueOf(transaction.getTradeStatus())))
        {
            txtSubtitle.setText(TransactionStatus.CodeToEnum(String.valueOf(transaction.getTradeStatus())).getDescription());
            txtSubtitle.setTextColor(getResources().getColor(R.color.bgreen_text));
            textAmount.setTextColor(getResources().getColor(R.color.trans_log_amount_green_text));
        }
        else
        {
            txtSubtitle.setText(TransactionStatus.CodeToEnum(String.valueOf(transaction.getTradeStatus())).getDescription());
            txtSubtitle.setTextColor(getResources().getColor(R.color.colorRedPrimary));
            textAmount.setTextColor(getResources().getColor(R.color.colorRedPrimary));
            textButtonReturn.setVisibility(View.GONE);
        }

        //image

        // ---
        textName.setText(transaction.getStoreName());
        textAmount.setText(FormatUtil.toDecimalFormat(transaction.getTradeAmount(), true));

        textTime.setText(toTimeFormatted(transaction.getMerchantTradeDate()));
        textCardName.setText(transaction.getCardName() + FormatUtil.toHideCardNumberString(transaction.getCardNumberShelter()));

        if(!TextUtils.isEmpty(transaction.getStoreAddress()) && !TextUtils.isEmpty(transaction.getStoreTel())) {
            textStoreNameDetail.setText(transaction.getStoreAddress() + "\n" + transaction.getStoreTel());
        } else if (!TextUtils.isEmpty(transaction.getStoreAddress()) && TextUtils.isEmpty(transaction.getStoreTel())) {
            textStoreNameDetail.setText(transaction.getStoreAddress());
        } else if (TextUtils.isEmpty(transaction.getStoreAddress()) && !TextUtils.isEmpty(transaction.getStoreTel())) {
            textStoreNameDetail.setText(transaction.getStoreTel());
        } else {
            linearLayoutStoreDetail.setVisibility(View.GONE);
        }

//        textStoreNameDetail.setText(transaction.getStoreAddress() + "\n" + transaction.getStoreTel());


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO 待重構作法
            if(menu != null) {
                MenuItem item = menu.findItem(R.id.action_contacts);
                if (item != null) {
                    item.setVisible(false);
                }
            }else {

                super.onPrepareOptionsMenu(menu);
            }
    }


    private String toTimeFormatted(String time) {
        return toTimeFormatted(time, true);
    }

    private String toTimeFormatted(String time, boolean isEllipse) {
        String strTime = time;
        try {
            strTime = strTime.replace(" ", "");
            strTime = strTime.substring(0, 14);
            strTime = FormatUtil.toTimeFormatted(strTime, isEllipse);
            return strTime;
        } catch (Exception e) {
            e.printStackTrace();
            return time;
        }
    }

    private void toMessageFormattedCallStyle(TextView view, String content, String phoneNumber) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new OpenPhoneViewURLSpan(phoneNumber, getContext()),
                spannableString.length() - 6, /// 最後邊的六個字 - 請聯絡店家。
                spannableString.length() - 1, /// 句點不要加連結
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spannableString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class OpenPhoneViewURLSpan extends URLSpan {
        private Context context;

        public OpenPhoneViewURLSpan(String phone, Context context) {
            super(phone);
            this.context = context;
        }

        @Override
        public void onClick(View widget) {
            int viewId = widget.getId();
            if(viewId == R.id.text_message_return) {
                ((ActivityBase) getActivity()).showAlertDialog(String.format(getString(R.string.extra_my_ticket_return_alert_info), transaction.getOrderNo()), R.string.extra_my_ticket_return_call_store, R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getURL()));
                                context.startActivity(intent);
                            }
                        }
                        ,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                        , true);
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getURL()));
                context.startActivity(intent);
            }
        }
    }


    /**
     * 顯示只有一個按鈕的提示對話框
     * @param btnResId 按鈕文字資源id
     * @param btnClickListener 按鈕點擊事件listener
     * @param cancelable 點擊背景是否可取消
     */
    public void showReturnAlertDialog(String message,int btnResId, DialogInterface.OnClickListener btnClickListener, boolean cancelable){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        final View view = inflater.inflate(R.layout.dialog_credit_card_return, null);
        imgBRCode = (ImageView) view.findViewById(R.id.img_bar_code);
        if(!TextUtils.isEmpty(transaction.getServiceTradeNO())) {
            GenerateQRCodeAsyncTask task = new GenerateQRCodeAsyncTask(
                    transaction.getServiceTradeNO(),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getActivity().getApplication().getResources().getDisplayMetrics()),  //Width
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getActivity().getApplication().getResources().getDisplayMetrics()),  //Height
                    MARGIN_AUTOMATIC,
                    Color.BLACK, Color.TRANSPARENT, false);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        builder.setView(view);
        builder.setMessage(message).setTitle(R.string.dialog_title)
                .setCancelable(cancelable);

        builder.setPositiveButton(btnResId, btnClickListener);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }


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
            ((ActivityBase) getActivity()).showProgressLoading();
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
            ((ActivityBase) getActivity()).dismissProgressLoading();
            if (bitmap == null) {
                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.error_qr_code_generate));
                return;
            }
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
