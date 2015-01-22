package com.shenma.tvlauncher.utils;

import com.android.volley.toolbox.DiskBasedCache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Image Cache Utils
 * @author drowtram
 *
 */
public class LruCacheUtils {

	private LruCache<String, Bitmap> mMemoryCache;
	private int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory() / 1024);
	private static LruCacheUtils mCacheUtils;
	
	public static LruCacheUtils getInstance() {
		if(mCacheUtils == null)
			mCacheUtils = new LruCacheUtils();
		return mCacheUtils;
	}
	
	private LruCacheUtils() {
		if (mMemoryCache == null) {
			mMemoryCache = new LruCache<String, Bitmap>(MAXMEMONRY / 8){
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
				}
				@Override
				protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
					Logger.v("zhouchuan", "hard cache is full , push to soft cache "+key);
				}
			};
		}
	}
	/**
	 * 清除缓存
	 */
	public void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                Logger.d("zhouchuan", "mMemoryCache.size() " + mMemoryCache.size());
                mMemoryCache.evictAll();
                Logger.d("zhouchuan", "mMemoryCache.size()" + mMemoryCache.size());
            }
            mMemoryCache = null;
        }
    }

	/**
	 * 添加图片到缓存
	 * @param key 位图key标示符
	 * @param bitmap 位图对象
	 */
    public synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (mMemoryCache.get(key) == null) {
            if (key != null && bitmap != null)
                mMemoryCache.put(key, bitmap);
        } else
            Logger.w("zhouchuan", "the res is aready exits");
    }

    /**
     * 从缓存中取得图片
     * @param key 
     * @return
     */
    public synchronized Bitmap getBitmapFromMemCache(String key) {
        Bitmap bm = mMemoryCache.get(key);
        if (key != null) {
            return bm;
        }
        return null;
    }

    /**
     * 移除缓存
     * 
     * @param key
     */
    public synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                Bitmap bm = mMemoryCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
    
    /**
     * 清空缓存
     */
    public void clearAllImageCache() {
		 if (mMemoryCache != null && mMemoryCache.size() > 0) {
	         Logger.d("zhouchuan", "before mMemoryCache.size() " + mMemoryCache.size() + "KB");
	         mMemoryCache.evictAll();
	         Logger.d("zhouchuan", "after mMemoryCache.size()" + mMemoryCache.size() + "KB");
	     }
    }
}
