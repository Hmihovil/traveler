package com.kennethatria.traveller.traveller;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double origin_latitude,origin_longtitude,destination_latitude,destination_longtitude;
    Intent intent;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /** retriving origin and destination coorginates **/

        intent = getIntent();
        extras = intent.getExtras();

        origin_latitude = extras.getDouble("origin_latitude");
        origin_longtitude = extras.getDouble("origin_longtitude");
        destination_latitude = extras.getDouble("destination_latitude");
        destination_longtitude = extras.getDouble("destination_longtitude");

        /** end lat and long coordinates **/

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
/**
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        **/

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(

                        //Toast.makeText(MapsActivity.this, Double.valueOf(origin_latitude).toString()+' '+ Double.valueOf(origin_longtitude).toString()+' '+Double.valueOf(destination_latitude).toString()+' '+Double.valueOf(destination_longtitude).toString()+' ', Toast.LENGTH_LONG).show();
                        new LatLng(origin_latitude, origin_longtitude),
                        new LatLng(destination_latitude, destination_longtitude)));

        // Position the map's camera set to Africa,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.7832, 34.50853), 4));

    }
}
