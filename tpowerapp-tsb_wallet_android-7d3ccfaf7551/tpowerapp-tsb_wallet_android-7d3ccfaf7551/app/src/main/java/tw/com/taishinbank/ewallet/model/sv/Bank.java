package tw.com.taishinbank.ewallet.model.sv;

/**
 * Data Model
 *
 * Created by oster on 2016/1/6.
 */
public class Bank {

    private String code;

    private String name;

    public Bank(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
