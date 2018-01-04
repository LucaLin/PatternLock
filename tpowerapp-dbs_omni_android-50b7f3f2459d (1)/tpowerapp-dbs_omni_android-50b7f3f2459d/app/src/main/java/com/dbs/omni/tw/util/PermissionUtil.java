package com.dbs.omni.tw.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;

import java.util.ArrayList;


public class PermissionUtil {

    public static final int PERMISSION_REQUEST_CODE_READ_CONTACTS = 1;
    public static final int PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE = 2;
    public static final int PERMISSION_REQUEST_CODE_SCAN = 3;
    public static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 4;


    //For Android ~5.x alert button event
    public interface OnPermissionListener
    {
        void onRequestPermissionsResultForAndroid5(int requestCode);
    }

    public interface OnPermissionForFragmentListener
    {
        void onRequestPermissionsResultForAndroid5(int requestCode);
    }

    /**
     * 確認是否有指定的權限
     */
    public static boolean needGrantRuntimePermission(final Activity activity, final String permission, final int requestCode/*,
                                              View snackbarParentView, int expanationResId*/){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = {permission};

            // TODO 確認是否要跟使用者解釋需要權限的理由
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    permission)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                Snackbar.make(snackbarParentView, expanationResId,
//                        Snackbar.LENGTH_INDEFINITE)
//                        .setAction(android.R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                ActivityCompat.requestPermissions(activity,
//                                        permissions, requestCode);
//                            }
//                        })
//                        .show();
//            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
//            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * 確認是否有指定的權限
     */
    public static boolean needGrantRuntimePermission(Activity activity, String[] permissions, final int requestCode/*,
                                              View snackbarParentView, int expanationResId*/){
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        if(Build.VERSION.SDK_INT < 23 ) {

            //TODO Android為6.0之前的版本，顯示權限使用提醒
            ArrayList<String> permissionsString = getPermissionsMessages(activity, permissions);
            //TODO 判斷權限是否有提醒過
            if(permissionsString.size() != 0) {
                String message = "";
                for (String str: permissionsString) {
                    if(!TextUtils.isEmpty(message)) {
                        message += activity.getString(R.string.dialog_permission_label_and);
                    }
                    message += str;
                }

                //TODO 顯示提醒之權限的訊息
                message = String.format(activity.getString(R.string.dialog_permission_message_for_remind), activity.getString(R.string.app_name), message);
                final Activity activity_final = activity;
                ((ActivityBase) activity).showAlertDialog(message, R.string.dialog_permission_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //TODO 回去執行接下去的動作
                        ((PermissionUtil.OnPermissionListener) activity_final).onRequestPermissionsResultForAndroid5(requestCode);
                        dialog.dismiss();
                    }
                }, false);

                return true;
            } else {
                //TODO 已提醒過，不需要顯示提醒
                return false;
            }
        } else {
            //Android為6.0以上的版本，根據官方提供的權限處理方式，顯示訊息要求使用者允許

            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permissions[i]);
                }
            }

            // 如果有權限沒有取得，要求權限
            if (permissionsToRequest.size() > 0) {
                ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * 確認是否有指定的權限(要求權限的對象是fragment)
     */
    public static boolean needGrantRuntimePermission(Fragment fragment, String[] permissions, final int requestCode/*,
                                              View snackbarParentView, int expanationResId*/){

        if(Build.VERSION.SDK_INT < 23 ) {

            //TODO Android為6.0之前的版本，顯示權限使用提醒
            ArrayList<String> permissionsString = getPermissionsMessages(fragment.getActivity(), permissions);
            //TODO 判斷權限是否有提醒過
            if(permissionsString.size() != 0) {
                String message = "";
                for (String str: permissionsString) {
                    if(!TextUtils.isEmpty(message)) {
                        message += fragment.getActivity().getString(R.string.dialog_permission_label_and);
                    }
                    message += str;
                }

                //TODO 顯示提醒之權限的訊息
                message = String.format(fragment.getActivity().getString(R.string.dialog_permission_message_for_remind), fragment.getActivity().getString(R.string.app_name), message);
                final Fragment fragment_final = fragment;
                ((ActivityBase) fragment.getActivity()).showAlertDialog(message, R.string.dialog_permission_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //TODO 回去執行接下去的動作
                        ((PermissionUtil.OnPermissionForFragmentListener) fragment_final).onRequestPermissionsResultForAndroid5(requestCode);
                        dialog.dismiss();
                    }
                }, false);

                return true;
            } else {
                //TODO 已提醒過，不需要顯示提醒
                return false;
            }
        } else {
            ArrayList<String> permissionsToRequest = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(fragment.getActivity(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permissions[i]);
                }
            }

            // 如果有權限沒有取得，要求權限
            if (permissionsToRequest.size() > 0) {
                fragment.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 判斷是否所有要求的權限都有獲得
     * @param grantResults 從Activity的onRequestPermissionsResult取得
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showNeedPermissionDialog(final Context context, String[] permissions, int[] grantResults){
        if(grantResults.length < 1){
            return;
        }

        // 記錄目前尚未取得的權限
        ArrayList<String> neededPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                String permissionDisplayName = getPermissionDisplayName(context, permissions[i]);
                if(!neededPermissions.contains(permissionDisplayName)) {
                    neededPermissions.add(permissionDisplayName);
                }
            }
        }

        if(neededPermissions.size() < 1){
            return;
        }

        // 組成缺少的權限名稱
        String permissionNames = neededPermissions.get(0);
        if(neededPermissions.size() > 1){
            String conjunctionFinal = context.getString(R.string.dialog_permission_label_conjunction_final);
            String conjunction = context.getString(R.string.dialog_permission_label_conjunction);
            for(int i = 1; i < neededPermissions.size() - 1; i++){
                permissionNames = permissionNames.concat(conjunction).concat( neededPermissions.get(neededPermissions.size() - 1) );
            }
            permissionNames = permissionNames.concat(conjunctionFinal).concat( neededPermissions.get(neededPermissions.size() - 1) );
        }

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(String.format(context.getString(R.string.dialog_permission_msg), permissionNames))
                .setCancelable(true);

        // On devices prior to Honeycomb, the button order (left to right) was POSITIVE - NEUTRAL - NEGATIVE.
        // On newer devices using the Holo theme, the button order (left to right) is now NEGATIVE - NEUTRAL - POSITIVE.
        builder.setPositiveButton(R.string.dialog_permission_button_goto_setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static String getPermissionDisplayName(Context context, String permission){
        int strResourceId = 0;
        if(permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            strResourceId = R.string.dialog_permission_label_storage;

        }else if(permission.equals(Manifest.permission.CAMERA)){
            strResourceId = R.string.dialog_permission_label_camera;

        }else if(permission.equals(Manifest.permission.READ_CONTACTS)){
            strResourceId = R.string.dialog_permission_label_contacts;

        }

        if(strResourceId > 0){
           return String.format(context.getString(R.string.dialog_permission_label_parentheses),
                   context.getString(strResourceId));
        }
        return "";
    }


    /**
     *  for Android 5.x message
     *  判斷權限是否提醒過，若沒有提醒過將回傳需顯示的內容
     **/
    private static ArrayList<String> getPermissionsMessages(Activity activity, String[] permissionsItem) {
        ArrayList<String> permissionsStringItems = new ArrayList<>();

        for (int i = 0; i < permissionsItem.length; i++) {
            if (permissionsItem[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (!PreferenceUtil.getPermissionSetted(activity, PreferenceUtil.ENUM_PERMISSION_TYPE.READ_EXTERNAL_STORAGE)) {
                    permissionsStringItems.add(activity.getString(R.string.dialog_permission_label_storage_message));
                    PreferenceUtil.setPermissionSetted(activity, true,PreferenceUtil.ENUM_PERMISSION_TYPE.READ_EXTERNAL_STORAGE);
                    PreferenceUtil.setPermissionSetted(activity, true,PreferenceUtil.ENUM_PERMISSION_TYPE.WRITE_EXTERNAL_STORAGE);
                }
            } else if (permissionsItem[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (!PreferenceUtil.getPermissionSetted(activity, PreferenceUtil.ENUM_PERMISSION_TYPE.WRITE_EXTERNAL_STORAGE)) {
                    permissionsStringItems.add(activity.getString(R.string.dialog_permission_label_storage_message));
                    PreferenceUtil.setPermissionSetted(activity, true,PreferenceUtil.ENUM_PERMISSION_TYPE.READ_EXTERNAL_STORAGE);
                    PreferenceUtil.setPermissionSetted(activity, true,PreferenceUtil.ENUM_PERMISSION_TYPE.WRITE_EXTERNAL_STORAGE);
                }
            } else if (permissionsItem[i].equalsIgnoreCase(Manifest.permission.CAMERA)){
                if (!PreferenceUtil.getPermissionSetted(activity, PreferenceUtil.ENUM_PERMISSION_TYPE.CAMERA)) {
                    permissionsStringItems.add(activity.getString(R.string.dialog_permission_label_camera_message));
                    PreferenceUtil.setPermissionSetted(activity, true, PreferenceUtil.ENUM_PERMISSION_TYPE.CAMERA);
                }
            }
        }
        return permissionsStringItems;
    }

}
