package com.dbs.omni.tw.util.http.mode.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/23.
 */

public class UserInfoDetail implements Parcelable {
    private UserNameDetail nameDetl;
    private EmailDetail emailDetl;
    private PhoneDetail phoneDetl;
    private AddressDetail addressDetl;

    private String	blockCode;	//客戶身分權限代碼
    private String	statmFlag;	//是否申請電子帳單 (Y：已申請、N：未申請)
    private String	userCode;	//使用者登入代碼
    private String	nID;	//National ID 身分證號/居留證號 (只顯示前三後二，其他用X代替)
    private String	nickname;	//暱稱

    protected UserInfoDetail(Parcel in) {
        nameDetl = in.readParcelable(UserNameDetail.class.getClassLoader());
        emailDetl = in.readParcelable(EmailDetail.class.getClassLoader());
        phoneDetl = in.readParcelable(PhoneDetail.class.getClassLoader());
        addressDetl = in.readParcelable(AddressDetail.class.getClassLoader());
        blockCode = in.readString();
        statmFlag = in.readString();
        userCode = in.readString();
        nID = in.readString();
        nickname = in.readString();
    }

    public static final Creator<UserInfoDetail> CREATOR = new Creator<UserInfoDetail>() {
        @Override
        public UserInfoDetail createFromParcel(Parcel in) {
            return new UserInfoDetail(in);
        }

        @Override
        public UserInfoDetail[] newArray(int size) {
            return new UserInfoDetail[size];
        }
    };

    public UserNameDetail getNameDetl() {
        return nameDetl;
    }

    public void setNameDetl(UserNameDetail nameDetl) {
        this.nameDetl = nameDetl;
    }

    public EmailDetail getEmailDetl() {
        return emailDetl;
    }

    public void setEmailDetl(EmailDetail emailDetl) {
        this.emailDetl = emailDetl;
    }

    public PhoneDetail getPhoneDetl() {
        return phoneDetl;
    }

    public void setPhoneDetl(PhoneDetail phoneDetl) {
        this.phoneDetl = phoneDetl;
    }

    public AddressDetail getAddressDetl() {
        return addressDetl;
    }

    public void setAddressDetl(AddressDetail addressDetl) {
        this.addressDetl = addressDetl;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getStatmFlag() {
        return statmFlag;
    }

    public void setStatmFlag(String statmFlag) {
        this.statmFlag = statmFlag;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getnID() {
        return nID;
    }

    public void setnID(String nID) {
        this.nID = nID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(nameDetl, flags);
        dest.writeParcelable(emailDetl, flags);
        dest.writeParcelable(phoneDetl, flags);
        dest.writeParcelable(addressDetl, flags);
        dest.writeString(blockCode);
        dest.writeString(statmFlag);
        dest.writeString(userCode);
        dest.writeString(nID);
        dest.writeString(nickname);
    }
}
