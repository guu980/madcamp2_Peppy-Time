package com.example.week2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        Uri myuri = Uri.parse(uri);

        ImageView imageView = (ImageView) findViewById(R.id.clicked_image);
        imageView.setImageURI(myuri);

    }

}
