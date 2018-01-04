package com.dbs.omni.tw.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by siang on 2017/5/18.
 */

public class BlurUtil {

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(c);
        return bitmap;
    }

    public static class RSBlurProcessor {

        private RenderScript rs;

        private static final boolean IS_BLUR_SUPPORTED = Build.VERSION.SDK_INT >= 17;
        private static final int MAX_RADIUS = 25;

        public RSBlurProcessor(RenderScript rs) {
            this.rs = rs;
        }

        @Nullable
        public Bitmap blur(@NonNull Bitmap bitmap, float radius, int repeat) {

            if (!IS_BLUR_SUPPORTED) {
                return null;
            }

            if (radius > MAX_RADIUS) {
                radius = MAX_RADIUS;
            }

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // Create allocation type
            Type bitmapType = new Type.Builder(rs, Element.RGBA_8888(rs))
                    .setX(width)
                    .setY(height)
                    .setMipmaps(false) // We are using MipmapControl.MIPMAP_NONE
                    .create();

            // Create allocation
            Allocation allocation = Allocation.createTyped(rs, bitmapType);

            // Create blur script
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setRadius(radius);

            // Copy data to allocation
            allocation.copyFrom(bitmap);

            // set blur script input
            blurScript.setInput(allocation);

            // invoke the script to blur
            blurScript.forEach(allocation);

            // Repeat the blur for extra effect
            for (int i=0; i<repeat; i++) {
                blurScript.forEach(allocation);
            }

            // copy data back to the bitmap
            allocation.copyTo(bitmap);

            // release memory
            allocation.destroy();
            blurScript.destroy();
            allocation = null;
            blurScript = null;

            return bitmap;
        }
    }


    public static Bitmap blurBitmap(Bitmap bitmap, float radius, Context context) {
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);

        Type t = allocation.getType();

        //Create allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t);

        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius);
        //Set input for script
        blurScript.setInput(allocation);
        //Call script for output allocation
        blurScript.forEach(blurredAllocation);

        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);

        //Destroy everything to free memory
        allocation.destroy();
        blurredAllocation.destroy();
        blurScript.destroy();
        t.destroy();
        rs.destroy();
        return bitmap;
    }
}
