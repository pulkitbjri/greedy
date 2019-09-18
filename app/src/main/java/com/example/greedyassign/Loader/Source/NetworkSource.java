package com.example.greedyassign.Loader.Source;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.greedyassign.Loader.GreedyImageLoader;
import com.example.greedyassign.Loader.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class NetworkSource {

    public Observable<Bitmap> downloadImage(final String imageUrl)
    {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter){
                try {
                    URL url = new URL(imageUrl);
                    InputStream input = url.openStream();
                    emitter.onNext(BitmapFactory.decodeStream(input));
                    emitter.onComplete();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    emitter.onError(new Throwable("Imvalid URL"));
                } catch (IOException e) {
                    emitter.onError(new Throwable("Download Error"));
                    e.printStackTrace();
                }

            }
        });
    }
}
