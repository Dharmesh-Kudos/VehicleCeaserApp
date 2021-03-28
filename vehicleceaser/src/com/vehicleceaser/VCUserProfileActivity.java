package com.vehicleceaser;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smart.common.BaseMasterActivity;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.smart.framework.SmartUtils.hideProgressDialog;
import static com.smart.framework.SmartUtils.showProgressDialog;

/**
 * Created by Vipul on 9/9/2018.
 */

public class VCUserProfileActivity extends BaseMasterActivity implements Constants {

    KudosTextView tvUsername, tvName, tvMobileNo, tvEmail;
    private EditText edtOldPassword;
    private EditText edtNewPassword;
    private EditText edtConfNewPassword;
//    private String URL_CHANGE_PWD = "http://13.233.239.93/laravel/api/change-password";
//    private String URL_CHANGE_PWD = "http://shruti-collection.viturvainfotech.com/api/change-password";
    private int IN_TYPE;
    LinearLayout layoutCPWD;

    @Override
    public int getLayoutID() {
        return R.layout.activity_vc_user_profile;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        tvName = (KudosTextView) findViewById(R.id.tvName);
        tvUsername = (KudosTextView) findViewById(R.id.tvUsername);
        tvMobileNo = (KudosTextView) findViewById(R.id.tvMobileNo);
        tvEmail = (KudosTextView) findViewById(R.id.tvEmail);
        layoutCPWD = (LinearLayout) findViewById(R.id.layoutCPWD);
    }

    @Override
    public void prepareViews() {
        super.prepareViews();

        IN_TYPE = getIntent().getIntExtra("IN_TYPE", 0);

        if (IN_TYPE == 2) {//if executive then hide change pwd box
            layoutCPWD.setVisibility(View.GONE);
        }

        try {
            JSONObject obj = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", "{}"));
            tvName.setText("Name - " + obj.getString("first_name") + " " + obj.getString("last_name"));
            tvUsername.setText("Username - " + obj.getString("username"));
            tvMobileNo.setText("Mobile No. - " + obj.getString("mobile_no"));
            tvEmail.setText("Email - " + obj.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void changePassword(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(dialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();// I was told to call show first I am not sure if this it to cause layout to happen so that we can override width?
        dialog.getWindow().setAttributes(lWindowParams);

        Button btnSubmitChangePassword = (Button) dialog.findViewById(R.id.btnSubmitChangePassword);
        edtOldPassword = (EditText) dialog.findViewById(R.id.edtOldPassword);
        edtNewPassword = (EditText) dialog.findViewById(R.id.edtNewPassword);
        edtConfNewPassword = (EditText) dialog.findViewById(R.id.edtConfNewPassword);

        btnSubmitChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call method
                if (allValid()) {
                    JSONObject obj = new JSONObject();
                    JSONObject dataObj = new JSONObject();
                    try {
                        JSONObject objUser = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString("LOGIN_USER_DATA", "{}"));
                        dataObj.put("user_id", objUser.getString("id"));
                        dataObj.put("old_password", edtOldPassword.getText().toString());
                        dataObj.put("new_password", edtNewPassword.getText().toString());
                        obj.put("data", dataObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("@@@ChangePwd=", obj.toString());

                    showProgressDialog(VCUserProfileActivity.this, "Changing Password...", false);

                    SmartUtils.makeWSCall(VCUserProfileActivity.this, 1, URL_CHANGE_PWD, obj, new CustomClickListner() {
                        @Override
                        public void onClick(JSONObject response, String message) {
                            dialog.dismiss();
                            try {
                                if (message.equals("success")) {
                                    hideProgressDialog();
                                    if (response.getString("status").equals("1")) {
                                        Toast.makeText(VCUserProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(VCUserProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                } else {

                                    hideProgressDialog();
                                    try {
                                        Log.d("@@@VOLLEY-ERR", response.getString("error"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        dialog.show();
    }

    private boolean allValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(edtOldPassword.getText().toString())) {
            edtOldPassword.setError("Enter Old Password");
            isValid = false;
        } else if (TextUtils.isEmpty(edtNewPassword.getText().toString())) {
            edtNewPassword.setError("Enter New Password");
            isValid = false;
        } else if (TextUtils.isEmpty(edtConfNewPassword.getText().toString()) || !edtNewPassword.getText().toString()
                .matches(edtConfNewPassword.getText().toString())) {
            edtConfNewPassword.setError("Passwords not matching");
            isValid = false;
        }
        return isValid;
    }

    public void doLogout(View view) {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_LOGIN", false);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("IS_DEMO", false);
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences("LOGIN_USER_DATA", "{}");
        startActivity(new Intent(VCUserProfileActivity.this, VCLoginActivity.class));
        finish();
    }
}
