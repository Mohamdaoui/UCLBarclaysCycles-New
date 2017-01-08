package com.mohamdaoui.uclbarclayscycles.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mohamdaoui.uclbarclayscycles.R;

import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_LAT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.SEARCH_LNG;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.STATION_LAT;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.STATION_LNG;
import static com.mohamdaoui.uclbarclayscycles.utils.Constants.STATION_NAME;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent mapIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapIntent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng searchPosition = new LatLng(mapIntent.getDoubleExtra(SEARCH_LAT, 0),
                mapIntent.getDoubleExtra(SEARCH_LNG, 0));
        LatLng stationPosition = new LatLng(Double.parseDouble(mapIntent.getStringExtra(STATION_LAT)),
                Double.parseDouble(mapIntent.getStringExtra(STATION_LNG)));
        String stationName = mapIntent.getStringExtra(STATION_NAME);

        mMap.addMarker(new MarkerOptions().position(
                searchPosition).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.search_marker)));
        mMap.addMarker(new MarkerOptions().position(
                stationPosition).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.station_marker)).title(stationName));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(searchPosition);
        builder.include(stationPosition);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}
