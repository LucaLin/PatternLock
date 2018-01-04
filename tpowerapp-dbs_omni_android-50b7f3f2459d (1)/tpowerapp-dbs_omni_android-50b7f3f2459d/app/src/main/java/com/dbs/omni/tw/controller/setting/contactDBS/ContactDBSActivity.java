package com.dbs.omni.tw.controller.setting.contactDBS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;

public class ContactDBSActivity extends ActivityBase {

    private TextView txtContact1,txtContact2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_dbs);

        setCenterTitle(R.string.personal_service_list_contact_dbs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        txtContact1 = (TextView)findViewById(R.id.txtContact1);
        txtContact2 = (TextView)findViewById(R.id.txtContact2);

        txtContact1.setOnClickListener(onClickListener);
        txtContact2.setOnClickListener(onClickListener);
    }

    private Button.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strPhoneNumber = "";

            if(v.getId() == R.id.txtContact1){
                strPhoneNumber = txtContact1.getText().toString();
            }else if(v.getId() == R.id.txtContact2){
                strPhoneNumber = txtContact2.getText().toString();
            }

            Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+strPhoneNumber));
            startActivity(myIntentDial);
        }
    };
}
