package com.vehicleceaser;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.smart.common.BaseMasterActivity;


public class VCHomeActivity extends BaseMasterActivity {

    LinearLayout btnSearchVehicles, btnLocateExecutives, btnSeeSearchHistory;

    @Override
    public int getLayoutID() {
        return R.layout.activity_vc_home;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        btnSearchVehicles = (LinearLayout) findViewById(R.id.btnSearchVehicles);
        btnLocateExecutives = (LinearLayout) findViewById(R.id.btnLocateExecutives);
        btnSeeSearchHistory = (LinearLayout) findViewById(R.id.btnSeeSearchHistory);
    }

    @Override
    public void prepareViews() {
        super.prepareViews();

    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }


    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {

    }

    public void goToSearchVehicle(View view) {
        startActivity(new Intent(VCHomeActivity.this, VCSearchVehicleActivity.class).putExtra("IN_TYPE", 1));
    }

    public void goToLocateExecutives(View view) {
        startActivity(new Intent(VCHomeActivity.this, VCLocateExecutivesActivity.class));
    }

    public void goToSearchHistory(View view) {
        startActivity(new Intent(VCHomeActivity.this, VCSearchHistoryActivity.class));
    }

    public void goToMyProfile(View view) {
        startActivity(new Intent(VCHomeActivity.this, VCUserProfileActivity.class).putExtra("IN_TYPE", 1));
    }

//    private void getBeacons() {
//
//        SmartUtils.showProgressDialog(VCSplashActivity.this, "Fetching Offers...", false);
//        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, VCSplashActivity.this);
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "getAllBeaconData");
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("task", "getBeaconData");
//            JSONObject taskData = new JSONObject();
//            jsonObject.put("taskData", taskData);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
//        Log.d("@@@WSASS", jsonObject.toString());
//        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {
//
//            @Override
//            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
//                SmartUtils.hideProgressDialog();
//                try {
//
//                    if (response.getInt("status_code") == 200) {
//                        Toast.makeText(VCSplashActivity.this, "200", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(VCSplashActivity.this, "500", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onResponseError() {
//                //Do Loop Again
//                SmartUtils.hideProgressDialog();
//            }
//        });
//
//        SmartWebManager.getInstance(VCSplashActivity.this).addToRequestQueueMultipart(requestParams, null, "", false);
//
//    }
}
