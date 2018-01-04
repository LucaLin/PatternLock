package tw.com.taishinbank.ewallet.model.creditcard;


import android.os.Parcel;
import android.os.Parcelable;

public class CreditCardData implements Parcelable {

    public static final String EXTRA_CREDIT_CARD_DATA = "extra_credit_card_data";

    protected CreditCardData(Parcel in) {
        CardID = in.readInt();
        CardName = in.readString();
        CardNumber = in.readString();
        CardExpireDate = in.readString();
        CardBank = in.readString();
        CardKey = in.readString();
        Token = in.readString();
        TokenExpire = in.readString();

        CardType = ENUM_CARD_TYPE.valueOf(in.readString());
        SettedMain = Boolean.getBoolean(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CardID);
        dest.writeString(CardName);
        dest.writeString(CardNumber);
        dest.writeString(CardExpireDate);
        dest.writeString(CardBank);
        dest.writeString(CardKey);
        dest.writeString(Token);
        dest.writeString(TokenExpire);
        dest.writeString(CardType.toString());
        dest.writeString(SettedMain.toString());
    }

    public static final Creator<CreditCardData> CREATOR = new Creator<CreditCardData>() {
        @Override
        public CreditCardData createFromParcel(Parcel in) {
            return new CreditCardData(in);
        }

        @Override
        public CreditCardData[] newArray(int size) {
            return new CreditCardData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditCardData that = (CreditCardData) o;

        return CardID == that.CardID;

    }

    @Override
    public int hashCode() {
        return CardID;
    }

    public enum ENUM_CARD_TYPE
    {
        Visa(1),
        MasterCard(2),
        AmericanExpress(3),
        JCB(4),
        Empty(5);

        private final int value;

        ENUM_CARD_TYPE(final int newValue) {
            value = newValue;
        }

        public static ENUM_CARD_TYPE valueOf(int value)
        {
            switch (value)
            {
                case 1:
                    return Visa;
                case 2:
                    return MasterCard;
                case 3:
                    return AmericanExpress;
                case 4:
                    return JCB;
                default:
                    return Empty;
            }
        }

        public int getValue() { return value; }
    }

    private int CardID;
    private String CardName;
    private String CardNumber;
    private String CardExpireDate;
    private ENUM_CARD_TYPE CardType;
    private String CardBank;
    private String CardKey;
    private String Token;
    private String TokenExpire;
    private Boolean SettedMain;

    public int getCardID() {
        return CardID;
    }

    public void setCardID(int cardID) {
        CardID = cardID;
    }


    public String getCardName() {
        return CardName;
    }

    public void setCardName(String cardName) {
        CardName = cardName;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getCardExpireDate() {
        return CardExpireDate;
    }

    public void setCardExpireDate(String cardExpireDate) {
        CardExpireDate = cardExpireDate;
    }

    public ENUM_CARD_TYPE getCardType() {
        return CardType;
    }

    public void setCardType(ENUM_CARD_TYPE cardType) {
        CardType = cardType;
    }

    public String getCardBank() {
        return CardBank;
    }

    public void setCardBank(String cardBank) {
        CardBank = cardBank;
    }

    public String getCardKey() {
        return CardKey;
    }

    public void setCardKey(String cardKey) {
        CardKey = cardKey;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getTokenExpire() {
        return TokenExpire;
    }

    public void setTokenExpire(String tokenExpire) {
        TokenExpire = tokenExpire;
    }

    public Boolean getSettedMain() {
        return SettedMain;
    }

    public void setSettedMain(Boolean settedMain) {
        SettedMain = settedMain;
    }

    public CreditCardData(){
        CardBank = "";
        CardID = 0;
        CardName = "";
        CardNumber = "";
        CardExpireDate = "";
        CardType = ENUM_CARD_TYPE.Empty;
        CardKey = "";
        Token = "";
        TokenExpire = "";
        SettedMain = false;
    }

    public CreditCardData(CreditCardData cardData){
        CardBank = cardData.getCardBank();
        CardID = cardData.getCardID();
        CardName = cardData.getCardName();
        CardNumber = cardData.getCardNumber();
        CardExpireDate = cardData.getCardExpireDate();
        CardType = cardData.getCardType();
        CardKey = cardData.getCardKey();
        Token = cardData.getToken();
        TokenExpire = cardData.getTokenExpire();
        SettedMain = cardData.getSettedMain();
    }

    public CreditCardData(int cardID, String cardName, String cardNumber, String cardExpireDate, ENUM_CARD_TYPE cardType, String cardBank, String cardKey, String token, String tokenExpire, Boolean settedMain) {
        CardBank = cardBank;
        CardID = cardID;
        CardName = cardName;
        CardNumber = cardNumber;
        CardExpireDate = cardExpireDate;
        CardType = cardType;
        CardKey = cardKey;
        Token = token;
        TokenExpire = tokenExpire;
        SettedMain = settedMain;
    }

}
