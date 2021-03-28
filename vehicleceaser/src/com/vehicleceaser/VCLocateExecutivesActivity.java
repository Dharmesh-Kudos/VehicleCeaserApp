package com.vehicleceaser;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smart.caching.SmartCaching;
import com.smart.framework.Constants;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.smart.framework.SmartUtils.hideProgressDialog;
import static com.smart.framework.SmartUtils.showProgressDialog;

public class VCLocateExecutivesActivity extends Activity implements OnMapReadyCallback, Constants {

    Spinner spnExecList;
    String SEL_EXEC_NAME = "Your";
    GoogleMap myMap;
    String[] execLocations = new String[]{};
    String[] demo_lat = {"23.0225", "19.0760", "13.0827", "12.9716",
            "23.0225", "19.0760", "13.0827", "12.9716",
            "23.0225", "19.0760", "13.0827", "12.9716"};
    String[] demo_long = {"72.5714", "72.8777", "80.2707", "77.5946",
            "72.5714", "72.8777", "80.2707", "77.5946",
            "72.5714", "72.8777", "80.2707", "77.5946"};

    private MapView mMapView;
    private Toolbar toolbar;
    private String OUR_LAT = "23.019835";
    private String OUR_LANG = "72.5447121";
    private com.smart.caching.SmartCaching smartCaching;
    //    private String URL_LIST_OF_EXECUTIVES = "http://13.233.239.93/laravel/api/list-of-executive";
//    private String URL_LOCATE_EXECUTIVE = "http://13.233.239.93/laravel/api/get-location";
//    private String URL_LIST_OF_EXECUTIVES = "http://shruti-collection.viturvainfotech.com/api/list-of-executive";
//    private String URL_LOCATE_EXECUTIVE = "http://shruti-collection.viturvainfotech.com/api/get-location";
    private String TABLE_EXECUTIVES = "executivesTable";
    private ArrayList<ContentValues> executivesData = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vc_locate_executives);
        initComponents(savedInstanceState);
        try {
            prepareViews();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setActionListeners();
    }


    public void initComponents(Bundle savedInstanceState) {


        smartCaching = new SmartCaching(VCLocateExecutivesActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spnExecList = (Spinner) findViewById(R.id.spnExecList);


        //Iniatializing and Setting up Map
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        //Map Setup Ended
    }

    public void prepareViews() throws IOException {
//        execLocations = getResources().getStringArray(R.array.demo_exec_locations);

        getExecutivesList();

    }

    private void getExecutivesList() {

        //Every time when this method is called, we will remove all data from our local DB and update it with new fresh data
        smartCaching.deleteFromTable("DELETE FROM " + TABLE_EXECUTIVES);

        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("page", "1");
            dataObj.put("limit", "10");
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@WSREQ=", obj.toString());

        showProgressDialog(VCLocateExecutivesActivity.this, "Loading Executives...", false);

        SmartUtils.makeWSCall(VCLocateExecutivesActivity.this, 1, URL_LIST_OF_EXECUTIVES, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                hideProgressDialog();
                if (message.equals("success")) {
                    Log.d("@@@RES", response.toString());
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("data"),
                                TABLE_EXECUTIVES);

                        executivesData = smartCaching.getDataFromCache(TABLE_EXECUTIVES);

                        execLocations = new String[executivesData.size() + 1];
                        execLocations[0] = "Choose Executive";
                        if (executivesData != null) {
                            for (int i = 0; i < executivesData.size(); i++) {
                                ContentValues row = executivesData.get(i);
                                execLocations[i + 1] = row.getAsString("first_name") + " " + row.getAsString("last_name");
                            }
                        }
                        ArrayAdapter<String> ExecAdapter =
                                new ArrayAdapter<>(VCLocateExecutivesActivity.this,
                                        R.layout.layout_spinner_item, execLocations);
                        spnExecList.setAdapter(ExecAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    try {
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void locateExecutiveAPI(String userID) {

        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("user_id", userID);
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@WSREQ=", obj.toString());

        showProgressDialog(VCLocateExecutivesActivity.this, "Fetching Location...", false);

        //1-post, 2-get
        SmartUtils.makeWSCall(VCLocateExecutivesActivity.this, 1, URL_LOCATE_EXECUTIVE, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                hideProgressDialog();
                if (message.equals("success")) {
                    Log.d("@@@RES", response.toString());

                    try {
                        if (!response.getString("latitude").equals("") && !response.getString("longitude").equals("")) {
                            setupMap(response.getString("latitude"), response.getString("longitude"), 15.0f);//for initial zoom and animation
                        } else {
                            Toast.makeText(VCLocateExecutivesActivity.this, "Unable to fetch current location.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    hideProgressDialog();
                    try {
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void setupMap(String lat, String lng, float zoomLevel) throws IOException {
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getParent(), requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // LatLng object to store user input coordinates
            LatLng point = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

            // Drawing the marker at the coordinates
            drawMarker(point, lat, lng, zoomLevel);
        }
    }

    public void setActionListeners() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spnExecList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    SEL_EXEC_NAME = spnExecList.getItemAtPosition(position).toString();

                    locateExecutiveAPI(executivesData.get(position - 1).getAsString("id"));
//                    try {
//                        setupMap(demo_lat[position], demo_long[position], 15.0f);//for zoom after selection
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        try {
            setupMap(OUR_LAT, OUR_LANG, 10.0f);//for initial zoom and animation
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Enabling MyLocation Layer of Google Map
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    private void drawMarker(LatLng point, String lat, String lng, float zoomLevel) throws IOException {

        myMap.clear();

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Setting title for the InfoWindow
        markerOptions.title(SEL_EXEC_NAME + " Location :");

        // Setting InfoWindow contents
        markerOptions.snippet(getAddressFromLatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

        // Adding marker on the Google Map
        myMap.addMarker(markerOptions);

        // Moving CameraPosition to the user input coordinates
        myMap.moveCamera(CameraUpdateFactory.newLatLng(point));

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), zoomLevel));
    }

    public String getAddressFromLatLng(double lat, double lng) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        return address;

    }

}
