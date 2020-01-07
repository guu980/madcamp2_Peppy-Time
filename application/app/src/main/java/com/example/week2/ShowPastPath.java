package com.example.week2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class ShowPastPath extends AppCompatActivity {
    private TMapView tMapView;
    private TMapData tMapData;

    private LinearLayout tMapContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_path);

        tMapContainer = findViewById(R.id.past_path);

        tMapView = new TMapView(getApplicationContext());
        tMapData = new TMapData();
        tMapView.setSKTMapApiKey("l7xx4cc55d11137f41b1b1f13b5e1259e2fc");
        tMapContainer.addView(tMapView);

        Intent intent = getIntent();
        final String[] position = intent.getStringArrayExtra("Position");

        TMapPoint startPoint = new TMapPoint(Double.parseDouble(position[0]), Double.parseDouble(position[1]));
        TMapPoint endPoint = new TMapPoint(Double.parseDouble(position[2]), Double.parseDouble(position[3]));

        tMapView.setCenterPoint(Double.parseDouble(position[1]), Double.parseDouble(position[0]));

        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                tMapPolyLine.setLineColor(Color.BLUE);
                tMapView.addTMapPolyLine("Line3", tMapPolyLine);
//                tMapView.setCenterPoint(Double.parseDouble(position[2]), Double.parseDouble(position[3]));
            }
        });
    }
}
