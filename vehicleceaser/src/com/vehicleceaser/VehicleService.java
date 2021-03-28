package com.vehicleceaser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

//import static com.google.android.gms.internal.zzahg.runOnUiThread;

/**
 * Created by Vipul on 9/1/2018.
 */

public class VehicleService extends Service implements Constants {

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30 * 1000;
    private static final float LOCATION_DISTANCE = 10;
//    private String URL_UPDATE_LOC = "http://13.233.239.93/laravel/api/update-location";
//    private String URL_UPDATE_LOC = "http://shruti-collection.viturvainfotech.com/api/update-location";

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location.getLatitude() + "/" + location.getLongitude());
//            Toast.makeText(VehicleService.this, "onLocationChanged: " + location.getLatitude() + "/" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            sendUpdatedLocation(location.getLatitude(), location.getLongitude());
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private void sendUpdatedLocation(double latitude, double longitude) {
        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {

            JSONObject loginObj = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", ""));
            dataObj.put("user_id", loginObj.getString("id"));
            dataObj.put("latitude", latitude);
            dataObj.put("longitude", longitude);
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@LOC-UPDATE=", obj.toString());


        SmartUtils.makeWSCall(this, 1, URL_UPDATE_LOC, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                if (message.equals("success")) {
                    Log.d("@@@WsRes", response.toString());
                } else {
                    try {
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");


        new VCSearchVehicleActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeLocationManager();
                try {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                            mLocationListeners[0]);
                } catch (java.lang.SecurityException ex) {
                    Log.i(TAG, "fail to request location update, ignore", ex);
                } catch (IllegalArgumentException ex) {
                    Log.d(TAG, "gps provider does not exist " + ex.getMessage());
                }
            }
        });

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


//                runOnUiThread(new Runnable() {
//                    public void run() {
                // do your work right here
//                        initializeLocationManager();
//                        try {
//                            mLocationManager.requestLocationUpdates(
//                                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                                    mLocationListeners[0]);
//                        } catch (java.lang.SecurityException ex) {
//                            Log.i(TAG, "fail to request location update, ignore", ex);
//                        } catch (IllegalArgumentException ex) {
//                            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//                        }
//                    }
//                });

            }
        }, 1000, 5000);
        //1000ms is delay means the timer will start after 1000 ms(1 sec)
        //5000ms is period means the code under the timer will be executed again in every 5000ms(5 sec)

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}

