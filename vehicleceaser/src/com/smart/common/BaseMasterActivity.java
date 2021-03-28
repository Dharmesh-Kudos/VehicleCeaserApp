package com.smart.common;

import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.smart.framework.SmartActivity;
import com.vehicleceaser.R;


//Keep and Place Any Code which you want to use and implement from one or many different other classes.
public abstract class BaseMasterActivity extends SmartActivity {

    private static SharedPreferences sharedPreferences;

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public void setAnimations() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

    }

    @Override
    public void postOnCreate() {

    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }

    @Override
    public int getDrawerLayoutID() {
        return R.layout.jom_drawer;
    }

    /**
     * @param coordinatorLayout
     * @param msg
     */
    public void showSnackbar(CoordinatorLayout coordinatorLayout, String msg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.white));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.accent));
        textView.setMaxLines(2);
        snackbar.show();
    }

//    public static void writeSharedPreferences(String key, String value) {
//        SharedPreferences.Editor editor = readSharedPreferences().edit();
//        editor.putString(key, value);
//        editor.commit();
//    }
//
//    public static SharedPreferences readSharedPreferences() {
//        return sharedPreferences;
//    }
}
