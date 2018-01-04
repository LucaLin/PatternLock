package tw.com.taishinbank.ewallet.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.view.ScaleImageView;

public class CropImageActivity extends ActivityBase implements View.OnClickListener {

    private ScaleImageView imagePic;
    private static final String TAG = "CropImageActivity";
    public final static String EXTRA_IMAGE = "extra_image";
    public final static String EXTRA_MEM_NO = "extra_memNO";
    private int imagePhotoSize;
    private int imagePhotoCropCircleRadius;
    private final int cropCircleDisplayScaleRate = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        imagePic = (ScaleImageView) findViewById(R.id.image_pic);
        imagePic.setDrawingCacheEnabled(true);
        ImageView imageMask = (ImageView) findViewById(R.id.image_mask);

        // 取得圖的大小
        Bitmap bitmap = ((BitmapDrawable) imagePic.getDrawable()).getBitmap();
        int maskHeight = bitmap.getHeight();
        int maskWidth = bitmap.getWidth();
        bitmap.recycle();
        bitmap = null;

        // 取得相片資料
        Uri uri = getIntent().getData();
        if(uri != null) {
            try {
                // 讀取圖片
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // 判斷相片畫素是否超過限制大小
                if(bitmap != null){
                    // 預設大小在這寫死為2048
                    int maxSize = 2048;
                    // 取得螢幕解析度
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    // 如果螢幕解析度
                    if(metrics.heightPixels > maxSize || metrics.widthPixels > maxSize) {
                        maxSize = (metrics.heightPixels > metrics.widthPixels) ? metrics.heightPixels : metrics.widthPixels;
                    }
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();
                    if(bitmapWidth > maxSize || bitmapHeight > maxSize){
                        if(bitmapHeight > bitmapWidth) {
                            bitmapWidth *= (float) maxSize / bitmapHeight;
                            bitmapHeight *= (float) maxSize / bitmapHeight;
                        }else{
                            bitmapHeight *= (float) maxSize / bitmapWidth;
                            bitmapWidth *= (float) maxSize / bitmapWidth;
                        }
                        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                        if (newBitmap != null && newBitmap != bitmap) {
                            bitmap.recycle();
                            bitmap = newBitmap;
                        }
                    }
                }

                // 確認相片翻轉角度
                int degree = BitmapUtil.getBitmapDegree(uri.getPath());
                bitmap = BitmapUtil.rotateBitmapByDegree(bitmap, degree);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imagePic.setImageBitmap(bitmap);

        imagePhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        imagePhotoCropCircleRadius = getResources().getDimensionPixelSize(R.dimen.photo_crop_circle_radius);

        Bitmap outputMask = generateMask(maskWidth, maskHeight, maskWidth / 2, maskHeight / 2, imagePhotoCropCircleRadius * cropCircleDisplayScaleRate);
        imageMask.setImageBitmap(outputMask);

        Button buttonOk = (Button) findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(this);
        Button buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(this);
    }

    /**
     * 產生一個半透明的遮罩，上面有個全透明的圓圈
     */
    public Bitmap generateMask(int width, int height, int centerX, int centerY, int radius) {
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        // 畫半透明背景
        canvas.drawARGB(128, 0, 0, 0);
        // 留下被裁切掉圓圈範圍的半透明圖
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawCircle(centerX, centerY, radius, paint);
        return output;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.button_ok){
            Bitmap bitmap = imagePic.getDrawingCache(true);

            Bitmap cropBitmap = BitmapUtil.cropCircleWithColor(bitmap, imagePhotoSize * cropCircleDisplayScaleRate, imagePhotoSize * ContactUtil.PhotoSaveSizeScaleRate, imagePhotoCropCircleRadius * ContactUtil.PhotoSaveSizeScaleRate,
                    getResources().getColor(R.color.crop_image_background));

            Intent intent = getIntent();
            // save to file and pass the file path
            String folderPath = ContactUtil.FolderPath;
            ContactUtil.checkFolderExists();
            String memNO = getIntent().getStringExtra(EXTRA_MEM_NO);
            String filePath = folderPath + File.separator + memNO + ".png";
            BitmapUtil.bitmapToFile(cropBitmap, filePath);

            intent.putExtra(EXTRA_IMAGE, filePath);
            setResult(RESULT_OK, intent);
            finish();

        }else if(viewId == R.id.button_cancel){
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
