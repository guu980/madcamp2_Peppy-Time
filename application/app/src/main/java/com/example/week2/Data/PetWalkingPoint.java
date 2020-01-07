package com.example.week2.Data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.week2.Adapter.WalkingRecordAdapter;
import com.example.week2.R;
import com.example.week2.Retrofit.RetrofitAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.content.ContextCompat.getSystemService;

public class PetWalkingPoint {

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;

    private NumberProgressBar pointBar;
    private TextView pointText;

    private Double realPoint;
    private Integer intPoint;
    private String deviceId;
    private JsonArray records;

    public PetWalkingPoint(String deviceId, NumberProgressBar pointBar, TextView pointText)
    {
        this.deviceId = deviceId;
        this.pointBar = pointBar;
        this.pointText = pointText;
    }

    private void setWalkingRetrofitInit() {
        String baseUrl = "http://192.249.19.252:2580/";
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }

    public void caclulatePoint(String year, String month) {
        setWalkingRetrofitInit();
        Call<JsonArray> walkingData = mRetrofitAPI.getWalkingRecordforPoint(deviceId, year, month);
        Callback<JsonArray> mRetrofitCallback = new Callback<JsonArray>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("Retrofit Success", response.toString());
                //Log.d(TAG, result);
                if (response.body() != null) {
                    records = response.body();
                    Double totalHours = 0.0;
                    Double totalMins = 0.0;
                    for(int i=0; i<records.size(); i++)
                    {
                        JsonObject eachRecord = (JsonObject) records.get(i);
                        JsonObject eachRecordData = (JsonObject) eachRecord.get("walkingData");
                        JsonObject eachTimeData = (JsonObject) eachRecordData.get("time");
                        String preHour = eachTimeData.get("hours").toString();
                        Double eachHours = Double.parseDouble(preHour.substring(1, preHour.length()-1));
                        String preMin = eachTimeData.get("mins").toString();
                        Double eachMins = Double.parseDouble(preMin.substring(1, preMin.length()-1));

                        totalHours = totalHours + eachHours;
                        totalMins = totalMins + eachMins;
                    }
                    realPoint = totalHours*10 +totalMins/6 ;

                    intPoint = Math.toIntExact(Math.round(realPoint));
                    if(intPoint>100)  //intPoint>100
                    {
                        if(intPoint>=150)
                        {
                            intPoint = 100 - (intPoint-150);
                            if(intPoint < 0)
                            {
                                intPoint = 0;
                            }
                        }
                        else{
                            intPoint=100;
                        }
                    }
                }
                else{ //response 가 null 이 왔다 == ??점수데이터가 없다!! 새로 계산하자
                    realPoint = 0.0;
                    intPoint = 0;
                }

                pointBar.setProgress(intPoint);
                pointText.setText(intPoint.toString() + " 점!!!");
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                Log.e("Err", t.getMessage());
            }
        };

        walkingData.enqueue(mRetrofitCallback);
    }
}
