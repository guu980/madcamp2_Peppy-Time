package com.example.week2.JSONCOM;

import android.os.AsyncTask;
import android.util.Log;

import com.example.week2.Data.UserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class JsonTask extends AsyncTask<String, String, ArrayList<UserInfo>> {

    ArrayList <UserInfo> userInfos;

    @Override
    protected ArrayList<UserInfo> doInBackground(String... urls) {
        try {
            JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("user_id", "androidTest");
//                jsonObject.accumulate("name", "yun");
            HttpURLConnection con = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                con = (HttpURLConnection) url.openConnection();
                con.connect();
                InputStream stream = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                try {
                    JSONArray jsonArray = new JSONArray(buffer.toString());
                    userInfos = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        userInfos.add(parsingJsonArr(jsonArray.getJSONObject(i)));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return userInfos;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("Error1", "--------------" + e + "---------------");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error2", "--------------" + e + "---------------");
            } finally {
                if (con != null) {
                    con.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Error3", "--------------" + e + "---------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error4", "--------------" + e + "---------------");
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<UserInfo> s) {
        super.onPostExecute(s);

        for (Object item: userInfos) {
            Log.d("Print ArrayList", "------------" + item.toString() +"--------------");
        }
    }

    private UserInfo parsingJsonArr (JSONObject jsonObject) {
        UserInfo userInfo = null;
        try {
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String phoneNumber = jsonObject.getString("number");
            String thumbNail = jsonObject.getString("_id");

            userInfo = new UserInfo(id, phoneNumber, name, thumbNail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }
}
