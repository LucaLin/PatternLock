package tw.com.taishinbank.ewallet.dbhelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.interfaces.CreditCardEntry;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;

public class CreditCardDBHelper extends DBHelperBase{

    public CreditCardDBHelper(Context context){
        super(context);
    }

    /**
     * 插入一筆資料
     * @return 回傳rowId
     */
    public long insert(CreditCardData card){
        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = toCardValues(card);

        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(CreditCardEntry.TABLE_NAME, null, values);
        db.close();
        return rowId;
    }

    /**
     * 插入所有資料
     * @param CardList LocalContact的ArrayList
     * @return true, 如果所有資料插入成功; 否則回傳false
     */
    public boolean insertAll(ArrayList<CreditCardData> CardList){
        if(CardList == null || CardList.size() <= 0){
            return false;
        }

        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        boolean isSuccess = false;
        db.beginTransaction();
        try {
            for(int i = 0; i < CardList.size(); i++){
                ContentValues values = toCardValues(CardList.get(i));
                db.insert(CreditCardEntry.TABLE_NAME, null, values);
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
     * 刪除某比資料
     * @return 刪除的資料筆數
     */
    public int delete(CreditCardData card){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數
        String selection = CreditCardEntry._ID + "=" + card.getCardID();
        // Issue SQL statement.
        int numDeleted = db.delete(CreditCardEntry.TABLE_NAME, selection, null);
        db.close();
        return numDeleted;
    }

    /**
     * 刪除全部資料
     * @return 刪除的資料筆數
     */
    public int deleteAll(){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        // Define 'where' part of query, 傳入"1"會回傳所有被刪除的資料數
        String selection = "1";
        // Issue SQL statement.
        int numDeleted = db.delete(CreditCardEntry.TABLE_NAME, selection, null);
        db.close();
        return numDeleted;
    }

    /**
     * 取得所有資料
     */
    public ArrayList<CreditCardData> getAll(){
        return getAll(null, null);
    }

    /**
     * 根據指定條件取得所有聯絡人資料
     */
    private ArrayList<CreditCardData> getAll(String selection, String[] selectionArgs){
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

        // 照原本insert順序排
        String sortOrder =
                CreditCardEntry._ID;

        Cursor cursor = db.query(
                CreditCardEntry.TABLE_NAME,  // The table to query
                null,                          // 列出所有欄位
                selection,                     // The columns for the WHERE clause
                selectionArgs,                 // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                sortOrder                      // The sort order
        );
        ArrayList<CreditCardData> cardList = new ArrayList<>();
        if(cursor != null){
            if(cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    cardList.add(toCardData(cursor));
                }
            }
            cursor.close();
        }

        db.close();
        return cardList;
    }

    /**
     * 將LocalContact物件的資料轉成用來insert的ContentValues
     */
    private ContentValues toCardValues(CreditCardData data){
        ContentValues values = new ContentValues();
        values.put(CreditCardEntry.COLUMN_CARD_NAME, data.getCardName());
        values.put(CreditCardEntry.COLUMN_CARD_NUMBER, data.getCardNumber());
        values.put(CreditCardEntry.COLUMN_CARD_EXPIREDATE, data.getCardExpireDate());
        values.put(CreditCardEntry.COLUMN_CARD_TYPE, data.getCardType().toString());
        values.put(CreditCardEntry.COLUMN_CARD_BANK, data.getCardBank());
        values.put(CreditCardEntry.COLUMN_CARD_KEY, data.getCardKey());
        values.put(CreditCardEntry.COLUMN_TOKEN, data.getToken());
        values.put(CreditCardEntry.COLUMN_TOKEN_EXPIRE_TIME, data.getTokenExpire().toString());
        values.put(CreditCardEntry.COLUMN_CARD_SETTED_MAIN, data.getSettedMain() ? 1 : 0);
        return values;
    }

    /**
     * 從cursor取出資料，產生LocalContact物件
     */
    private CreditCardData toCardData(Cursor cursor){
        CreditCardData cardData = new CreditCardData();
        int valueInt = getInt(cursor, CreditCardEntry._ID);
        cardData.setCardID(valueInt);

        String valueString = getString(cursor, CreditCardEntry.COLUMN_CARD_NAME);
        cardData.setCardName(valueString);

        valueString = getString(cursor, CreditCardEntry.COLUMN_CARD_NUMBER);
        cardData.setCardNumber(valueString);

        valueString = getString(cursor, CreditCardEntry.COLUMN_CARD_EXPIREDATE);
        cardData.setCardExpireDate(valueString);

        valueString = getString(cursor, CreditCardEntry.COLUMN_CARD_TYPE);
        cardData.setCardType(CreditCardData.ENUM_CARD_TYPE.valueOf(valueString));

        valueString = getString(cursor, CreditCardEntry.COLUMN_CARD_BANK);
        cardData.setCardBank(valueString);

        valueString = getString(cursor, CreditCardEntry.COLUMN_CARD_KEY);
        cardData.setCardKey(valueString);

        valueString = getString(cursor, CreditCardEntry.COLUMN_TOKEN);
        cardData.setToken(valueString);

        valueString = getString(cursor, CreditCardEntry.COLUMN_TOKEN_EXPIRE_TIME);
        cardData.setTokenExpire(valueString);

        boolean valueBool = getBoolean(cursor, CreditCardEntry.COLUMN_CARD_SETTED_MAIN);
        cardData.setSettedMain(valueBool);

        return cardData;
    }

    /**
     * 更新一筆資料
     * @param card CreditCardData
     * @param whereClause String 搜尋item條件
     * @param whereArgs String[]
     * @return 回傳rowId
     */
    public long update(CreditCardData card, String whereClause, String[] whereArgs){
        // Gets the data repository in write mode
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = toCardValues(card);

        // Insert the new row, returning the primary key value of the new row
        long rowId = db.update(CreditCardEntry.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
        return rowId;
    }

}
