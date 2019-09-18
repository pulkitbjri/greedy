package com.example.greedyassign.Loader.Source;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemorySource implements CachingType{
    private int maxCacheSize = ((int)(Runtime.getRuntime().maxMemory() / 1024))/8;
    private LruCache<String, Bitmap> memoryCache;

    public MemorySource()
    {
        memoryCache = new LruCache<String, Bitmap>(maxCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
    }


    @Override
    public void addInMemory(Bitmap bitmap,String url){
        memoryCache.put(url , bitmap);
    }

    @Override
    public Bitmap getFromMemory(String url){
       return memoryCache.get(url);
    }
}
