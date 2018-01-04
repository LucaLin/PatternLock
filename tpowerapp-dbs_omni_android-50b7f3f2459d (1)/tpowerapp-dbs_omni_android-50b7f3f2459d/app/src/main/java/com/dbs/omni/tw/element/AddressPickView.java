package com.dbs.omni.tw.element;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.util.AddressUtil;

import java.util.HashMap;

/**
 * Created by siang on 2017/6/6.
 */

public class AddressPickView extends DialogFragment {

    public static final String ARG_TYPE = "ARG_TYPE";
    public static final String ARG_SELECTED_CITY = "ARG_SELECTED_CITY";
    

    private ENUM_SELECT_TYPE mType;
    private String selectedCity = "";
    
    private TextView mTextTitle;
    private NumberPicker mPicker;

    private AddressUtil mAddressUtil;

    private HashMap<String, String> mRegionMap;

    public enum ENUM_SELECT_TYPE {
        CITY,
        POSTAL,
        REGION
    }

    private OnSelectedListener onSelectedListener;

    public void setOnSelectedListener(OnSelectedListener listener) {
        onSelectedListener = listener;
    }

    public interface OnSelectedListener {
        void OnSelectedCity(String city);
        void OnSelectedRegion(String region, String postal);
    }

    public static AddressPickView newCityInstance() {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, ENUM_SELECT_TYPE.CITY);

        AddressPickView fragment = new AddressPickView();
        fragment.setArguments(args);
        return fragment;
    }

    public static AddressPickView newRegionInstance(String cityName) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, ENUM_SELECT_TYPE.REGION);
        args.putString(ARG_SELECTED_CITY, cityName);

        AddressPickView fragment = new AddressPickView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAddressUtil = new AddressUtil();
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullWidthThemeDialog);

        if(getArguments().containsKey(ARG_TYPE)) {
            mType = (ENUM_SELECT_TYPE) getArguments().getSerializable(ARG_TYPE);
        }
        
        if(mType.equals(ENUM_SELECT_TYPE.REGION) && getArguments().containsKey(ARG_SELECTED_CITY)) {
            selectedCity =  getArguments().getString(ARG_SELECTED_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View view = inflater.inflate(R.layout.address_pick_dialog_container, container, false);
        
        mPicker = (NumberPicker) view.findViewById(R.id.address_pick);
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        Button buttonCancel = (Button) view.findViewById(R.id.button_cancel);
        Button buttonOK = (Button) view.findViewById(R.id.button_ok);
        
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seletIndex = mPicker.getValue();
                String selectedString = mPicker.getDisplayedValues()[seletIndex];
                if(onSelectedListener != null) {


                    switch (mType) {
                        case POSTAL:

                            break;
                        case REGION:
                            String postal = null;
                            if(mRegionMap != null) {
                                postal = mRegionMap.get(selectedString);
                            }
                            onSelectedListener.OnSelectedRegion(selectedString, postal);
                            break;
                        default: // City
                            onSelectedListener.OnSelectedCity(selectedString);
                            break;
                    }
                }
                getDialog().dismiss();
//                Log.v("Select", xxx);
            }
        });

        setTitle();

        setPicker();
       


        
        Window window = getDialog().getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        getDialog().setCancelable(true);
        return view;
    }

    private void setPicker() {
        String[] showDataArray = new String[0];
        switch (mType) {
            case POSTAL:
                
                break;
            case REGION:
                mRegionMap = mAddressUtil.getCityMap().get(selectedCity);
                showDataArray = mRegionMap.keySet().toArray(new String[mRegionMap.size()]);
                break;
            default: // City
                showDataArray = mAddressUtil.getCityArray().toArray(new String[mAddressUtil.getCityArray().size()]);
                break;
        }

        if(showDataArray != null) {
            mPicker.setMaxValue(showDataArray.length - 1);
            mPicker.setMinValue(0);
            mPicker.setDisplayedValues(showDataArray);
        }
    }

    private void setTitle() {
        switch (mType) {
            case POSTAL:
                mTextTitle.setText(R.string.address_modify_postal);
                break;
            case REGION:
                mTextTitle.setText(R.string.address_modify_region);
                break;
            default: // City
                mTextTitle.setText(R.string.address_modify_city);
                break;
        }
    }





}
