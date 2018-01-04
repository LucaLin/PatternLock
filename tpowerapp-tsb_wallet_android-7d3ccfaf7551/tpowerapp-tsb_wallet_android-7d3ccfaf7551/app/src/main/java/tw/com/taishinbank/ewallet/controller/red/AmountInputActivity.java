package tw.com.taishinbank.ewallet.controller.red;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.sv.SVAccountDetailFragment;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class AmountInputActivity extends ActivityBase implements View.OnClickListener{

    private static final String TAG_DIALOG_FRAGMENT = "dialog";
    public static final String EXTRA_CURRENT_PAGE = "extra_current_page";
    public static final int PAGE_TOTAL_MONEY = 1;
    public static final int PAGE_TOTAL_PEOPLE = 2;
    private static final int PAGE_MIN_MONEY = 3;
    private EditText editAmount;
    private int currentPage = PAGE_TOTAL_MONEY;
    private RedEnvelopeInputData inputData;
    private int maxAmountLimit;
    private int minAmountLimit;
    private int inputLengthLimit;
    private Button buttonNext;
    private TextView textInfo;
    private ImageView imageWarning;
    private SVAccountInfo svAccountInfo;
    private TextView textNames;
    private TextView textNamesNumber;
    private TextView textTitle;
    private TextView textUnitPeople;
    private TextView textUnitMoney;
    private View layoutInfo;
    private ImageView imageInfoArrow;
    private View layoutAccountInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_input);

        // 設置toolbar與置中的標題文字
        setCenterTitle(R.string.title_amount_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 取得財神紅包物件
        inputData = getIntent().getParcelableExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA);
        currentPage = getIntent().getIntExtra(EXTRA_CURRENT_PAGE, PAGE_TOTAL_MONEY);

        // 初始輸入框的顯示數值
        editAmount = (EditText)findViewById(R.id.edit_amount);
        editAmount.setText("0");

        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(this);

        layoutAccountInfo = findViewById(R.id.layout_account_info);
        textNames = (TextView) findViewById(R.id.text_names);
        textNamesNumber = (TextView) findViewById(R.id.text_names_number);
        textTitle = (TextView) findViewById(R.id.text_title);
        textUnitPeople = (TextView) findViewById(R.id.text_unit_people);
        textUnitMoney = (TextView) findViewById(R.id.text_unit_money);
        layoutInfo = findViewById(R.id.layout_info);
        imageInfoArrow = (ImageView) findViewById(R.id.btn_info_arrow);
        textInfo = (TextView) findViewById(R.id.text_info);
        imageWarning = (ImageView) findViewById(R.id.image_warning);

        ImageView imagePhoto = (ImageView) findViewById(R.id.image_photo);
        if(inputData.getMemNOs().length > 1) {
            imagePhoto.setImageResource(R.drawable.img_taishin_photo_dark);
        } else {
            // 設定頭像
            ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(inputData.getMemNOs()[0], imagePhoto);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        svAccountInfo = PreferenceUtil.getSVAccountInfo(this);
        setViewContent();

        // 檢查info文字跟按鈕顯示狀態
        String amountString = (editAmount.getText() != null) ? editAmount.getText().toString() : "";
        // 如果已包含逗號，先去掉逗號
        if(amountString.contains(",")){
            amountString = amountString.replace(",", "");
        }
        long amountNum;
        // 如果為空字串，則設為0，否則設為字串表示的數值
        if (amountString.equals("")) {
            amountNum = 0;
        } else {
            amountNum = Long.parseLong(amountString);
        }
        updateInfoAndButton(amountNum);
    }

    private void setViewContent(){
        switch (currentPage){
            // 總金額
            case PAGE_TOTAL_MONEY:
                textUnitMoney.setVisibility(View.VISIBLE);

                // 設定接收者的顯示名稱
                textNames.setText(inputData.getMergedNames());
                textNamesNumber.setText(inputData.getNamesNumberString());

                // 設定可用金額 = 儲值支付帳戶餘額、當日/當月交易限額以及紅包限額等中的最小值
                maxAmountLimit = Math.min(
                        Math.min(Integer.valueOf(svAccountInfo.getBalance()), Integer.valueOf(svAccountInfo.getDailyLimCurr())),
                        Integer.valueOf(svAccountInfo.getMonthlyLimCurr()));

                //總額不超過 單筆限額*人數
                if (maxAmountLimit > Integer.valueOf(svAccountInfo.getSingleLimCurr()) * inputData.getTotalPeople()){
                    maxAmountLimit = Integer.valueOf(svAccountInfo.getSingleLimCurr()) * inputData.getTotalPeople();
                }
                if(inputData.getType().equals(RedEnvelopeType.TYPE_GENERAL)) {
                    // 一般紅包模式多人時 需要平均最大金額
                    maxAmountLimit = maxAmountLimit/inputData.getTotalPeople();
                    minAmountLimit = 1;
                }else{
                    minAmountLimit = inputData.getTotalPeople();
                }
                textInfo.setText(String.format(getString(R.string.input_number_info_money), FormatUtil.toDecimalFormat(maxAmountLimit)));
                imageInfoArrow.setVisibility(View.VISIBLE);
                layoutInfo.setClickable(true);
                layoutInfo.setOnClickListener(this);
                break;

            // 總人數
            case PAGE_TOTAL_PEOPLE:
                layoutAccountInfo.setVisibility(View.INVISIBLE);
                textTitle.setText(R.string.title_set_random_people_num);
                textUnitPeople.setVisibility(View.VISIBLE);
                // 設定最多人數上限為預設200人跟選中人數的最小值
                maxAmountLimit = getResources().getInteger(R.integer.red_envelope_random_people_limit);
                if(inputData.getMemNOs() != null){
                    if(inputData.getMemNOs().length < maxAmountLimit){
                        maxAmountLimit = inputData.getMemNOs().length;
                    }
                }
                textInfo.setText(String.format(getString(R.string.input_number_info_people), maxAmountLimit));
                break;

            // 最小金額
            case PAGE_MIN_MONEY:
                layoutAccountInfo.setVisibility(View.INVISIBLE);
                textTitle.setText(R.string.title_set_min_money);
                textUnitMoney.setVisibility(View.VISIBLE);
                // 設定最多不可超過平均值
                maxAmountLimit = (int) inputData.getTotalAmount()/inputData.getTotalPeople();
                textInfo.setText(String.format(getString(R.string.input_number_info_money), FormatUtil.toDecimalFormat(maxAmountLimit)));
                break;
        }

        // 設定字串長度限制
        inputLengthLimit = String.valueOf(maxAmountLimit).length();
    }

    /**
     * 在layout有指定每個按鈕onClick會執行此方法
     */
    public void onButtonClick(View view) {
        int id = view.getId();
        String amountString = (editAmount.getText() != null) ? editAmount.getText().toString() : "";
        // 如果已包含逗號，先去掉逗號
        if(amountString.contains(",")){
            amountString = amountString.replace(",", "");
        }

        long amountNum;

        // 判斷按下的按鈕
        switch (id) {
            // 如果是數字鍵，在目前輸入框文字加上數字
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                // 如果輸入長度已達上限，不允許輸入
                if(!amountString.equals("0") && amountString.length() >= inputLengthLimit){
                    return ;
                }
                amountString += view.getTag();
                break;
            // 如果是刪除鍵，將最右邊的數字拿掉
            case R.id.btn_delete:
                if (amountString.length() <= 1) {
                    amountString = "";
                } else {
                    amountString = amountString.substring(0, amountString.length() - 1);
                }
                break;
        }

        // 如果為空字串，則設為0，否則設為字串表示的數值
        if (amountString.equals("")) {
            amountNum = 0;
        } else {
            amountNum = Long.parseLong(amountString);
        }

        updateInfoAndButton(amountNum);
    }

    private void updateInfoAndButton(long amountNum){
        // 根據輸入的數量更新按鈕狀態與輸入框的顯示文字
        if(amountNum > maxAmountLimit || (currentPage == PAGE_TOTAL_MONEY && amountNum < minAmountLimit)){
            textInfo.setTextColor(getResources().getColor(R.color.amount_input_info_warning));
            imageWarning.setVisibility(View.VISIBLE);
            buttonNext.setEnabled(false);
        } else {
            buttonNext.setEnabled(amountNum > 0 && amountNum <= maxAmountLimit);
            textInfo.setTextColor(getResources().getColor(R.color.amount_input_info));
            imageWarning.setVisibility(View.GONE);
        }
        editAmount.setText(FormatUtil.toDecimalFormat(amountNum));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_next){
            Intent intent = null;
            String amountString = (editAmount.getText() != null) ? editAmount.getText().toString() : "";
            // 如果已包含逗號，先去掉逗號
            if(amountString.contains(",")){
                amountString = amountString.replace(",", "");
            }
            switch (currentPage){
                // 總金額
                case PAGE_TOTAL_MONEY:

                    if(inputData.getType().equals(RedEnvelopeType.TYPE_MONEY_GOD)) {
                        // 財神紅包
                        // 開啟下一頁（總人數）
                        intent = new Intent(v.getContext(), AmountInputActivity.class);
                        intent.putExtra(EXTRA_CURRENT_PAGE, PAGE_MIN_MONEY);
                        inputData.setTotalAmount(Long.parseLong(amountString));
                        intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);

                    }
                    else {
                        //一般紅包模式
                        // 開啟下一頁（確認紅包）
                        intent = new Intent(v.getContext(), RedEnvelopeDetailConfirmActivity.class);
                        inputData.setTotalAmount(Long.parseLong(amountString) * inputData.getTotalPeople());
                        intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
                    }
                    break;
                // 總人數
                case PAGE_TOTAL_PEOPLE:
                    // 開啟下一頁（最小金額）
                    intent = new Intent(v.getContext(), AmountInputActivity.class);
                    intent.putExtra(EXTRA_CURRENT_PAGE, PAGE_TOTAL_MONEY);
                    inputData.setTotalPeople(Integer.parseInt(amountString));
                    intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
                    break;
                // 最小金額
                case PAGE_MIN_MONEY:
                    // TODO 這邊先直接開啟列表頁
                    intent = new Intent(v.getContext(), RedEnvelopeDetailConfirmActivity.class);
                    inputData.setMinAmountPerPerson(Integer.parseInt(amountString));
                    intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
                    break;
            }
            if(intent != null) {
                startActivity(intent);
            }
        }else if(v.getId() == R.id.layout_info){
            int accountBalance = Integer.parseInt(svAccountInfo.getBalance());
            showSVDetail(SVAccountDetailFragment.ENUM_TYPE.RED,
                    accountBalance,
                    true);
        }
    }

    /**
     * 顯示帳戶詳情對話框
//     * @param commonTitleID 主要動作標題，如：儲值、提領或轉帳
//     * @param accountTitle 「XX帳戶」餘額
     * @param useType 使用的類型
     * @param accountBalance 帳戶餘額
     */
    public void showSVDetail(SVAccountDetailFragment.ENUM_TYPE useType , int accountBalance, boolean showGoDeposit){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
        if (prev != null) {
            ft.remove(prev);
        }
        // Create and show the dialog.
        DialogFragment newFragment = SVAccountDetailFragment.newInstance(useType, accountBalance, svAccountInfo, showGoDeposit);
        newFragment.show(ft, TAG_DIALOG_FRAGMENT);
    }
}
