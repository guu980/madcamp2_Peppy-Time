package com.example.week2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.week2.Data.Permission;
import com.example.week2.Data.PetWalkingPoint;
import com.example.week2.MainActivity;
import com.example.week2.R;
import com.example.week2.RecordActivity;
import com.example.week2.Retrofit.RetrofitAPI;
import com.example.week2.WalkActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
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

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class PetFragment extends Fragment {

    private View v;

    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    private String provider;
    private double longitude;
    private double latitude;

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;

    private MainActivity mainActivity;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        v = inflater.inflate(R.layout.fragment_pet, container, false);

        Button walkBtn = v.findViewById(R.id.walk_btn);

        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WalkActivity.class);
                startActivity(intent);
            }
        });

        Button checkBtn = v.findViewById(R.id.check_btn);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecordActivity.class);
                startActivity(intent);
            }
        });

        ProgressBar pointBar = (ProgressBar) v.findViewById(R.id.point_bar);
        TextView pointText = (TextView) v. findViewById(R.id.point_text);
        PetWalkingPoint petWalkingPoint = new PetWalkingPoint(getDeviceId(), pointBar, pointText);
        List<String> currentDate = getCurrentDate();
        petWalkingPoint.caclulatePoint(currentDate.get(1), currentDate.get(2));

        Location location = getLastKnownLocation();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        getWeather(longitude, latitude);
//        debugText.setText(Double.toString(location.getLatitude()) + ", " + Double.toString(location.getAltitude()));
//        List<Double> tmData = getTm(latitude, longitude);
//        Double tmX = tmData.get(0);
//        Double tmY = tmData.get(1);
//        if (tmData != null) {
//            tmText.setText(tmX + ", " + tmY);
//        }

        return v;
    }

    private Location getLastKnownLocation() {
        while (true) {
            LocationManager mLocationManager;
            mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getContext(), Permission.getCertainPerm(5)) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return null;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    mLocationListener);


            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                if (ActivityCompat.checkSelfPermission(getContext(), Permission.getCertainPerm(5)) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return null;
                }
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            if (bestLocation != null) {
                mLocationManager.removeUpdates(mLocationListener);
                return bestLocation;
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
//여기서 위치값이 갱신되면 이벤트가 발생한다.
//값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
//Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.


//                double longitude = location.getLongitude();    //경도
//                double latitude = location.getLatitude();         //위도
//                float accuracy = location.getAccuracy();        //신뢰도
            } else {
//Network 위치제공자에 의한 위치변화
//Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void setRetrofitInit() {
        String baseUrl = "http://api.openweathermap.org/data/2.5/";
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }

    public void getWeather(Double longitude, Double latitude) {
        setRetrofitInit();
        String appid = "3b06ac55d534621e2f31911db8db7985";
        Call<JsonObject> weatherData = mRetrofitAPI.getCurrentWeather(Double.toString(latitude), Double.toString(longitude), appid);

        Callback<JsonObject> mRetrofitCallback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Retrofit Success", response.toString());
                //Log.d(TAG, result);
                if (response.body() != null) {
                    JsonObject totalData = response.body();
                    JsonArray weatherData = totalData.getAsJsonArray("weather");
                    JsonObject weatherDataJsonObject = (JsonObject) weatherData.get(0);
                    String weatherImgData = weatherDataJsonObject.get("icon").toString();
                    String weatherImgItemUrl = weatherImgData.substring(1, weatherImgData.length() - 1);
                    String weatherImgUrl = "http://openweathermap.org/img/w/" + weatherImgItemUrl + ".png";
                    ImageView weatherImgView = v.findViewById(R.id.weatherImage);
                    //Glide.with(getContext()).load(weatherImgUrl).into(weatherImgView);
                    Glide.with(getContext()).load(weatherImgUrl)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.image_error)
                            .into(weatherImgView);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.e("Err", t.getMessage());
            }
        };

        weatherData.enqueue(mRetrofitCallback);
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
}

//    private void setTmRetrofitInit() {
//        String baseUrl = "https://dapi.kakao.com/v2/local/geo/";
//        mRetrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
//    }

//    public List<Double> getTm(Double longitude, Double latitude) {
//        setTmRetrofitInit();
//        String x = "x=" + Double.toString(longitude);
////        String dataInput = x + "&y=" + Double.toString(latitude) + "&input_coord=" + "WGS84" + "&output_coord=" + "TM";
//        Call<JsonObject> tmData = mRetrofitAPI.getTmData(Double.toString(longitude), Double.toString(latitude), "WGS84", "TM");
//
////        Call<JsonObject> tmData = mRetrofitAPI.getTmData(dataInput);
//
//        final List<Double> tmValues = new ArrayList<Double>();
//
//        Callback<JsonObject> mRetrofitCallback = new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.d("Retrofit Success Tm", response.toString());
//                //Log.d(TAG, result);
//                if (response.body() != null)
//                {
//                    JsonObject totalData = response.body();
//                    JsonArray documentsData = (JsonArray) totalData.get("documents");
//                    JsonObject documentsDataJsonObject = (JsonObject) documentsData.get(0);
//                    JsonElement tmXelem = documentsDataJsonObject.get("x");
//                    Double tmX = Double.parseDouble(tmXelem.toString());
//                    JsonElement tmYelem = documentsDataJsonObject.get("y");
//                    Double tmY = Double.parseDouble(tmYelem.toString());
//                    tmValues.add(tmX);
//                    tmValues.add(tmY);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                t.printStackTrace();
//                Log.e("Err", t.getMessage());
//            }
//
//        };
//
//        tmData.enqueue(mRetrofitCallback);
//
//        return tmValues;
//    }
//}

