package com.dbs.omni.tw.controller.payment.convenient;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.element.BarcodeItem;
import com.dbs.omni.tw.util.BarcodeGeneratorUtil;
import com.dbs.omni.tw.util.FormatUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarcodeFragment extends Fragment {

    private BarcodeItem barcode_1, barcode_2, barcode_3;
    private TextView textView_amount;

    public static final String ARG_INPUT_DATA = "ARG_INPUT_DATA";
    public static final String ARG_AMOUNT = "ARG_AMOUNT";

    public static BarcodeFragment newInstance(String[] inputData, double amount) {
        Bundle args = new Bundle();
        args.putStringArray(ARG_INPUT_DATA, inputData);
        args.putDouble(ARG_AMOUNT, amount);

        BarcodeFragment barcodeFragment = new BarcodeFragment();
        barcodeFragment.setArguments(args);
        return barcodeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode, container, false);

        textView_amount = (TextView)view.findViewById(R.id.textView_amount);

        barcode_1 = (BarcodeItem)view.findViewById(R.id.barcode_1);
        barcode_2 = (BarcodeItem)view.findViewById(R.id.barcode_2);
        barcode_3 = (BarcodeItem)view.findViewById(R.id.barcode_3);

        Bundle arguments = getArguments();
        if (arguments != null) {
            //設定barcode
            if(arguments.containsKey(ARG_INPUT_DATA)) {
                String[] inputData = arguments.getStringArray(ARG_INPUT_DATA);

                barcode_1.setContent(inputData[0]);
                barcode_2.setContent(inputData[1]);
                barcode_3.setContent(inputData[2]);

                createBarcode(barcode_1.getImageView(), inputData[0]);
                createBarcode(barcode_2.getImageView(), inputData[1]);
                createBarcode(barcode_3.getImageView(), inputData[2]);
            }

            if(arguments.containsKey(ARG_AMOUNT)) {
                double amount = arguments.getDouble(ARG_AMOUNT);
                textView_amount.setText(FormatUtil.toDecimalFormat(getContext(), amount, true));
            }

        }



        return view;
    }

    private void createBarcode(ImageView imageView ,String stringBarcode){
        BarcodeGeneratorUtil.GenerateQRCodeAsyncTask task = new BarcodeGeneratorUtil.GenerateQRCodeAsyncTask(
                getActivity(),
                imageView,
                stringBarcode,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 295, getActivity().getApplication().getResources().getDisplayMetrics()),  //Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getActivity().getApplication().getResources().getDisplayMetrics()),  //Height
                BarcodeGeneratorUtil.MARGIN_AUTOMATIC,
                Color.BLACK, Color.TRANSPARENT, false);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
