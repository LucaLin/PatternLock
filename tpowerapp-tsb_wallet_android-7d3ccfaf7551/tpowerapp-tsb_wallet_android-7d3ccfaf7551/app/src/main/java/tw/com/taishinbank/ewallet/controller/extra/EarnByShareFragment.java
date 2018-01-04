package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.model.sv.ShareToOption;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

public class EarnByShareFragment extends Fragment {

    private static final String TAG = "EarnByShareFragment";
    private final int REQUEST_CODE_SHARE = 9889;

    // -- View Hold --
    private Button btnShareInviteCode;
    private TextView txtInviteCode;
    private AlertDialog dlgShare;

    // -- Listener / Adapter / ....
    private ArrayAdapter<ShareToOption> shareToOptionArrayAdapter;

    public EarnByShareFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_earn_by_share, container, false);

        // Set view hold
        btnShareInviteCode = (Button) view.findViewById(R.id.btn_earn_share);
        txtInviteCode = (TextView) view.findViewById(R.id.txt_invite_code);

        // Set listener
        btnShareInviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShareInviteCode();
            }
        });

        // 呼叫api取得分享邀請碼
        try {
            ExtraHttpUtil.queryInviteCode(responseListener, getActivity(), TAG);
            ((ActivityBase) getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        ExtraHttpUtil.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            getFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getActivity() != null) {
            ((ActivityBase) getActivity()).dismissProgressLoading();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("EarnByShare", " onActivityResult() - requestCode:" + requestCode
                + ", resultCode:" + resultCode + ", data:" + data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ---
    // My methods
    // ---
    protected AlertDialog createShareDialog() {
        shareToOptionArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice);
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_LINE,         getResources().getString(R.string.extra_earn_share_to_line)));
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_FB_MESSENGER, getResources().getString(R.string.extra_earn_share_to_fb_messenger)));
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_BY_EMAIL,     getResources().getString(R.string.extra_earn_share_to_by_email)));
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_BY_SMS,     getResources().getString(R.string.extra_earn_share_to_by_sms)));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setAdapter(shareToOptionArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareTo(which);
                        dlgShare.dismiss();
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    // ----
    // User interaction
    // ----
    protected void clickShareInviteCode() {
        if(dlgShare == null) {
            dlgShare = createShareDialog();
        }
        dlgShare.show();
    }

    protected void shareTo(int which) {
        ShareToOption shareToOption = shareToOptionArrayAdapter.getItem(which);

        // Determine Intent Type and parameters...
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String messageFormat = "下載並註冊LETSPAY行動錢包，輸入我的邀請碼：「%1$s」，就有機會得到優惠好康！\n" +
                                "Android Google Play：\n https://goo.gl/Mq7mW4\n" +
                                "\n" +
                                "APPLE app store：\n https://goo.gl/nL7hLd";
        String message = String.format(messageFormat, txtInviteCode.getText());
        String appName = "";
        switch (shareToOption.getId()) {
            case (ShareToOption.SHARE_TO_FB_MESSENGER):
                EventAnalyticsUtil.uploadHitRecordEvent(getActivity(), HitRecord.HitEvent.SHARE_INVITE_CODE, HitRecord.HitType.FACEBOOK);

                shareIntent.setPackage("com.facebook.orca");
                appName = "FB Messenger";

                break;

            case (ShareToOption.SHARE_TO_LINE):
                EventAnalyticsUtil.uploadHitRecordEvent(getActivity(), HitRecord.HitEvent.SHARE_INVITE_CODE, HitRecord.HitType.LINE);

                shareIntent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
                appName = "Line Messenger";

                break;

            case (ShareToOption.SHARE_TO_BY_EMAIL):
                EventAnalyticsUtil.uploadHitRecordEvent(getActivity(), HitRecord.HitEvent.SHARE_INVITE_CODE, HitRecord.HitType.EMAIL);

                shareIntent.setAction(Intent.ACTION_SENDTO);
                shareIntent.setData(Uri.parse("mailto:"));

                break;

            case (ShareToOption.SHARE_TO_BY_SMS):
                EventAnalyticsUtil.uploadHitRecordEvent(getActivity(), HitRecord.HitEvent.SHARE_INVITE_CODE, HitRecord.HitType.SMS);

                shareIntent.setAction(Intent.ACTION_SENDTO);
                shareIntent.setData(Uri.parse("smsto:"));
                shareIntent.putExtra("sms_body", message);

                startActivityForResult(shareIntent, REQUEST_CODE_SHARE);

                return;

            default:
        }


        if (shareIntent != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, PreferenceUtil.getNickname(getContext()) + "發送邀請");

            startActivityForResult(Intent.createChooser(shareIntent, getResources().getString(R.string.share)), REQUEST_CODE_SHARE);
        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage(getResources().getString(R.string.share, appName))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    // 呼叫查詢分享碼的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null) {
                return;
            }

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，更新邀請碼
                String shareCode = ExtraResponseBodyUtil.getInviteCode(result.getBody());
                txtInviteCode.setText(shareCode);
            }else{
                // 執行預設的錯誤處理
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };
}
