package com.vehicleceaser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.smart.framework.SmartApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class VCSplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vc_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean("IS_DEMO", false)) {//This is for DEMO
                    startActivity(new Intent(VCSplashActivity.this, VCHomeActivity.class).putExtra("IN_TYPE", 1));
                } else {
                    if (SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean("IS_LOGIN", false)) {//If Already Login is True
                        try {
                            JSONObject obj = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", "{}"));
                            if (obj.getString("role_id").equals("1")) {//Admin - 1
                                startActivity(new Intent(VCSplashActivity.this, VCHomeActivity.class).putExtra("IN_TYPE", 1));
                            } else {//Executive - 2
                                startActivity(new Intent(VCSplashActivity.this, VCSearchVehicleActivity.class).putExtra("IN_TYPE", 2));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {//If not already Logged in
                        startActivity(new Intent(VCSplashActivity.this, VCLoginActivity.class));
                        finish();
                    }
                }

            }
        }, 1000);

    }

}
