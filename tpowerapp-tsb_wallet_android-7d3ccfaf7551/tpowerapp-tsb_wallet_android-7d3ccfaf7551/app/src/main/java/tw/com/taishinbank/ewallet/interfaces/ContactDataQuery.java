package tw.com.taishinbank.ewallet.interfaces;

import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;

public interface ContactDataQuery {

    // 姓名欄位index
    int GIVEN_NAME = 0;
    int FAMILY_NAME = 1;
    // 電話欄位index
    int NUMBER = 0;

    // 所有聯絡人詳細資料表的Content Uri
    Uri CONTENT_URI = Data.CONTENT_URI;

    // 姓名的select條件
    String SELECTION_NAME = Data.CONTACT_ID + "=?"
            + " AND " + Data.MIMETYPE + "='" + CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";

    // select姓名時要取的欄位
    String[] PROJECTION_NAME = {
            CommonDataKinds.StructuredName.GIVEN_NAME,  // given name
            CommonDataKinds.StructuredName.FAMILY_NAME  // family name
    };

    // 電話的select條件
    String SELECTION_PHONE = Data.CONTACT_ID + "=?"
            + " AND " + Data.MIMETYPE + "='" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"
            + " AND (" + CommonDataKinds.Phone.NUMBER + " LIKE '09%'" + " OR "
            + CommonDataKinds.Phone.NUMBER + " LIKE '+886%'" + " OR "
            + CommonDataKinds.Phone.NUMBER + " LIKE '886%')";

    // select電話時要取的欄位
    String[] PROJECTION_PHONE = {
            CommonDataKinds.Phone.NUMBER  // number
    };
}
