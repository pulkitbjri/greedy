package com.example.greedyassign;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greedyassign.Loader.GreedyImageLoader;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.Order;
import com.kc.unsplash.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Photo> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        final Adapter adapter=new Adapter(list,this);
        recyclerView.setAdapter(adapter);

        Unsplash unsplash = new Unsplash("c33f5be61f00009eda7c36c84eb8836c4cec818322676acf67ff0771ceca9ae7");


        unsplash.getPhotos(1, 100, Order.LATEST, new Unsplash.OnPhotosLoadedListener() {
            @Override
            public void onComplete(List<Photo> photos) {
                Log.i("", "onComplete: ");
                list.addAll(photos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Log.i("", "onError: ");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void openFullScreen(Photo photo) {
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        Dialog dialog = new Dialog(this, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View v= LayoutInflater.from(this).inflate(R.layout.fullscreen,viewGroup,false);
        v.setMinimumWidth((int) (displayRectangle.width() * 1f));
        v.setMinimumHeight((int) (displayRectangle.height() * 1f));
        dialog.setContentView(v);
        ImageView imageView =v.findViewById(R.id.imageView);
        GreedyImageLoader.with(this).setCachingType(GreedyImageLoader.DISK).load(imageView,photo.getUrls().getRegular());

        dialog.show();
    }
}
