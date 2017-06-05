package kr.ac.kumoh.ce.mobile.team25;

/**
 * Created by dlgus on 2017-04-24.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Created by 60974 on 2017-04-14.
 */

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {
    public LruBitmapCache(int maxSize){
        super(maxSize);
    }
    public LruBitmapCache(Context ctx){
        this(getCacheSize(ctx));
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes()*value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url,bitmap);
    }
    public static int getCacheSize(Context ctx){
        final DisplayMetrics displayMetrics=ctx.getResources().getDisplayMetrics();
        final int screenWidth=displayMetrics.widthPixels;
        final int screenHeight=displayMetrics.heightPixels;
        final int screenBytes=screenWidth*screenHeight*4;
        return screenBytes*3;
    }
}