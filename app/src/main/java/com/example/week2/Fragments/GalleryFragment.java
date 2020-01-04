package com.example.week2.Fragments;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.week2.MainActivity;
import com.example.week2.Adapter.MyAdapter;
import com.example.week2.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class GalleryFragment extends Fragment{
    public final int PICTURE_REQUEST_CODE = 100;
    private ArrayList<Uri> image_ids = new ArrayList<>();
    private MainActivity mainActivity;
    View v;

    private ArrayList<Uri> prepareData(){
        Button btnImage = v.findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
            }
        });

        Button btnCamera = v.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.permissionCheck("STORAGE") != 0) {
                    mainActivity.requestPerms();
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }
            }
        });


        return image_ids;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_REQUEST_CODE) {
            image_ids.clear();
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < 20; i++) {
                        if (i < clipData.getItemCount()) {
                            image_ids.add(clipData.getItemAt(i).getUri());
                        }
                    }
                }
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Context inContext = getContext();
                Uri uri = getImageUri(inContext, bitmap);
                image_ids.add(uri);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_gallery, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.image_gallery);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        if (prepareData() != null){
            ArrayList<Uri> createLists = prepareData();
            MyAdapter adapter = new MyAdapter(getContext(), createLists);
            recyclerView.setAdapter(adapter);
        }
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        RecyclerView recyclerView = v.findViewById(R.id.image_gallery);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        if (prepareData() != null){
            ArrayList<Uri> createLists = prepareData();
            MyAdapter adapter = new MyAdapter(getContext(), createLists);
            recyclerView.setAdapter(adapter);
        }
    }
}
