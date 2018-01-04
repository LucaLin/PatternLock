package com.dbs.omni.tw.controller.installment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstallmentHomeFragment extends Fragment {

    private Button button_installment;
    private Button button_installment_history;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setCenterTitle(R.string.center_title_installment);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installment_home, container, false);

        button_installment = (Button)view.findViewById(R.id.button_installment);
        button_installment_history = (Button)view.findViewById(R.id.button_installment_history);

        button_installment.setOnClickListener(buttonListener);
        button_installment_history.setOnClickListener(buttonListener);

        buttonListener.onClick(button_installment);

        return view;
    }

    private Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_installment:
                    button_installment.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorRedPrimary));
                    button_installment.setBackgroundResource(R.drawable.bg_bottom_red_line);

                    button_installment_history.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorGrayPrimaryDark));
                    button_installment_history.setBackgroundResource(android.R.color.transparent);

                    InstallmentHomeButtonFragment installmentFragment = new InstallmentHomeButtonFragment();
                    goToPage(installmentFragment);
                    break;


                case R.id.button_installment_history:
                    button_installment_history.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorRedPrimary));
                    button_installment_history.setBackgroundResource(R.drawable.bg_bottom_red_line);

                    button_installment.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorGrayPrimaryDark));
                    button_installment.setBackgroundResource(android.R.color.transparent);

                    InstallmentHistoryFragment installmentHistoryFragment = new InstallmentHistoryFragment();
                    goToPage(installmentHistoryFragment);
                    break;
            }
        }
    };

    //前往頁面
    private void goToPage(Fragment fragment) {
       FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);

        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            getChildFragmentManager().popBackStack(getChildFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        ft.commit();
    }

}
