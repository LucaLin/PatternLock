package tw.com.taishinbank.ewallet.dbhelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.interfaces.log.SpecialEventEntry;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;

public class SpecialEventsDBHelper extends DBHelperBase{

    public SpecialEventsDBHelper(Context context){
        super(context);
    }

    /**
     * 插入一筆事件資料
     * @return 回傳rowId
     */
    public long insert(SpecialEvent specialEvent){
        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SpecialEventEntry.COLUMN_TYPE, specialEvent.getType());
        values.put(SpecialEventEntry.COLUMN_NOTE, specialEvent.getNote());
        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(SpecialEventEntry.TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    /**
     * 根據指定條件取得所有聯絡人資料
     */
    public ArrayList<SpecialEvent> getAll(){
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

        // 照原本insert順序排
        String sortOrder =
                SpecialEventEntry._ID;

        Cursor cursor = db.query(
                SpecialEventEntry.TABLE_NAME,  // The table to query
                null,                          // 列出所有欄位
                null,                          // The columns for the WHERE clause
                null,                          // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                sortOrder                      // The sort order
        );
        ArrayList<SpecialEvent> specialEvents = new ArrayList<>();
        if(cursor != null){
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int recordId = getInt(cursor, SpecialEventEntry._ID);
                    String type = getString(cursor, SpecialEventEntry.COLUMN_TYPE);
                    String note = getString(cursor, SpecialEventEntry.COLUMN_NOTE);
                    specialEvents.add(new SpecialEvent(recordId, type, note));
                }
            }
            cursor.close();
        }

        db.close();
        return specialEvents;
    }

    /**
     * 刪除指定事件資料群
     * @return 刪除的資料筆數
     */
    public int deleteList(ArrayList<SpecialEvent> specialEvents){
        ArrayList<String> items = new ArrayList<>();

        String selection = SpecialEventEntry._ID + " IN (" + new String(new char[specialEvents.size() - 1]).replace("\0", "?,") + "?)";

        for (SpecialEvent data: specialEvents) {
            items.add(String.valueOf(data.getRecordId()));
        }

        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數

        // Issue SQL statement.
        int numDeleted = db.delete(SpecialEventEntry.TABLE_NAME, selection, items.toArray(new String[0]));
        db.close();

        return numDeleted;
    }

    /**
     * 刪除某比資料
     * @return 刪除的資料筆數
     */
    public int delete(SpecialEvent specialEvent){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數
        String selection = SpecialEventEntry._ID + "=" + specialEvent.getRecordId();
        // Issue SQL statement.
        int numDeleted = db.delete(SpecialEventEntry.TABLE_NAME, selection, null);
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
        int numDeleted = db.delete(SpecialEventEntry.TABLE_NAME, selection, null);
        db.close();
        return numDeleted;
    }

}
