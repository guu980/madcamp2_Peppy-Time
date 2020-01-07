package com.example.week2.JSONCOM;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonTaskTm extends AsyncTask<String, String, ArrayList<String>> {

    ArrayList <String> tmData;
    TextView tv;

    public JsonTaskTm(TextView tv){
        this.tv = tv;
    }

    @Override
    protected ArrayList<String> doInBackground(String... urls) {
        try {
            JSONObject jsonObject = new JSONObject();
            HttpURLConnection con = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Authorization", "c41182fa3633071e849d3e55fd1f5231");
                con.connect();

                StringBuilder responseStringBuilder = new StringBuilder();

                StringBuffer buffer = new StringBuffer();
                String line = "";
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    for (;;){
                        String stringLine = bufferedReader.readLine();
                        if (stringLine == null ) break;
                        responseStringBuilder.append(stringLine + '\n');
                    }

                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }

                    bufferedReader.close();
                }
                con.disconnect();

//                InputStream stream = con.getInputStream();
//                reader = new BufferedReader(new InputStreamReader(stream));
//                StringBuffer buffer = new StringBuffer();
//                String line = "";
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    buffer.append(line);
//                }

                try {
                    JSONObject jsonData = new JSONObject(buffer.toString());
                    tmData=parsingJsonArr(jsonData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return tmData;
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
    protected void onPostExecute(ArrayList <String> s) {
        super.onPostExecute(s);
        tv.setText(s.get(0) + ", " + s.get(1));
    }


    private ArrayList<String> parsingJsonArr (JSONObject jsonObject){

        ArrayList<String> result = new ArrayList<String>();
        try {
            String jsonStringData = jsonObject.getString("documents");
            JSONObject jsonData = new JSONObject(jsonStringData);

            String tmX = jsonData.getString("x");
            String tmY = jsonData.getString("y");

            result.add(tmX);
            result.add(tmY);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

