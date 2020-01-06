package com.example.week2.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.week2.Data.Permission;
import com.example.week2.R;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class getLocation extends AsyncTask<String, String, String> {

    TextView textView;
    Location locationResult;
    Context context;

    public getLocation(TextView textView, Context context) {
        this.textView = textView;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        Location location = getLastKnownLocation();
        locationResult = location;
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        textView.setText(Double.toString(locationResult.getLatitude()) + ", " + Double.toString(locationResult.getAltitude()));
    }

    private Location getLastKnownLocation() {
        while (true) {
            LocationManager mLocationManager;
            mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Permission.getCertainPerm(5)) != PackageManager.PERMISSION_GRANTED) {
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
                if (ActivityCompat.checkSelfPermission(context, Permission.getCertainPerm(5)) != PackageManager.PERMISSION_GRANTED) {
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
            if(bestLocation != null)
            {
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


                locationResult = location;
            }
            else {
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
}
