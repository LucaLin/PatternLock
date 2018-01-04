package tw.com.taishinbank.ewallet.controller.red;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.util.InputFilterUtil;

public class BlessingInputActivity extends ActivityBase implements View.OnClickListener {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 123;
    private RedEnvelopeInputData inputData;
    private String numberOfChars;
    private int lengthLimit;
    private TextView textCount;
    private EditText editText;
    private View activityRootView;
    private Button buttonNext;
    private Button buttonChooseSentence;
    private FrameLayout layoutButtonChooseSentence;
    private View viewChooseSentence;
    private ImageView imagePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessing_input);

        // 設定置中的標題與返回鈕
        setCenterTitle(R.string.send_red_envelope);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 取得財神紅包物件
        inputData = getIntent().getParcelableExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA);
        activityRootView = findViewById(R.id.root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        numberOfChars = getString(R.string.current_text_count);
        lengthLimit = getResources().getInteger(R.integer.blessing_maxlength);

        // 設定上方顯示名稱
        TextView textNames = (TextView) findViewById(R.id.text_names);
        textNames.setText(inputData.getMergedNames());
        TextView textNamesNumber = (TextView) findViewById(R.id.text_names_number);
        textNamesNumber.setText(inputData.getNamesNumberString());

        textCount = (TextView) findViewById(R.id.text_count);
        editText = (EditText) findViewById(R.id.edit_blessing);
        editText.addTextChangedListener(textWatcher);
        InputFilterUtil.setFilter(editText, new InputFilterUtil.ENUM_FILTER_TYPE[]{InputFilterUtil.ENUM_FILTER_TYPE.FILTER_WRAP, InputFilterUtil.ENUM_FILTER_TYPE.FILTER_SPACE});

        // 20160322 加入下列兩行，搭配layout xml的修改
        // 讓虛擬鍵盤的enter可顯示為Done，且不會變成singleline
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(Integer.MAX_VALUE);

        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);

        layoutButtonChooseSentence = (FrameLayout) findViewById(R.id.layout_button_choose_sentence);
        buttonChooseSentence = (Button) findViewById(R.id.button_choose_sentence);
        buttonChooseSentence.setTag(R.drawable.btn_red_choose_sentence);
        buttonChooseSentence.setOnClickListener(this);
        imagePhoto = (ImageView) findViewById(R.id.image_photo);
        if(inputData.getMemNOs().length > 1) {
            imagePhoto.setImageResource(R.drawable.img_taishin_photo_dark);
        } else {
            // 設定頭像
            ImageLoader imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(inputData.getMemNOs()[0], imagePhoto);
        }
    }

    // 偵測文字變更
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String countShow;
            // 根據輸入的字串更新輸入字數與按鈕狀態
            if(TextUtils.isEmpty(s)){
                countShow = String.format(numberOfChars, 0, lengthLimit);
            }else{
                countShow = String.format(numberOfChars, s.length(), lengthLimit);
            }
            textCount.setText(countShow);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // 用來偵測keyboard有沒有跳出的listener
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            activityRootView.getWindowVisibleDisplayFrame(r);

            // 取得content view的bottom
            int baseBottom = activityRootView.getBottom();
            // 如果content view的bottom小於目前顯示區塊的bottom
            if(baseBottom < r.bottom) {
                // 改取content view的root view的bottom
                baseBottom = activityRootView.getRootView().getBottom();
            }

            int heightDiff = baseBottom - (r.bottom);

            // 鍵盤顯示
            if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                layoutButtonChooseSentence.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.INVISIBLE);

            // 鍵盤隱藏
            }else{
                layoutButtonChooseSentence.setVisibility(View.INVISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                if(viewChooseSentence != null) {
                    viewChooseSentence.setVisibility(View.INVISIBLE);
                }

                buttonChooseSentence.setBackgroundResource(R.drawable.btn_red_choose_sentence);
                buttonChooseSentence.setTag(R.drawable.btn_red_choose_sentence);
            }

        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_next){
            // 設定祝福語
            if(editText.getText() != null) {
                inputData.setBlessing(editText.getText().toString());
            }
            // 開啟下一頁
            Intent intent = new Intent(v.getContext(), AmountInputActivity.class);
            intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
            if(inputData.getType().equals(RedEnvelopeType.TYPE_MONEY_GOD))
                intent.putExtra(AmountInputActivity.EXTRA_CURRENT_PAGE, AmountInputActivity.PAGE_TOTAL_PEOPLE);
            startActivity(intent);
        }else if(v.getId() == R.id.button_choose_sentence){

            // 如果是android 6.0以上，且沒有權限的話，要先開啟頁面請使用者去開權限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);

            // 如果有權限
            }else {
                // 切換成吉祥話
                if (((int) v.getTag()) == R.drawable.btn_red_choose_sentence) {
                    // 如果還沒建立過吉祥話的view，先建立
                    if (viewChooseSentence == null) {
                        viewChooseSentence = LayoutInflater.from(this).inflate(R.layout.choose_sentence, null);
                        final ListView listView = (ListView) viewChooseSentence.findViewById(android.R.id.list);
                        final String[] sentences = getResources().getStringArray(R.array.choose_sentences);
                        ArrayAdapter<String> itemsAdapter =
                                new ArrayAdapter<String>(this, R.layout.choose_sentence_list_item, sentences){
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        // 由於有的手機上收不到onItemClick，改用view的點擊事件
                                        View view =  super.getView(position, convertView, parent);
                                        view.setTag(position);
                                        if(!view.hasOnClickListeners()) {
                                            view.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    editText.append(sentences[(int)v.getTag()]);
                                                }
                                            });
                                        }
                                        return view;
                                    }
                                };
                        listView.setAdapter(itemsAdapter);
                        WindowManager.LayoutParams params = getChooseSentenceLayoutParams();
                        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                        wm.addView(viewChooseSentence, params);

                    // 如果已經建立過吉祥話的view，更新layout params
                    } else {
                        WindowManager.LayoutParams params = getChooseSentenceLayoutParams();
                        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                        wm.updateViewLayout(viewChooseSentence, params);
                        viewChooseSentence.setVisibility(View.VISIBLE);
                    }

                    // 按鈕背景換成有鍵盤圖示
                    v.setBackgroundResource(R.drawable.btn_red_choose_keyboard);
                    v.setTag(R.drawable.btn_red_choose_keyboard);

                // 切換成輸入法
                } else {
                    // 按鈕背景換成有選吉祥話
                    v.setBackgroundResource(R.drawable.btn_red_choose_sentence);
                    v.setTag(R.drawable.btn_red_choose_sentence);
                    viewChooseSentence.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 計算出吉祥話的layoutparams
     */
    private WindowManager.LayoutParams getChooseSentenceLayoutParams(){
        Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);

        // 取得content view的bottom
        int baseBottom = activityRootView.getBottom();
        // 如果content view的bottom小於目前顯示區塊的bottom
        if(baseBottom < r.bottom) {
            // 改取content view的root view的bottom
            baseBottom = activityRootView.getRootView().getBottom();
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                activityRootView.getWidth(),
                baseBottom - r.bottom,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.OPAQUE);
        params.gravity = Gravity.BOTTOM;
        return params;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            // do nothing
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
        // 如果有吉祥話的view，將view從畫面上移除
        if(viewChooseSentence != null) {
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeViewImmediate(viewChooseSentence);
            viewChooseSentence = null;
        }
    }
}
