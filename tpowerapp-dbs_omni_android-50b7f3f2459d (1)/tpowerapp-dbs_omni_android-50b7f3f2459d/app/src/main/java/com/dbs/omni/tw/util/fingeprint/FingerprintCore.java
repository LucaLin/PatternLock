package com.dbs.omni.tw.util.fingeprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by siang on 2017/6/2.
 */


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintCore {

    private static final String DIALOG_FRAGMENT_TAG = "TouchID_Fragment";
    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "DBS_APP";
    private Cipher cipher;

    private Activity mActivity;

    private boolean isShowAlert = false;

    private enum FingerprintStatus {
        Pass,
        NotDevice,
        NotAuthenticationPermission,
        NotRegisterFingerprint,
        NotEnableLockScreen
    }

    private FingerprintAuthenticationDialogFragment.OnFingerprinListener onFingerprinListener;

    private OnDetectFingerprintListener onDetectFingerprintListener;

    public void setOnDetectFingerprintListener (OnDetectFingerprintListener listener) {
        onDetectFingerprintListener = listener;
    }

    public interface OnDetectFingerprintListener {
        void OnIsSupport();
        void OnIsClose();
        void OnIsNotSupport();

    }

//    private boolean isCheckTouchID = false;

    /**
     * 執行指紋辨識
     * @param activity
     */
    public FingerprintCore(Activity activity, FingerprintAuthenticationDialogFragment.OnFingerprinListener listener) {
        this.mActivity = activity;
        if (detectFingerprintStatus().equals(FingerprintStatus.Pass)) {
            onFingerprinListener = listener;
            setFingerprints();
        }
    }

    /**
     *  檢查是指紋辨識狀態
     * @param activity
     * @param isShowAlert
     * @param listener
     */
    public FingerprintCore(Activity activity, boolean isShowAlert, OnDetectFingerprintListener listener) {
        onDetectFingerprintListener = listener;
        this.mActivity = activity;
        this.isShowAlert = isShowAlert;

        onlyDetectFingerprint();
    }

    public void onlyDetectFingerprint() {

        FingerprintStatus status = detectFingerprintStatus();

        switch (status) {
            case NotEnableLockScreen:
                showNotRegisterFingerprintAlert(R.string.fingerprint_not_enable_lock_screen, new Intent(Settings.ACTION_SECURITY_SETTINGS));
                if(onDetectFingerprintListener != null) {
                    onDetectFingerprintListener.OnIsClose();
                }
                break;
            case NotDevice:
                showNotRegisterFingerprintAlert(R.string.fingerprint_not_device, new Intent(Settings.ACTION_SECURITY_SETTINGS));
                if(onDetectFingerprintListener != null) {
                    onDetectFingerprintListener.OnIsNotSupport();
                }
                break;
            case NotAuthenticationPermission:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                showNotRegisterFingerprintAlert(R.string.fingerprint_not_authentication_permission,intent);
                if(onDetectFingerprintListener != null) {
                    onDetectFingerprintListener.OnIsClose();
                }
                break;
            case NotRegisterFingerprint:
                showNotRegisterFingerprintAlert(R.string.fingerprint_not_register_fingerprint, new Intent(Settings.ACTION_SECURITY_SETTINGS));
                if(onDetectFingerprintListener != null) {
                    onDetectFingerprintListener.OnIsClose();
                }
                break;
            default:
                //Pass
                if(onDetectFingerprintListener != null) {
                    onDetectFingerprintListener.OnIsSupport();
                }
                break;
        }
    }

    private FingerprintStatus detectFingerprintStatus() {
        // 初始 Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) mActivity.getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) mActivity.getSystemService(FINGERPRINT_SERVICE);

        //檢查有無打開權限
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.USE_FINGERPRINT) != PERMISSION_GRANTED) {
            /**
             * 沒有打開權限。
             * "Fingerprint authentication permission not enabled"
             */
            return FingerprintStatus.NotAuthenticationPermission;
        }else{
        // 檢查手機有沒有指紋硬體
            if(!fingerprintManager.isHardwareDetected()){
                /**
                 * 設備不包含指紋硬體，將顯示錯誤信息。
                 * "Your Device does not have a Fingerprint Sensor"
                 */
                return FingerprintStatus.NotDevice;
            }else {

                // 檢查手機系統中有無註冊指紋
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    /**
                     * 沒有註冊任何指紋
                     * "Register at least one fingerprint in Settings"
                     */
    //                    textView.setText("Register at least one fingerprint in Settings");
                    return FingerprintStatus.NotRegisterFingerprint;

                } else {
                    //檢查有沒有開啟lock screen security （此專案不需要判斷）
                    boolean isLockScreenSrecurity;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        isLockScreenSrecurity = keyguardManager.isDeviceSecure();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        isLockScreenSrecurity = keyguardManager.isKeyguardSecure();
                    } else {
                        isLockScreenSrecurity = false;
                    }


                    if (!isLockScreenSrecurity) {
    //                        textView.setText("Lock screen security not enabled in Settings");
                        return FingerprintStatus.NotEnableLockScreen;
                    } else {
                        return FingerprintStatus.Pass;
                    }
                }
            }
        }
    }

    private void setFingerprints() {
        generateKey();


        if (cipherInit()) {
            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
            showTouchDialog(cryptoObject);
//                            F helper = new FingerprintHandler(mActivity);
//                            helper.startAuth(fingerprintManager, cryptoObject, authenticationCallback);
        }
    }

    private void showTouchDialog(FingerprintManager.CryptoObject cryptoObject) {
        FingerprintAuthenticationDialogFragment fragment
                = new FingerprintAuthenticationDialogFragment();
        fragment.setCryptoObject(cryptoObject);
        fragment.setOnFingerprinListener(onFingerprinListener);
        fragment.show(mActivity.getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void showNotRegisterFingerprintAlert(int massage, final Intent gotoSetting) {
        if(!this.isShowAlert) {
            return;
        }
//
//        ((ActivityBase) mActivity).showAlertDialog(mActivity.getString(massage), android.R.string.ok, android.R.string.cancel,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mActivity.startActivity(gotoSetting); //但有些手機配置不一樣 不一定有指紋的項目
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }, true);

        ((ActivityBase) mActivity).showAlertDialog(mActivity.getString(massage), android.R.string.ok,
                 new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }
}
