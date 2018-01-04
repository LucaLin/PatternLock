package tw.com.taishinbank.ewallet.interfaces.log;

import android.provider.BaseColumns;

/**
 * 特殊事件紀錄.
 */
public interface SpecialEventEntry extends BaseColumns {
    String TABLE_NAME = "special_events";
    String COLUMN_TYPE = "type";
    String COLUMN_NOTE = "note";
}
