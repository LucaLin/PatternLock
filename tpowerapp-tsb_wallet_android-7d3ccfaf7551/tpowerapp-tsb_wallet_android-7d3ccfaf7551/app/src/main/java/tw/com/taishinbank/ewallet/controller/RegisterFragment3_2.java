package tw.com.taishinbank.ewallet.controller;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import org.json.JSONException;

import java.io.File;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

/**
 * 註冊頁--設定頭像與名稱
 */
public class RegisterFragment3_2 extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment3_1";

    private EditText editNickname;
    private Button buttonOk;
    private ImageView imagePhoto;
    private FrameLayout buttonPickImage;
    private Bitmap bitmap = null;
    private Uri photoUri = null;

    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = RegisterFragment3_1.PAGE_INDEX + 1;
    private static final int PICK_IMAGE_REQUEST = 9999;
    private static final int CROP_IMAGE_REQUEST = 8888;
    private static final int CAMERA_IMAGE_REQUEST = 7777;

    public RegisterFragment3_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register3_2, container, false);
        buttonOk = (Button) view.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(this);

        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_title3_2, R.string.register_subtitle3_2);

        editNickname = (EditText) view.findViewById(R.id.edit_nickname);
        editNickname.addTextChangedListener(new BasicEditTextWatcher(editNickname, null) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                checkNextButtonEnable();
            }
        });

        buttonPickImage = (FrameLayout) view.findViewById(R.id.button_pickImage);
        buttonPickImage.setOnClickListener(this);

        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);
        imagePhoto.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNextButtonEnable();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    /**
     * 檢查是否enable按鈕
     */
    private void checkNextButtonEnable(){
        if(editNickname.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            buttonOk.setEnabled(true);
        }else{
            buttonOk.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view){
        int viewId = view.getId();
        // 按下確認的處理
        if(viewId == R.id.button_ok){
            // 如果沒有網路連線，顯示提示對話框
            if(!NetworkUtil.isConnected(getActivity())){
                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return ;
            }
            // 呼叫web service設定稱呼
            try {
                GeneralHttpUtil.setMemberNickname(((RegisterActivity) getActivity()).memNo,
                        editNickname.getText().toString(), responseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }

        // TODO 開啟系統相機 or 開相簿選照片
        }else if (viewId == R.id.button_pickImage || viewId == R.id.image_photo){

//            // 先確認是否有讀取外部儲存空間的權限
//            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if (PermissionUtil.needGrantRuntimePermission(this, permissions,
//                    PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
//                return ;
//            }

            showPopupMenu(view);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnButtonNextClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // 呼叫api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            ((ActivityBase)getActivity()).dismissProgressLoading();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功註冊完成後，將memNO與nickname存到preference
                PreferenceUtil.setMemNO(getActivity(), ((RegisterActivity) getActivity()).memNo);
                PreferenceUtil.setNickname(getActivity(), editNickname.getText().toString());
                PreferenceUtil.setWalletToken(getActivity(), result.getTokenID());

                // 如果有圖片，就呼叫api上傳圖片
                if(bitmap != null) {
                    String base64Headshot = BitmapUtil.bitmapToBase64(bitmap);
                    if (base64Headshot != null && !base64Headshot.isEmpty()) {
                        try {
                            GeneralHttpUtil.uploadImage(base64Headshot, responseListener_SetHeadshot, getActivity(), TAG);
                            ((ActivityBase) getActivity()).showProgressLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // TODO
                        }
                    }
                }else{
                    // 否則直接跳下一步
                    mListener.onButtonNextClicked(RegisterFragment4.PAGE_INDEX);
                }
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private ResponseListener responseListener_SetHeadshot = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 不論上傳頭像成功或失敗都往下一步走
            mListener.onButtonNextClicked(RegisterFragment4.PAGE_INDEX);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                Intent intent = new Intent(getActivity(), CropImageActivity.class);
                // 設定圖片來源
                intent.setData(uri);
                // 設定切圖存檔的memNO
                intent.putExtra(CropImageActivity.EXTRA_MEM_NO, ((RegisterActivity)getActivity()).memNo);
                startActivityForResult(intent, CROP_IMAGE_REQUEST);

            // 如果是裁切圖片
            }else if(requestCode == CROP_IMAGE_REQUEST){
                if(data.hasExtra(CropImageActivity.EXTRA_IMAGE) && data != null) {
                    // 如果是檔案路徑
                    File imgFile = new File(data.getStringExtra(CropImageActivity.EXTRA_IMAGE));
                    if (imgFile.exists()) {
                        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
                        bitmap = BitmapUtil.decodeSampledBitmap(imgFile.getAbsolutePath(), photoSize, photoSize);
                    }
                    // 設置讀出來的圖
                    imagePhoto.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // 顯示選單
                showPopupMenu(buttonPickImage);
            }else{
                PermissionUtil.showNeedPermissionDialog(getActivity(), permissions, grantResults);
            }
        }
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_camera:
                        // 先確認是否有讀取外部儲存空間的權限
                        String[] permissions_camera = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!PermissionUtil.needGrantRuntimePermission(RegisterFragment3_2.this, permissions_camera,
                                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE)) {
                            Intent intent_camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); // save to file and pass the file path

                            // 確認資料夾是否存在
                            String folderPath = ContactUtil.FolderPath;
                            ContactUtil.checkFolderExists();
                            // 組合並指定輸出路徑
                            String filePath = folderPath + File.separator + ((RegisterActivity) getActivity()).memNo + ".png";
                            photoUri = Uri.fromFile(new File(filePath));
                            intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent_camera, CAMERA_IMAGE_REQUEST);
                        }

                        return true;
                    case R.id.item_picture:
                        // 先確認是否有讀取外部儲存空間的權限
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (!PermissionUtil.needGrantRuntimePermission(RegisterFragment3_2.this, permissions,
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
}
