package tw.com.taishinbank.ewallet.model.sv;

/**
 * Data Model
 *
 * Created by oster on 2016/1/8.
 */
public class TxOne {
    private int txToMemNO;
    private double perAmount;

    public TxOne(int txToMemNO, double perAmount) {
        this.txToMemNO = txToMemNO;
        this.perAmount = perAmount;
    }

    public int getTxToMemNO() {
        return txToMemNO;
    }

    public void setTxToMemNO(int txToMemNO) {
        this.txToMemNO = txToMemNO;
    }

    public double getPerAmount() {
        return perAmount;
    }

    public void setPerAmount(double perAmount) {
        this.perAmount = perAmount;
    }
}
