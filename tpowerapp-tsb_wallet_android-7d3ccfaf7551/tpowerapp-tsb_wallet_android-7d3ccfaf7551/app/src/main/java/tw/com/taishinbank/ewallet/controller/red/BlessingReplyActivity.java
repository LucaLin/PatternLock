package tw.com.taishinbank.ewallet.controller.red;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.InputFilterUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;

public class BlessingReplyActivity extends ActivityBase {

    private static final String TAG = "BlessingReplyActivity";

    public final static String EXTRA_SENDERMEM = "extra_senderMem";
    public final static String EXTRA_SENDER = "extra_sender";
    public final static String EXTRA_AMOUNT = "extra_amount";
    public final static String EXTRA_BLESSING = "extra_blessing";
    public final static String EXTRA_TXFSEQ = "extra_txfSeq";
    public final static String EXTRA_TXFDSEQ = "extra_txfdSeq";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessing_reply);

        // 設定置中的標題與返回鈕
        setCenterTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 設定上方顯示名稱
        TextView textName = (TextView) findViewById(R.id.text_name);
        textName.setText(getIntent().getStringExtra(EXTRA_SENDER));
        // 設定顯示金額
        TextView textAmount = (TextView) findViewById(R.id.text_amount);
        String formattedAmount = FormatUtil.toDecimalFormatFromString(getIntent().getStringExtra(EXTRA_AMOUNT), true);
        textAmount.setText(formattedAmount);
        // 設定祝福語
        final TextView textBlessing = (TextView) findViewById(R.id.text_blessing);
        String blessing = getIntent().getStringExtra(EXTRA_BLESSING);
        textBlessing.setText(blessing);

        final ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        // 設定頭像
        ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(getIntent().getStringExtra(EXTRA_SENDERMEM), imagePhoto);



        // TODO 確認要followＵＩ還是ＵＸ
//        final CheckBox checkBoxIsExpand = (CheckBox) findViewById(R.id.checkbox_singleline);

        // 根據祝福語否為空，隱藏或顯示標示展開的圖示，祝福語不為空才設定點擊事件處理
//        if(TextUtils.isEmpty(blessing)) {
//            checkBoxIsExpand.setVisibility(View.INVISIBLE);
//        }else{
//            final CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    // 折疊或打開祝福語
//                    textBlessing.setSingleLine(isChecked);
//                }
//            };
//            checkBoxIsExpand.setOnCheckedChangeListener(checkedChangeListener);
//            textBlessing.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 折疊或打開祝福語，變更按鈕樣式
//                    checkBoxIsExpand.setOnCheckedChangeListener(null);
//                    boolean becomeChecked = !checkBoxIsExpand.isChecked();
//                    textBlessing.setSingleLine(becomeChecked);
//                    checkBoxIsExpand.setChecked(becomeChecked);
//                    checkBoxIsExpand.setOnCheckedChangeListener(checkedChangeListener);
//                }
//            });
//        }

        final TextView textCount = (TextView) findViewById(R.id.text_count);
        final EditText editText = (EditText) findViewById(R.id.edit_blessing_reply);
        Button buttonOk = (Button) findViewById(R.id.button_ok);

        final String numberOfChars = getString(R.string.current_text_count);
        final int lengthLimit = getResources().getInteger(R.integer.blessing_maxlength);

        InputFilterUtil.setFilter(editText, new InputFilterUtil.ENUM_FILTER_TYPE[]{InputFilterUtil.ENUM_FILTER_TYPE.FILTER_WRAP, InputFilterUtil.ENUM_FILTER_TYPE.FILTER_SPACE});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String countShow;
                // 根據輸入的字串更新輸入字數與按鈕狀態
                if (TextUtils.isEmpty(s)) {
                    countShow = String.format(numberOfChars, 0, lengthLimit);
                } else {
                    countShow = String.format(numberOfChars, s.length(), lengthLimit);
                }
                textCount.setText(countShow);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 20160322 加入下列兩行，搭配layout xml的修改
        // 讓虛擬鍵盤的enter可顯示為Done，且不會變成singleline
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(Integer.MAX_VALUE);

        final String txfSeq = getIntent().getStringExtra(EXTRA_TXFSEQ);
        final String txfdSeq = getIntent().getStringExtra(EXTRA_TXFDSEQ);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 檢查輸入是否為空，如果是，顯示提示對話框
                String replyMsg = editText.getText().toString();
                if(replyMsg == null || replyMsg.trim().replace("\n", "").equals("")){
                    showAlertDialog(getString(R.string.msg_input_reply_blessing), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return ;
                }

                // 如果沒有網路連線，顯示提示對話框
                if(!NetworkUtil.isConnected(BlessingReplyActivity.this)){
                    showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return ;
                }

                // 呼叫api回覆訊息
                try {
                    RedEnvelopeHttpUtil.replyRedEnvelopeMsg(txfSeq, txfdSeq, editText.getText().toString(), responseListener, BlessingReplyActivity.this, TAG);
                    showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
        hideKeyboard();
    }

    // 留言回覆的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 回到上一頁
                setResult(RESULT_OK);
                finish();
            }else{
                // 執行預設的錯誤處理
                handleResponseError(result, BlessingReplyActivity.this);
            }
        }
    };

}
