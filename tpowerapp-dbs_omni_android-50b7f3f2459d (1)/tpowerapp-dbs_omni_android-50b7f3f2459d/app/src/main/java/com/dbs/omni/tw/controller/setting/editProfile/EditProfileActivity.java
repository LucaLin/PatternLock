package com.dbs.omni.tw.controller.setting.editProfile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.setting.editProfile.changeUserData.ChangeUserDataActivity;
import com.dbs.omni.tw.controller.setting.editProfile.modifyNicknameAndAva.ModifyNicknameAndAvaActivity;
import com.dbs.omni.tw.element.EditProfileItem;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.BitmapUtil;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.UserInfoUtil;

import java.io.File;

public class EditProfileActivity extends ActivityBase {

    private TextView btn_change_ava; //由於套圖的問題 , 所以宣告為textview
    private TextView text_nickname;
    private EditProfileItem editProfileItem_nickname;
    private LinearLayout linearLayout_press;
    private LinearLayout linearLayoutList;
    private ImageView image_avatar;

    public static final String FUNCTION＿NAME = "FUNCTIONNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setCenterTitle(R.string.personal_service_list_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        image_avatar = (ImageView)findViewById(R.id.image_avatar);

        text_nickname = (TextView)findViewById(R.id.text_nickname);


//        btn_change_ava = (TextView)findViewById(R.id.btn_change_ava);
//        btn_change_ava.setOnClickListener(btnListListener);
        linearLayout_press = (LinearLayout)findViewById(R.id.linearLayout_press);
        linearLayout_press.setOnClickListener(btnListListener);

        linearLayoutList = (LinearLayout)findViewById(R.id.LinearLayout_List);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //更新個人資料
        UserInfoUtil.getUserInfo(this, new UserInfoUtil.OnUserInfoListener() {
            @Override
            public void OnFinish() {
                initScreen();
                setView();
            }

            @Override
            public void OnFail() {

            }
        });
    }

    private void setView() {
        linearLayoutList.removeAllViews();

        String stringAccount = "";
        if(!TextUtils.isEmpty(UserInfoUtil.getsUserCode())){
            stringAccount = UserInfoUtil.getsUserCode();
        }
        String stringNickName = "";
        if(!TextUtils.isEmpty(UserInfoUtil.getsNickname())){
            stringNickName = UserInfoUtil.getsNickname();
        }
        String stringEmail = "";
        if(UserInfoUtil.getsEmailDetail() != null){
            stringEmail = UserInfoUtil.getsEmailDetail().getEmail();
        }
        String stringPhoneNumber = "";
        if(UserInfoUtil.getsPhoneDetail() != null){
            stringPhoneNumber = UserInfoUtil.getsPhoneDetail().getPhoneNumber();
        }
        String stringAddress = "";
        if(UserInfoUtil.getsAddressDetail() != null){
            stringAddress = UserInfoUtil.getsAddressDetail().getAddress();
        }

        addItemView(R.string.edit_profile_user_account, stringAccount);
        addItemView(R.string.edit_profile_user_nickname, stringNickName);
        addItemView(R.string.edit_profile_user_email, FormatUtil.getHiddenEmail(stringEmail));
        addItemView(R.string.edit_profile_user_phone_number, FormatUtil.getHiddenPhoneNumber(stringPhoneNumber));
        addItemView(R.string.edit_profile_user_address, FormatUtil.getHiddenAddress(stringAddress));
        addItemView(R.string.edit_profile_user_password, FormatUtil.getHiddenPassword());
    }

    //region add scroll view item的method
    private void addItemView(int titleResourceId, String strContent){

        EditProfileItem editProfileItem = new EditProfileItem(this);

        //設定text
        editProfileItem.setTitle(titleResourceId);
        editProfileItem.setContent(strContent);

        //設定更新中的圖示
        if(titleResourceId == R.string.edit_profile_user_phone_number  && UserInfoUtil.getsPhoneDetail() != null){
            if(!TextUtils.isEmpty(UserInfoUtil.getsPhoneDetail().getUpdatedPhoneNumber())){
                editProfileItem.turnOnTextWaitForUpdate();
            }
        }
        if(titleResourceId == R.string.edit_profile_user_address && UserInfoUtil.getsAddressDetail() != null){
            if(!TextUtils.isEmpty(UserInfoUtil.getsAddressDetail().getUpdatedAddress())){
                editProfileItem.turnOnTextWaitForUpdate();
            }
        }

        //設定listener和tag
        editProfileItem.setOnClickListener(onListListener);
        editProfileItem.setTag(titleResourceId);

        linearLayoutList.addView(editProfileItem);

        if(titleResourceId == R.string.edit_profile_user_nickname){
            editProfileItem_nickname = editProfileItem;
        }
    }
    //endregion


    private void initScreen(){
        //設定暱稱
        text_nickname.setText(PreferenceUtil.getNickname(this));

        if(editProfileItem_nickname != null){
            editProfileItem_nickname.setContent(PreferenceUtil.getNickname(EditProfileActivity.this));
        }

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

    //region 各項目的listener
    private View.OnClickListener onListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            Intent intent = new Intent();

            switch (tag){

                //變更使用者帳號
                case R.string.edit_profile_user_account:
                    intent.setClass(EditProfileActivity.this , ChangeUserDataActivity.class);
                    intent.putExtra(FUNCTION＿NAME , R.string.personal_service_list_account_modify);
                    startActivity(intent);
                    break;

                //變更使用者暱稱
                case R.string.edit_profile_user_nickname:
                    intent.setClass(EditProfileActivity.this , ModifyNicknameAndAvaActivity.class);
                    startActivity(intent);
                    break;

                //變更電子信箱
                case R.string.edit_profile_user_email:
                    intent.setClass(EditProfileActivity.this , ChangeUserDataActivity.class);
                    intent.putExtra(FUNCTION＿NAME , R.string.personal_service_list_email_modify);
                    startActivity(intent);
                    break;

                //變更手機
                case R.string.edit_profile_user_phone_number:
                    intent.setClass(EditProfileActivity.this , ChangeUserDataActivity.class);
                    intent.putExtra(FUNCTION＿NAME , R.string.personal_service_list_phone_modify);
                    startActivity(intent);
                    break;

                //變更地址
                case R.string.edit_profile_user_address:
                    intent.setClass(EditProfileActivity.this , ChangeUserDataActivity.class);
                    intent.putExtra(FUNCTION＿NAME , R.string.personal_service_list_address_modify);
                    startActivity(intent);
                    break;

                //變更使用者密碼
                case R.string.edit_profile_user_password:
                    intent.setClass(EditProfileActivity.this , ChangeUserDataActivity.class);
                    intent.putExtra(FUNCTION＿NAME , R.string.personal_service_list_password_modify);
                    startActivity(intent);
                    break;

            }

        }
    };

    private View.OnClickListener btnListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent();
//            intent.setClass(EditProfileActivity.this , ModifyNicknameAndAvaActivity.class);
//            startActivity(intent);
            showPopupMenu(linearLayout_press);
        }
    };
    //endregion

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                enum_update_item = ENUM_UPDATE_ITEM.IMAGE;
                Intent intent = new Intent();
                switch (item.getItemId()) {
                    case R.id.item_camera:
                        intent.putExtra(FUNCTION＿NAME , R.id.item_camera);
                        intent.setClass(EditProfileActivity.this , ModifyNicknameAndAvaActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.item_picture:
                        intent.putExtra(FUNCTION＿NAME , R.id.item_picture);
                        intent.setClass(EditProfileActivity.this , ModifyNicknameAndAvaActivity.class);
                        startActivity(intent);
                        return true;

                    case  R.id.item_delete:
                        intent.putExtra(FUNCTION＿NAME , R.id.item_delete);
                        intent.setClass(EditProfileActivity.this , ModifyNicknameAndAvaActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.personal_sevice_popup_menu);
        popupMenu.show();
    }
}
