package com.example.week2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.Data.Permission;
import com.example.week2.Data.UserInfo;
import com.example.week2.Fragments.ContactFragment;
import com.example.week2.MainActivity;
import com.example.week2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static android.content.Context.TELEPHONY_SERVICE;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UserInfo> users;
    private Context context;
    private MainActivity mainActivity;

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNumber;
        private TextView name;
        private Button deleteButton;
        private Button callButton;



        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            name = itemView.findViewById(R.id.name);
            deleteButton = itemView.findViewById(R.id.delete_button);
            callButton = itemView.findViewById(R.id.call_button);
            mainActivity = (MainActivity) context;



            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, users.get(getAdapterPosition()).getId(), Toast.LENGTH_SHORT).show();
                    String deviceId = getDeviceId();
                    String Url = "http://192.249.19.252:2580/delete/one/" + deviceId;
                    NetworkTask sendPhoneContacts = new NetworkTask(Url, users.get(getAdapterPosition()).getId());
                    sendPhoneContacts.execute();

                    context.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.CONTACT_ID + " = " + users.get(getAdapterPosition()).getId(), null);
                    users.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), users.size());

                }
            });

            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainActivity.permissionCheck("CALL") != 0) {
                        mainActivity.requestPerms();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+users.get(getAdapterPosition()).getPhoneNumber()));
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    public void setAdapter(ArrayList<UserInfo> users) {
        this.users = users;
    }

    public ContactAdapter(Context context, ArrayList<UserInfo> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_rows, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ContactViewHolder viewHolder = (ContactViewHolder) holder;
        viewHolder.phoneNumber.setText(users.get(position).getPhoneNumber());
        viewHolder.name.setText(users.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class NetworkTask extends AsyncTask<String, String, String> {

        private String url;
        private  String contactId;

        public NetworkTask(String url, String id) {
            this.url = url;
            this.contactId = id;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpPost post = new HttpPost(this.url);

            HttpClient httpClient = new DefaultHttpClient();
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("id",this.contactId));
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //설정한 URL을 실행시키기
                HttpResponse response = httpClient.execute(post);

                //httpClient.getConnectionManager().shutdown();

                //통신 값을 받은 Log 생성. (200이 나오는지 확인할 것~) 200이 나오면 통신이 잘 되었다는 뜻!
                Log.v("Insert Log", "response.getStatusCode:" + response.getStatusLine().getStatusCode());

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            //tv_outPut.setText(start_walking_image_icon);
        }
    }

    public String getDeviceId()
    {
        TelephonyManager tm = (TelephonyManager) mainActivity.getSystemService(TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Permission.getCertainPerm(4)) != PackageManager.PERMISSION_GRANTED) {
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
