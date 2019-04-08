package com.example.akshayverma.maplocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private double lat,lng;
    private List<Address> address;
    private SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //get location service
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //map
         mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

       //check location permissions
        getAllPermissions();
        //getting location
        getLocation();

    }


    private void getAllPermissions() {
        //getting permission for first time
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 012);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 012) {
            // if no permission then get permission
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getAllPermissions();
            }else{
                //getting location ,if permission allowed
                getLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //map object reference
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            //geocoder.getFromLocation pass your current lat and lnt
            address = geocoder.getFromLocation(getLat(),getLng(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String currentAddress =address.get(0).getAddressLine(0);

        //Toast the current location
        Toast.makeText(getApplicationContext(),currentAddress,Toast.LENGTH_SHORT).show();

        LatLng sydney = new LatLng(getLat(), getLng());
        //making a marker on map
        mMap.addMarker(new MarkerOptions().position(sydney).title(currentAddress));
        //moving camera focus
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(16.0f); //min zoom
        mMap.setMaxZoomPreference(26.0f); //max zoom
    }

    public void setLng(double lng) {
        this.lng = lng; //setLongitude
    }

    public void setLat(double lat) {
        this.lat = lat; //setLatitude
    }

    public double getLat() {
        return lat; //getLatitude
    }

    public double getLng() {
        return lng; //getLongitude
    }

    private void getLocation() {

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLat(location.getLatitude());
                setLng(location.getLongitude());
                if(mMap!=null){
                    //clear the map if already created a marker
                    mMap.clear();
                }
                //if location got then show current location
                mapFragment.getMapAsync(MapsActivity.this); //this method callback the method onMapReady
              Log.d("Map","Location Changed");
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
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getAllPermissions();
        }
        //minTime is the time interval for getting location as i have set for 5seconds, the location will refresh for every 5sec;
        //minDistance provide you the range of your current location;
        //passing the listener reference

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);
       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, listener);
        //two ways of getting location get location GPS or Network choose any one
    }
}
