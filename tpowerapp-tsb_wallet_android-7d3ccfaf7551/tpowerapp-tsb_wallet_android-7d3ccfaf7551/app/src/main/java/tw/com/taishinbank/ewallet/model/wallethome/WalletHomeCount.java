package tw.com.taishinbank.ewallet.model.wallethome;


import android.text.TextUtils;

public class WalletHomeCount {

    public interface CountType {
        /**
         * 本月紅包
         */
        String RED_ENVELOPES = "redEnvelopes";
        /**
         * 待付款項
         */
        String PAY_REQUESTS = "payRequests";
        /**
         * 本月收款
         */
        String INCOMES = "incomes";
        /**
         * 優惠券
         */
        String RECEIVED_COUPON = "receivedCoupon";
    }

    private String type;
    private String hasNew;
    private int count;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public boolean hasNew(){
        if(TextUtils.isEmpty(hasNew)){
            return false;
        }
        return hasNew.equalsIgnoreCase("Y");
    }

    /**
     *
     * @return
     * The hasNew
     */
    public String getHasNew() {
        return hasNew;
    }

    /**
     *
     * @param hasNew
     * The hasNew
     */
    public void setHasNew(String hasNew) {
        this.hasNew = hasNew;
    }

    /**
     *
     * @return
     * The count
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(int count) {
        this.count = count;
    }

}
