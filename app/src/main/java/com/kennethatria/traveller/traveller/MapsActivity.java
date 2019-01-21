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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /** retriving origin and destination coorginates **/

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        origin_latitude = extras.getDouble("origin_latitude");
        origin_longtitude = extras.getDouble("origin_longtitude");
        destination_latitude = extras.getDouble("destination_latitude");
        destination_longtitude = extras.getDouble("destination_longtitude");

        /** end lat and long coordinates **/

        /** enabling back button on map activity **/


        getActionBar().setDisplayHomeAsUpEnabled(true); // In `OnCreate();`






        /** end : back button **/
    }
    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /**mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        **/

        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng( origin_latitude, origin_longtitude),
                        //new LatLng(-34.747, 145.592),
                        //new LatLng(-34.364, 147.891),
                        //new LatLng(-33.501, 150.217),
                        new LatLng(destination_latitude, destination_longtitude)));


        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.7832, 34.50853), 4));

        // Set listeners for click events.
        //googleMap.setOnPolylineClickListener(this);
        //googleMap.setOnPolygonClickListener(this);
    }
}
