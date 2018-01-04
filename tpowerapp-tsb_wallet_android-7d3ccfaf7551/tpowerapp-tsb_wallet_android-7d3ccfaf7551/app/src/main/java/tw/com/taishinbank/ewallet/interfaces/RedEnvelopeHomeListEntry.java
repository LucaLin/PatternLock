package tw.com.taishinbank.ewallet.interfaces;

import android.provider.BaseColumns;

/**
 * Deprecated(這是已經移除的列表資料model)
 * 用來判斷紅包首頁列表項目是否看過
 */
public interface RedEnvelopeHomeListEntry extends BaseColumns{
    String TABLE_NAME = "red_envelope_home_list";
    // 交易明細序號
    String COLUMN_TXFD_SEQ = "txfd_seq";
    // 交易序號
    String COLUMN_TXF_SEQ = "txf_seq";
}
