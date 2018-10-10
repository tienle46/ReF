package com.example.tienle.ref;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class ResList extends AppCompatActivity {
    private static final String TAG = "ResList";

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImagesUrls = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_list);
        Log.d(TAG,"onCreate: started.");

        initImageBitmaps();


    }

    private void initImageBitmaps() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        mImagesUrls.add("https://media-cdn.tripadvisor.com/media/photo-s/11/17/4c/b8/the-royal-peacock.jpg");
        mNames.add("Royal Peacock");

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init RecyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImagesUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
