package tw.com.taishinbank.ewallet.dbhelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.interfaces.LocalContactEntry;
import tw.com.taishinbank.ewallet.model.LocalContact;

public class DatabaseHelper extends DBHelperBase {

    public DatabaseHelper(Context context){
        super(context);
    }

    /**
     * 插入一筆聯絡人資料
     * @return 回傳rowId
     */
    public long insert(LocalContact localContact){
        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = toContentValues(localContact);

        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(LocalContactEntry.TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    /**
     * 插入所有資料
     * @param localContacts LocalContact的ArrayList
     * @return true, 如果所有資料插入成功; 否則回傳false
     */
    public boolean insertAll(ArrayList<LocalContact> localContacts){
        if(localContacts == null || localContacts.size() <= 0){
            return false;
        }

        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        boolean isSuccess = false;
        db.beginTransaction();
        try {
            for(int i = 0; i < localContacts.size(); i++){
                ContentValues values = toContentValues(localContacts.get(i));
                db.insert(LocalContactEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch(Exception e){
            // TODO 要做什麼處理？
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }

    /**
     * 刪除全部聯絡人資料
     * @return 刪除的資料筆數
     */
    public int deleteAll(){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數
        String selection = "1";
        // Issue SQL statement.
        int numDeleted = db.delete(LocalContactEntry.TABLE_NAME, selection, null);
        db.close();
        return numDeleted;
    }

    /**
     * 取得所有聯絡人資料
     */
    public ArrayList<LocalContact> getAll(){
        return getAll(null, null);
    }

    /**
     * 取得所有除聯絡人資料
     */
    public ArrayList<LocalContact> getAllSV(){
        String selection = LocalContactEntry.COLUMN_IS_SV_ACCOUNT + "=1" ;
        return getAll(selection, null);
    }

    /**
     * 根據指定條件取得所有聯絡人資料
     */
    private ArrayList<LocalContact> getAll(String selection, String[] selectionArgs){
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

        // 照原本insert順序排
        String sortOrder =
                LocalContactEntry._ID;

        Cursor cursor = db.query(
                LocalContactEntry.TABLE_NAME,  // The table to query
                null,                          // 列出所有欄位
                selection,                     // The columns for the WHERE clause
                selectionArgs,                 // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                sortOrder                      // The sort order
        );
        ArrayList<LocalContact> localContacts = new ArrayList<>();
        if(cursor != null){
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    localContacts.add(toLocalContact(cursor));
                }
            }
            cursor.close();
        }

        db.close();
        return localContacts;
    }

    /**
     * 根據keyword對電話號碼做搜尋所有聯絡人
     */
    public ArrayList<LocalContact> searchByPhone(String keyword){
        // 匹配姓、名與電話號碼
        String selection = LocalContactEntry.COLUMN_PHONE_NUMBER + " = ?";
        String[] selectionArgs = {keyword};
        return search(selection, selectionArgs);
    }

    /**
     * 根據MemNo對電話號碼做搜尋所有聯絡人
     */
    public ArrayList<LocalContact> searchByMemNo(String memNo){
        // 匹配姓、名與電話號碼
        String selection = LocalContactEntry.COLUMN_MEM_NO + " = ?";
        String[] selectionArgs = {memNo};
        return search(selection, selectionArgs);
    }

    /**
     * 根據keyword與selection做搜尋
     */
    private ArrayList<LocalContact> search(String selection, String[] selectionArgs){
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

        // 照原本insert順序排
        String sortOrder =
                LocalContactEntry._ID;

        Cursor cursor = db.query(
                LocalContactEntry.TABLE_NAME,  // The table to query
                null,                          // 列出所有欄位
                selection,                     // The columns for the WHERE clause
                selectionArgs,                 // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                sortOrder                      // The sort order
        );
        ArrayList<LocalContact> localContacts = new ArrayList<>();
        if(cursor != null){
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    localContacts.add(toLocalContact(cursor));
                }
            }
            cursor.close();
        }

        db.close();
        return localContacts;
    }

    /**
     * 回傳資料庫是否有聯絡人資料
     */
    public boolean hasLocalContacts(){
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

        boolean hasContacts = false;
        Cursor cursor = db.query(
                LocalContactEntry.TABLE_NAME,  // The table to query
                null,                          // 列出所有欄位
                null,                     // The columns for the WHERE clause
                null,                 // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                null                      // The sort order
        );
        if(cursor != null){
            if(cursor.getCount() > 0) {
                hasContacts =  true;
            }
            cursor.close();
        }

        db.close();
        return hasContacts;
    }

    /**
     * 將LocalContact物件的資料轉成用來insert的ContentValues
     */
    private ContentValues toContentValues(LocalContact localContact){
        ContentValues values = new ContentValues();
        values.put(LocalContactEntry.COLUMN_MEM_NO, localContact.getMemNO());
        values.put(LocalContactEntry.COLUMN_STATUS, localContact.getStatus());
        values.put(LocalContactEntry.COLUMN_EMAIL, localContact.getEmail());
        values.put(LocalContactEntry.COLUMN_FAMILY_NAME, localContact.getFamilyName());
        values.put(LocalContactEntry.COLUMN_GIVEN_NAME, localContact.getGivenName());
        values.put(LocalContactEntry.COLUMN_NICK_NAME, localContact.getNickname());
        values.put(LocalContactEntry.COLUMN_PHONE_NUMBER, localContact.getPhoneNumber());
        values.put(LocalContactEntry.COLUMN_IS_WALLET_ACCOUNT, localContact.isWalletAccount() ? 1 : 0);
        values.put(LocalContactEntry.COLUMN_IS_SV_ACCOUNT, localContact.isSVAccount() ? 1 : 0);
        values.put(LocalContactEntry.COLUMN_IS_NEW_ADDED, localContact.isNewAdded() ? 1 : 0);
        values.put(LocalContactEntry.COLUMN_PHOTO_FILENAME, localContact.getPhotoFileName());
        values.put(LocalContactEntry.COLUMN_NUM_OF_TRANSACTIONS, localContact.getNumOfTransactions());
        return values;
    }

    /**
     * 從cursor取出資料，產生LocalContact物件
     */
    private LocalContact toLocalContact(Cursor cursor){
        LocalContact localContact = new LocalContact();
        String valueString = getString(cursor, LocalContactEntry.COLUMN_FAMILY_NAME);
        localContact.setFamilyName(valueString);

        valueString = getString(cursor, LocalContactEntry.COLUMN_MEM_NO);
        localContact.setMemNO(valueString);

        valueString = getString(cursor, LocalContactEntry.COLUMN_STATUS);
        localContact.setStatus(valueString);

        valueString = getString(cursor, LocalContactEntry.COLUMN_EMAIL);
        localContact.setEmail(valueString);

        valueString = getString(cursor, LocalContactEntry.COLUMN_GIVEN_NAME);
        localContact.setGivenName(valueString);

        valueString = getString(cursor, LocalContactEntry.COLUMN_NICK_NAME);
        localContact.setNickname(valueString);

        valueString = getString(cursor, LocalContactEntry.COLUMN_PHONE_NUMBER);
        localContact.setPhoneNumber(valueString);

        boolean valueBool = getBoolean(cursor, LocalContactEntry.COLUMN_IS_WALLET_ACCOUNT);
        localContact.setIsWalletAccount(valueBool);

        valueBool = getBoolean(cursor, LocalContactEntry.COLUMN_IS_SV_ACCOUNT);
        localContact.setIsSVAccount(valueBool);

        valueBool = getBoolean(cursor, LocalContactEntry.COLUMN_IS_NEW_ADDED);
        localContact.setIsNewAdded(valueBool);

        valueString = getString(cursor, LocalContactEntry.COLUMN_PHOTO_FILENAME);
        localContact.setPhotoFileName(valueString);

        int valueInt = getInt(cursor, LocalContactEntry.COLUMN_NUM_OF_TRANSACTIONS);
        localContact.setNumOfTransactions(valueInt);

        return localContact;
    }

}
