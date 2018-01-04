package tw.com.taishinbank.ewallet.controller.red;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResult;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResultEach;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.view.TextViewWithUnderLine;

public class SendingResultActivity extends ActivityBase implements View.OnClickListener {

    public static final String EXTRA_SENT_RESULT = "extra_sent_result";
    public static final String EXTRA_BLESSING = "extra_blessing";
    private static final String TAG_DIALOG_FRAGMENT = "dialog_fragment";
    private RedEnvelopeSentResult sentResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_result);

        // 設置toolbar與置中的標題文字與返回鈕
        setCenterTitle(R.string.send_red_envelope);

        // 取得發送結果物件
        sentResult = getIntent().getParcelableExtra(EXTRA_SENT_RESULT);
        RedEnvelopeSentResultEach[] eachResults = sentResult.getTxResult();
        String blessing = getIntent().getStringExtra(EXTRA_BLESSING);

        FrameLayout layoutMultiBackground = (FrameLayout) findViewById(R.id.layout_multi_bg_envelope);
        View viewTopEmpty = findViewById(R.id.view_top_empty);

        // 發送成功時，顯示金額與祝福語
        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        TextView textResult = (TextView) findViewById(R.id.text_result);

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

        // TODO 會有沒列表的情況嗎？
        int countFail = 0;
        String mergedNames = "";
        if(eachResults != null && eachResults.length > 0) {
            // 檢查是否有失敗跟組合收紅包人的名稱
            if(eachResults[0].getResult().equalsIgnoreCase("N")){
                countFail++;
            }
            mergedNames += "給 " + eachResults[0].getName();
            for (int i = 1; i < eachResults.length; i++) {
                if (eachResults[i].getResult().equalsIgnoreCase("N")) {
                    countFail++;
                }
                mergedNames += ", " + eachResults[i].getName();
            }

            // 根據結果個數顯示或隱藏紅包背景
            if(eachResults.length > 1){
                layoutMultiBackground.setVisibility(View.VISIBLE);
                viewTopEmpty.setVisibility(View.VISIBLE);
                imagePhoto.setImageResource(R.drawable.img_taishin_photo_dark_shadow);
                textNamesNumber.setText("(" + eachResults.length + ")");
            } else {
                // 設定頭像
                ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));
                imageLoader.loadImage(eachResults[0].getToMem(), imagePhoto);
                textNamesNumber.setText("");
            }
        }

        // 設定收紅包的人名
        textNames.setText(mergedNames);

        // 根據成功失敗數量顯示對應的畫面
        // 有失敗
        if(countFail > 0){
            // 設定標題顏色
            textResult.setTextColor(resources.getColor(R.color.red_envelope_sent_result_warning));

            // 隱藏正確紅包資訊
            layoutAmount.setVisibility(View.INVISIBLE);
            textBlessing.setVisibility(View.INVISIBLE);
            textBlessingLine.setVisibility(View.INVISIBLE);

            // 發送失敗或有錯誤時，顯示圖示與錯誤訊息
            imageWarning.setVisibility(View.VISIBLE);
            textErrorMessage.setVisibility(View.VISIBLE);

            // 全部失敗
            if(countFail == eachResults.length) {
                // 設定標題
                textResult.setText(R.string.sent_result_failed);

                // 設定icon與文字內容
                imageWarning.setImageResource(R.drawable.ic_red_title_envelope_big_failed);
                textErrorMessage.setText(R.string.sent_result_failed_msg);

            // 部分失敗
            }else {
                // 設定標題
                textResult.setText(R.string.sent_result_incompleted);

                // 設定icon與文字內容
                imageWarning.setImageResource(R.drawable.ic_red_envelope_big_warning);
                textErrorMessage.setText(R.string.sent_result_incompleted_msg);
            }

        // 全部成功
        } else {
            // 設定收紅包的總額、祝福語
            textAmount.setText(FormatUtil.toDecimalFormatFromString(sentResult.getAmount()));
            textBlessing.setText(blessing);
        }

        Button buttonDetail = (Button) findViewById(R.id.button_detail);
        buttonDetail.setOnClickListener(this);
        Button buttonRedHome = (Button) findViewById(R.id.button_left);
        buttonRedHome.setOnClickListener(this);
        Button buttonMine = (Button) findViewById(R.id.button_right);
        buttonMine.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        // 改成開啟紅包首頁
        if(viewId == R.id.button_left){
            Intent intent = getGoToHomeIntent(MainActivity.EXTRA_GO_RED_ENVELOPE, "");
            startActivity(intent);
        // 開啟我的紅包
        }else if(viewId == R.id.button_right) {
            Intent intent = getGoToHomeIntent(MainActivity.EXTRA_GO_MY_RED_ENVELOPE, "");
            intent.putExtra(MyRedEnvelopeFragment.EXTRA_DEFAULT_TAB, MyRedEnvelopeFragment.TAB_SENT);
            startActivity(intent);
        // 顯示紅包詳情fragment
        }else if(viewId == R.id.button_detail){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
            if (prev != null) {
                ft.remove(prev);
            }
            // Create and show the dialog.
            DialogFragment newFragment = SentResultDetailFragment.newInstance(sentResult);
            newFragment.show(ft, TAG_DIALOG_FRAGMENT);
        }
    }

    @Override
    public void onBackPressed() {
        // 如果目前頁面上有對話框，執行原本父類別的backPressed（dismiss對話框）
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
        if (prev != null) {
            super.onBackPressed();
        } else { // 否則不做事

        }
    }

    /**
     * 開啟首頁
     * @param extra 要包含的extra名稱，若無則傳null
     */
    private Intent getGoToHomeIntent(String extra, String value){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(extra != null) {
            intent.putExtra(extra, value);
        }
        return intent;
    }
}
