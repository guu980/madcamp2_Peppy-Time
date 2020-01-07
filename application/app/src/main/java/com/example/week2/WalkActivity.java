package com.example.week2;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week2.Adapter.PathAdapter;
import com.example.week2.Data.Permission;
import com.example.week2.Data.Place;
import com.example.week2.Retrofit.RetrofitAPI;
import com.google.gson.JsonObject;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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


public class WalkActivity extends AppCompatActivity implements View.OnClickListener, LocationListener
        , TMapView.OnLongClickListenerCallback {
    private TMapView tMapView;
    private TMapData tMapData;

    private Button getCurrentBtn;
    private Button searchAroundBtn;
    private Button searchPath;
    private Button stopBtn;
    private TextView departure;
    private TextView arrival;
    private RecyclerView pathRecyclerView;
    private RecyclerView.LayoutManager pathLayoutManager;
    private PathAdapter pathAdapter;

    private double departureLongitude;
    private double departureLatitude;
    private ArrayList<String> path;

    private Place currentPosition;
    private Place arrivalPosition;
    private Place departurePosition;

    private ArrayList<Place> checkPoints;

    private String address;

    private LocationManager locationManager;

    private boolean isClickgetCurrent = false;

    private final static String KEY = "l7xx4cc55d11137f41b1b1f13b5e1259e2fc";

/////////////////////////////////////////////////////////////////////////////////

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;
    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        LinearLayout linearLayoutTMap = findViewById(R.id.TMap_container);
        tMapView = new TMapView(getApplicationContext());
        tMapData = new TMapData();

        tMapView.setSKTMapApiKey(KEY);
        linearLayoutTMap.addView(tMapView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        getCurrentBtn = findViewById(R.id.current_btn);
        getCurrentBtn.setOnClickListener(this);

        searchAroundBtn = findViewById(R.id.search_around);
        searchAroundBtn.setOnClickListener(this);

        tMapView.setOnLongClickListenerCallback(this);

        departure = findViewById(R.id.departure);
        arrival = findViewById(R.id.arrival);

        searchPath = findViewById(R.id.path_search);
        searchPath.setOnClickListener(this);

        stopBtn = findViewById(R.id.stop);
        stopBtn.setOnClickListener(this);

        pathRecyclerView = findViewById(R.id.path_container);
        pathLayoutManager = new LinearLayoutManager(getApplicationContext());
        pathRecyclerView.setLayoutManager(pathLayoutManager);
    }


    private void getCurrentPlace() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WalkActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            currentPosition = new Place();
            departurePosition = new Place();
            tMapView.removeAllMarkerItem();

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            String provider = location.getProvider();

            departurePosition.setLongitude(location.getLongitude());
            departurePosition.setLatitude(location.getLatitude());

            currentPosition.setLongitude(location.getLongitude());
            currentPosition.setLatitude(location.getLongitude());

            String text = "위치정보 : " + provider + "위도 : " + currentPosition.getLongitude() + "경도 : " + currentPosition.getLatitude() + "\n" + location.getAccuracy();

            Log.d("현재위치", "~~~~~~~~~~~~" + text + "~~~~~~~~~~~~~");

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);

            tMapData.convertGpsToAddress(departurePosition.getLatitude(), departurePosition.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                @Override
                public void onConvertToGPSToAddress(String s) {
                    departure.setText(s);
                }
            });

            tMapView.addMarkerItem("Departure", addMarker(new TMapPoint(departurePosition.getLatitude(), departurePosition.getLongitude()), R.drawable.start, "출발"));
            tMapView.setLocationPoint(currentPosition.getLongitude(), currentPosition.getLatitude());
            tMapView.setIconVisibility(true);
            tMapView.setTrackingMode(true);
        }
    }

    private TMapMarkerItem addMarker(TMapPoint point, int icon, String title) {
        TMapMarkerItem markerItem = new TMapMarkerItem();

        markerItem.setCanShowCallout(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), icon);
        markerItem.setIcon(bitmap);
        markerItem.setCalloutTitle(title);
        markerItem.setTMapPoint(point);

        return markerItem;
    }

    public String getDeviceId()
    {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Permission.getCertainPerm(4)) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    private void setWalkingRetrofitInit() {
        String baseUrl = "http://192.249.19.252:2580/";
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }

    public void sendRecord() {
        setWalkingRetrofitInit();
        String deviceId = getDeviceId();

        List<String> totalTime = getTotalTime(startDate, endDate);

        Log.d("DEBUG", departurePosition.getLatitude() + ", " + departurePosition.getLongitude());
        JsonObject walkingRecord = setWalkingRecord(String.valueOf(departurePosition.getLatitude())
                , String.valueOf(departurePosition.getLongitude()), String.valueOf(arrivalPosition.getLatitude())
                , String.valueOf(arrivalPosition.getLongitude()),
                totalTime.get(0), totalTime.get(1), "3.0");
        Call<JsonObject> walkingData = mRetrofitAPI.storeWalkingRecord(walkingRecord, deviceId);

        Callback<JsonObject> mRetrofitCallback = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Retrofit Success", response.toString());
                //Log.d(TAG, result);
                if (response.body() != null) {
                    Log.i("record sending","Success!!!");
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

    /* Return today's date string data */
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
        Long diffInMilSec = endTime.getTime() - startTime.getTime(); //ms / 1000 -> s, /60 -> m
        Long diffInM = diffInMilSec/(1000*60);
        Long diffHour = diffInM/60;
        Long diffMin =  diffInM%60;

        List <String> difference = new ArrayList<String>();
        difference.add( Long.toString(diffHour) );
        difference.add( Long.toString(diffMin) );

        return difference;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_btn:
                isClickgetCurrent = !isClickgetCurrent;
                if (isClickgetCurrent) {
                    getCurrentBtn.setBackgroundResource(R.drawable.baseline_gps_fixed_black_18);
                    getCurrentPlace();
                } else {
                    getCurrentBtn.setBackgroundResource(R.drawable.baseline_gps_not_fixed_black_18);
                    locationManager.removeUpdates(this);
                    tMapView.setCompassMode(false);
                    tMapView.setTrackingMode(false);
                    tMapView.setSightVisible(false);
                }
                break;
            case R.id.search_around:
                tMapData.findAroundNamePOI(new TMapPoint(currentPosition.getLatitude(), currentPosition.getLongitude()), "편의점;화장실;카페", 3, 10, new TMapData.FindAroundNamePOIListenerCallback() {
                    @Override
                    public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            TMapPOIItem item = arrayList.get(i);

                            TMapPoint currentItemPos = new TMapPoint(item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                            tMapView.addMarkerItem("keyword" + i, addMarker(currentItemPos, R.drawable.point, item.getPOIName()));
                        }
                    }
                });
                break;
            case R.id.path_search:
                TMapPoint startPoint = new TMapPoint(departurePosition.getLatitude(), departurePosition.getLongitude());
                TMapPoint endPoint = new TMapPoint(arrivalPosition.getLatitude(), arrivalPosition.getLongitude());

                startDate = getCurrentDateInDate();

                if (checkPoints != null) {
                    for (int i = 0; i < checkPoints.size(); i++) {
                        tMapView.removeMarkerItem("CheckPoint" + i);
                    }
                }

                tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        tMapPolyLine.setLineColor(Color.MAGENTA);
                        tMapView.removeTMapPolyLine("Line2");
                        tMapView.addTMapPolyLine("Line1", tMapPolyLine);
                        tMapView.setCenterPoint(currentPosition.getLongitude(), currentPosition.getLatitude());
                    }
                });

                tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        checkPoints = new ArrayList<>();
                        path = new ArrayList<>();

                        Element root = document.getDocumentElement();

                        NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                        NodeList totalDistance = root.getElementsByTagName("tmap:totalDistance");

                        for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                            String point;
                            String[] position;
                            Place checkPoint = new Place();

                            NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();

                            for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {

                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("name")) {
                                    if (!nodeListPlacemarkItem.item(j).getTextContent().trim().equals("")) {
                                        break;
                                    }
                                }

                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("description")) {
                                    path.add(nodeListPlacemarkItem.item(j).getTextContent().trim());
                                }

                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                                    point = nodeListPlacemarkItem.item(j).getTextContent().trim();
                                    position = point.split(",");
                                    checkPoint.setLongitude(Double.parseDouble(position[0]));
                                    checkPoint.setLatitude(Double.parseDouble(position[1]));
                                    checkPoints.add(checkPoint);
                                }
                            }
                        }
                        Log.i("debug", "+++++++++++++++++" + totalDistance.item(0).getTextContent());
                        for (int i = 0; i < checkPoints.size(); i++) {
                            TMapPoint checkPointGPS = new TMapPoint(checkPoints.get(i).getLatitude(), checkPoints.get(i).getLongitude());
                            tMapView.addMarkerItem("CheckPoint" + i, addMarker(checkPointGPS, R.drawable.point, "경유지"));
                        }
                        pathAdapter = new PathAdapter(path);
                        pathRecyclerView.setAdapter(pathAdapter);
                    }
                });
                break;
            case R.id.stop:
                TMapPoint start = new TMapPoint(currentPosition.getLatitude(), currentPosition.getLongitude());
                TMapPoint end = new TMapPoint(departurePosition.getLatitude(), departurePosition.getLongitude());

                endDate = getCurrentDateInDate();

                tMapData.convertGpsToAddress(currentPosition.getLatitude(), currentPosition.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String s) {
                        departure.setText(s);
                    }
                });

                tMapData.convertGpsToAddress(departurePosition.getLatitude(), departurePosition.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String s) {
                        arrival.setText(s);
                    }
                });

                tMapView.removeMarkerItem("Arrival");
                tMapView.removeMarkerItem("Departure");
                tMapView.removeAllMarkerItem();

                tMapView.addMarkerItem("Departure", addMarker(start, R.drawable.start, "출발"));
                tMapView.addMarkerItem("Arrival", addMarker(end, R.drawable.arrival, "도착"));

                tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, start, end, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        tMapPolyLine.setLineColor(Color.MAGENTA);
                        tMapView.removeTMapPolyLine("Line1");
                        tMapView.addTMapPolyLine("Line2", tMapPolyLine);
                        tMapView.setCenterPoint(currentPosition.getLongitude(), currentPosition.getLatitude());
                    }
                });

                tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, start, end, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        Element root = document.getDocumentElement();

                        NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                        NodeList totalDistance = root.getElementsByTagName("tmap:totalDistance");

                        for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                            NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                            for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
//                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("description")) {
//                                    Log.i("debug", "--------" + nodeListPlacemarkItem.item(j).getTextContent().trim());
//                                }

                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                                    Log.i("debug", "~~~~~~~~~~~~~" + nodeListPlacemarkItem.item(j).getTextContent());

                                    String point = nodeListPlacemarkItem.item(j).getTextContent().trim();
                                    String[] position = point.split(",");
                                    Double[] dPosition = new Double[] {Double.parseDouble(position[0]), Double.parseDouble(position[1])};

                                    tMapView.addMarkerItem("CheckPoint"+ j, addMarker(new TMapPoint(dPosition[1], dPosition[0]), R.drawable.point, "경유지"));
                                }
                            }
                        }
                        Log.i("debug", "+++++++++++++++++" + totalDistance.item(0).getTextContent());
                    }
                });
                sendRecord();
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        currentPosition.setLongitude(location.getLongitude());
        currentPosition.setLatitude(location.getLatitude());

        tMapView.setLocationPoint(currentPosition.getLongitude(), currentPosition.getLatitude());
        tMapView.setCenterPoint(currentPosition.getLongitude(), currentPosition.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
        arrivalPosition = new Place();
        arrivalPosition.setLatitude(tMapPoint.getLatitude());
        arrivalPosition.setLongitude(tMapPoint.getLongitude());

        tMapData.convertGpsToAddress(arrivalPosition.getLatitude(), arrivalPosition.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
            @Override
            public void onConvertToGPSToAddress(String s) {
                arrival.setText(s);
            }
        });

        tMapView.addMarkerItem("Arrival", addMarker(tMapPoint, R.drawable.arrival, "도착"));
    }


    class GetTmap extends AsyncTask<Double, String, String> {
        @Override
        protected String doInBackground(Double... location) {
            try {
                String address = new TMapData().convertGpsToAddress(location[0], location[1]);
                Log.d("Print", "------------------------------------------------");
                return address;
            } catch (Exception e) {
                Log.d("Print", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


}
