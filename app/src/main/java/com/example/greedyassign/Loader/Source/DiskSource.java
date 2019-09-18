package com.example.greedyassign.Loader.Source;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.Environment.isExternalStorageRemovable;
import static com.example.greedyassign.Loader.Utils.md5;

public class DiskSource implements CachingType {
    private DiskLruCache diskLruCache;
    private final Object diskCacheLock = new Object();
    private boolean diskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private Context context;

    public DiskSource(Context context)
    {
        this.context = context;
        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);

    }


    @Override
    public void addInMemory(Bitmap bitmap, String url) {
        boolean isOk = false;
        synchronized (diskCacheLock) {

            if (diskLruCache != null) {
                String cacheKey = md5(url.toString());
                try {
                    DiskLruCache.Editor editor = diskLruCache.edit(cacheKey);
                    if (editor != null) {
                        OutputStream out = editor.newOutputStream(0);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 95, out);
                        out.close();
                        editor.commit();

                    }
                } catch (IOException e) {
                    isOk = false;
                    Log.d("", "write disk lru cache error:" + e.toString(), e);
                }
            } else {
                Log.w("", "read disk lru cache is null");
            }
        }

    }

    @Override
    public Bitmap getFromMemory(String url) {
        String cacheKey = md5(url.toString());

        synchronized (diskCacheLock) {
            // Wait while disk cache is started from background thread
            while (diskCacheStarting) {
                try {
                    diskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (diskLruCache != null) {
                try {
                    DiskLruCache.Snapshot snapshot = diskLruCache.get(cacheKey);
                    if (snapshot==null)
                        return null;
                    InputStream in = snapshot.getInputStream(0);
                    Bitmap bm = BitmapFactory.decodeStream(in);
                    in.close();
                    snapshot.close();

                    return bm;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null; 
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (diskCacheLock) {
                File cacheDir = params[0];
                try {
                    diskLruCache = DiskLruCache.open(cacheDir, 1,1,DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                diskCacheStarting = false; // Finished initialization
                diskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }
}
