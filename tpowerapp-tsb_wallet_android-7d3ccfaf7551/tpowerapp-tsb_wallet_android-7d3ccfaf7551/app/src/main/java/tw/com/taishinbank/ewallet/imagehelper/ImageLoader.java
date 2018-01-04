package tw.com.taishinbank.ewallet.imagehelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.BuildConfig;
import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.model.ContactImage;
import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;


/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";
    static final int FADE_IN_TIME = 200;

    ImageCache mImageCache;
    private Bitmap mLoadingBitmap;
    boolean mFadeInBitmap = true;
    boolean mPauseWork = false;
    final Object mPauseWorkLock = new Object();
    private int mImageSize;
    Resources mResources;
    private Activity context;

    public ImageLoader(Activity context, int imageSize) {
        mResources = context.getResources();
        mImageSize = imageSize;
        this.context = context;
        addImageCache(context.getFragmentManager(), 0.1f);
    }

    public int getImageSize() {
        return mImageSize;
    }

    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link ImageLoader#processBitmap(Object)} to define the processing logic). If the image is
     * found in the memory cache, it is set immediately, otherwise an {@link AsyncTask} will be
     * created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(String data, ImageView imageView) {
        if (data == null) {
            imageView.setImageBitmap(mLoadingBitmap);
            return;
        }

        Bitmap bitmap = null;

        if (mImageCache != null) {
            bitmap = mImageCache.getBitmapFromMemCache(data);
        }

        if (bitmap != null) {
            // Bitmap found in memory cache
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(data, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, this);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(data);
        }
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId Resource ID of loading image.
     */
    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
    }

    /**
     * Adds an {@link ImageCache} to this image loader.
     *
     * @param fragmentManager A FragmentManager to use to retain the cache over configuration
     *                        changes such as an orientation change.
     * @param memCacheSizePercent The cache size as a percent of available app memory.
     */
    public void addImageCache(FragmentManager fragmentManager, float memCacheSizePercent) {
        mImageCache = ImageCache.getInstance(fragmentManager, memCacheSizePercent);
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    /**
     * Subclasses should override this to define any processing or work that must happen to produce
     * the final bitmap. This will be executed in a background thread and be long running. For
     * example, you could resize a large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     *            {@link ImageLoader#loadImage(Object, ImageView)}
     * @return The processed bitmap
     */
    protected Bitmap processBitmap(Object data) {
        Bitmap bitmap = null;
        // This gets called in a background thread and passed the data from
        // ImageLoader.loadImage().
        try {
            // 嘗試從手機讀取暫存圖檔
            bitmap = DiskCache.get((String) data, mImageSize, mImageSize);
            if(bitmap != null){
                return bitmap;
            }
            boolean isExist = false;
            for (String menid: WalletApplication.GlobalHeadImageList) {
                if(data.toString().equals(menid)) {
                    isExist = true;
                    break;
                }
            }


            if(!isExist)
            {
                WalletApplication.GlobalHeadImageList.add(data.toString());
                // 記憶體與手機暫存都無資料，呼叫api重新load圖檔
                ContactImage contactImage = GeneralHttpUtil.downloadImages((String) data, context, TAG);
                if (contactImage != null && !TextUtils.isEmpty(contactImage.getPic())) {
                    bitmap = BitmapUtil.base64ToBitmap(contactImage.getPic());
                    if (bitmap != null) {
                        DiskCache.put((String) data, bitmap);
                        // 縮放圖片
                        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, mImageSize, mImageSize, true);
                        if (newBitmap != null && newBitmap != bitmap) {
                            bitmap.recycle();
                            return newBitmap;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     */
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.data;
                Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }



    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * {@link android.widget.AbsListView.OnScrollListener} to keep
     * scrolling smooth.
     * <p>
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed (for example during
     * {@link android.app.Activity#onPause()}), or there is a risk the
     * background thread will never finish.
     */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }


    /**
     * 從記憶體暫存中移除
     */
    public void removeFromMemCache(String data){
        mImageCache.removeBitmapFromCache(data);
    }

}