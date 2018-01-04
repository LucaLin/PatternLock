package tw.com.taishinbank.ewallet.controller.extra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import tw.com.taishinbank.ewallet.R;

public class EarnHomeFragment extends Fragment {

    // -- View Hold --
    private LinearLayout btnEarnShare;
    private LinearLayout btnEarnEnter;

    public EarnHomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_earn_promotion, container, false);

        // Set view hold
        btnEarnShare = (LinearLayout) view.findViewById(R.id.btn_earn_share);
        btnEarnEnter = (LinearLayout) view.findViewById(R.id.btn_earn_enter);

        // Set listener
        btnEarnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShareToEarn();
            }
        });
        btnEarnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEnterToEarn();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            getFragmentManager().popBackStack();
        return true;
    }

    // ---
    // My methods
    // ---

    // ----
    // User interaction
    // ----
    protected void clickShareToEarn() {
        ((EarnActivity) getActivity()).gotoShareToEarn();
    }

    protected void clickEnterToEarn() {
        ((EarnActivity) getActivity()).gotoEnterToEarn();

    }
}
