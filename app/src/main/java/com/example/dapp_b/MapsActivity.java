package com.example.dapp_b;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        lat = getIntent().getDoubleExtra("lat", 0.0);
        lng = getIntent().getDoubleExtra("lng", 0.0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        btnGetLocation.setOnClickListener(v -> {
            if (mMap != null) {
                LatLng center = mMap.getCameraPosition().target;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat", center.latitude);
                resultIntent.putExtra("lng", center.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (lat != 0.0 && lng != 0.0) {
            LatLng location = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(location).title("Selected Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        }
    }
}
