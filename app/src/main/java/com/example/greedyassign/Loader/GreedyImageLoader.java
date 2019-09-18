package com.example.greedyassign.Loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.example.greedyassign.Loader.Source.CachingType;
import com.example.greedyassign.Loader.Source.DataSource;
import com.example.greedyassign.Loader.Source.DiskSource;
import com.example.greedyassign.Loader.Source.MemorySource;
import com.example.greedyassign.Loader.Source.NetworkSource;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GreedyImageLoader {

    public static final int DISK=1,MEMORY=2;
    public static int screenHeight=0,screenWidth=0;
    private HashMap<ImageView,String> imageViewMap = new HashMap<>();
    private static GreedyImageLoader INSTANCE;

    DataSource dataSource;
    CachingType cachingType;


    private GreedyImageLoader(Context context)
    {
        dataSource=new DataSource(new MemorySource(),new DiskSource(context),new NetworkSource());
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        cachingType=dataSource.getMemoryDataSource();

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    public synchronized static GreedyImageLoader with(Context context)
    {
        if (INSTANCE ==null)
        {
            INSTANCE=new GreedyImageLoader(context);
        }
        return INSTANCE;
    }

    public void load(final ImageView imageView, final String imageUrl) {

        if(imageView == null) {
            return;
        }

        if(imageUrl == null && imageUrl.isEmpty()) {
            return;
        }
        imageView.setImageResource(0);
        imageViewMap.put(imageView , imageUrl);

        Bitmap bitmap = checkImageInCache(imageUrl);
        if (bitmap!=null)
        {
            loadImageIntoImageView(imageView, bitmap, imageUrl);
        }
        else{
            final ImageRequest imageRequest=new ImageRequest(imageUrl, imageView);
            dataSource.getNetworkDataSource().downloadImage(imageRequest.imageUrl)
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            if(isImageViewReused(imageRequest))
                                return;

                            cachingType.addInMemory(bitmap,imageRequest.imageUrl);

                            if(!isImageViewReused(imageRequest))
                                loadImageIntoImageView(imageRequest.imageView, bitmap, imageRequest.imageUrl);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }


    private void loadImageIntoImageView(ImageView imageView, Bitmap bitmap, String imageUrl) {
        if(!isImageViewReused(new ImageRequest(imageUrl, imageView)))
            imageView.setImageBitmap(bitmap);

    }

    private boolean isImageViewReused(ImageRequest imageRequest) {
        String tag = imageViewMap.get(imageRequest.imageView);
        return tag == null || !tag.equals(imageRequest.imageUrl);
    }

    private synchronized Bitmap checkImageInCache(String imageUrl) {
        return cachingType.getFromMemory(imageUrl);
    }

    public GreedyImageLoader setCachingType(int type) {
        if (type==DISK)
            cachingType=dataSource.getDiskDataSource();
        else
            cachingType=dataSource.getMemoryDataSource();

        return INSTANCE;
    }

    private class ImageRequest {
        private final String imageUrl;
        private final ImageView imageView;

        public ImageRequest(String imageUrl, ImageView imageView) {
            this.imageUrl = imageUrl;
            this.imageView = imageView;
        }
    }
}
