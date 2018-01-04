package tw.com.taishinbank.ewallet.interfaces;

import android.provider.BaseColumns;
////      會員代碼
//        String memNO;
////      狀態
//        String status;
////      電子郵件
//        String email;
////    ．姓
//        String familyName;
////    ．名
//        String givenName;
////    ．縮寫（例如adam wang = AW）
////    ．稱呼
//        String nickname;
////    ．電話 (比對是否為相同用戶)
//        String phoneNumber;
////    ．是否為錢包用戶
//        boolean isWalletAccount = false;
////    ．是否為儲值用戶
//        boolean isSVAccount = false;
////    ．是否為新增的好友
//        boolean isNewAdded = false;
////    ．頭像圖檔檔名（圖片另外下載）
//        String photoFileName;
////    ．交易次數(本機自行紀錄)
//        int numOfTransactions = 0;
////    ．use local name(本機自行紀錄)

public interface LocalContactEntry extends BaseColumns{
    String TABLE_NAME = "localcontacts";
    String COLUMN_MEM_NO = "mem_no";
    String COLUMN_STATUS = "status";
    String COLUMN_EMAIL = "email";
    String COLUMN_FAMILY_NAME = "family_name";
    String COLUMN_GIVEN_NAME = "given_name";
    String COLUMN_NICK_NAME = "nick_name";

    String COLUMN_PHONE_NUMBER = "phone_number";
    String COLUMN_IS_WALLET_ACCOUNT = "is_wallet_account";
    String COLUMN_IS_SV_ACCOUNT = "is_sv_account";

    String COLUMN_IS_NEW_ADDED = "is_new_added";
    String COLUMN_PHOTO_FILENAME = "photo_filename";
    String COLUMN_NUM_OF_TRANSACTIONS = "num_of_transactions";
}
