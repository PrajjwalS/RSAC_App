package com.rsac_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity
{




    private TextView mLatitudeView, mLongitudeView;
    private AutoCompleteTextView mAddressView, mInfoView,mCategoryView;
    private DatabaseReference mDatabaseRefrence;

    // Firebase instance variables
//    private DatabaseReference mDatabaseReference;

    // Location seeking variables
    private double mLatitude,mLongitude;

    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 3000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 5;

    String LOCATION_PROVIDER= LocationManager.GPS_PROVIDER;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    final int REQUEST_CODE=123;

    protected DrawerLayout dl;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initializing the variables



//        if(mDatabaseReference==null)
//            Log.d("hey","yo null!!1");
        mLatitudeView = findViewById(R.id.textView_latitude);
        mLongitudeView = findViewById(R.id.textView_longitude);
        mAddressView = findViewById(R.id.autoTextInput_address);
        mInfoView = findViewById(R.id.autoTextInput_info);
        mCategoryView = findViewById(R.id.autoTextInput_category);
        setCurrentLatLong();



    }


    // to run when Save to database Button is tapped:
    public void saveToDB(View V)
    {
        if(TextUtils.isEmpty(mAddressView.getText()) || TextUtils.isEmpty(mInfoView.getText()) || TextUtils.isEmpty(mCategoryView.getText()))
            Toast.makeText(AdminActivity.this,"Please Fill the Address and Info field ",Toast.LENGTH_SHORT).show();

        else
        {
            // Making a Place object to save onto database
            Place place = new Place(mLatitude,mLongitude,mAddressView.getText().toString(),mInfoView.getText().toString());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();
            String category=mCategoryView.getText().toString();
            myRef.child(category).push().setValue(place);
            Toast.makeText(AdminActivity.this,"Entered Location was added to DB",Toast.LENGTH_SHORT).show();
        }

    }


    // gets the devices current location and initializes & sets view for mLatitude and mLongitude
    private void setCurrentLatLong()
    {
        mLocationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener=new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.d("hey","On LocationChanged() got a callback");
                mLatitude=location.getLatitude();
                mLongitude=location.getLongitude();
                mLatitudeView.setText(String.valueOf(mLatitude));
                mLongitudeView.setText(String.valueOf(mLongitude));
                Toast.makeText(AdminActivity.this,"Current Location Just Got Updated",Toast.LENGTH_SHORT).show();
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
                setCurrentLatLong();
            }
            else
            {
                Log.d("hey","onReqPerRes(): Perimissin is denied");
            }
        }
    }
}
