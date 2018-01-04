package tw.com.taishinbank.ewallet.interfaces.log;

import android.provider.BaseColumns;

/**
 * 點擊事件紀錄
 */
public interface HitRecordEntry extends BaseColumns {
    String TABLE_NAME = "hit_records";
    String COLUMN_HIT_EVENT = "hit_event";
    String COLUMN_HIT_TYPE = "hit_type";
}
