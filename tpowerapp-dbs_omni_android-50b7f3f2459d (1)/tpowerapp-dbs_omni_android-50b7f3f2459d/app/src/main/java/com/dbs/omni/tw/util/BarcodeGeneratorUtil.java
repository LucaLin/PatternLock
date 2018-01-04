package com.dbs.omni.tw.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by sherman-thinkpower on 2017/5/17.
 */

public class BarcodeGeneratorUtil extends ActivityBase {


    static public int MARGIN_AUTOMATIC = -1;

    // ----
    // Interface, class, implements and so on....
    // ----
    public static class GenerateQRCodeAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        ImageView imageView;
        String contentsToEncode;
        int imageWidth;
        int imageHeight;
        int marginSize;
        int color;
        int colorBack;
        boolean isQRCode = true;
        Activity activity;

        public GenerateQRCodeAsyncTask(Activity activity, ImageView imageView, String contentsToEncode, int imageWidth, int imageHeight, int marginSize, int color, int colorBack, boolean isQRCode) {
            this.imageView = imageView;
            this.contentsToEncode = contentsToEncode;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.marginSize = marginSize;
            this.color = color;
            this.colorBack = colorBack;
            this.isQRCode = isQRCode;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
//            ((ActivityBase)activity).showProgressLoading();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return generateBitmap(contentsToEncode, imageWidth, imageHeight, marginSize, color, colorBack);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
//            ((ActivityBase)activity).dismissProgressLoading();
            if (bitmap == null) {
                ((ActivityBase)activity).showAlertDialog(activity.getString(R.string.error_barcode_generate));
                return;
            }
            if(isQRCode)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageBitmap(bitmap);
        }

        protected Bitmap generateBitmap(@NonNull String contentsToEncode,
                                        int imageWidth, int imageHeight,
                                        int marginSize, int color, int colorBack)
                throws WriterException, IllegalStateException {

            Map<EncodeHintType, Object> hints = null;
            if (marginSize != MARGIN_AUTOMATIC) {
                hints = new EnumMap<>(EncodeHintType.class);
                // We want to generate with a custom margin size
                hints.put(EncodeHintType.MARGIN, marginSize);
            }

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix result;
            if(isQRCode)
                result = writer.encode(contentsToEncode, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);
            else
                result = writer.encode(contentsToEncode, BarcodeFormat.CODE_128, imageWidth, imageHeight, hints);

            final int width = result.getWidth();
            final int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? color : colorBack;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }
    }
}
