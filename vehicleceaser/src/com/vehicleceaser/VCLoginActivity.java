package com.vehicleceaser;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.smart.common.BaseMasterActivity;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.smart.framework.SmartUtils.hideProgressDialog;
import static com.smart.framework.SmartUtils.showProgressDialog;

public class VCLoginActivity extends BaseMasterActivity implements Constants {


    private int IN_TYPE = 1;//1-admin, 2-exec
    //    private String URL_LOGIN = "http://13.233.239.93/laravel/api/login";
//    private String URL_LOGIN = "http://shruti-collection.viturvainfotech.com/api/login";
    EditText edtMobileNumber, edtPassword;

    @Override
    public int getLayoutID() {
        return R.layout.activity_vc_login_demo;
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
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

    public void submitLogin(View view) {

        if (allValid()) {
            if (edtMobileNumber.getText().toString().equals("8000913849") && edtPassword.getText().toString().equals("admin@123")) {
                //For Demo Purpose
                //Open Admin Side
                IN_TYPE = 1;//1-Admin
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_DEMO", true);
                startActivity(new Intent(VCLoginActivity.this, VCHomeActivity.class).putExtra("IN_TYPE", IN_TYPE));
            } else {// 2-Executive
                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_DEMO", false);
                doLogin(edtMobileNumber.getText().toString(), edtPassword.getText().toString());
            }
        }

    }

    public void openForgotPassword(View view) {
        startActivity(new Intent(VCLoginActivity.this, VCForgotPasswordActivity.class));
    }

    private void doLogin(final String mobileNumber, final String password) {
        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("mobile_no", mobileNumber);
            dataObj.put("password", password);
            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@LOGIN=", obj.toString());

        showProgressDialog(VCLoginActivity.this, "Logging in...", false);

        SmartUtils.makeWSCall(VCLoginActivity.this, 1, URL_LOGIN, obj, new CustomClickListner() {
            @Override
            public void onClick(JSONObject response, String message) {
                hideProgressDialog();
                if (message.equals("success")) {
                    Log.d("@@@RES", response.toString());
                    try {
                        Toast.makeText(VCLoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        if (response.getString("status").equals("1")) {
                            JSONObject obj = new JSONObject(response.getString("data"));
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOGIN", true);
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("LOGIN_USER_DATA", String.valueOf(obj));
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOADED", false);
                            if (obj.getString("role_id").equals("1")) {//Admin - 1
                                IN_TYPE = 1;
                                startActivity(new Intent(VCLoginActivity.this, VCHomeActivity.class).putExtra("IN_TYPE", IN_TYPE));
                            } else {//Executive - 2
                                IN_TYPE = 2;
                                startActivity(new Intent(VCLoginActivity.this, VCSearchVehicleActivity.class).putExtra("IN_TYPE", IN_TYPE));
                            }
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (message.equals("nothing")) {
                    try {
                        Toast.makeText(VCLoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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

//        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("@@@RES", response.toString());
//                        try {
//                            Toast.makeText(VCLoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
//                            if (response.getString("status").equals("1")) {
//                                JSONObject obj = new JSONObject(response.getString("data"));
//                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOGIN", true);
//                                SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("LOGIN_USER_DATA", String.valueOf(obj));
//                                if (obj.getString("role_id").equals("2")) {//Admin
//                                    IN_TYPE = 1;
//                                    startActivity(new Intent(VCLoginActivity.this, VCHomeActivity.class).putExtra("IN_TYPE", IN_TYPE));
//                                } else {//Executive
//                                    IN_TYPE = 2;
//                                    startActivity(new Intent(VCLoginActivity.this, VCSearchVehicleActivity.class).putExtra("IN_TYPE", IN_TYPE));
//                                }
//                                finish();
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        hideProgressDialog();
//                        Log.d("@@@VOLLEY-ERR", error.toString());
//                    }
//                }) {
//
//        };
//
//        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjRequest);

    }

    private boolean allValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(edtMobileNumber.getText().toString())) {
            edtMobileNumber.setError("Enter Mobile Number");
            isValid = false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Enter Password");
            isValid = false;
        }
        return isValid;
    }
}

