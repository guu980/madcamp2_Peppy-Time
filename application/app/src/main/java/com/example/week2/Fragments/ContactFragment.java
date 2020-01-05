package com.example.week2.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.Adapter.ContactAdapter;
import com.example.week2.Data.Permission;
import com.example.week2.JSONCOM.JsonTask;
import com.example.week2.MainActivity;
import com.example.week2.R;
import com.example.week2.Data.ReadContact;
import com.example.week2.Data.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static android.content.Context.TELEPHONY_SERVICE;

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
            String deviceId = getDeviceId();
            users = jsonTask.execute("http://192.249.19.252:2580/get/all_data/" + deviceId).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        final Context temp = getContext();
        Button syncBtn = v.findViewById(R.id.sync_button);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reader = new ReadContact(getContext());
                Reader.getContactList();
                storePhoneContacts(Reader);


                JsonTask jsonTask = new JsonTask();
                String deviceId = getDeviceId();
                try {
                    users = jsonTask.execute("http://192.249.19.252:2580/get/all_data/" + deviceId).get();
                    contactAdapter.setAdapter(users);
                    contactAdapter.notifyDataSetChanged();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

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

        contactRecyclerView = v.findViewById(R.id.recycler_view);
        contactLayoutManager = new LinearLayoutManager(getContext());
        contactRecyclerView.setLayoutManager(contactLayoutManager);


//        if (ActivityCompat.checkSelfPermission(getContext(), Permission.getCertainPerm(0)) == PackageManager.PERMISSION_GRANTED) {
//            Reader = new ReadContact(getContext());
//            users = Reader.getContactList();
//
//            // Adding phone contacts to the dataaserver
//            storePhoneContacts(Reader);
//
//            Log.d("test", " " + users.size());
//            contactAdapter = new ContactAdapter(getContext(), users);
//            contactRecyclerView.setAdapter(contactAdapter);
//        }

    }

    private void storePhoneContacts(ReadContact reader) {

        String deviceId = getDeviceId();

        JSONArray phoneContacts = reader.getPhoneContacts();
        Log.v ("Deviceid", "---------------------------"+deviceId);
        //String phoneContactsString = reader.getPhoneContactsString();
        String URL = "http://192.249.19.252:2580/push/phonecontacts/" + deviceId ;
        NetworkTask sendPhoneContacts = new NetworkTask(URL, phoneContacts);
        sendPhoneContacts.execute();
    }

    public class NetworkTask extends AsyncTask<String, String, String> {

        private String url;
        //private JSONArray contacts;
        private JSONArray contacts;

        public NetworkTask(String url, JSONArray contacts_data) {
            this.url = url;
            this.contacts = contacts_data;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpPost post = new HttpPost(this.url);

            JSONArray contacts = this.contacts;

            for(int i=0; i<contacts.length(); i++)
            {
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    JSONObject jsonToken = contacts.getJSONObject(i);

                    //HttpPost에 넘길 값을들 Set해주기
                    StringEntity request_param = new StringEntity(jsonToken.toString(), "UTF-8");
                    request_param.setContentType("application/json");
                    //new UrlEncodedFormEntity(contacts, "UTF-8")
                    post.setEntity(request_param);
                    //post.setHeader("Content-Type", "application/json; charset=utf8");

                    //설정한 URL을 실행시키기
                    HttpResponse response = httpClient.execute(post);


                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line="";
                    while((line = rd.readLine()) != null)
                    {
                        System.out.println(line);
                    }

                    //httpClient.getConnectionManager().shutdown();

                    //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                    Log.v("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            //tv_outPut.setText(s);
        }
    }

    public String getDeviceId()
    {
        TelephonyManager tm = (TelephonyManager) mainActivity.getSystemService(TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Permission.getCertainPerm(4)) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }

        String deviceId = tm.getDeviceId();
        return deviceId;
    }
}
