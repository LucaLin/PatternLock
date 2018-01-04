package com.dbs.omni.tw.controller.setting.editProfile.modifyNicknameAndAva;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.setting.editProfile.EditProfileActivity;
import com.dbs.omni.tw.element.InputTextView;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.BitmapUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PermissionUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.SettingHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;

import org.json.JSONException;

import java.io.File;

public class ModifyNicknameAndAvaActivity extends ActivityBase implements View.OnClickListener, PermissionUtil.OnPermissionListener {

    private static final String TAG = "ModifyNicknameAndAvaActivity";

    private Uri photoUri = null;
    private TextView btn_change_ava; //由於套圖的問題 , 所以宣告為textview
    private TextView text_nickname;
    private Button btnSave;
    private ContentResolver resolver;
    private ImageView image_avatar;
    private InputTextView inputTextNickName;

    private static final int PICK_IMAGE_REQUEST = 9999;
    private static final int CROP_IMAGE_REQUEST = 8888;
    private static final int CAMERA_IMAGE_REQUEST = 7777;

    private Bitmap bitmap = null;
    private String strNickname;
    private boolean isAvatarChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname_and_ava);

        setCenterTitle(R.string.personal_service_list_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(btnSaveListener);
        btnSave.setEnabled(false);

        inputTextNickName = (InputTextView) findViewById(R.id.inputText_nickname);
        inputTextNickName.setTitle(R.string.input_new_nickname);
//        inputTextNickName.setAmountSign(R.string.amount_sign_for_tw);
        inputTextNickName.setOnFinishEdit(onFinishEditListener);

        text_nickname = (TextView)findViewById(R.id.text_nickname);
        image_avatar = (ImageView)findViewById(R.id.image_avatar);

        initScreen();

        btn_change_ava = (TextView)findViewById(R.id.btn_change_ava);
        btn_change_ava.setOnClickListener(this);

        Intent intent = this.getIntent();
        if (getIntent().hasExtra(EditProfileActivity.FUNCTION＿NAME)){
            int intFunctionName = intent.getIntExtra(EditProfileActivity.FUNCTION＿NAME,0);

            switch (intFunctionName){
                case R.id.item_camera:
                    actInPopUpMenu(R.id.item_camera);
                    break;

                case R.id.item_picture:
                    actInPopUpMenu(R.id.item_picture);
                    break;

                case R.id.item_delete:
                    actInPopUpMenu(R.id.item_delete);
                    break;

                default:
                    break;
            }
        }

//        checkPickImage();

    }

//    private void checkPickImage()
//    {
//        if(imageLoader == null){
//            imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.photo_size));
//        }
//        // 設定頭像
//        imageLoader.loadImage(PreferenceUtil.getMemNO(this), image_avatar);
//    }

    @Override
    protected void onResume(){
        super.onResume();

//        resolver = getContentResolver();
//
//        try {
//            if(photoUri != null){
//                Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, photoUri);
//                if(bm != null){
//                    image_avatar.setImageBitmap(bm);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void initScreen(){
        //設定暱稱
        text_nickname.setText(PreferenceUtil.getNickname(ModifyNicknameAndAvaActivity.this));

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

    private void setAvatarTemp(){
        //設定頭像
        String folderPath = GlobalConst.FolderPath;
        String filePath = folderPath + File.separator + "avatar_temp" + ".png";
        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        File imgFile = new File(filePath);
        Bitmap bmAvaTemp = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);

        if (bmAvaTemp != null){
            image_avatar.setImageBitmap(bmAvaTemp);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_change_ava){
            showPopupMenu(v);
        }
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                enum_update_item = ENUM_UPDATE_ITEM.IMAGE;
                switch (item.getItemId()) {
                    case R.id.item_camera:
                        actInPopUpMenu(R.id.item_camera);
                        return true;

                    case R.id.item_picture:
                        actInPopUpMenu(R.id.item_picture);
                        return true;

                    case  R.id.item_delete:
                        actInPopUpMenu(R.id.item_delete);
                        return true;
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.personal_sevice_popup_menu);
        popupMenu.show();
    }

    private void actInPopUpMenu (int functionID){
        switch (functionID) {
            case R.id.item_camera:
                // 先確認是否有讀取外部儲存空間的權限
                String[] permissions_c = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!PermissionUtil.needGrantRuntimePermission(ModifyNicknameAndAvaActivity.this, permissions_c,
                        PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
                    Intent intent_camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); // save to file and pass the file path

                    // 確認資料夾是否存在
                    String folderPath = GlobalConst.FolderPath;
                    File folder = new File(folderPath);
                    if (!folder.exists()) {
                        if (!folder.mkdir()) {
                        }
                    }
                    // 組合並指定輸出路徑
                    String filePath = folderPath + File.separator + "temp.png";
                    photoUri = Uri.fromFile(new File(filePath));
                    intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent_camera, CAMERA_IMAGE_REQUEST);

                }
                break;

            case R.id.item_picture:
                // 先確認是否有權限讀寫外部儲存空間
                String[] permissions_p = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!PermissionUtil.needGrantRuntimePermission(ModifyNicknameAndAvaActivity.this, permissions_p,
                        PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
                break;

            case R.id.item_delete:
                image_avatar.setImageResource(R.drawable.ic_ava_default);
                isAvatarChange = true;
                isEnableSaveButton();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 判斷是否登入成功
        if(resultCode == Activity.RESULT_OK) {

            // 如果是拍攝照片
            if ((requestCode == PICK_IMAGE_REQUEST || requestCode == CAMERA_IMAGE_REQUEST )){
                // 取得相片資料
                Uri uri;
                if(data != null && data.getData() != null){
                    uri = data.getData();
                }else{
                    uri = photoUri;
                }

                // 開啟裁切圖片的頁面
                Intent intent = new Intent(this, CropImageActivity.class);
                // 設定圖片來源
                intent.setData(uri);
                // 設定切圖存檔的memNO
//                intent.putExtra(CropImageActivity.EXTRA_MEM_NO, PreferenceUtil.getMemNO(this));
                startActivityForResult(intent, CROP_IMAGE_REQUEST);


            // 如果是裁切圖片
            }else if(requestCode == CROP_IMAGE_REQUEST){
                if(data.hasExtra(CropImageActivity.EXTRA_IMAGE) && data != null) {
                    // 如果是檔案路徑
                    String filePath = data.getStringExtra(CropImageActivity.EXTRA_IMAGE);
                    if (filePath.indexOf(GlobalConst.FolderPath) != -1) {

                        File imgFile = new File(filePath);

                        if (imgFile.exists()) {
                            // 取出圖片
                            int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
                            bitmap = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);

                            //設定flag , 並暫時把頭像換成avatar_temp.png , 等成功呼API上傳再換成avatar.png
                            isAvatarChange = true;
                            setAvatarTemp();
                            isEnableSaveButton();
                        }
                    }
                }

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                showPopupMenu(image_avatar);
            }else{
                PermissionUtil.showNeedPermissionDialog(this, permissions, grantResults);
            }
        }
    }

    private Button.OnClickListener btnSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //儲存暱稱
            strNickname = inputTextNickName.getContent();

            if(strNickname != null && !strNickname.isEmpty()){

                if (!NetworkUtil.isConnected(ModifyNicknameAndAvaActivity.this)) {
                    showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                } else {
                    try {
                        SettingHttpUtil.updateNickName(strNickname, responseListener_updateNickName, ModifyNicknameAndAvaActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAvatarChange == true){
                callAPIUploadPicture();
            }
        }
    };


    private ResponseListener responseListener_updateNickName = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                PreferenceUtil.setNickname(ModifyNicknameAndAvaActivity.this, strNickname);
                text_nickname.setText(PreferenceUtil.getNickname(ModifyNicknameAndAvaActivity.this));

                //繼續做上傳頭像
                if(isAvatarChange == true){
                    callAPIUploadPicture();
                }else{
                    finish();
                }
            } else {
                handleResponseError(result, ModifyNicknameAndAvaActivity.this);
            }

        }
    };


    private void callAPIUploadPicture(){
        String base64Ava = "";

        //如果有圖片，上傳圖像
        if (bitmap != null) {
            //將圖片轉成base64
            base64Ava = BitmapUtil.bitmapToBase64(bitmap);

        }

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(ModifyNicknameAndAvaActivity.this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                SettingHttpUtil.uploadPicture(base64Ava, "png", responseListener_uploadPicture, ModifyNicknameAndAvaActivity.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_uploadPicture = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {

                if(bitmap != null){
                    //設置頭像
                    image_avatar.setImageBitmap(bitmap);

                    //刪除不必要的檔案
                    BitmapUtil.delTempFile();

                    //將上傳的檔案覆寫本機端的檔案
                    BitmapUtil.put(bitmap);
                }

                finish();
            } else {
                handleResponseError(result, ModifyNicknameAndAvaActivity.this);
            }
        }
    };

    //檢查輸入內容
    private void isEnableSaveButton() {
        if(isAvatarChange == true || !TextUtils.isEmpty(inputTextNickName.getContent())) {
            btnSave.setEnabled(true);
            return;
        }
        btnSave.setEnabled(false);
    }

    private InputTextView.OnFinishEditListener onFinishEditListener  = new InputTextView.OnFinishEditListener() {
        @Override
        public void OnFinish() {
            isEnableSaveButton();
        }
    };

    //當使用者有修改內容卻沒有點儲存時 , 跳出alert詢問是否確定離開
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if(isAvatarChange == true || !TextUtils.isEmpty(inputTextNickName.getContent())) {
            showAlertDialog(getString(R.string.edit_profile_alert), R.string.button_confirm, android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            superOnBackPressed();
                        }
                    }, false);
        }else{
            superOnBackPressed();
        }
    }

    private void superOnBackPressed(){
        super.onBackPressed();
    }

}
