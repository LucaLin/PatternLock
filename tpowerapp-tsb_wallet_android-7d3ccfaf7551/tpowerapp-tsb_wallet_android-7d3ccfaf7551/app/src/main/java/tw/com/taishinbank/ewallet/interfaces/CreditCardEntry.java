package tw.com.taishinbank.ewallet.interfaces;

import android.provider.BaseColumns;


public interface CreditCardEntry extends BaseColumns{
    String TABLE_NAME = "credit_cards_list";
    String COLUMN_CARD_NAME = "card_name"; //卡片名稱
    String COLUMN_CARD_NUMBER = "card_number";
    String COLUMN_CARD_EXPIREDATE = "card_expiredate";
    String COLUMN_CARD_TYPE= "card_type";
    String COLUMN_CARD_BANK = "card_bank";
    String COLUMN_CARD_KEY = "card_key";
    String COLUMN_TOKEN = "token";
    String COLUMN_TOKEN_EXPIRE_TIME = "token_expire_time";
    String COLUMN_CARD_SETTED_MAIN = "card_setted_main";
}
