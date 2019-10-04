package com.cic.mappermission;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private LocationManager locationManager = null;
    private LatLng currentLocation = new LatLng(19.432608, -99.133208);
    private boolean moveCameraCurrentLocation = true;
    private static final int DEFAULT_ZOOM_LEVEL = 10;
    private int timeUpdateLocation = 2000;
    private float distanceUpateLocation = (float) 0.05;
    private GoogleMap googleMap;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGoogleMaps();
    }

    private void initGoogleMaps() {
        //Obtain the SupportMapFragment and get notified whem the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Mexico City and move the camera
        LatLng mexicoCity = new LatLng(-99.133209, 19.432608);
        this.googleMap.addMarker(new MarkerOptions().position(mexicoCity).title("Marker in Sydney"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(mexicoCity));
        initLocationService();
    }

    private void initLocationService() {
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.d("initLocationService", "Registrando Servicio....");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, 10);
                return;
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    timeUpdateLocation, distanceUpateLocation, this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    timeUpdateLocation, distanceUpateLocation, this);
                return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // This method is called when the location changes.
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        currentLocation = new LatLng(latitude, longitude);
        Log.d("onLocationChanged", "Latitud:" + latitude + " Longitud:" + longitude);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Posición Actual")
                .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
        if(moveCameraCurrentLocation) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
        TextView latitud = (TextView) findViewById(R.id.latitud);
        TextView longitud = (TextView) findViewById(R.id.longitud);
        latitud.setText("Latitud: "+df.format(latitude));
        longitud.setText("Longitud: "+df.format(longitude));
    }
}
