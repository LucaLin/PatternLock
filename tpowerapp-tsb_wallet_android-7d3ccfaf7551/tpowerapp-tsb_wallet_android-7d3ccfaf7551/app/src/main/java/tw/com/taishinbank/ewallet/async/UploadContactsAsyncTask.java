package tw.com.taishinbank.ewallet.async;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import tw.com.taishinbank.ewallet.BuildConfig;
import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.DiskCache;
import tw.com.taishinbank.ewallet.interfaces.ContactDataQuery;
import tw.com.taishinbank.ewallet.interfaces.ContactListQuery;
import tw.com.taishinbank.ewallet.interfaces.OnTaskStartFinishListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;


public class UploadContactsAsyncTask extends AsyncTask<Void, Void, Void>{

    private static final String TAG = "UploadContacts";
    private Activity context;
    private OnUploadStartFinishListener listener;

    private int imagePhotoSize;
    private int imagePhotoCropCircleRadius;

    public UploadContactsAsyncTask(Activity context, OnUploadStartFinishListener listener){
        this.context = context;
        this.listener = listener;

        imagePhotoSize = context.getResources().getDimensionPixelSize(R.dimen.photo_size);
        imagePhotoCropCircleRadius = context.getResources().getDimensionPixelSize(R.dimen.photo_crop_circle_radius);
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "upload started");
        if(listener != null){
            listener.onTaskStarted();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // 讀聯絡人
        ArrayList<LocalContact> localContacts = getContacts();

        // 呼叫webservice更新通訊錄
        try {
            GeneralHttpUtil.addFriends(localContacts, responseListener, context, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
            onFinished();
        }
        return null;
    }

    /**
     * 取得手機通訊錄資料
     */
    private ArrayList<LocalContact> getContacts(){
        ContentResolver contentResolver = context.getContentResolver();
        // 取得所有有電話的聯絡人列表
        Cursor cursor = contentResolver.query(ContactListQuery.CONTENT_URI,
                ContactListQuery.PROJECTION,
                ContactListQuery.SELECTION,
                null,
                ContactListQuery.SORT_ORDER);

        // 如果列表不為空
        if(cursor != null){
            ArrayList<LocalContact> localContacts = new ArrayList<>();
            // 如果cursor不為空
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // 取得contact id
                    long contactId = cursor.getLong(ContactListQuery.ID);

                    // Creates a contact lookup Uri from contact ID and lookup_key
                    final Uri contactUri = ContactsContract.Contacts.getLookupUri(
                            contactId,
                            cursor.getString(ContactListQuery.LOOKUP_KEY));
                    Bitmap bitmap = loadContactPhoto(contactUri, imagePhotoSize * ContactUtil.PhotoSaveSizeScaleRate);
                    if(bitmap != null) {
                        int size = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getWidth() : bitmap.getHeight();
                        bitmap = BitmapUtil.cropCircleWithColor(bitmap, size, imagePhotoSize * ContactUtil.PhotoSaveSizeScaleRate, imagePhotoCropCircleRadius * ContactUtil.PhotoSaveSizeScaleRate,
                                context.getResources().getColor(R.color.crop_image_background));
                    }
                    // TODO 確認一下是不是取givenname跟familyname就夠
                    String givenName = "";
                    String familyName = null;
                    // 取得姓、名
                    Cursor cursorName = contentResolver.query(ContactDataQuery.CONTENT_URI,
                            ContactDataQuery.PROJECTION_NAME,
                            ContactDataQuery.SELECTION_NAME,
                            new String[]{String.valueOf(contactId)},
                            null);
                    if (cursorName != null) {
                        if (cursorName.moveToFirst()) {
                            givenName = cursorName.getString(ContactDataQuery.GIVEN_NAME);
                            familyName = cursorName.getString(ContactDataQuery.FAMILY_NAME);
                        }
                        cursorName.close();
                    }

                    // 取得所有電話號碼
                    Cursor cursorPhone = contentResolver.query(ContactDataQuery.CONTENT_URI,
                            ContactDataQuery.PROJECTION_PHONE,
                            ContactDataQuery.SELECTION_PHONE,
                            new String[]{String.valueOf(contactId)},
                            null);
                    if (cursorPhone != null) {
                        if (cursorPhone.getCount() > 0) {
                            int i = 1;
                            LocalContact contact = new LocalContact();
                            contact.setGivenName(givenName);
                            contact.setFamilyName(familyName);
                            while (cursorPhone.moveToNext()) {
                                // 取得電話號碼
                                String phoneNumber = cursorPhone.getString(ContactDataQuery.NUMBER);
                                // 去掉空格
                                phoneNumber = phoneNumber.replace(" ", "");
                                contact.setPhoneNumber(phoneNumber);
                                // 如果有頭像，暫先用電話號碼檔名存檔（等下載聯絡人時再用會員序號重新命名）
                                if(bitmap != null){
                                    DiskCache.put(phoneNumber, bitmap);
                                }
                                // 加到列表
                                localContacts.add(contact);
                                i++;
                                // 產生新的聯絡人物件，下個迴圈用
                                contact = new LocalContact();
                                contact.setGivenName(givenName + "_" + i);
                                contact.setFamilyName(familyName);
                            } // end while
                        } // end if cursorPhone.getCount() > 0
                        cursorPhone.close();
                    } // end if cursorPhone != null
                } // end while
            } // end if cursor.getCount() > 0
            cursor.close();
            return localContacts;
        }
        return null;
    }

    /**
     * Decodes and returns the contact's thumbnail image.
     * @param contactUri The Uri of the contact containing the image.
     * @param imageSize The desired target width and height of the output image in pixels.
     * @return If a thumbnail image exists for the contact, a Bitmap image, otherwise null.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap loadContactPhoto(Uri contactUri, int imageSize) {

        // Ensures the Fragment is still added to an activity. As this method is called in a
        // background thread, there's the possibility the Fragment is no longer attached and
        // added to an activity. If so, no need to spend resources loading the contact photo.
        if (context == null) {
            return null;
        }

        // Instantiates a ContentResolver for retrieving the Uri of the image
        final ContentResolver contentResolver = context.getContentResolver();

        // Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
        // ContentResolver can return an AssetFileDescriptor for the file.
        AssetFileDescriptor afd = null;

        // On platforms running Android 4.0 (API version 14) and later, a high resolution image
        // is available from Photo.DISPLAY_PHOTO.
        try {
            // Constructs the content Uri for the image
            Uri displayImageUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);

            // Retrieves an AssetFileDescriptor from the Contacts Provider, using the
            // constructed Uri
            afd = contentResolver.openAssetFileDescriptor(displayImageUri, "r");
            // If the file exists
            if (afd != null) {
                // Reads and decodes the file to a Bitmap and scales it to the desired size
                return BitmapUtil.decodeSampledBitmapFromDescriptor(
                        afd.getFileDescriptor(), imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            // Catches file not found exceptions
            if (BuildConfig.DEBUG) {
                // Log debug message, this is not an error message as this exception is thrown
                // when a contact is legitimately missing a contact photo (which will be quite
                // frequently in a long contacts list).
                Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                        + ": " + e.toString());
            }
        } finally {
            // Once the decode is complete, this closes the file. You must do this each time
            // you access an AssetFileDescriptor; otherwise, every image load you do will open
            // a new descriptor.
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                    // Closing a file descriptor might cause an IOException if the file is
                    // already closed. Nothing extra is needed to handle this.
                }
            }
        }

        // If the platform version is less than Android 4.0 (API Level 14), use the only available
        // image URI, which points to a normal-sized image.
        try {
            // Constructs the image Uri from the contact Uri and the directory twig from the
            // Contacts.Photo table
            Uri imageUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

            // Retrieves an AssetFileDescriptor from the Contacts Provider, using the constructed
            // Uri
            afd = context.getContentResolver().openAssetFileDescriptor(imageUri, "r");

            // If the file exists
            if (afd != null) {
                // Reads the image from the file, decodes it, and scales it to the available screen
                // area
                return BitmapUtil.decodeSampledBitmapFromDescriptor(
                        afd.getFileDescriptor(), imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            // Catches file not found exceptions
            if (BuildConfig.DEBUG) {
                // Log debug message, this is not an error message as this exception is thrown
                // when a contact is legitimately missing a contact photo (which will be quite
                // frequently in a long contacts list).
                Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                        + ": " + e.toString());
            }
        } finally {
            // Once the decode is complete, this closes the file. You must do this each time you
            // access an AssetFileDescriptor; otherwise, every image load you do will open a new
            // descriptor.
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                    // Closing a file descriptor might cause an IOException if the file is
                    // already closed. Ignore this.
                }
            }
        }

        // If none of the case selectors match, returns null.
        return null;
    }

    // 呼叫加入好友api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，更新畫面列表
            }else{
                // TODO 不成功的判斷與處理
            }
            onFinished();
        }
    };

    private void onFinished(){
        Log.d(TAG, "upload finished");
        if(listener != null){
            listener.onTaskFinished();
            listener.onUploadFinished();
        }
    }

    public interface OnUploadStartFinishListener extends OnTaskStartFinishListener{
        void onUploadFinished();
    }
}

