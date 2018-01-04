package com.dbs.omni.tw.controller.setting;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.logout.LogoutActivity;
import com.dbs.omni.tw.controller.setting.applyCreditCard.ApplyCreditCardActivity;
import com.dbs.omni.tw.controller.setting.editProfile.EditProfileActivity;
import com.dbs.omni.tw.controller.setting.contactDBS.ContactDBSActivity;
import com.dbs.omni.tw.controller.setting.editProfile.modifyNicknameAndAva.ModifyNicknameAndAvaActivity;
import com.dbs.omni.tw.controller.setting.touchid.TouchIDActivity;
import com.dbs.omni.tw.element.PersonalServiceListItem;

import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.BitmapUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PermissionUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.fingeprint.FingerprintCore;
import com.dbs.omni.tw.util.fingeprint.FingerprintUtil;
import com.dbs.omni.tw.util.http.RegisterHttpUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.setting.DownloadImageData;
import com.dbs.omni.tw.util.http.mode.register.LogoutDate;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.RegisterResponseBodyUtil;
import com.dbs.omni.tw.util.http.responsebody.SettingResponseBodyUtil;

import org.json.JSONException;

import java.io.File;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonlServiceFragment extends Fragment implements PermissionUtil.OnPermissionForFragmentListener{

    public final static String TAG = "PersonlServiceFragment";

    private LayoutInflater inflater;
    private LinearLayout linearLayoutList;
    private ImageView image_avatar,icon_modify_ava;
    private TextView text_nickname;

    private String strAva;

    public static final String FUNCTION＿NAME = "FUNCTIONNAME";

//    private OnLogoutListener mOnLogoutListener;
//
//    public void setOnLogoutListener(OnLogoutListener listener) {
//        this.mOnLogoutListener = listener;
//    }
//
//    public interface OnLogoutListener {
//        void OnLogout();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).setCenterTitle("");
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((ActivityBase) getActivity()).setHeadHide(true);
            ((ActivityBase) getActivity()).setStatusBarShow(true);
        }

        inflater = getActivity().getLayoutInflater();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personl_service, container, false);

        image_avatar = (ImageView)view.findViewById(R.id.image_avatar);
        image_avatar.setOnClickListener(btnOnClickListener);

        icon_modify_ava = (ImageView)view.findViewById(R.id.icon_modify_ava);
        icon_modify_ava.setOnClickListener(btnOnClickListener);

        text_nickname = (TextView)view.findViewById(R.id.text_nickname);
        
        //logout
        TextView buttonLogout = (TextView) view.findViewById(R.id.btn_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mOnLogoutListener.OnLogout();
//                goToLogoutPage();

                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.logout),
                        getString(R.string.logout_hint), R.string.button_confirm, android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                if(GlobalConst.UseLocalMock) {
//                                    goToLogoutPage(null);
//                                } else {
                                    logout("1");
//                                }
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false);

            }
        });

        linearLayoutList = (LinearLayout) view.findViewById(R.id.LinearLayout_List);

        //region 加入scroll view裡面的項目
        addItemView(R.drawable.ic_apply_credit_card, R.string.personal_service_list_apply_credit_card);
        final View touchIDItemView = addItemView(R.drawable.ic_personal_service_touch_id, R.string.personal_service_list_touch_id);
        addItemView(R.drawable.ic_change_account, R.string.personal_service_list_edit_profile);
        addItemView(R.drawable.ic_change_email, R.string.personal_service_list_notification_setting);
        addItemView(R.drawable.ic_contact_dbs, R.string.personal_service_list_contact_dbs);
        //endregion

        FingerprintUtil.detectFingerprint(getActivity(), false, new FingerprintCore.OnDetectFingerprintListener() {
            @Override
            public void OnIsSupport() {

            }

            @Override
            public void OnIsClose() {

            }

            @Override
            public void OnIsNotSupport() {
                linearLayoutList.removeView(touchIDItemView);
            }
        });


        getImageAvaFromAPI();
        setNickName();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserImage();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void setNickName() {
        //設定暱稱
        text_nickname.setText(PreferenceUtil.getNickname(getActivity()));
    }

    private void setUserImage() {
        //設定頭像
        String folderPath = GlobalConst.FolderPath;
        String filePath = folderPath + File.separator + "avatar" + ".png";
        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        File imgFile = new File(filePath);
        Bitmap bmAva = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);

        if (bmAva != null){
            image_avatar.setImageBitmap(bmAva);
        }
    }

    private Button.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity() , ModifyNicknameAndAvaActivity.class);
            startActivity(intent);
        }
    };


    //region add scroll view item的method
    private View addItemView(int iconResourceId, int titleResourceId){

        PersonalServiceListItem personalServiceListItem = new PersonalServiceListItem(getActivity());

        //設定title
        personalServiceListItem.setIcon(iconResourceId);
        //設icon
        personalServiceListItem.setTitle(titleResourceId);

        //設定listener和tag
        personalServiceListItem.setOnClickListener(onListListener);
        personalServiceListItem.setTag(titleResourceId);

        linearLayoutList.addView(personalServiceListItem);

        return personalServiceListItem;
    }
    //endregion

    //region 各項目的listener
    private View.OnClickListener onListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            Intent intent = new Intent();

            switch (tag){

                //加辦信用卡
                case R.string.personal_service_list_apply_credit_card:
                    intent.setClass(getActivity() , ApplyCreditCardActivity.class);
                    startActivity(intent);
                    break;

                //TOUCH ID
                case R.string.personal_service_list_touch_id:
                    intent.setClass(getActivity() , TouchIDActivity.class);
                    startActivity(intent);
                    break;

                //變更使用者資訊
                case R.string.personal_service_list_edit_profile:
                    intent.setClass(getActivity() , EditProfileActivity.class);
                    startActivity(intent);
                    break;

                //通知訊息設定
                case R.string.personal_service_list_notification_setting:
                    break;

                //聯繫星展
                case R.string.personal_service_list_contact_dbs:
                    intent.setClass(getActivity() , ContactDBSActivity.class);
                    startActivity(intent);
                    break;

            }

        }
    };
    //endregion

    private void goToLogoutPage(LogoutDate logoutDate){
        Intent intent = new Intent(getActivity(), LogoutActivity.class);
        intent.putExtra(LogoutActivity.EXTRA_DATA, logoutDate);
        startActivity(intent);
    }

//region cell Api
    private void logout(String logoutType) {

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase) getActivity()).showAlertDialog(getActivity().getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                RegisterHttpUtil.logoutUser(logoutType ,responseListener, getActivity());
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private ResponseListener responseListener = new ResponseListener() {


        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
//                getSystmInfo();
                LogoutDate logoutDate = RegisterResponseBodyUtil.getLogoutDate(result.getBody());
                goToLogoutPage(logoutDate);
            } else {
                handleResponseError(result, ((ActivityBase) getActivity()));

            }

        }
    };




    //從API下載頭像
    private void getImageAvaFromAPI(){
        //從API下載頭像並存到local端相應的位置
        String[] permissions_c = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!PermissionUtil.needGrantRuntimePermission(this, permissions_c,
                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {


            // 如果沒有網路連線，顯示提示對話框
            if (!NetworkUtil.isConnected(getActivity())) {
                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
            } else {
                try {
                    String downloadTime = PreferenceUtil.getHeaderDownloadTime(getContext());
                    SettingHttpUtil.downloadPicture(downloadTime, responseListener_downloadPicture, getActivity());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private ResponseListener responseListener_downloadPicture = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得base64的字串
                DownloadImageData downloadImageData = SettingResponseBodyUtil.getDownloadImage(result.getBody());
                strAva = downloadImageData.getPic();

                //如果字串有值就反組譯成bitmap
                if(!TextUtils.isEmpty(strAva)){


                    Bitmap bitmap = BitmapUtil.base64ToBitmap(strAva);

                    //如果bitmap不為空 , 就存到local端相應的位置
                    if(bitmap != null){
                        BitmapUtil.put(bitmap);
                        setUserImage();
                        Calendar calendar = Calendar.getInstance();
                        CharSequence currentTime = DateFormat.format("yyyyMMddHHmmss", calendar.getTime());
                        PreferenceUtil.setHeaderDownloadTime(getContext(), currentTime.toString());
                    }
                }
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                handleCommonError(result, (ActivityBase) getActivity());
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                getImageAvaFromAPI();
            }
        }
    }

    @Override
    public void onRequestPermissionsResultForAndroid5(int requestCode) {
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            getImageAvaFromAPI();
        }
    }
//regioned

}
