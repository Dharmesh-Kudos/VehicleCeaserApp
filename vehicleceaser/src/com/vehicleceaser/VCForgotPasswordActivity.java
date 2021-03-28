package com.vehicleceaser;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smart.common.BaseMasterActivity;

public class VCForgotPasswordActivity extends BaseMasterActivity {



    @Override
    public int getLayoutID() {
        return R.layout.activity_vc_forgot_password;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
    }

    public void submitForgotPassword(View view) {

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
