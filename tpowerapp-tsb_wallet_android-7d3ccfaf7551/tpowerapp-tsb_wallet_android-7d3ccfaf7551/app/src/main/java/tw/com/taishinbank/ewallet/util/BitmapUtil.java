package tw.com.taishinbank.ewallet.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     *
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromDescriptor(
            FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    /**
     讀取指定路徑的檔案製作出符合寬度或高度的圖
     */
    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {

        File file = new File(filePath);
        if(file.exists()) {
            BitmapFactory.Options opt = new BitmapFactory.Options();

            opt.inJustDecodeBounds = true; //設定BitmapFactory.decodeStream不decode，只抓取原始圖片的長度和寬度
            BitmapFactory.decodeFile(filePath, opt);//抓取原始圖片的長度和寬度

            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opt.inPurgeable  = true;
            opt.inInputShareable = true;
            opt.inSampleSize = BitmapUtil.calculateInSampleSize(opt, reqWidth, reqHeight); //計算適合的縮放大小，避免OutOfMemory
            opt.inJustDecodeBounds = false;//設定BitmapFactory.decodeStream需decodeFile

            return BitmapFactory.decodeFile(filePath, opt);
        }
        return null;
    }

    public static void bitmapToFile(Bitmap bitmap, String filePath){
        FileOutputStream out = null;
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把bitmap轉成base64字串
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    /**
     * 把base64字串轉成bitmap
     */
    public static Bitmap base64ToBitmap(String b64) {
        try {
            byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 在bitmap中心裁切一個圓，圓未被覆蓋的部分是透明
     */
    public static Bitmap cropCircle(Bitmap bitmap, int rectSizeIn, int rectSizeOut, int radiusOut) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        return cropCircle(bitmap, rectSizeIn, rectSizeOut, radiusOut, paint, PorterDuff.Mode.SRC_IN);
    }

    /**
     * 在bitmap中心裁切一個圓，圓未被覆蓋的部分是指定的color，
     * 回傳前會將傳入的bitmap回收掉
     * @param bitmap 要被裁切的圖，切好後會被回收
     * @param color The new color (including alpha) to set in the paint.
     */
    public static Bitmap cropCircleWithColor(Bitmap bitmap, int rectSizeIn, int rectSizeOut, int radiusOut, int color) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        Bitmap croppedBitmap = cropCircle(bitmap, rectSizeIn, rectSizeOut, radiusOut, paint, PorterDuff.Mode.SRC_ATOP);
        bitmap.recycle();
        return croppedBitmap;
    }

    private static Bitmap cropCircle(Bitmap bitmap, int rectSizeIn, int rectSizeOut, int radiusOut, Paint paint, PorterDuff.Mode mode){
        Bitmap output = Bitmap.createBitmap(rectSizeOut,
                rectSizeOut, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        // 畫圓圈
        canvas.drawCircle(rectSizeOut/2, rectSizeOut/2, radiusOut, paint);
        // 設定模式是留下接下來bitmap被裁成圓形的部分
        paint.setXfermode(new PorterDuffXfermode(mode));

        final Rect rectSrc = new Rect(bitmap.getWidth()/2 - rectSizeIn/2, bitmap.getHeight()/2 - rectSizeIn/2,
                bitmap.getWidth()/2 + rectSizeIn/2, bitmap.getHeight()/2 + rectSizeIn/2);
        final Rect rectDst = new Rect(0, 0, output.getWidth(), output.getHeight());
        canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);
        return output;
    }

    /**
     * 讀取圖片的旋轉的角度
     *
     * @param path 檔案路徑
     *
     * @return 圖片的旋轉角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 從指定路徑下讀取圖片，並獲取其EXIF資訊
            ExifInterface exifInterface = new ExifInterface(path);
            // 獲取圖片的旋轉資訊
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 將圖片按照某個角度進行旋轉
     *
     * @param bm
     *            需要旋轉的圖片
     * @param degree
     *            旋轉角度
     * @return 旋轉後的圖片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根據旋轉角度，生成旋轉矩陣
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

}
