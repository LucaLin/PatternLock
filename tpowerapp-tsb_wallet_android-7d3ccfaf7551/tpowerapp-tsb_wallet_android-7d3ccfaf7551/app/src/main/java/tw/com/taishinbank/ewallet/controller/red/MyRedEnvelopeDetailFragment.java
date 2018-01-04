package tw.com.taishinbank.ewallet.controller.red;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.red.MyRedEnvelopeDetailReceivedAdapter;
import tw.com.taishinbank.ewallet.adapter.red.MyRedEnvelopeDetailSentAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentHeader;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class MyRedEnvelopeDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView textName;
    private TextView textTime;
    private TextView textAmount;
    private TextView textMessage;
    private ImageView imagePhoto;
    private RedEnvelopeHeader data;
    private View view;


    private static final String ARG_DETAIL_DATA = "arg_detail_data";

    /**
     * 用來建立Fragment
     */
    public static MyRedEnvelopeDetailFragment newInstance(RedEnvelopeHeader data) {
        MyRedEnvelopeDetailFragment f = new MyRedEnvelopeDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_DETAIL_DATA, data);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        data = getArguments().getParcelable(ARG_DETAIL_DATA);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_envelope_detail, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 設定layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        this.view = view;
        setupView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 根據資料類型設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        if(data != null) {
            // 如果是送出的紅包詳情
            if (data instanceof RedEnvelopeSentHeader) {
                activityBase.setCenterTitle(R.string.title_sent_red_envelope);
            // 如果是收到的紅包詳情
            } else {
                activityBase.setCenterTitle(R.string.title_received_red_envelope);
            }
        }else{
            activityBase.setCenterTitle(R.string.my_red_envelope);
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

    /**
     * 根據是收到還是發出，設定畫面顯示資料
     */
    // TODO 很醜的code，可能要改成用繼承比較好
    private void setupView(){
        if(data == null || view == null){
            return ;
        }

        textName = (TextView) view.findViewById(R.id.text_name);
        textTime = (TextView) view.findViewById(R.id.text_time);
        textAmount = (TextView) view.findViewById(R.id.text_amount);
        textMessage = (TextView) view.findViewById(R.id.text_message);
        textMessage.setMovementMethod(new ScrollingMovementMethod());
        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);

        // 如果是發送的紅包詳情
        if(data instanceof RedEnvelopeSentHeader){
            RedEnvelopeSentHeader header = (RedEnvelopeSentHeader)data;
            // 顯示所有收紅包的人的名稱
            textName.setText(PreferenceUtil.getNickname(getActivity()));
            String formattedTime = FormatUtil.toTimeFormatted(header.getCreateDate());
            textTime.setText(formattedTime);
            String formattedAmount = FormatUtil.toDecimalFormatFromString(header.getAmount(), true);
            textAmount.setText(formattedAmount);
            textMessage.setText(header.getSenderMessage());
            // TODO 設成真正的大頭
            // 設定頭像
            ImageLoader imageLoader = new ImageLoader(getActivity(), getActivity().getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(PreferenceUtil.getMemNO(getActivity()), imagePhoto);

            MyRedEnvelopeDetailSentAdapter adapter = new MyRedEnvelopeDetailSentAdapter(imageLoader, header.getTxDetailList());
            recyclerView.setAdapter(adapter);

        // 如果是收到的紅包詳情
        }else{
            RedEnvelopeReceivedHeader header = (RedEnvelopeReceivedHeader)data;
            // 顯示發送者的名稱
            textName.setText(header.getSender());
            String formattedTime = FormatUtil.toTimeFormatted(header.getCreateDate());
            textTime.setText(formattedTime);
            // 不顯示總金額
            textAmount.setVisibility(View.INVISIBLE);
            textMessage.setText(header.getMessage());
            // TODO 設成真正的大頭
            // 設定頭像
            ImageLoader imageLoader = new ImageLoader(getActivity(), getActivity().getResources().getDimensionPixelSize(R.dimen.list_photo_size));
            imageLoader.loadImage(header.getSenderMem(), imagePhoto);

            MyRedEnvelopeDetailReceivedAdapter adapter = new MyRedEnvelopeDetailReceivedAdapter(imageLoader, header.getTxDetailList());
            recyclerView.setAdapter(adapter);
        }
    }
}
