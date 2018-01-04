package tw.com.taishinbank.ewallet.interfaces;

import android.net.Uri;
import android.provider.ContactsContract;

public interface ContactListQuery {
    // 所有聯絡人資料表的Content Uri
    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    // select條件
    String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
            "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1" +
            " AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1" ;

    // 排列順序
    String SORT_ORDER = ContactsContract.Contacts.SORT_KEY_PRIMARY;


    String[] PROJECTION = {
            // _ID與是用來查尋詳細資料
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            // 用在sectionindexer
            SORT_ORDER
    };

    // 對應projection中的query欄位index
    int ID = 0;
    int LOOKUP_KEY = 1;

}
