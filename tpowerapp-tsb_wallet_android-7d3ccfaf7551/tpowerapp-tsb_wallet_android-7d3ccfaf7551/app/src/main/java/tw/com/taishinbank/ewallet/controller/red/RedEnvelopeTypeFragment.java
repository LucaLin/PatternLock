package tw.com.taishinbank.ewallet.controller.red;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.ContactListActivity;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeInputData;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;

public class RedEnvelopeTypeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RedEnvelopeTypeFragment";
    private static final String ARG_SV_INFO = "arg_sv_token";
    private static final String ARG_BACK_TO_SOURCE_PAGE = "arg_back_to_source_page";
    private boolean backToSourcePage = false;

    public RedEnvelopeTypeFragment() {
        // Required empty public constructor
    }

    public static RedEnvelopeTypeFragment newInstance(SVAccountInfo svAccountInfo) {
        RedEnvelopeTypeFragment fragment = new RedEnvelopeTypeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SV_INFO, svAccountInfo);
        fragment.setArguments(args);
        return fragment;
    }

    public static RedEnvelopeTypeFragment newInstance(boolean backToSourcePage) {
        RedEnvelopeTypeFragment fragment = new RedEnvelopeTypeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_BACK_TO_SOURCE_PAGE, backToSourcePage);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(getArguments() == null || getArguments().getParcelable(ARG_SV_INFO) == null) {
            // 更新儲值帳戶
            try {
                RedEnvelopeHttpUtil.getSVAccountInfo(responseListenerSV, getActivity(), TAG);
            } catch (JSONException e) {
                e.printStackTrace();
                // DO NOTHING
            }
        }

        // 取得是否返回呼叫頁
        backToSourcePage = (getArguments() != null && getArguments().getBoolean(ARG_BACK_TO_SOURCE_PAGE));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_red_envelope_type, container, false);

        ImageView imageMoneyGod = (ImageView) view.findViewById(R.id.image_money_god);
        imageMoneyGod.setOnClickListener(this);

        ImageView imageGeneralRed = (ImageView) view.findViewById(R.id.image_general_red_envelope);
        imageGeneralRed.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.send_red_envelope);
        activityBase.getWindow().setBackgroundDrawableResource(R.drawable.bg_red);
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            // 如果要返回前一個頁面，執行activity的onBackPressed
            if(backToSourcePage){
                getActivity().onBackPressed();
            }else {
                getFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // 財神紅包
        if(v.getId() == R.id.image_money_god){
            Intent intent = new Intent(getActivity(), ContactListActivity.class);
            // 產生財神紅包需要的物件並放到intent
            RedEnvelopeInputData inputData = new RedEnvelopeInputData(RedEnvelopeType.TYPE_MONEY_GOD);
            intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
            // 告訴聯絡人頁面，只顯示儲值帳戶，並顯示可勾選的列表
            intent.putExtra(ContactListActivity.EXTRA_SHOW_SV_ONLY, true);
            intent.putExtra(ContactListActivity.EXTRA_IS_LISTITEM_SELECTABLE, true);
            startActivity(intent);
        }
        else if(v.getId() == R.id.image_general_red_envelope){
            Intent intent = new Intent(getActivity(), ContactListActivity.class);
            // 產生紅包需要的物件並放到intent
            RedEnvelopeInputData inputData = new RedEnvelopeInputData(RedEnvelopeType.TYPE_GENERAL);
            intent.putExtra(RedEnvelopeInputData.EXTRA_RED_ENVELOPE_DATA, inputData);
            // 告訴聯絡人頁面，只顯示儲值帳戶，並顯示可勾選的列表
            intent.putExtra(ContactListActivity.EXTRA_SHOW_SV_ONLY, true);
            intent.putExtra(ContactListActivity.EXTRA_IS_LISTITEM_SELECTABLE, true);
            startActivity(intent);
        }
    }

    // 呼叫取得儲值帳戶資訊api的listener
    private ResponseListener responseListenerSV = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            // 成功的話，更新儲值帳戶資訊存sharedpreference
            // 失敗就算了
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                if(getContext() != null) {
                    PreferenceUtil.setSVAccountInfo(getContext(), result.getBody().toString());
                }
            }
        }
    };
}
