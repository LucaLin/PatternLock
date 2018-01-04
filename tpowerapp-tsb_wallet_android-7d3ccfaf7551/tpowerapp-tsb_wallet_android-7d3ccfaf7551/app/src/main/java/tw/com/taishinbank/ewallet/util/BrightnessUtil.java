package tw.com.taishinbank.ewallet.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import tw.com.taishinbank.ewallet.controller.ActivityBase;

/**
 * Created by Siang on 7/11/16.
 */
public class BrightnessUtil {

    //--- 畫面調整使用的變數 ---
    private int originalBrightness;
    private boolean isAutoForOriginaBrightness = false;
    private boolean isMaxBrightness = false;
    private Context context;
    //--- ---------------- ---

    public BrightnessUtil(Context context) {
        this.context = context;
    }

    /**
     * 調整畫面亮度至最亮，以及調整回原本設定
     * isMaxBrightness：用來判斷現在是否有執行“調整畫面亮度至最亮”
     */

    public void toMaxBrightness(){
        // 判斷是否有權限可以調整亮度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  {
            if (Settings.System.canWrite(context)) {
                setMaxBrightness();
            } else {
                ((ActivityBase) context).showAlertDialog("沒有授權調整畫面亮度，是否要進入設定權限？", android.R.string.ok, android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((ActivityBase) context).startActivity(intent);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, false);


            }
        } else {
            setMaxBrightness();
        }
    }

    private void setMaxBrightness() {

        try {
            //判斷亮度是否為自動
            isAutoForOriginaBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

            if(isAutoForOriginaBrightness) {
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }

            // 取得螢幕亮度
            originalBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);

            // 亮度範圍為 0 - 255
            int brightness = 255;
            brightness = Math.min(brightness, 255);
            brightness = Math.max(brightness, 0);
            // 設定螢幕亮度
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
            isMaxBrightness = true;
        }
        catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void toOriginalBrightness() {
        if(!isMaxBrightness)
            return;

        try {

            if(isAutoForOriginaBrightness) {
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            }

            // 亮度範圍為 0 - 255
            int brightness = originalBrightness;
            brightness = Math.min(brightness, 255);
            brightness = Math.max(brightness, 0);
            // 設定螢幕亮度
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
            isMaxBrightness = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
