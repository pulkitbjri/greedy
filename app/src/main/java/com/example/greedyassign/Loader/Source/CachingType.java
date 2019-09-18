package com.example.greedyassign.Loader.Source;

import android.graphics.Bitmap;

public interface CachingType {
    public void addInMemory(Bitmap bitmap, String url);
    public Bitmap getFromMemory(String url);
}
