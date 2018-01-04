package com.dbs.omni.tw.controller.bill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.setted.OmniApplication;


public class BillHomeFragment extends Fragment {
    private static final String TAG = "BillHomeFragment";

    private Button mButtonCurrentBill, mButtonElectronicBill;

//    private BillOverview mBillOverview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setCenterTitle(R.string.tab_statement);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bill_home, container, false);

        mButtonCurrentBill = (Button) view.findViewById(R.id.button_current_bill);
        mButtonElectronicBill = (Button)view.findViewById(R.id.button_electronic_bill);

        mButtonCurrentBill.setOnClickListener(buttonListener);
        mButtonElectronicBill.setOnClickListener(buttonListener);


        buttonListener.onClick(mButtonCurrentBill);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_current_bill:
                    mButtonCurrentBill.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRedPrimary));
                    mButtonCurrentBill.setBackgroundResource(R.drawable.bg_bottom_red_line);

                    mButtonElectronicBill.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrayPrimaryDark));
                    mButtonElectronicBill.setBackgroundResource(R.drawable.bg_bottom_line);
                    goToCurrentBill();

                    break;


                case R.id.button_electronic_bill:
                    mButtonElectronicBill.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRedPrimary));
                    mButtonElectronicBill.setBackgroundResource(R.drawable.bg_bottom_red_line);

                    mButtonCurrentBill.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrayPrimaryDark));
                    mButtonCurrentBill.setBackgroundResource(R.drawable.bg_bottom_line);

                    goToElectronicBill();
                    break;
            }
        }
    };

//region Go to function
    private void goToCurrentBill() {
        CurrentBillFragment fragment = CurrentBillFragment.newInstance(OmniApplication.sBillOverview);

        fragment.setOnEventListener(new CurrentBillFragment.OnEventListener() {
            @Override
            public void OnHeaderRightClick() {
                goToBillResult();
            }
        });

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }

    private void goToBillResult() {
        BillResultFragment fragment = BillResultFragment.newInstance(OmniApplication.sBillOverview);

        fragment.setOnEventListener(new BillResultFragment.OnEventListener() {
            @Override
            public void OnHeaderLeftClick() {
                getChildFragmentManager().popBackStack();
            }

        });

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.addToBackStack(TAG);
        ft.replace(R.id.fragment_content, fragment, BillResultFragment.TAG);
        ft.commit();
    }

    private void goToElectronicBill() {
        ElectronicBillFragment fragment = new ElectronicBillFragment();

        fragment.setOnEventListener(new ElectronicBillFragment.OnEventListener() {

        });

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
//        ft.addToBackStack(TAG);
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }
//endregion

//region api
//    private void toGetBillOverview() {
//        // 如果沒有網路連線，顯示提示對話框
//        if (!NetworkUtil.isConnected(getContext())) {
//            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }, true);
//        } else {
//            try {
//                BillHttpUtil.getBillOverview(responseListener_getBillOverview, getActivity());
//                ((ActivityBase)getActivity()).showProgressLoading();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    private ResponseListener responseListener_getBillOverview = new ResponseListener() {
//
//        @Override
//        public void onResponse(ResponseResult result) {
//            if(getActivity() == null)
//                return;
//
//            ((ActivityBase)getActivity()).dismissProgressLoading();
//
//            String returnCode = result.getReturnCode();
//            // 如果returnCode是成功
//            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
////                mBillOverview = BillResponseBodyUtil.getBilledOverview(result.getBody());
//
//                goToCurrentBill();
////                setBillView(billOverview);
//            } else {
//                handleResponseError(result, ((ActivityBase)getActivity()));
//            }
//
//        }
//    };
//endregion
}
