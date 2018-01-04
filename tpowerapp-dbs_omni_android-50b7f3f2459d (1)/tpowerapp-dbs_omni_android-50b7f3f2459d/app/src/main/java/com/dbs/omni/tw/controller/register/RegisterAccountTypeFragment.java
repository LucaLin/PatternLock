package com.dbs.omni.tw.controller.register;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.typeMapping.AccountType;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterAccountTypeFragment extends Fragment {

    public static final String TAG = "RegisterAccountTypeFragment";

    public static final String ARG_USER_TYPE = "ARG_USER_TYPE";

    protected interface OnFragmentListener {
        void onDBSNext();
        void onANZNext();
    }

    private OnFragmentListener onFragmentListener;

    public void setOnFragmentListener (OnFragmentListener onFragmentListener) {
        this.onFragmentListener = onFragmentListener;
    }


    private LayoutInflater inflater;
    private AccountType mUserType = AccountType.ALL;

    public static RegisterAccountTypeFragment newInstance(String userType) {

        Bundle args = new Bundle();

        args.putString(ARG_USER_TYPE, userType);

        RegisterAccountTypeFragment fragment = new RegisterAccountTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_USER_TYPE)) {

            String userType = getArguments().getString(ARG_USER_TYPE);
            mUserType = AccountType.valueOf(Integer.valueOf(userType));
        }

        inflater = getActivity().getLayoutInflater();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_account_type, container, false);

        LinearLayout linearLayoutAccountTypes = (LinearLayout) view.findViewById(R.id.linearLayout_account_types);

        switch (mUserType) {
            case DBS:
                addAccountItem(linearLayoutAccountTypes, R.drawable.ic_dbs_logo, getString(R.string.account_type_dbs_title), onClickDBSListener , "DBS");
                break;
            case ANZ:
                addAccountItem(linearLayoutAccountTypes, R.drawable.ic_anz_logo, getString(R.string.account_type_anz_title), onClickANZListener , "ANZ");
                break;
            default:
                addAccountItem(linearLayoutAccountTypes, R.drawable.ic_dbs_logo, getString(R.string.account_type_dbs_title), onClickDBSListener , "DBS");
                addAccountItem(linearLayoutAccountTypes, R.drawable.ic_anz_logo, getString(R.string.account_type_anz_title), onClickANZListener , "ANZ");
                break;
        }


        return view;
    }



    private void addAccountItem(LinearLayout rootView, int imageID, String title, View.OnClickListener listener, String TAG) {

        View itemView = inflater.inflate(R.layout.register_account_type_item, null);

        ImageView imageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
        TextView textTitle = (TextView) itemView.findViewById(R.id.text_title);

        imageIcon.setBackgroundResource(imageID);
        textTitle.setText(title);
        itemView.setOnClickListener(listener);


        itemView.setTag(TAG);
        rootView.addView(itemView);

    }

    private View.OnClickListener onClickDBSListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onFragmentListener.onDBSNext();
        }
    };

    private View.OnClickListener onClickANZListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onFragmentListener.onANZNext();
        }
    };


}
