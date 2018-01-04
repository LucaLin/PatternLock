package tw.com.taishinbank.ewallet.controller.sv;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.sv.ShareToOption;
import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

/**
 * 產生條碼，分享或是再導到掃描條碼 - Oster
 */
public class QRCodeGeneratorActivity extends ActivityBase {

    /**
     * Allow the zxing engine use the default argument for the margin variable
     */
    static public int MARGIN_AUTOMATIC = -1;
    private final String qrStringFormat = "TSB-MemberNumber:%s-MemberNickName:%s";

    private TextView txtNickName;
    private Button btnLaunchQRCodeScanner;
    private ImageView imgQRCode;
    private ImageView imgPhoto;
    private AlertDialog dlgshareQRCode;

    private ArrayAdapter<ShareToOption> shareToOptionArrayAdapter;
    private Bitmap bitmap;

    @Override
    @SuppressWarnings("null")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_qrcode_generator);
        setCenterTitle(R.string.my_qr_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set view hold
        btnLaunchQRCodeScanner = (Button) findViewById(R.id.btn_next);
        imgQRCode = (ImageView) findViewById(R.id.img_qr_code);
        txtNickName = (TextView) findViewById(R.id.txt_nickname);

        // Set view value
        txtNickName.setText(PreferenceUtil.getNickname(this));

        // Set Action
        btnLaunchQRCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchScanner();
            }
        });

        imgPhoto = (ImageView) findViewById(R.id.pickImage_view);

        ImageLoader imageLoader = new ImageLoader(this,  getResources().getDimensionPixelSize(R.dimen.large_photo_size));
        imageLoader.loadImage(String.valueOf(PreferenceUtil.getMemNO(this)), imgPhoto);

        //Generate QR Code
        //String stringToGen = "This is a code generate example....";
        GenerateQRCodeAsyncTask task = new GenerateQRCodeAsyncTask(
                String.format(qrStringFormat, PreferenceUtil.getMemNO(this),PreferenceUtil.getNickname(this)),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190, this.getResources().getDisplayMetrics()),  //Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 190, this.getResources().getDisplayMetrics()),  //Height
                MARGIN_AUTOMATIC,
                Color.BLACK, Color.WHITE);
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_qr_code_generator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 如果是全選
        if (id == R.id.action_share_qr_code) {
            popupShareDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ----
    // public
    // ----
    public void launchScanner() {
        String[] permissionsForScan = {Manifest.permission.CAMERA};
        if (!PermissionUtil.needGrantRuntimePermission(QRCodeGeneratorActivity.this, permissionsForScan,
                PermissionUtil.PERMISSION_REQUEST_CODE_SCAN)) {
            Intent intent = new Intent();
            intent.setClass(this, QRCodeScannerActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_SCAN) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                launchScanner();
            }else{
                PermissionUtil.showNeedPermissionDialog(this, permissions, grantResults);
            }
        }
    }

    // ----
    // User Action
    // ----

    protected void shareTo(int which) {
        ShareToOption shareToOption = shareToOptionArrayAdapter.getItem(which);
//        Toast.makeText(this, shareToOption.getTitle(), Toast.LENGTH_LONG).show();

        //Alert the barcode is not generated...
        Bitmap bitmapQRCode = ((BitmapDrawable)imgQRCode.getDrawable()).getBitmap();
        if (bitmapQRCode == null) {
            showAlert(R.string.error_qr_code_generate);
            return;
        }

        // Save to file and pass the parameter 'file path'
        String folderPath = ContactUtil.FolderPath;
        ContactUtil.checkFolderExists();
        String filePath = folderPath + File.separator + GlobalConst.FILE_NAME_QR_CODE + ".png";
        BitmapUtil.bitmapToFile(bitmapQRCode, filePath);

        File filePathObj = new File(filePath);
        if (!filePathObj.exists()) {
            showAlert(R.string.error_qr_code_save);
            return;
        }

        // Determine Intent Type and parameters...
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        String appName = "";
        switch (shareToOption.getId()) {
            case (ShareToOption.SHARE_TO_FB_MESSENGER):
                shareIntent.setPackage("com.facebook.orca");
                appName = "FB Messenger";

                break;

            case (ShareToOption.SHARE_TO_LINE):
                shareIntent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
                appName = "Line Messenger";

                break;

            case (ShareToOption.SHARE_TO_BY_EMAIL):
                shareIntent.setAction(Intent.ACTION_SENDTO);
                shareIntent.setData(Uri.parse("mailto:"));

                break;

            default:
        }


        if (shareIntent != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePathObj));

            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share)));
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.share))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    protected void popupShareDialog() {
        if(dlgshareQRCode == null){
            dlgshareQRCode = createShareQRCodeDialog();
        }
        dlgshareQRCode.show();
    }

    // ----
    // Private
    // ----

    protected AlertDialog createShareQRCodeDialog() {
        shareToOptionArrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_LINE,         getResources().getString(R.string.sv_share_to_line)));
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_FB_MESSENGER, getResources().getString(R.string.sv_share_to_fb_messenger)));
        shareToOptionArrayAdapter.add(
                new ShareToOption(ShareToOption.SHARE_TO_BY_EMAIL,     getResources().getString(R.string.sv_share_to_by_email)));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setAdapter(shareToOptionArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareTo(which);
                        dlgshareQRCode.dismiss();
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    protected void showAlert(int resId) {
        new AlertDialog.Builder(this)
                .setMessage(resId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        return;
    }

    protected void onClickWhatsApp(View view) {



    }

    // ----
    // Interface, class, implements and so on....
    // ----
    class GenerateQRCodeAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        String contentsToEncode;
        int imageWidth;
        int imageHeight;
        int marginSize;
        int color;
        int colorBack;

        public GenerateQRCodeAsyncTask(String contentsToEncode, int imageWidth, int imageHeight, int marginSize, int color, int colorBack) {
            this.contentsToEncode = contentsToEncode;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.marginSize = marginSize;
            this.color = color;
            this.colorBack = colorBack;
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
            if (bitmap == null) {
                showAlert(R.string.error_qr_code_generate);
                return;
            }

            imgQRCode.setImageBitmap(bitmap);
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
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            }
            else
            {
                hints = new EnumMap<>(EncodeHintType.class);
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            }


            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix result = writer.encode(contentsToEncode, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);

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
