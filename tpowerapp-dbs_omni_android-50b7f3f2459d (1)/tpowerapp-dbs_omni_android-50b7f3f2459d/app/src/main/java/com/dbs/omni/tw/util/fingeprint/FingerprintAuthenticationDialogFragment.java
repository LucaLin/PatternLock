/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.dbs.omni.tw.util.fingeprint;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbs.omni.tw.R;

public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements FingerprintUiHelper.Callback {

    private Button mCancelButton;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private Activity mActivity;

    private OnFingerprinListener onFingerprinListener;

    public void setOnFingerprinListener(OnFingerprinListener listener) {
        onFingerprinListener = listener;
    }

    public interface OnFingerprinListener {
        void OnAuthenticated();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopFingerprint();
                dismiss();
            }
        });

        TextView textStatus = (TextView) v.findViewById(R.id.fingerprint_status);
        TextView textHint = (TextView) v.findViewById(R.id.fingerprint_hint);

        textStatus.setText(String.format(getString(R.string.fingerprint_description), getString(R.string.app_name)));
        textHint.setText(String.format(getString(R.string.fingerprint_hint), getString(R.string.app_name)));


//        textStatus.setText(Html.fromHtml("<b>Description here</b>Description here<a href=\"http://www.google.com\">link text</a>，xxx"));
//        textStatus.setMovementMethod(LinkMovementMethod.getInstance());
        mFingerprintUiHelper = new FingerprintUiHelper(
                mActivity.getSystemService(FingerprintManager.class),
                (TextView) v.findViewById(R.id.fingerprint_status), this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity =  getActivity();
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    @Override
    public void onAuthenticated() {
        if(onFingerprinListener != null) {
            onFingerprinListener.OnAuthenticated();
        }

        dismiss();
    }

    @Override
    public void onError() {
        stopFingerprint();
    }

    private void stopFingerprint() {
        mFingerprintUiHelper.stopListening();
    }
}
