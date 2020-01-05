package com.example.week2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private String mapx;
    private String mapy;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_activity_detail);

        LinearLayout telContainer = findViewById(R.id.tel_container);

        Button callButton = findViewById(R.id.call_button);

        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        String addr1 = intent.getStringExtra("addr1");
        String firstimage = intent.getStringExtra("firstimage");
        final String tel = intent.getStringExtra("tel");
        mapx = intent.getStringExtra("mapx");
        mapy = intent.getStringExtra("mapy");
        String overview = intent.getStringExtra("overview");
        String homepage = intent.getStringExtra("homepage");

        ImageView imageView = findViewById(R.id.firstimage);
        if (firstimage != null) {
            Glide.with(getApplicationContext()).load(firstimage).centerCrop().into(imageView);
        } else {
            imageView.getLayoutParams().height = 0;
        }

        TextView titleText = findViewById(R.id.title);
        titleText.setText(title);

        TextView addressText = findViewById(R.id.addr1);
        addressText.setText(addr1);

        TextView telText = findViewById(R.id.tel);

        if (tel == null) {
            telContainer.setVisibility(View.GONE);
        } else {
            telContainer.setVisibility(View.VISIBLE);
            telText.setText(tel);
        }

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        TextView overviewText = findViewById(R.id.overview);
        overviewText.setText(Html.fromHtml(overview));

        TextView linkText = findViewById(R.id.homepage);

        if (homepage != null) {
            linkText.setText(Html.fromHtml(homepage));
            Linkify.addLinks(linkText, Linkify.WEB_URLS);
            linkText.setMovementMethod(LinkMovementMethod.getInstance());
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng POINT = new LatLng(Double.parseDouble(mapy), Double.parseDouble(mapx));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(POINT);
        markerOptions.title(title);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(POINT));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }
}

