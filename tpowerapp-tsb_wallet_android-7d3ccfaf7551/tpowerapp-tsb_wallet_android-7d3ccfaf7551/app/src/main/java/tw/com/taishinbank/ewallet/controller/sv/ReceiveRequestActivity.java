package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;

/**
 *
 */
public class ReceiveRequestActivity extends ActivityBase {

    public static final String EXTRA_SV_TRX_OUT = "EXTRA_SV_TRX_OUT";

    private SVTransactionOut svTransactionOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_receive_request_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        svTransactionOut = getIntent().getParcelableExtra(EXTRA_SV_TRX_OUT);

        //Set Fragment
        ReceiveRequestFragment fragment = new ReceiveRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SV_TRX_OUT, svTransactionOut);
        fragment.setArguments(bundle);

        //Start Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();


    }
}
