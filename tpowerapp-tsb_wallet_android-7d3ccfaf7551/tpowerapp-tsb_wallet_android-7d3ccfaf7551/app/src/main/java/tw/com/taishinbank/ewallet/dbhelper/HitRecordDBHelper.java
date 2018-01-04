package tw.com.taishinbank.ewallet.dbhelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.interfaces.log.HitRecordEntry;
import tw.com.taishinbank.ewallet.model.log.HitRecord;

public class HitRecordDBHelper extends DBHelperBase{

    public HitRecordDBHelper(Context context){
        super(context);
    }

    /**
     * 插入一筆事件資料
     * @return 回傳rowId
     */
    public long insert(HitRecord.HitEvent event, HitRecord.HitType type){
        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HitRecordEntry.COLUMN_HIT_EVENT, event.getCode());
        values.put(HitRecordEntry.COLUMN_HIT_TYPE, type.getCode());
        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(HitRecordEntry.TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    /**
     * 根據指定條件取得所有聯絡人資料
     */
    public ArrayList<HitRecord> getAll(){
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

        // 照原本insert順序排
        String sortOrder =
                HitRecordEntry._ID;

        Cursor cursor = db.query(
                HitRecordEntry.TABLE_NAME,  // The table to query
                null,                          // 列出所有欄位
                null,                          // The columns for the WHERE clause
                null,                          // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                sortOrder                      // The sort order
        );
        ArrayList<HitRecord> hitRecords = new ArrayList<>();
        if(cursor != null){
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int recordId = getInt(cursor, HitRecordEntry._ID);
                    String event = getString(cursor, HitRecordEntry.COLUMN_HIT_EVENT);
                    String type = getString(cursor, HitRecordEntry.COLUMN_HIT_TYPE);
                    hitRecords.add(new HitRecord(recordId, event, type));
                }
            }
            cursor.close();
        }

        db.close();
        return hitRecords;
    }

    /**
     * 刪除指定事件資料群
     * @return 刪除的資料筆數
     */
    public int deleteList(ArrayList<HitRecord> hitRecords){
        ArrayList<String> items = new ArrayList<>();

        String selection = HitRecordEntry._ID + " IN (" + new String(new char[hitRecords.size() - 1]).replace("\0", "?,") + "?)";

        for (HitRecord data: hitRecords) {
            items.add(String.valueOf(data.getRecordId()));
        }

        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數

        // Issue SQL statement.
        int numDeleted = db.delete(HitRecordEntry.TABLE_NAME, selection, items.toArray(new String[0]));
        db.close();

        return numDeleted;
    }

    /**
     * 刪除某比資料
     * @return 刪除的資料筆數
     */
    public int delete(HitRecord hitRecord){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數
        String selection = HitRecordEntry._ID + "=" + hitRecord.getRecordId();
        // Issue SQL statement.
        int numDeleted = db.delete(HitRecordEntry.TABLE_NAME, selection, null);
        db.close();
        return numDeleted;
    }

    /**
     * 刪除全部事件資料
     * @return 刪除的資料筆數
     */
    public int deleteAll(){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數
        String selection = "1";
        // Issue SQL statement.
        int numDeleted = db.delete(HitRecordEntry.TABLE_NAME, selection, null);
        db.close();
        return numDeleted;
    }

}
