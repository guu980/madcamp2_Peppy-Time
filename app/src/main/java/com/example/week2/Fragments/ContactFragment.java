package com.example.week2.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.Adapter.ContactAdapter;
import com.example.week2.JSONCOM.JsonTask;
import com.example.week2.MainActivity;
import com.example.week2.R;
import com.example.week2.Data.ReadContact;
import com.example.week2.Data.UserInfo;

import java.util.ArrayList;

public class ContactFragment extends Fragment {
    private RecyclerView contactRecyclerView;
    private RecyclerView.LayoutManager contactLayoutManager;
    private ContactAdapter contactAdapter;
    private View v;

    private ArrayList<UserInfo> users;
    private ReadContact Reader;
    private MainActivity mainActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        v = inflater.inflate(R.layout.fragment_contact, container, false);

        Log.d("Start", "--------------------------");

        JsonTask jsonTask = new JsonTask();

        try {
            users = jsonTask.execute("http://192.249.19.252:2680/all_data").get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /* Post Version
        Button btn = v.findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.permissionCheck("CONTACT") == 0) {
                    Intent intent = new Intent(Intent.ACTION_INSERT, Uri.parse("content://contacts/people"));
                    startActivity(intent);
                } else {
                    mainActivity.requestPerms();
                }
            }
        });
        */
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        contactRecyclerView = v.findViewById(R.id.recycler_view);
        contactLayoutManager = new LinearLayoutManager(getContext());
        contactRecyclerView.setLayoutManager(contactLayoutManager);
        contactAdapter = new ContactAdapter(getContext(), users);
        contactRecyclerView.setAdapter(contactAdapter);

        /* Post Version
        contactRecyclerView = v.findViewById(R.id.recycler_view);
        contactLayoutManager = new LinearLayoutManager(getContext());
        contactRecyclerView.setLayoutManager(contactLayoutManager);

        if (ActivityCompat.checkSelfPermission(getContext(), Permission.getCertainPerm(0)) == PackageManager.PERMISSION_GRANTED) {
            Reader = new ReadContact(getContext());
            users = Reader.getContactList();
            Log.d("test", " " + users.size());
            contactAdapter = new ContactAdapter(getContext(), users);
            contactRecyclerView.setAdapter(contactAdapter);
        }
        */
    }

}
