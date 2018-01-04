package tw.com.taishinbank.ewallet.controller.red;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedDetail;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.view.TextViewWithUnderLine;

public class ReceiveRedEnvelopeActivity extends ActivityBase implements View.OnClickListener {

    public static final String EXTRA_RECEIVED_HEADER = "extra_received_header";
    public static final int REQUEST_CODE_REPLY_MSG = 1234;
    private RedEnvelopeReceivedHeader receivedHeader;
    private RedEnvelopeReceivedDetail myReceivedDetail = null;
    private Button buttonReplyMsg;
    private Button buttonReplyRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_result);

        // 設置toolbar與置中的標題文字與返回鈕
        setCenterTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 取得發送結果物件
        receivedHeader = getIntent().getParcelableExtra(EXTRA_RECEIVED_HEADER);

        FrameLayout layoutMultiBackground = (FrameLayout) findViewById(R.id.layout_multi_bg_envelope);
        View viewTopEmpty = findViewById(R.id.view_top_empty);

        // 發送成功時，顯示金額與祝福語
        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        TextView textResult = (TextView) findViewById(R.id.text_result);
        textResult.setText(R.string.receive_red_title);

        TextView textNames = (TextView) findViewById(R.id.text_names);
        TextView textNamesNumber = (TextView) findViewById(R.id.text_names_number);
        LinearLayout layoutAmount = (LinearLayout) findViewById(R.id.layout_amount);
        TextView textAmount = (TextView) findViewById(R.id.text_amount);

        // 設定有底線的TextView行數、底線顏色、底線寬度
        TextViewWithUnderLine textBlessingLine = (TextViewWithUnderLine) findViewById(R.id.text_blessing_line);
        Resources resources = getResources();
        textBlessingLine.setNumberOfLines(resources.getInteger(R.integer.red_envelope_sent_result_blessing_lines));
        textBlessingLine.setLineColor(resources.getColor(R.color.red_envelope_sent_result_divider_color));
        textBlessingLine.setLineWidth(resources.getDimensionPixelSize(R.dimen.red_envelope_sent_result_divider_height));

        TextView textBlessing = (TextView) findViewById(R.id.text_blessing);

        // 發送失敗或有錯誤時，顯示圖示與錯誤訊息
        ImageView imageWarning = (ImageView) findViewById(R.id.image_warning);
        TextView textErrorMessage = (TextView) findViewById(R.id.text_error_message);

        // 設定發紅包的人名
        String senderName = "From " + receivedHeader.getSender();
        textNames.setText(senderName);

        // 設定頭像
        ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(receivedHeader.getSenderMem(), imagePhoto);
        textNamesNumber.setText("");

        // 設定收紅包的祝福語
        textBlessing.setText(receivedHeader.getMessage());

        Button buttonDetail = (Button) findViewById(R.id.button_detail);
        buttonDetail.setText(String.format(getString(R.string.receive_red_reply_hint), receivedHeader.getSender()));
        buttonDetail.setCompoundDrawablePadding(0);
        buttonDetail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        buttonDetail.setClickable(false);
        buttonDetail.setAllCaps(false);

        buttonReplyMsg = (Button) findViewById(R.id.button_left);
        buttonReplyMsg.setText(R.string.receive_red_reply_message);
        buttonReplyMsg.setOnClickListener(this);

        buttonReplyRed = (Button) findViewById(R.id.button_right);
        buttonReplyRed.setText(R.string.receive_red_reply_red_envelope);
        buttonReplyRed.setOnClickListener(this);

        ArrayList<RedEnvelopeReceivedDetail> receivedDetails = receivedHeader.getTxDetailList();
        if(receivedDetails != null) {
            String memNO = PreferenceUtil.getMemNO(this);
            for (RedEnvelopeReceivedDetail tmpReceivedDetail : receivedDetails) {
                if(tmpReceivedDetail.getToMem().equals(memNO)) {
                    myReceivedDetail = tmpReceivedDetail;
                    break;
                }
            }

        }
        if(myReceivedDetail != null){
            // 設定金額
            textAmount.setText(FormatUtil.toDecimalFormatFromString(myReceivedDetail.getAmount()));
            buttonReplyMsg.setEnabled(TextUtils.isEmpty(myReceivedDetail.getReplyMessage()));
            buttonReplyRed.setEnabled(TextUtils.isEmpty(myReceivedDetail.getReplyAmount()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        // 回禮訊息
        if(viewId == R.id.button_left){
            Intent intent = new Intent(this, BlessingReplyActivity.class);
            // 設定要顯示需要的資料
            intent.putExtra(BlessingReplyActivity.EXTRA_SENDERMEM, receivedHeader.getSenderMem());
            intent.putExtra(BlessingReplyActivity.EXTRA_SENDER, receivedHeader.getSender());
            intent.putExtra(BlessingReplyActivity.EXTRA_AMOUNT, myReceivedDetail.getAmount());
            intent.putExtra(BlessingReplyActivity.EXTRA_BLESSING, receivedHeader.getMessage());
            // 設定呼叫api需要的資料
            intent.putExtra(BlessingReplyActivity.EXTRA_TXFSEQ, receivedHeader.getTxfSeq());
            intent.putExtra(BlessingReplyActivity.EXTRA_TXFDSEQ, myReceivedDetail.getTxfdSeq());
            startActivityForResult(intent, REQUEST_CODE_REPLY_MSG);

        // 回禮紅包
        }else if(viewId == R.id.button_right) {
            Intent intent = new Intent(this, BlessingInputActivity.class);
            // 產生紅包需要的物件並放到intent
            RedEnvelopeInputData inputData = new RedEnvelopeInputData(RedEnvelopeType.TYPE_REPLY);
            inputData.setReplyToTxfdSeq(myReceivedDetail.getTxfdSeq());
            inputData.setMemNOs(new String[]{receivedHeader.getSenderMem()});
            inputData.setNames(new String[]{receivedHeader.getSender()});
            inputData.setTotalPeople(1);

            // 開啟輸入祝福語的頁面，並傳遞資料
            intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_REPLY_MSG){
            // 更新按鈕狀態
            buttonReplyMsg.setEnabled(resultCode != RESULT_OK);
            // 跟iOS同步，發送完訊息就關掉頁面
            if(resultCode == RESULT_OK) {
                finish();
            }
        }
    }
}
