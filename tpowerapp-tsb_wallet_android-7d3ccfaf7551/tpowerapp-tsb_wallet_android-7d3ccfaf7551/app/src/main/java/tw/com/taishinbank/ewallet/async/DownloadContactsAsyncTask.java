package tw.com.taishinbank.ewallet.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.dbhelper.DatabaseHelper;
import tw.com.taishinbank.ewallet.imagehelper.DiskCache;
import tw.com.taishinbank.ewallet.interfaces.OnTaskStartFinishListener;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.RemoteContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;


public class DownloadContactsAsyncTask extends AsyncTask<Void, Void, Void>{

    private static final String TAG = "DownloadContacts";
    private Activity context;
    private OnDownloadStartFinishListener listener;
    private ArrayList<RemoteContact> remoteContacts;
    private ResponseResult responseResult = null;

    public DownloadContactsAsyncTask(Activity context, OnDownloadStartFinishListener listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "download started");
        if(listener != null){
            listener.onTaskStarted();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        // 呼叫webservice撈回聯絡人資料
        try {
            GeneralHttpUtil.getFriendsList(responseListener, context, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
            onFinished();
        }

        return null;
    }


    public interface OnDownloadStartFinishListener extends OnTaskStartFinishListener{
        void onDownloadFinished(ResponseResult errorResult);
    }

    // 呼叫取得朋友列表api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，更新畫面列表
                remoteContacts = GeneralResponseBodyUtil.getRemoteContactList(result.getBody());
            }else{
                responseResult = result;
            }
            // 不論成功或失敗，處理完，停止waiting
            updateLocalContact();
            onFinished();
        }
    };

    private void updateLocalContact(){
        // 轉成local資料列表
        ArrayList<LocalContact> contacts = null;
        if(remoteContacts != null) {
            contacts = new ArrayList<>();
            for (int i = 0; i < remoteContacts.size(); i++) {
                RemoteContact remoteContact = remoteContacts.get(i);
                // 如果有跟手機號碼相同的圖檔，就將圖檔重新命名成會員序號
                if(DiskCache.isExists(remoteContact.getMemPhone())){
                    DiskCache.rename(remoteContact.getMemPhone(), remoteContact.getMemNO());
                }
                LocalContact localContact = new LocalContact(remoteContact);
                contacts.add(localContact);
            }
        }

        // TODO 應該要改成用update
        // 存入DB
        if(contacts != null){
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            int deletedCount = dbHelper.deleteAll();
            Log.d(TAG, deletedCount + " deleted.");
            boolean isSuccess = dbHelper.insertAll(contacts);
            if(isSuccess) {
                Log.d(TAG, contacts.size() + " inserted.");
            }
        }
    }

    private void onFinished(){
        Log.d(TAG, "download finished2");
        if(listener != null){
            listener.onTaskFinished();
            listener.onDownloadFinished(responseResult);
        }
    }
}

