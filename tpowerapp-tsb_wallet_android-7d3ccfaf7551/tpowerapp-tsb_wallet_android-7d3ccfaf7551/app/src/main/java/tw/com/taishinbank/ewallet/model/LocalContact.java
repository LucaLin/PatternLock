package tw.com.taishinbank.ewallet.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class LocalContact implements Parcelable{

    // 狀態
    /* 11:從好友建立
       a1:手機未認證
       a2:信箱未認證
       a3:密碼未設定
       a4:稱呼未設定
       00:註冊成功
    */
    String status;
    // 會員代碼
    String memNO;
    // 電子信箱
    String email;
    // 姓
    String familyName;
    // 名
    String givenName;
    // 稱呼
    String nickname;
    // 電話 (比對是否為相同用戶)
    String phoneNumber;
    // 是否為錢包用戶
    boolean isWalletAccount = false;
    // 是否為儲值用戶
    boolean isSVAccount = false;
    // 是否為新增的好友
    boolean isNewAdded = false;
    // 頭像圖檔檔名（圖片另外下載）
    String photoFileName;
    // 交易次數(本機自行紀錄)
    int numOfTransactions = 0;
    // use local name(本機自行紀錄)

    public LocalContact(){

    }

    public LocalContact(RemoteContact remoteContact){
        givenName = remoteContact.getFirstName();
        familyName = remoteContact.getLastName();
        nickname = remoteContact.getNickname();
        if(remoteContact.getSvFlag() != null) {
            isSVAccount = remoteContact.getSvFlag().equalsIgnoreCase("Y");
        }
        if(remoteContact.getStatus() != null) {
            isWalletAccount = remoteContact.getStatus().equals("00");
        }
        if(remoteContact.getNewFlag() != null) {
            isNewAdded = remoteContact.getNewFlag().equals("0");
        }
        phoneNumber = remoteContact.getMemPhone();
        status = remoteContact.getStatus();
        memNO = remoteContact.getMemNO();
        email = remoteContact.getMemEmail();
    }

    protected LocalContact(Parcel in) {
        status = in.readString();
        memNO = in.readString();
        email = in.readString();
        familyName = in.readString();
        givenName = in.readString();
        nickname = in.readString();
        phoneNumber = in.readString();
        isWalletAccount = in.readByte() != 0;
        isSVAccount = in.readByte() != 0;
        isNewAdded = in.readByte() != 0;
        photoFileName = in.readString();
        numOfTransactions = in.readInt();
    }

    public static final Creator<LocalContact> CREATOR = new Creator<LocalContact>() {
        @Override
        public LocalContact createFromParcel(Parcel in) {
            return new LocalContact(in);
        }

        @Override
        public LocalContact[] newArray(int size) {
            return new LocalContact[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemNO() {
        return memNO;
    }

    public void setMemNO(String memNO) {
        this.memNO = memNO;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isWalletAccount() {
        return isWalletAccount;
    }

    public void setIsWalletAccount(boolean isWalletAccount) {
        this.isWalletAccount = isWalletAccount;
    }

    public boolean isSVAccount() {
        return isSVAccount;
    }

    public void setIsSVAccount(boolean isSVAccount) {
        this.isSVAccount = isSVAccount;
    }

    public boolean isNewAdded() {
        return isNewAdded;
    }

    public void setIsNewAdded(boolean isNewAdded) {
        this.isNewAdded = isNewAdded;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public int getNumOfTransactions() {
        return numOfTransactions;
    }

    public void setNumOfTransactions(int numOfTransactions) {
        this.numOfTransactions = numOfTransactions;
    }

    /**
     * 取得顯示名稱
     */
    public String getDisplayName(){
        // 如果nickname不為空，就用nickname
        if(!TextUtils.isEmpty(nickname)){
            return nickname;
        }

        // 否則回傳姓名的組合
        String name = "";
        if(!TextUtils.isEmpty(familyName)){
            name += familyName;
        }
        if(!TextUtils.isEmpty(givenName)){
            name += givenName;
        }

        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(memNO);
        dest.writeString(email);
        dest.writeString(familyName);
        dest.writeString(givenName);
        dest.writeString(nickname);
        dest.writeString(phoneNumber);
        dest.writeByte((byte) (isWalletAccount ? 1 : 0));
        dest.writeByte((byte) (isSVAccount ? 1 : 0));
        dest.writeByte((byte) (isNewAdded ? 1 : 0));
        dest.writeString(photoFileName);
        dest.writeInt(numOfTransactions);
    }
}
