package tw.com.taishinbank.ewallet.dbhelper;


import android.content.Context;
import android.database.Cursor;

public class DBHelperBase {

    protected TSBSQLiteOpenHelper sqliteOpenHelper;

    public DBHelperBase(Context context){
        sqliteOpenHelper = new TSBSQLiteOpenHelper(context);
    }

    /**
     * 從cursor取出指定欄位名稱的字串
     */
    protected String getString(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    /**
     * 從cursor取出指定欄位名稱的整數值
     */
    protected int getInt(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    /**
     * 從cursor取出指定欄位名稱的boolean值
     */
    protected boolean getBoolean(Cursor cursor, String columnName){
        return (getInt(cursor, columnName) == 1);
    }
}
