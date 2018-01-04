package tw.com.taishinbank.ewallet.model;

public class RemoteContact {
    // 會員代碼
    private String memNO;
    // 手機號碼
    private String memPhone;
    // 電子郵件
    private String memEmail;

    /**
     * 排序2
     * 稱呼
     */
    private String nickname;
    /**
     * 排序3
     * 姓
     */
    private String lastName;
    /**
     * 排序4
     * 名
     */
    private String firstName;

    /**
     11:從好友建立
     a1:手機未認證
     a2:信箱未認證
     a3:密碼未設定
     a4:稱呼未設定
     00:註冊成功
     */
    private String status;

    /**
     * Y:儲值會員/ N:非儲值會員
     */
    private String svFlag;

    /**
     * 排序1
     * 0:新增/1:既有
     */
    private String newFlag;

    /**
     *
     * @return
     * The memNO
     */
    public String getMemNO() {
        return memNO;
    }

    /**
     *
     * @param memNO
     * The memNO
     */
    public void setMemNO(String memNO) {
        this.memNO = memNO;
    }

    /**
     *
     * @return
     * The memPhone
     */
    public String getMemPhone() {
        return memPhone;
    }

    /**
     *
     * @param memPhone
     * The memPhone
     */
    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone;
    }

    /**
     *
     * @return
     * The memEmail
     */
    public String getMemEmail() {
        return memEmail;
    }

    /**
     *
     * @param memEmail
     * The memEmail
     */
    public void setMemEmail(String memEmail) {
        this.memEmail = memEmail;
    }

    /**
     *
     * @return
     * The nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     *
     * @param nickname
     * The nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The svFlag
     */
    public String getSvFlag() {
        return svFlag;
    }

    /**
     *
     * @param svFlag
     * The svFlag
     */
    public void setSvFlag(String svFlag) {
        this.svFlag = svFlag;
    }

    /**
     *
     * @return
     * The newFlag
     */
    public String getNewFlag() {
        return newFlag;
    }

    /**
     *
     * @param newFlag
     * The newFlag
     */
    public void setNewFlag(String newFlag) {
        this.newFlag = newFlag;
    }
}
