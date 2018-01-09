package tw.com.taishinbank.ewallet.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tw.com.taishinbank.ewallet.interfaces.CreditCardEntry;
import tw.com.taishinbank.ewallet.interfaces.LocalContactEntry;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeHomeListEntry;
import tw.com.taishinbank.ewallet.interfaces.log.HitRecordEntry;
import tw.com.taishinbank.ewallet.interfaces.log.SpecialEventEntry;


public class TSBSQLiteOpenHelper extends SQLiteOpenHelper {
    // TODO CHECK If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "TSB_Wallet.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    // 建立聯絡人資料表的語法
    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE IF NOT EXISTS " + LocalContactEntry.TABLE_NAME + " (" +
                    LocalContactEntry._ID + " INTEGER PRIMARY KEY," +
                    LocalContactEntry.COLUMN_MEM_NO + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_STATUS + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_FAMILY_NAME + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_GIVEN_NAME + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_NICK_NAME + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_IS_WALLET_ACCOUNT + INT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_IS_SV_ACCOUNT + INT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_IS_NEW_ADDED + INT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_PHOTO_FILENAME + TEXT_TYPE + COMMA_SEP +
                    LocalContactEntry.COLUMN_NUM_OF_TRANSACTIONS + INT_TYPE +
            " )";

    // 建立信用卡列表用來判斷是否看過的資料表語法
    private static final String SQL_CREATE_CREDIT_CARD_LIST =
            "CREATE TABLE IF NOT EXISTS " + CreditCardEntry.TABLE_NAME + " (" +
                    CreditCardEntry._ID + " INTEGER PRIMARY KEY," +
                    CreditCardEntry.COLUMN_CARD_NAME + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_CARD_NUMBER + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_CARD_EXPIREDATE + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_CARD_TYPE + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_CARD_BANK + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_CARD_KEY + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_TOKEN + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_TOKEN_EXPIRE_TIME + TEXT_TYPE + COMMA_SEP +
                    CreditCardEntry.COLUMN_CARD_SETTED_MAIN + INT_TYPE +
                    " )";

    // 建立點擊事件紀錄的資料表語法
    private static final String SQL_CREATE_HIT_RECORDS =
            "CREATE TABLE IF NOT EXISTS " + HitRecordEntry.TABLE_NAME + " (" +
                    HitRecordEntry._ID + " INTEGER PRIMARY KEY," +
                    HitRecordEntry.COLUMN_HIT_EVENT + TEXT_TYPE + COMMA_SEP +
                    HitRecordEntry.COLUMN_HIT_TYPE + TEXT_TYPE +
                    " )";

    // 建立點擊特殊事件紀錄的資料表語法
    private static final String SQL_CREATE_SPECIAL_EVENTS =
            "CREATE TABLE IF NOT EXISTS " + SpecialEventEntry.TABLE_NAME + " (" +
                    SpecialEventEntry._ID + " INTEGER PRIMARY KEY," +
                    SpecialEventEntry.COLUMN_TYPE + TEXT_TYPE + COMMA_SEP +
                    SpecialEventEntry.COLUMN_NOTE + TEXT_TYPE +
                    " )";

    // 刪除資料表的語法
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ";

    public TSBSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立資料表
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_CREDIT_CARD_LIST);
        db.execSQL(SQL_CREATE_HIT_RECORDS);
        db.execSQL(SQL_CREATE_SPECIAL_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // TODO 升級DB應該要做什麼

//        db.execSQL(SQL_DELETE_ENTRIES + LocalContactEntry.TABLE_NAME);
//        db.execSQL(SQL_DELETE_ENTRIES + RedEnvelopeHomeListEntry.TABLE_NAME);
//        db.execSQL(SQL_DELETE_ENTRIES + CreditCardEntry.TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

