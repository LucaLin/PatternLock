package com.dbs.omni.tw.controller.setting.touchid;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.fingeprint.FingerprintUtil;

public class TouchIDActivity extends ActivityBase {

    private Switch touchIDSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_id);

        setCenterTitle(R.string.personal_service_list_touch_id);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHeadHide(false);

        touchIDSwitch = (Switch) findViewById(R.id.switchTouchID);
        if(PreferenceUtil.getTouchIDStatus(this)) {
            touchIDSwitch.setChecked(true);
//            deteceTouchID(false);
            FingerprintUtil.detectFingerprint(TouchIDActivity.this, touchIDSwitch, false, null);
        }

        touchIDSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
//                    deteceTouchID(true);
                    FingerprintUtil.detectFingerprint(TouchIDActivity.this, touchIDSwitch, true, null);
                } else {
                    PreferenceUtil.setTouchIDStatus(TouchIDActivity.this, false);
                }
            }
        });
    }


//    private void deteceTouchID(boolean isShowAlert) {
//        FingerprintUtil.detectFingerprint(this, isShowAlert, new FingerprintCore.OnDetectFingerprintListener() {
//            @Override
//            public void OnIsSupport() {
//                if(!PreferenceUtil.getTouchIDStatus(TouchIDActivity.this)) {
//
//                    if(PreferenceUtil.getSaveUserCode(TouchIDActivity.this)) {
//                        PreferenceUtil.setTouchIDStatus(TouchIDActivity.this, true);
//                    } else {
//                        showAlertDialog("同步幫您啟用「指紋辨識」以及「記住您的帳號」功能，您可以指紋辨識取代使用者密碼進行登入。", R.string.button_confirm, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                PreferenceUtil.setSaveUserCode(TouchIDActivity.this, true);
//                                PreferenceUtil.setTouchIDStatus(TouchIDActivity.this, true);
//                                dialog.dismiss();
//                            }
//                        }, false);
//                    }
//                }
//            }
//
//            @Override
//            public void OnIsClose() {
//                PreferenceUtil.setTouchIDStatus(TouchIDActivity.this, false);
//                touchIDSwitch.setChecked(false);
//            }
//
//            @Override
//            public void OnIsNotSupport() {
//            }
//        });
//
//    }
}
