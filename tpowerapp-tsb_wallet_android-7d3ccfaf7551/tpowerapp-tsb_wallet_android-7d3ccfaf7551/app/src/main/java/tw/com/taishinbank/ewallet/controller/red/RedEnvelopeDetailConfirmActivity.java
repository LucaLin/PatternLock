package tw.com.taishinbank.ewallet.controller.red;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.red.RedEnvelopeDetailConfirmAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeData;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class RedEnvelopeDetailConfirmActivity extends ActivityBase implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private VideoView videoView;
    private CheckBox checkBoxIsExpand;
    private TextView textBlessing;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RedEnvelopeDetailConfirmAdapter adapter;
    private RedEnvelopeInputData inputData;
    private String[] selectedNames = null;
    private String[] selectedMemNos = null;
    private String[] sortedMoney = null;
    private View framelayout_red;
    private View appbar;
    private View relativeLayout_video;
    private ImageView bg_imageview;
    private int mAnimationTime;
    private Timer timer;
    private boolean isActive = false;
    boolean animation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelop_detail_confirm);
        // 設置toolbar與置中的標題文字與返回鈕
        setCenterTitle(R.string.send_red_envelope);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        framelayout_red =  findViewById(R.id.framelayout_red);

        appbar = findViewById(R.id.appbar);

        // 取得財神紅包物件
        inputData = getIntent().getParcelableExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA);

        // 設定祝福語
        textBlessing = (TextView) findViewById(R.id.text_blessing);
        textBlessing.setText(inputData.getBlessing());

        checkBoxIsExpand = (CheckBox) findViewById(R.id.checkbox_singleline);

        // 根據祝福語否為空，隱藏或顯示標示展開的圖示，祝福語不為空才設定點擊事件處理
        if(TextUtils.isEmpty(inputData.getBlessing())) {
            checkBoxIsExpand.setVisibility(View.INVISIBLE);
        }else{
            checkBoxIsExpand.setOnCheckedChangeListener(this);
            textBlessing.setOnClickListener(this);
        }

        Button buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);

        Button buttonRandomAgain = (Button) findViewById(R.id.button_random_again);
        buttonRandomAgain.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // 設定layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Layout Mode
        relativeLayout_video = findViewById(R.id.relativelayout_vide);
        if(inputData.getType().equals(RedEnvelopeType.TYPE_MONEY_GOD)) {
            //財神紅包
            relativeLayout_video.setVisibility(View.VISIBLE);
            framelayout_red.setVisibility(View.GONE);
            appbar.setVisibility(View.GONE);
            //動畫
            mAnimationTime = 300;
            videoView = (VideoView) this.findViewById(R.id.videoView);
            bg_imageview = (ImageView) this.findViewById(R.id.bg_imageview);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.redpage));

            // 設定顯示的總金額
            TextView textTotalMoney = (TextView) findViewById(R.id.text_total_money);
            textTotalMoney.setText(String.format(getString(R.string.red_envelope_detail_total_money), FormatUtil.toDecimalFormat(inputData.getTotalAmount())));
            // 設定隨機挑選人數與總人數
            TextView textRandomPeopleCount = (TextView) findViewById(R.id.text_random_people_count);
            textRandomPeopleCount.setText(String.format(getString(R.string.red_envelope_detail_random_people_count), inputData.getTotalPeople(), inputData.getMemNOs().length));

            // 隨機挑選並設定列表
            ArrayList<RedEnvelopeData> items = getRandomResult();
            adapter = new RedEnvelopeDetailConfirmAdapter(this, items);
            recyclerView.setAdapter(adapter);
        }
        else
        {
            //一般紅包
            relativeLayout_video.setVisibility(View.GONE);
            framelayout_red.setVisibility(View.VISIBLE);
            appbar.setVisibility(View.VISIBLE);
            buttonRandomAgain.setVisibility(View.GONE);

            // 設定顯示的總金額
            TextView textTotalMoney = (TextView) findViewById(R.id.text_total_money);
            textTotalMoney.setText(String.format(getString(R.string.red_envelope_detail_total_money), FormatUtil.toDecimalFormat(inputData.getTotalAmount())));
            // 設定總人數
            TextView textRandomPeopleCount = (TextView) findViewById(R.id.text_random_people_count);
            textRandomPeopleCount.setText(String.format(getString(R.string.red_envelope_detail_total_people_count), inputData.getTotalPeople()));
            if(inputData.getTotalPeople() == 1) {
                textTotalMoney.setVisibility(View.GONE);
                textRandomPeopleCount.setVisibility(View.GONE);
            }
            // 設定列表
            ArrayList<RedEnvelopeData> items = getGeneralRedResult();
            adapter = new RedEnvelopeDetailConfirmAdapter(this, items, true);
            recyclerView.setAdapter(adapter);
        }



    }


    @Override
    protected void onResume() {
        super.onResume();

        //只有財神紅包執行動畫
        if(inputData.getType().equals(RedEnvelopeType.TYPE_MONEY_GOD)) {
            isActive = true;
            videoView.requestFocus(); // 初始化
            videoView.start();
            initVideoProgressPooling(4406); // 監控影片播放, 以及停止時間
        }
    }

    private void initVideoProgressPooling(final int stopAtMsec) {
        cancelProgressPooling();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                videoView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isActive) {
                            cancelProgressPooling();
                            return;
                        }
                        if(videoView.getCurrentPosition() >= stopAtMsec && !animation) {
                            ViewPutin();
                            ViewPutout();

                            animation = true;
                        }

                        if(videoView.getCurrentPosition() >= 5100)
                        {
                            cancelProgressPooling();
                            isActive = false;
                        }

                    }
                });
            }
        }, 0, 10);
    }

    private void cancelProgressPooling() {
        if(timer != null) {
            timer.cancel();
        }
        timer = null;
    }



    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.button_next){
            // 前往密碼輸入驗證頁
            Intent intent = new Intent(v.getContext(), PasswordInputActivity.class);
            RedEnvelopeInputData data = new RedEnvelopeInputData(inputData.getType());
            data.setReplyToTxfdSeq(inputData.getReplyToTxfdSeq());
            data.setBlessing(inputData.getBlessing());
            data.setTotalAmount(inputData.getTotalAmount());
            data.setNames(selectedNames);
            data.setMemNOs(selectedMemNos);
            data.setAmounts(sortedMoney);
            intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, data);
            startActivity(intent);

        }else if(viewId == R.id.text_blessing){
            // 折疊或打開祝福語，變更按鈕樣式
            checkBoxIsExpand.setOnCheckedChangeListener(null);
            boolean becomeChecked = !checkBoxIsExpand.isChecked();
            textBlessing.setSingleLine(becomeChecked);
            checkBoxIsExpand.setChecked(becomeChecked);
            checkBoxIsExpand.setOnCheckedChangeListener(this);

        // 按下隨機重選
        }else if(viewId == R.id.button_random_again){
            // TODO 邊跑動畫

            // 重新random並更新列表
            ArrayList<RedEnvelopeData> items = getRandomResult();
            adapter.setList(items);
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 折疊或打開祝福語
        textBlessing.setSingleLine(isChecked);
    }

    /**
     * 取得隨機列表結果，同時將選中的人名與會員代號存到陣列
     */
    private ArrayList<RedEnvelopeData> getRandomResult(){
        // 取得隨機的錢
        SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(this);
        sortedMoney = sharedMethods.getRandomMoney(String.valueOf(inputData.getTotalAmount()), String.valueOf(inputData.getMinAmountPerPerson()),
                String.valueOf(inputData.getTotalPeople()), svAccountInfo.getSingleLimCurr());

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> memNos = new ArrayList<>();
        Collections.addAll(names, inputData.getNames());
        Collections.addAll(memNos, inputData.getMemNOs());
        Random random = new Random();
        ArrayList<RedEnvelopeData> items = new ArrayList<>();
        selectedMemNos = new String[sortedMoney.length];
        selectedNames = new String[sortedMoney.length];
        for(int i = sortedMoney.length-1; i >= 0; i--){
            // 隨機挑人
            int index = Math.abs(random.nextInt()) % names.size();
            items.add(new RedEnvelopeData(memNos.get(index), names.get(index), FormatUtil.toDecimalFormatFromString(sortedMoney[i]), null));
            // 記錄選中的人
            selectedMemNos[i] = memNos.remove(index);
            selectedNames[i] = names.remove(index);
        }
        return items;
    }

    /**
     * 取得一般紅包列表結果
     */
    private ArrayList<RedEnvelopeData> getGeneralRedResult(){

        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> memNos = new ArrayList<>();
        Collections.addAll(names, inputData.getNames());
        Collections.addAll(memNos, inputData.getMemNOs());
        ArrayList<RedEnvelopeData> items = new ArrayList<>();
        selectedMemNos = new String[inputData.getTotalPeople()];
        selectedNames = new String[inputData.getTotalPeople()];
        sortedMoney = new String[inputData.getTotalPeople()];

        String averageMoney = String.valueOf(inputData.getTotalAmount()/inputData.getTotalPeople());

        for(int i = inputData.getTotalPeople()-1; i >= 0; i--){
            // 隨機挑人
            items.add(new RedEnvelopeData(memNos.get(i), names.get(i), FormatUtil.toDecimalFormatFromString(averageMoney), null));
            // 記錄選中的人
            selectedMemNos[i] = memNos.remove(i);
            selectedNames[i] = names.remove(i);
            sortedMoney[i] = averageMoney;
        }
        return items;
    }

    private void ViewPutin() {
        framelayout_red.setAlpha(0f);
        framelayout_red.setVisibility(View.VISIBLE);

        framelayout_red.animate().alpha(1f).setDuration(mAnimationTime)
                .setListener(null);
    }


    private void ViewPutout() {
        bg_imageview.animate().alpha(1f).setDuration(mAnimationTime-100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        relativeLayout_video.setVisibility(View.GONE);
                        appbar.setVisibility(View.VISIBLE);
                    }
                } );
    }

}
