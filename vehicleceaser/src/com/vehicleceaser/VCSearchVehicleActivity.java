package com.vehicleceaser;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.smart.caching.SmartCaching;
import com.smart.common.BaseMasterActivity;
import com.smart.framework.AlertNeutral;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class VCSearchVehicleActivity extends BaseMasterActivity implements Constants {

//    String URL_LIST_OF_BIKE = "http://13.233.239.93/laravel/api/list-of-bike";
//    String URL_SEARCH_BIKE = "http://13.233.239.93/laravel/api/search-bike";
//    String URL_LIST_OF_BIKE = "http://shruti-collection.viturvainfotech.com/api/list-of-bike";
//    String URL_SEARCH_BIKE = "http://shruti-collection.viturvainfotech.com/api/search-bike";
    EditText edtSearchTerm;
    Spinner spnSearchTerms;
    String SEARCH_TERM = "";
    RecyclerView rvVehiclesList;
    LinearLayoutManager layoutManager;

    private int IN_TYPE;
    private static final int REQUEST_CODE = 101;

    SmartCaching smartCaching;
    private String TABLE_VEHICLES = "vehicleTable";
    private ArrayList<ContentValues> vehiclesData = new ArrayList<>();
    private String IN_REG_NO = "";
    private String IN_CHASIS_NO = "";
    private String IN_ENGINE_NO = "";
    private HashMap<String, ArrayList<ContentValues>> data;

    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;

    PaginationAdapter adapter;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int IN_TOTAL_PAGES;
    private int currentPage = PAGE_START;

    private String IN_TAG = "VCSearchVehicle";
    private static final String TAG = "VCSearchVehicleActivity";

    @Override
    public int getLayoutID() {
        return R.layout.activity_vc_search_vehicle;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();

        smartCaching = new SmartCaching(VCSearchVehicleActivity.this);

        edtSearchTerm = (EditText) findViewById(R.id.edtSearchTerm);
        spnSearchTerms = (Spinner) findViewById(R.id.spnSearchTerms);
        progressBar = (ProgressBar) findViewById(R.id.prgBarLoading);

        rvVehiclesList = (RecyclerView) findViewById(R.id.rvVehiclesList);

        IN_TYPE = getIntent().getIntExtra("IN_TYPE", 0);
        adapter = new PaginationAdapter(VCSearchVehicleActivity.this, IN_TYPE);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvVehiclesList.setLayoutManager(linearLayoutManager);

        rvVehiclesList.setItemAnimator(new DefaultItemAnimator());

        rvVehiclesList.setAdapter(adapter);


        rvVehiclesList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                Log.d(IN_TAG, "in loadMoreItems");
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return IN_TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }


    @Override
    public void prepareViews() {
        super.prepareViews();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        if (getIntent().hasExtra("IN_TYPE")) {
            if (IN_TYPE == 2) {//Executive
                final PermissionManager permissionManager = new PermissionManager();

                if (!permissionManager.userHasPermission(VCSearchVehicleActivity.this)) {
                    permissionManager.requestPermission(VCSearchVehicleActivity.this);
                } else {
                    if (displayLocationSettingsRequest(this))//It will ask user to switch on location of device
                    {
                        //It will start the background service to fetch current lat long if the user is executive
                        startService(new Intent(VCSearchVehicleActivity.this, VehicleService.class));
                    }
                }
            }
        }


//        SEARCH_TERM = CHASIS_NO;
//        IN_CHASIS_NO = "MD2A57";
//        currentPage = 1;
//        loadFirstPage();
    }


    @Override
    public void setActionListeners() {
        super.setActionListeners();


        spnSearchTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    vehiclesData = new ArrayList<ContentValues>();
                    edtSearchTerm.setText("");

                    IN_CHASIS_NO = "";
                    IN_ENGINE_NO = "";
                    IN_REG_NO = "";

                    switch (position) {
                        case 1:
                            edtSearchTerm.setHint("Enter Chassis Number");
                            SEARCH_TERM = CHASIS_NO;
                            break;
                        case 2:
                            edtSearchTerm.setHint("Enter Engine Number");
                            SEARCH_TERM = ENGINE_NO;
                            break;
                        case 3:
                            edtSearchTerm.setHint("Enter Registration Number");
                            SEARCH_TERM = REG_NO;
                            break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void loadFirstPage() {

        Log.d(IN_TAG, "loadFirstPage: ");

        searchBikesApiCall();

    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    private void loadNextPage() {

        Log.d(IN_TAG, "loadNextPage: " + currentPage);

        searchBikesApiCallNext();

    }


    private void searchBikesApiCall() {

        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put(PAGE, currentPage);
            dataObj.put(REG_NO, IN_REG_NO);
            dataObj.put(CHASIS_NO, IN_CHASIS_NO);
            dataObj.put(ENGINE_NO, IN_ENGINE_NO);

            if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean("IS_DEMO", false)) {
                dataObj.put(USER_ID, "9");
            } else {
                JSONObject objUser = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", "{}"));
                dataObj.put(USER_ID, objUser.getString("id"));
            }


            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("@@@WsReqURL=", URL_SEARCH_BIKE);
        Log.d("@@@WsReqSearchVehicle=", obj.toString());

        SmartUtils.showProgressDialog(VCSearchVehicleActivity.this, "Searching...", true);

        SmartUtils.makeWSCall(VCSearchVehicleActivity.this, 1, URL_SEARCH_BIKE, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                SmartUtils.hideProgressDialog();
                Log.d(TAG, "WSRES : "+response);
                Log.d(TAG, "WSMESSAGE : "+message);
                if (message.equals("success")) {
                    Log.d("@@@BAR=", "In If");
                    adapter.clear();
                    progressBar.setVisibility(View.GONE);
                    Log.d("@@@WsRes", response.length() + " ^^^ " + response.toString());
                    try {

                        smartCaching.deleteDataFromCache("SELECT * FROM " + TABLE_VEHICLES);

                        IN_TOTAL_PAGES = response.getInt(TOTAL_PAGES);
                        Log.d(TAG, "TOTAL_PAGE : "+IN_TOTAL_PAGES);
                        smartCaching.cacheResponse(response.getJSONArray("data"),
                                TABLE_VEHICLES, true);

                        adapter.addAll(smartCaching.getDataFromCache(TABLE_VEHICLES));

                        rvVehiclesList.setAdapter(adapter);

                        if (currentPage <= IN_TOTAL_PAGES && IN_TOTAL_PAGES > 1) {
                            adapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }


//                        JSONObject objUser = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", "{}"));
//
//                        if (objUser.getString("role_id").equals("1")) {//Admin
//                            getDataAndShowData("1");
//                        } else {//Executive
//                            getDataAndShowData("2");
//                        }
//
//                        if (currentPage <= IN_TOTAL_PAGES) adapter.addLoadingFooter();
//                        else isLastPage = true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("nothing")) {
                    Log.d("@@@BAR=", "In Else If");
                    try {
                        if (adapter.isEmpty()) {//If User is deleted

                            if(response.getString("is_login").equals("0")){

                                SmartUtils.getOKDialog(VCSearchVehicleActivity.this, "Account Deleted !!!", "Your account is deleted by admin.",
                                        "Ok", false, new AlertNeutral() {
                                    @Override
                                    public void NeutralMathod(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                        //Logout kara do
                                        doLogout();

                                    }
                                });

                            }else{

                                Toast.makeText(VCSearchVehicleActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            adapter.removeLoadingFooter();
                            isLastPage = true;
                            Toast.makeText(VCSearchVehicleActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("@@@BAR=", "In else");
                    adapter.removeLoadingFooter();
                    isLastPage = true;
                    try {
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void doLogout() {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOGIN", false);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_DEMO", false);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("LOGIN_USER_DATA", "{}");
        startActivity(new Intent(VCSearchVehicleActivity.this, VCLoginActivity.class));
        finish();
    }

    private void searchBikesApiCallNext() {


        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put(PAGE, currentPage);
            dataObj.put(REG_NO, IN_REG_NO);
            dataObj.put(CHASIS_NO, IN_CHASIS_NO);
            dataObj.put(ENGINE_NO, IN_ENGINE_NO);
            if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean("IS_DEMO", false)) {
                dataObj.put(USER_ID, "9");
            } else {
                JSONObject objUser = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", "{}"));
                dataObj.put(USER_ID, objUser.getString("id"));
            }
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@WsReqSearchVehicle=", obj.toString());


        SmartUtils.makeWSCall(VCSearchVehicleActivity.this, 1, URL_SEARCH_BIKE, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                if (message.equals("success")) {
                    Log.d("@@@BAR2=", "In If");
                    adapter.removeLoadingFooter();
                    isLoading = false;

                    Log.d("@@@WsRes", response.length() + " ^^^ " + response.toString());
                    try {

                        IN_TOTAL_PAGES = response.getInt(TOTAL_PAGES);

                        smartCaching.cacheResponse(response.getJSONArray("data"),
                                TABLE_VEHICLES, true);

                        adapter.addAll(smartCaching.getDataFromCache(TABLE_VEHICLES));

                        adapter.notifyDataSetChanged();

                        if (currentPage != IN_TOTAL_PAGES) adapter.addLoadingFooter();
                        else isLastPage = true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("nothing")) {
                    Log.d("@@@BAR2=", "In Else If");
                    try {
                        adapter.removeLoadingFooter();
                        isLastPage = true;
                        Toast.makeText(VCSearchVehicleActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("@@@BAR2=", "In Else");
                    adapter.removeLoadingFooter();
                    isLastPage = true;
                    try {
//                        Toast.makeText(VCSearchVehicleActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

//    private void fetchVehiclesData() {
//
//        //Every time when this method is called, we will remove all data from our local DB and update it with new fresh data
//        smartCaching.deleteFromTable("DELETE FROM " + TABLE_VEHICLES);
//
//        JSONObject obj = new JSONObject();
//        JSONObject dataObj = new JSONObject();
//        try {
//            dataObj.put("page", "1");
//            dataObj.put("limit", "10");
//            obj.put("data", dataObj);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.d("@@@WSREQ=", obj.toString());
//
//        showProgressDialog(VCSearchVehicleActivity.this, "Loading Data...", false);
//
//        SmartUtils.makeWSCall(VCSearchVehicleActivity.this, 1, URL_LIST_OF_BIKE, obj, new CustomClickListner() {
//            @Override
//            public void onClick(JSONObject response, String message) {
//                hideProgressDialog();
//                if (message.equals("success")) {
//                    Log.d("@@@RES", response.toString());
//                    try {
//                        smartCaching.cacheResponse(response.getJSONArray("data"),
//                                TABLE_VEHICLES);
//                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOADED", true);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOADED", false);
//
//                    }
//                } else {
//                    hideProgressDialog();
//                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOADED", false);
//                    try {
//                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        });
//
//    }



    public void searchVehicle(View view) {

//        try{
//            edtSearchTerm.clearFocus();
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                imm.hideSoftInputFromWindow(edtSearchTerm.getWindowToken(), 0);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        SmartUtils.hideSoftKeyboard(VCSearchVehicleActivity.this);


        if (edtSearchTerm.getText().length() > 0 && SEARCH_TERM.length() > 0) {

            switch (SEARCH_TERM) {
                case CHASIS_NO:
                    IN_CHASIS_NO = edtSearchTerm.getText().toString();
                    break;
                case REG_NO:
                    IN_REG_NO = edtSearchTerm.getText().toString();
                    break;
                case ENGINE_NO:
                    IN_ENGINE_NO = edtSearchTerm.getText().toString();
                    break;
            }

            isLastPage = false;
            isLoading = false;
            currentPage = 1;
            loadFirstPage();

        } else {

            edtSearchTerm.setError("Please enter something Or Choose Search Term");

        }

    }


//    public void getDataAndShowData(String USERTYPE) {
//
////        if (USERTYPE.equals("1")) {
////            vehiclesData = smartCaching.getDataFromCacheForAdmin(TABLE_VEHICLES);
////        } else {
////            vehiclesData = smartCaching.getDataFromCacheForExec(TABLE_VEHICLES);
////        }
//
////        vehiclesData = data.get(TABLE_VEHICLES);
////
////        if (vehiclesData != null && vehiclesData.size() > 0) {
////            rvVehiclesAdapter = new RVVehiclesAdapter();
////            rvVehiclesList.setAdapter(rvVehiclesAdapter);
////        } else {
////            Toast.makeText(VCSearchVehicleActivity.this, "No Vehicles Found.", Toast.LENGTH_SHORT).show();
////        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (displayLocationSettingsRequest(this))//It will ask user to switch on location of device
            {
                startService(new Intent(VCSearchVehicleActivity.this, VehicleService.class));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vc_search_vehicle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {

            startActivity(new Intent(VCSearchVehicleActivity.this, VCUserProfileActivity.class).putExtra("IN_TYPE", IN_TYPE));
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

//        menu.findItem(R.id.action_refresh).setVisible(true);
        if (IN_TYPE == 2) {//Executive
            menu.findItem(R.id.action_profile).setVisible(true);
        } else {//Admin
            menu.findItem(R.id.action_profile).setVisible(false);
        }
        return true;
    }


    private boolean displayLocationSettingsRequest(Context context) {
        final boolean[] isLocationOn = new boolean[1];

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(10000 / 4);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        isLocationOn[0] = true;
                        Log.i(IN_TAG, "All location settings are satisfied.");
                        Toast.makeText(VCSearchVehicleActivity.this, "All location settings are satisfied.", Toast.LENGTH_SHORT).show();
                        //fetchVehiclesData();
                        startService(new Intent(VCSearchVehicleActivity.this, VehicleService.class));
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        isLocationOn[0] = false;
//                        fetchVehiclesData();
                        startService(new Intent(VCSearchVehicleActivity.this, VehicleService.class));
                        Log.i(IN_TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//                        Toast.makeText(VCSearchVehicleActivity.this, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ", Toast.LENGTH_SHORT).show();
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(VCSearchVehicleActivity.this, 10);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(IN_TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        isLocationOn[0] = false;
                        Log.i(IN_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
        return isLocationOn[0];
    }


    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        if (getIntent().hasExtra("IN_TYPE") && getIntent().getIntExtra("IN_TYPE", 0) == 1) {//If Admin Login
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportFinishAfterTransition();
                }
            });
        }

    }

}
