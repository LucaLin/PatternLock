package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;

public class RedEnvelopeInputData implements Parcelable {
    public static final String EXTRA_RED_ENVELOPE_DATA = "extra_red_envelope_data";

    // 紅包類型：1:一般紅包, 2:財神紅包
    private String type;
    // 候選列表的會員號碼
    private String[] memNOs;
    // 候選列表的聯絡人名稱
    private String[] names;
    // 祝福語
    private String blessing;
    // 總金額
    private long totalAmount;
    // 總人數
    private int totalPeople;
    // 每人最小金額
    private int minAmountPerPerson;
    // 要發送給每人的金額
    private String[] amounts;
    // 回禮交易明細序號(*txType=4時，此欄位必填，為何筆交易的回禮)
    private String replyToTxfdSeq = null;

    public RedEnvelopeInputData(){
        // 設定類型為財神紅包
        type = RedEnvelopeType.TYPE_MONEY_GOD;
    }

    public RedEnvelopeInputData(String redEnvelopeType){
        // 設定類型為財神紅包
        this.type = redEnvelopeType;
    }

    protected RedEnvelopeInputData(Parcel in) {
        type = in.readString();
        memNOs = in.createStringArray();
        names = in.createStringArray();
        blessing = in.readString();
        totalAmount = in.readLong();
        totalPeople = in.readInt();
        minAmountPerPerson = in.readInt();
        amounts = in.createStringArray();
        replyToTxfdSeq = in.readString();
    }

    public static final Creator<RedEnvelopeInputData> CREATOR = new Creator<RedEnvelopeInputData>() {
        @Override
        public RedEnvelopeInputData createFromParcel(Parcel in) {
            return new RedEnvelopeInputData(in);
        }

        @Override
        public RedEnvelopeInputData[] newArray(int size) {
            return new RedEnvelopeInputData[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(int totalPeople) {
        this.totalPeople = totalPeople;
    }

    public int getMinAmountPerPerson() {
        return minAmountPerPerson;
    }

    public void setMinAmountPerPerson(int minAmountPerPerson) {
        this.minAmountPerPerson = minAmountPerPerson;
    }

    public String[] getAmounts() {
        return amounts;
    }

    public void setAmounts(String[] amounts) {
        this.amounts = amounts;
    }

    public String[] getMemNOs() {
        return memNOs;
    }

    public void setMemNOs(String[] memNOs) {
        this.memNOs = memNOs;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String getBlessing() {
        return blessing;
    }

    public void setBlessing(String blessing) {
        this.blessing = blessing;
    }

    public String getReplyToTxfdSeq() {
        return replyToTxfdSeq;
    }

    public void setReplyToTxfdSeq(String replyToTxfdSeq) {
        this.replyToTxfdSeq = replyToTxfdSeq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeStringArray(memNOs);
        dest.writeStringArray(names);
        dest.writeString(blessing);
        dest.writeLong(totalAmount);
        dest.writeInt(totalPeople);
        dest.writeInt(minAmountPerPerson);
        dest.writeStringArray(amounts);
        dest.writeString(replyToTxfdSeq);
    }

    /**
     * 取得所有顯示名稱連接起來的字串, 例如：Name1, Name2, Name2 (3)
     */
    public String getMergedNames(){
        // 串出顯示名稱
        if(names!= null && names.length > 0) {
            String displayNames = names[0];
            for (int i = 1; i < names.length; i++) {
                displayNames += ", " + names[i];
            }
            return displayNames;
        }
        return "";
    }

    public String getNamesNumberString(){
        if(names != null && names.length > 1) {
            return "(" + names.length + ")";
        }
        return "";
    }
}
