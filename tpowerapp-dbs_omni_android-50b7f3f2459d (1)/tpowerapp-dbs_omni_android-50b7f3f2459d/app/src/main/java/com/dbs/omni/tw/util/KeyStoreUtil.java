package com.dbs.omni.tw.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Created by siang on 2017/6/7.
 */

public class KeyStoreUtil {
    private static final String TAG = "KeyStoreUtil";

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final String ProviderMode = "AndroidOpenSSL";

    public static final String SAVE_TAG = "OMMI_SAVE_TAG";

    private static KeyStore keyStore;
//    private ArrayList<String> keyAliases;

    private Context context;


    public KeyStoreUtil(Context context) {
        this.context = context;
        if(keyStore == null) {
            try {
                keyStore = KeyStore.getInstance(AndroidKeyStore);
                keyStore.load(null);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> refreshKeys() {
        ArrayList<String> keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
            return keyAliases;
        }
        catch(Exception e) {
            return null;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void createNewKeys(String alias) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
//
//                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
//                        .setAlias(alias)
//                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
//                        .setSerialNumber(BigInteger.ONE)
//                        .setStartDate(start.getTime())
//                        .setEndDate(end.getTime())
//                        .build();
//                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
//                generator.initialize(spec);
//
//                KeyPair keyPair = generator.generateKeyPair();
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias + "O=Android Authority"))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore);
                kpg.initialize(spec);
                kpg.generateKeyPair();
            }
        } catch (Exception e) {
//            Toast.makeText(context, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG).show();
            Log.d(TAG, Log.getStackTraceString(e));
        }
//        checkAlias();
    }

    private void checkAlias(String alias) {
        ArrayList<String> aliasList = refreshKeys();
        if(aliasList != null) {
            if(aliasList.indexOf(alias) == -1) {
                createNewKeys(alias);
            } else {
                return;
            }
        }


    }

    public String encryptString(String alias, String initialText ) {
        checkAlias(alias);

        if(TextUtils.isEmpty(initialText)) {
            return null;
        }

        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher input = Cipher.getInstance(RSA_MODE, ProviderMode);
            input.init(Cipher.ENCRYPT_MODE, publicKey);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(initialText.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();

            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
            return null;
        }
    }

    public String decryptString(String alias, String encryptedText) {
        checkAlias(alias);

        if(TextUtils.isEmpty(encryptedText)) {
            return null;
        }

        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);

            Cipher output = Cipher.getInstance(RSA_MODE);

            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            String cipherText = encryptedText;
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            return new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }


}
