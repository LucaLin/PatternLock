package tw.com.taishinbank.ewallet.imagehelper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import tw.com.taishinbank.ewallet.BuildConfig;


/**
 * The actual AsyncTask that will asynchronously process the image.
 */
public class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
    private static final String TAG = "BitmapWorkerTask";
    Object data;
    private final WeakReference<ImageView> imageViewReference;
    private ImageLoader imageLoader;

    public BitmapWorkerTask(ImageView imageView, ImageLoader imageLoader) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.imageLoader = imageLoader;
    }

    /**
     * Background processing.
     */
    @Override
    protected Bitmap doInBackground(Object... params) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doInBackground - starting work");
        }

        data = params[0];
        final String dataString = String.valueOf(data);
        Bitmap bitmap = null;

        // Wait here if work is paused and the task is not cancelled
        synchronized (imageLoader.mPauseWorkLock) {
            while (imageLoader.mPauseWork && !isCancelled()) {
                try {
                    imageLoader.mPauseWorkLock.wait();
                } catch (InterruptedException e) {}
            }
        }

        // If the task has not been cancelled by another thread and the ImageView that was
        // originally bound to this task is still bound back to this task and our "exit early"
        // flag is not set, then call the main process method (as implemented by a subclass)
        if (!isCancelled() && getAttachedImageView() != null) {
            bitmap = imageLoader.processBitmap(params[0]);
        }

        // If the bitmap was processed and the image cache is available, then add the processed
        // bitmap to the cache for future use. Note we don't check if the task was cancelled
        // here, if it was, and the thread is still running, we may as well add the processed
        // bitmap to our cache as it might be used again in the future
        if (bitmap != null && imageLoader.mImageCache != null) {
            imageLoader.mImageCache.addBitmapToCache(dataString, bitmap);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doInBackground - finished work");
        }

        return bitmap;
    }

    /**
     * Once the image is processed, associates it to the imageView
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // if cancel was called on this task or the "exit early" flag is set then we're done
        if (isCancelled()) {
            bitmap = null;
        }

        final ImageView imageView = getAttachedImageView();
        if (bitmap != null && imageView != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onPostExecute - setting bitmap");
            }
            if(!bitmap.isRecycled()) {
                setImageBitmap(imageView, bitmap);
            }

        }
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        super.onCancelled(bitmap);
        synchronized (imageLoader.mPauseWorkLock) {
            imageLoader.mPauseWorkLock.notifyAll();
        }
    }

    /**
     * Returns the ImageView associated with this task as long as the ImageView's task still
     * points to this task as well. Returns null otherwise.
     */
    private ImageView getAttachedImageView() {
        final ImageView imageView = imageViewReference.get();
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (this == bitmapWorkerTask) {
            return imageView;
        }

        return null;
    }

    /**
     * Called when the processing is complete and the final bitmap should be set on the ImageView.
     *
     * @param imageView The ImageView to set the bitmap to.
     * @param bitmap The new bitmap to set.
     */
    private void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if (imageLoader.mFadeInBitmap) {
            // Transition drawable to fade from loading bitmap to final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(imageLoader.mResources, bitmap)
                    });
            imageView.setImageDrawable(td);
            td.startTransition(ImageLoader.FADE_IN_TIME);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
