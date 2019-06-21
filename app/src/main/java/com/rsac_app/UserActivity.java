package com.rsac_app;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class UserActivity extends FragmentActivity implements OnMapReadyCallback
{
    //constants:
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1;
    // variables:
    GoogleMap mGoogleMap;
    Marker mUserLocationMarker;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    String LOCATION_PROVIDER= LocationManager.GPS_PROVIDER;
    final int REQUEST_CODE=123;
    //layout variables:
    AutoCompleteTextView mSearchView;
    //
    ArrayList<Marker> markersArray;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        markersArray= new ArrayList<Marker>();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragent_map);
        mapFragment.getMapAsync(this);
        mSearchView=findViewById(R.id.autoTextInput_search);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mGoogleMap=googleMap;
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        // putting a marker on current Location:
        updateCurrentLocationCoordinates();

    }


    // To run when Search is Pressed:
        public void searchDB(View V)
        {
            if(TextUtils.isEmpty(mSearchView.getText()))
                Toast.makeText(UserActivity.this,"Please, Input Something to Search",Toast.LENGTH_SHORT).show();
            else
            {
                DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference(mSearchView.getText().toString());
                mDatabaseReference.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot snapshot)
                    {
                        if(snapshot.exists()==false)
                            Toast.makeText(UserActivity.this,"Sorry \""+mSearchView.getText().toString()+"\" is not present in Database",Toast.LENGTH_SHORT).show();
                        else
                        {

                            resetMarkers();
                            for (DataSnapshot i : snapshot.getChildren())
                            {
                                 Place place=i.getValue(Place.class);
                                 LatLng loc = new LatLng(place.getmLatitude(),place.getmLongitude());
                                 markersArray.add(mGoogleMap.addMarker(new MarkerOptions().position(loc).snippet("Address: "+place.getmAddress()+"\n"+"Info: "+place.getmInfo()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Some Title")));
                            }
                            Toast.makeText(UserActivity.this,markersArray.size()+" Results were found and Marked on the map",Toast.LENGTH_SHORT);

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }


        }
    public void resetMarkers()
    {
        for(int i=0;i<markersArray.size();i++)
        {
            markersArray.get(i).remove();
        }
        markersArray.clear();
    }

    // updates the Current User's Position Marker The Green Marker
    private void updateCurrentPositionMarker(double mCurrentLatitude, double mCurrentLongitude)
    {

        LatLng currentLocation = new LatLng(mCurrentLatitude,mCurrentLongitude);
        //mMap.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("I am Here!"));
        if(mUserLocationMarker==null)
        {
            mUserLocationMarker=mGoogleMap.addMarker(new MarkerOptions().position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("I am Here!"));

            Log.d("hey","User Location Marker was set for the first time, camera: animates");
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
//            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
            // Zoom in, animating the camera.
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        }
        else
        {
            mUserLocationMarker.setPosition(currentLocation);
            Log.d("hey","User Location Marker got shifted, No camera animation");
        }


    }
    // gets the devices current location and initializes & sets view for mLatitude and mLongitude
    private void updateCurrentLocationCoordinates()
    {

        mLocationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener=new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.d("hey","On LocationChanged() got a callback");
                // calling method to update currentpositionmarker
                updateCurrentPositionMarker(location.getLatitude(),location.getLongitude());

                Toast.makeText(UserActivity.this,"Current Location Just Got Updated",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("hey","onProviderEnabled got a call back");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("hey","onProviderDisabled() got a callback");

            }
        };


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER,MIN_TIME,MIN_DISTANCE,mLocationListener);



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED )
            {
                Log.d("hey","onReqPerRes(): Permission is granted");
                updateCurrentLocationCoordinates();
            }
            else
            {
                Log.d("hey","onReqPerRes(): Perimissin is denied");
            }
        }
    }
}
