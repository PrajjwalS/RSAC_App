package com.rsac_app;


// Class to create objects of type Place that can be searched under some category
// marked over the map.
public class Place
{

    private double mLatitude,mLongitude;
    private String mAddress,mInfo;

        // Constructor
        public Place(double latitude,double longitude,String address, String info)
        {
           this.mLatitude=latitude;
           this.mLongitude=longitude;
           this.mAddress=address;
           this.mInfo=info;
        }


        // Empty constructor
        public Place()
        {}



        // Getters
        public double getmLatitude()
        {
            return mLatitude;
        }

        public double getmLongitude()
        {
            return mLongitude;
        }

        public String getmAddress()
        {
            return mAddress;
        }

        public String getmInfo()
        {
            return mInfo;
        }
}
