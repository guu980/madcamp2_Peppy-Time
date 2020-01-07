package com.example.week2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.week2.Adapter.ContactAdapter;
import com.example.week2.Adapter.WalkingRecordAdapter;
import com.example.week2.Data.Permission;
import com.example.week2.Data.WalkingRecordInfo;
import com.example.week2.Fragments.ContactFragment;
import com.example.week2.Retrofit.RetrofitAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RecordActivity extends AppCompatActivity {

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;

    private MainActivity mainActivity;

    private RecyclerView walkingRecordRecyclerView;
    private RecyclerView.LayoutManager walkingRecordLayoutManager;
    private WalkingRecordAdapter walkingRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record);

        Button getDataBtn = (Button) findViewById(R.id.input_button);
        getDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecord();
            }
        });

        Button sendDataBtn = (Button) findViewById(R.id.send_button);
        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecord();
            }
        });
    }

    private void setWalkingRetrofitInit() {
        String baseUrl = "http://192.249.19.252:2580/";
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }

    public void getRecord() {
        setWalkingRetrofitInit();
        String deviceId = getDeviceId();
        EditText yearInput = (EditText) findViewById(R.id.year_input);
        String year = yearInput.getText().toString();
        EditText monthInput = (EditText) findViewById(R.id.month_input);
        String month = monthInput.getText().toString();
        EditText dayInput = (EditText) findViewById(R.id.day_input);
        String day = dayInput.getText().toString();

        Call<JsonArray> walkingData = null;

        Boolean checking1 = month == null;
        Boolean checking2 = day == "";

        if(!year.equals("") && !month.equals("") && !day.equals(""))
        {
            walkingData = mRetrofitAPI.getWalkingRecordYmd(deviceId, year, month, day);
        }
        else if(!year.equals("") && !month.equals("") && day.equals(""))
        {
            walkingData = mRetrofitAPI.getWalkingRecordYm(deviceId, year, month);

        }
        else if(!year.equals("") && month.equals("") && day.equals(""))
        {
            walkingData = mRetrofitAPI.getWalkingRecordY(deviceId, year);
        }
        else
        {
            walkingData = mRetrofitAPI.getWalkingRecordY(deviceId, year);
        }

        Callback<JsonArray> mRetrofitCallback = new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("Retrofit Success", response.toString());
                //Log.d(TAG, result);
                JsonArray walkingRecordsJA = response.body();

                if(response.body().size() == 0)
                {
                    //taost message
                    Toast.makeText(getApplicationContext(),
                            "해당하는 산책기록이 없습니다",
                            Toast.LENGTH_SHORT).show();
                }

                //recycler view 로 표시
                walkingRecordRecyclerView = (RecyclerView) findViewById(R.id.walking_record_recycler_view);
                walkingRecordLayoutManager = new LinearLayoutManager(getApplicationContext());
                walkingRecordRecyclerView.setLayoutManager(walkingRecordLayoutManager);

                ArrayList<WalkingRecordInfo> walkingRecords = parseWalkingRecords(walkingRecordsJA);

                walkingRecordAdapter = new WalkingRecordAdapter(getApplicationContext(), walkingRecords);

                walkingRecordRecyclerView.setAdapter(walkingRecordAdapter);

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                Log.e("Err", t.getMessage());
            }
        };
        walkingData.enqueue(mRetrofitCallback);
    }

    public void sendRecord() {
        setWalkingRetrofitInit();
        String deviceId = getDeviceId();
        JsonObject walkingRecord = setWalkingRecord("100", "110", "200", "210",
                "3", "34", "3.5");
        Call<JsonObject> walkingData = mRetrofitAPI.storeWalkingRecord(walkingRecord, deviceId);

        Callback<JsonObject> mRetrofitCallback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Retrofit Success", response.toString());
                //Log.d(TAG, result);
                if (response.body() != null) {
                    Log.i("record seding","Success!!!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.e("Err", t.getMessage());
            }
        };

        walkingData.enqueue(mRetrofitCallback);
    }

    public String getDeviceId()
    {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //getApplicationContext 가능한지 체크
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Permission.getCertainPerm(4)) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    public JsonObject setWalkingRecord(String startLatitude, String startLongitude, String endLatitude, String endLongitude, String usedHours, String usedMins, String distance)
    {
        JsonObject finalData = new JsonObject();

        JsonObject start = new JsonObject();
        start.addProperty("lat", startLatitude);
        start.addProperty("lon", startLongitude);

        JsonObject end = new JsonObject();
        end.addProperty("lat", endLatitude);
        end.addProperty("lon", endLongitude);

        List<String> dateInList = getCurrentDate();
        JsonObject date = new JsonObject();
        date.addProperty("year", dateInList.get(1));
        date.addProperty("month", dateInList.get(2));
        date.addProperty("day", dateInList.get(3));
        date.addProperty("hour", dateInList.get(4));
        date.addProperty("min", dateInList.get(5));

        JsonObject time = new JsonObject();
        time.addProperty("hours", usedHours);
        time.addProperty("mins", usedMins);

        finalData.add("start", start);
        finalData.add("end", end);
        finalData.add("date", date);
        finalData.add("time", time);
        finalData.addProperty("distance", distance);

        return finalData;
    }

    /* Return today'start_walking_image_icon date string data */
    private List<String> getCurrentDate()
    {
        List<String> dataList = new ArrayList<String>();

        //Calculating the current date
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
        SimpleDateFormat minFormat = new SimpleDateFormat("mm", Locale.getDefault());

        String weekDay = weekdayFormat.format(currentTime);
        String year = yearFormat.format(currentTime);
        String month = monthFormat.format(currentTime);
        String day = dayFormat.format(currentTime);
        String hour = hourFormat.format(currentTime);
        String min = minFormat.format(currentTime);

        Log.v("CurretDating","Brought weekday = " + weekDay + "\n"
                + "Brought year = " + year + "\n"
                + "Brought month = " + month + "\n"
                + "Brought day = " + day + "\n");

        /* Return results(date intofrmation) by list<String> */

        dataList.add(weekDay);
        dataList.add(year);
        dataList.add(month);
        dataList.add(day);
        dataList.add(hour);
        dataList.add(min);

        return dataList;
    }

    private Date getCurrentDateInDate()
    {
        //Calculating the current date
        Date currentTime = Calendar.getInstance().getTime();

        return currentTime;
    }

    private List<String> getTotalTime(Date startTime, Date endTime)
    {
        Long diffInMilSec = endTime.getTime() - startTime.getTime(); //ms / 1000 -> start_walking_image_icon, /60 -> m
        Long diffInM = diffInMilSec/(1000*60);
        Long diffHour = diffInM/60;
        Long diffMin =  diffInM%60;

        List <String> difference = new ArrayList<String>();
        difference.add( Long.toString(diffHour) );
        difference.add( Long.toString(diffMin) );

        return difference;
    }

    private ArrayList<WalkingRecordInfo> parseWalkingRecords(JsonArray data)
    {
        ArrayList<WalkingRecordInfo> wholeData= new ArrayList<WalkingRecordInfo>();
        if(data != null)
        {
            for(int i=0; i<data.size(); i++)
            {
                JsonElement temp = data.get(i);
                JsonObject eachItemJsonObject = (JsonObject)temp;

                JsonObject realEachItemJsonObject = (JsonObject) eachItemJsonObject.get("walkingData") ;


                String preDistance = realEachItemJsonObject.get("distance").toString();
                String distanceString = preDistance.substring(1, preDistance.length()-1);

                WalkingRecordInfo eachRecord = new WalkingRecordInfo((JsonObject)realEachItemJsonObject.get("start"), (JsonObject)realEachItemJsonObject.get("end"), (JsonObject)realEachItemJsonObject.get("date"),
                        (JsonObject)realEachItemJsonObject.get("time"), Double.parseDouble(distanceString), eachItemJsonObject.get("_id").toString());

                wholeData.add(eachRecord);
            }
        }
        return wholeData;
    }
}
