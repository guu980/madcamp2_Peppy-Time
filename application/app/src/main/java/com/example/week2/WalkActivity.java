package com.example.week2;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.week2.Fragments.ContactFragment;
import com.example.week2.Fragments.GalleryFragment;
import com.example.week2.Fragments.PetFragment;
import com.google.android.material.tabs.TabLayout;

public class WalkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
    }


}
