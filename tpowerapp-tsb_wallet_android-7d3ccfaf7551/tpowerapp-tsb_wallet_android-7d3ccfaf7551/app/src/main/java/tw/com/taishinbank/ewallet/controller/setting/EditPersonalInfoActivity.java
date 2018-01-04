package tw.com.taishinbank.ewallet.controller.setting;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.CropImageActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class EditPersonalInfoActivity extends ActivityBase implements View.OnClickListener {

    private static final String TAG = "EditPersonalInfoActivity";

    public enum ENUM_UPDATE_ITEM
    {
        IMAGE,
        NAME,
        PASSWORD,
        EMAIL,
        PHONE
    }

    public static final String EXTRA_CHANGE_ITEM = "extra_change_item";
    public static final String EXTRA_CHANGE_DATA = "extra_change_data";


    private static final int REQUEST_LOGIN_SV = 123;
    private static final int REQUEST_LOGIN_SV_FROM_SEND = 124;
    private static final int PICK_IMAGE_REQUEST = 9999;
    private static final int CROP_IMAGE_REQUEST = 8888;
    private static final int CAMERA_IMAGE_REQUEST = 7777;

    private ImageLoader imageLoader;

    private Bitmap bitmap = null;
    private Uri photoUri = null;
    private ImageView imagePhoto;
    private FrameLayout buttonPickImage;

    private Activity activity;
    private ENUM_UPDATE_ITEM enum_update_item;
    private String updateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal);

        activity = this;

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.drawer_item_settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View topInfoView = findViewById(R.id.headline);
        this.setHeadline(topInfoView, R.string.edit_personal_title, R.string.edit_personal_message);


        buttonPickImage = (FrameLayout) findViewById(R.id.button_pickImage);
        buttonPickImage.setOnClickListener(this);

        imagePhoto = (ImageView) findViewById(R.id.image_photo);
        imagePhoto.setOnClickListener(this);


        View item_name = setItemContent(R.id.item_name, getString(R.string.edit_personal_name_title), PreferenceUtil.getNickname(this));
        item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enum_update_item = ENUM_UPDATE_ITEM.NAME;
                showEditNameAlert(v);

            }
        });

        View item_mima = setItemContent(R.id.item_password, getString(R.string.login_password), "*************");
        item_mima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enum_update_item = ENUM_UPDATE_ITEM.PASSWORD;
                ToChangeCertification(v);

            }
        });
        String emailDecrypt = null;
        try {
            emailDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getEmail(this));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String text_email = FormatUtil.getEncodedEmail(emailDecrypt);
        View item_email = setItemContent(R.id.item_email, getString(R.string.edit_personal_email_title), text_email);
        item_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enum_update_item = ENUM_UPDATE_ITEM.EMAIL;
                ToChangeCertification(v);
            }
        });

        String phoneDecrypt = null;
        try {
            phoneDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getPhoneNumber(this));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String textPhone = FormatUtil.getEncodedCellPhoneNumber(phoneDecrypt);
        View item_phone = setItemContent(R.id.item_phone, getString(R.string.edit_personal_phone_title), textPhone);
        item_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enum_update_item = ENUM_UPDATE_ITEM.PHONE;
                ToChangeCertification(v);
            }
        });

        checkPickImage();

        if(getIntent().hasExtra(EXTRA_CHANGE_ITEM) && getIntent().hasExtra(EXTRA_CHANGE_DATA)) {
            ENUM_UPDATE_ITEM update_item = ENUM_UPDATE_ITEM.valueOf(getIntent().getStringExtra(EXTRA_CHANGE_ITEM));
            updateString = getIntent().getStringExtra(EXTRA_CHANGE_DATA);
            showUpdateStatus(update_item, true);
        }
        else if(getIntent().hasExtra(EXTRA_CHANGE_ITEM)) {
            //password
            ENUM_UPDATE_ITEM update_item = ENUM_UPDATE_ITEM.valueOf(getIntent().getStringExtra(EXTRA_CHANGE_ITEM));
            showUpdateStatus(update_item, true);
        }

    }

    private void ToChangeCertification(View view)
    {
        Intent intent = new Intent(view.getContext(), UserInfoChangeCertificationActivity.class);
        intent.putExtra(EXTRA_CHANGE_ITEM, enum_update_item.toString());
        startActivity(intent);
    }

    private void checkPickImage()
    {
        if(imageLoader == null){
            imageLoader = new ImageLoader(this, getResources().getDimensionPixelSize(R.dimen.photo_size));
        }
        // 設定頭像
        imageLoader.loadImage(PreferenceUtil.getMemNO(this), imagePhoto);
    }

    private View setItemContent(int layoutID, String title, String content)
    {

        View itemView = findViewById(layoutID);
        TextView text_title = (TextView) itemView.findViewById(android.R.id.title);
        text_title.setText(title);
        TextView text_content = (TextView) itemView.findViewById(android.R.id.content);
        text_content.setText(content);
        return itemView;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if (viewId == R.id.button_pickImage || viewId == R.id.image_photo){
            showPopupMenu(v);
        }
    }



    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                enum_update_item = ENUM_UPDATE_ITEM.IMAGE;
                switch (item.getItemId()) {
                    case R.id.item_camera:
                        // 先確認是否有讀取外部儲存空間的權限
                        String[] permissions_c = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!PermissionUtil.needGrantRuntimePermission(EditPersonalInfoActivity.this, permissions_c,
                                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
                            Intent intent_camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); // save to file and pass the file path

                            // 確認資料夾是否存在
                            String folderPath = ContactUtil.FolderPath;
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
                        return true;
                    case R.id.item_picture:
                        // 先確認是否有權限讀寫外部儲存空間
                        String[] permissions_p = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!PermissionUtil.needGrantRuntimePermission(EditPersonalInfoActivity.this, permissions_p,
                                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        }

                        return true;
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.fragment_register3_2_popup_menu);
        popupMenu.show();
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
                intent.putExtra(CropImageActivity.EXTRA_MEM_NO, PreferenceUtil.getMemNO(this));
                startActivityForResult(intent, CROP_IMAGE_REQUEST);

                // 如果是裁切圖片
            }else if(requestCode == CROP_IMAGE_REQUEST){
                if(data.hasExtra(CropImageActivity.EXTRA_IMAGE) && data != null) {
                    // 如果是檔案路徑
                    String memNO = PreferenceUtil.getMemNO(this);
                    File imgFile = new File(data.getStringExtra(CropImageActivity.EXTRA_IMAGE));
                    if (imgFile.exists()) {
                        // 從記憶體暫存清除
                        imageLoader.removeFromMemCache(memNO);
                        // 取出圖片
                        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
                        bitmap = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);
                        // 如果有圖片，上傳圖像
                        if(bitmap != null) {
                            //
                            String base64Headshot = BitmapUtil.bitmapToBase64(bitmap);
                            if (base64Headshot != null && !base64Headshot.isEmpty()) {
                                try {
                                    GeneralHttpUtil.uploadImage(base64Headshot, responseListener_result, this, TAG);
                                    this.showProgressLoading();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    // TODO
                                }
                            }

                        }
                    }
                    // 設置讀出來的圖
                    imageLoader.loadImage(memNO, imagePhoto);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                showPopupMenu(imagePhoto);
            }else{
                PermissionUtil.showNeedPermissionDialog(this, permissions, grantResults);
            }
        }
    }

    /**
     * 顯示更新結果
     * @param enum_item 設定更新項目
     * @param result_status 結果狀態
     */
    private void showUpdateStatus(ENUM_UPDATE_ITEM enum_item, boolean result_status)
    {
        String message = "";
        switch (enum_item)
        {
            case IMAGE:
                if(result_status)
                    message = getString(R.string.edit_personal_image_success_status);
                else
                    message = getString(R.string.edit_personal_image_faill_status);
                break;

            case NAME:
                if(result_status) {
                    message = getString(R.string.edit_personal_name_success_status);
                    PreferenceUtil.setNickname(this, updateString);
                    setItemContent(R.id.item_name, getString(R.string.edit_personal_name_title), PreferenceUtil.getNickname(this));
                }
                else
                    message = getString(R.string.edit_personal_name_faill_status);
                break;

            case EMAIL:
                if(result_status)
                {
                    message = getString(R.string.edit_personal_email_success_status);
                    String emailDecrypt = null;
                    try {
                        String emailEncrypt = sharedMethods.AESEncrypt(updateString);
                        PreferenceUtil.setEmail(this, emailEncrypt);
                        emailDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getEmail(this));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    setItemContent(R.id.item_email, getString(R.string.edit_personal_email_title), FormatUtil.getEncodedEmail(emailDecrypt));
                }
                else
                    message = getString(R.string.edit_personal_email_faill_status);
                break;

            case PASSWORD:
                if(result_status) {
                    message = getString(R.string.edit_personal_password_success_status);
                }
                else
                    message = getString(R.string.edit_personal_password_faill_status);
                break;

            case PHONE:
                if(result_status) {
                    message = getString(R.string.edit_personal_phone_success_status);
                    String phoneDecrypt = null;
                    try {
                        String phoneEncrypt = sharedMethods.AESEncrypt(updateString);
                        PreferenceUtil.setPhoneNumber(this, phoneEncrypt);
                        phoneDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getPhoneNumber(this));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    setItemContent(R.id.item_phone, getString(R.string.edit_personal_phone_title), FormatUtil.getEncodedCellPhoneNumber(phoneDecrypt));
                }
                else
                    message = getString(R.string.edit_personal_phone_faill_status);
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 叫出編輯稱呼的視窗
     * @param view
     */

    private void showEditNameAlert(final View view)
    {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        final View alert_view = inflater.inflate(R.layout.edit_personal_alert, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

//        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//       // builder.setTitle(getString(R.string.edit_personal_setting_name_title));
//      //  builder.setMessage(getString(R.string.edit_personal_message));
//        builder.setView(alert_view);

        //alert.xml上的元件 要用屬於元件的view.才可以不然會FC也就是要加alert_view.findViewById
        TextView text_title = (TextView) alert_view.findViewById(android.R.id.title);
        text_title.setText(getString(R.string.edit_personal_setting_name_title));
        final EditText input_name = (EditText)alert_view.findViewById(android.R.id.text1);
        input_name.setText(PreferenceUtil.getNickname(this));
        Button button_save =(Button) alert_view.findViewById(android.R.id.button1);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_name.getText().toString().equals(""))
                {
                    showAlertDialog(getString(R.string.name_can_not_null), R.string.button_edit, R.string.button_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                    dialog.dismiss();
                                }
                            }, false);
                }
                else {
//                input_name.getText();
                    try {

                        updateString = input_name.getText().toString();
                        GeneralHttpUtil.uploadNickname(updateString, responseListener_result, activity, TAG);
                        ((ActivityBase) v.getContext()).showProgressLoading();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // TODO
                    }
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog.setView(alert_view);
        alertDialog.show();//把dialog秀出來
    }

    /***** ResponseListener ******/
    /**
     *  共用的ResponseListener
     **/
    private ResponseListener responseListener_result = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode不是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS) || !GlobalConst.UseOfficialServer)
            {
                showUpdateStatus(enum_update_item, true);
            }else {
                // 如果不是共同error
                if(!handleCommonError(result, EditPersonalInfoActivity.this)){
                    // TODO 其他不成功的判斷與處理
                    //showAlertDialog(result.getReturnMessage());
                    showUpdateStatus(enum_update_item, false);

                }
            }
        }
    };

}
