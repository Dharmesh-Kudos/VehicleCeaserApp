package com.smart.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.smart.weservice.WebkitCookieManagerProxy;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class SmartApplication extends Application {

    public String APP_NAME;
    public String CRASH_HANDLER_FILE_NAME;
    public String SECURITY_KEY;
    public String DATABASE_NAME;
    public String FACEBOOK_APP_ID;
    public String SHARED_PREFERENCE_NAME;
    public String DOMAIN_NAME;
    public String TWITTER_CONSUMER_KEY;
    public String TWITTER_SECRET_KEY;
    public String FONT_NAME;
    public String BOLDFONT_NAME;
    public String GCM_ID;
    public String GCM_VERSION;
    public String MAP_KEY;
    public String LOGFILENAME;
    public String DBSQL;
    public Typeface FONT;
    public Typeface BOLDFONT;

    public boolean IS_DB_ENABLE;
    public boolean IS_CRASH_HANDLER_ENABLE;
    public boolean IS_SHARED_PREFERENCE_ENABLE;
    public boolean IS_DEBUG_ON;

    public boolean IS_HTTP_ALLOW_ACCESS;

    public int DATABASE_VERSION;

    private SmartDataHelper dataHelper;
    public static SmartApplication REF_SMART_APPLICATION;
    private SharedPreferences sharedPreferences;
    private SmartApplicationConfiguration smartApplicationConfiguration;
    private SmartVersionHandler smartVersionHandler;

    private AQuery aQuery;

    static Class<?> a = Activity.class;

    public SmartApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        android.webkit.CookieSyncManager.createInstance(this);
        // unrelated, just make sure cookies are generally allowed
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        // magic starts here
        WebkitCookieManagerProxy coreCookieManager = new WebkitCookieManagerProxy(java.net.CookiePolicy.ACCEPT_ALL);
        java.net.CookieHandler.setDefault(coreCookieManager);

        REF_SMART_APPLICATION = this;

        SmartUtils.setNetworkStateAvailability(this);

        smartApplicationConfiguration = new ApplicationConfiguration();

        loadConfiguration();

        if (IS_SHARED_PREFERENCE_ENABLE) {
            sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        }

        if (IS_DB_ENABLE) {
            try {
                dataHelper = new SmartDataHelper(getApplicationContext(), DATABASE_NAME, DATABASE_VERSION, DBSQL, getSmartVersionHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AQuery getaQuery() {
        if (aQuery == null) {
            aQuery = new AQuery(this);
        }
        AjaxCallback.setReuseHttpClient(true);
        return aQuery;
    }

    /**
     * Version Handler setter for handling application database versions and
     * providing scope for executing statements whenever the application is
     * installed or updated.
     *
     * @param smartVersionHandler = Reference of current Version Handler interface instance.
     */
    public void setSmartVersionHandler(SmartVersionHandler smartVersionHandler) {
        this.smartVersionHandler = smartVersionHandler;
    }

    /**
     * Getter for SmartVersionHandler
     *
     * @return = Instance of SmartVersionHandler
     */
    public SmartVersionHandler getSmartVersionHandler() {
        return smartVersionHandler;
    }

    /**
     * Loads configurations from ApplicationConfiguration Class.
     */
    private void loadConfiguration() {
        APP_NAME = smartApplicationConfiguration.getAppName();
        CRASH_HANDLER_FILE_NAME = smartApplicationConfiguration.getCrashHandlerFileName();
        SECURITY_KEY = smartApplicationConfiguration.getSecurityKey();
        DATABASE_NAME = smartApplicationConfiguration.getDatabaseName();
        DATABASE_VERSION = smartApplicationConfiguration.getDatabaseVersion();
        DBSQL = smartApplicationConfiguration.getDatabaseSQL();
        FACEBOOK_APP_ID = smartApplicationConfiguration.getFacebookAppID();
        SHARED_PREFERENCE_NAME = smartApplicationConfiguration.getAppName();
        DOMAIN_NAME = smartApplicationConfiguration.getDomain();
        TWITTER_CONSUMER_KEY = smartApplicationConfiguration.getTwitterConsumerKey();
        TWITTER_SECRET_KEY = smartApplicationConfiguration.getTwitterSecretKey();

        FONT_NAME = smartApplicationConfiguration.getFontName();
        BOLDFONT_NAME = smartApplicationConfiguration.getBoldFontName();

        GCM_ID = smartApplicationConfiguration.getGCMProjectId();
        GCM_VERSION = smartApplicationConfiguration.getGCMVersion();
        MAP_KEY = smartApplicationConfiguration.getMapAPIKey();
        LOGFILENAME = smartApplicationConfiguration.getCrashHandlerFileName();

        IS_DB_ENABLE = smartApplicationConfiguration.isDBEnable();
        IS_CRASH_HANDLER_ENABLE = smartApplicationConfiguration.isCrashHandlerEnable();
        IS_SHARED_PREFERENCE_ENABLE = smartApplicationConfiguration.isSharedPrefrenceEnable();
        IS_DEBUG_ON = smartApplicationConfiguration.isDebugOn();
        IS_HTTP_ALLOW_ACCESS = smartApplicationConfiguration.isHttpAccessAllow();

        if (smartApplicationConfiguration instanceof SmartVersionHandler) {
            smartVersionHandler = (SmartVersionHandler) smartApplicationConfiguration;
        }
    }

    /**
     * This method will set object of <b>SmartDataHelper</b> to framework. If
     * not set, it will use the default created by framework itself.
     *
     * @param dataHelper = Object of SmartDataHelper class
     */
    public void setDataHelper(SmartDataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

    /**
     * This method will return instance of <b>SharedPreferences</b> generated by
     * SmartFramework. Framework will use SharedPreference name as given in
     * <b>ApplicationConfiguration</b> for generation of SharedPreference.
     * <b>Note</b> : SharedPreference Mode will be private whenever generated by
     * SmartFramework.
     *
     * @return sharedPreferences = Instance of SharedPreferences created by
     * SmartFramework.
     */
    public SharedPreferences readSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = String <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = boolean <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, boolean value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = float <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, float value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = int <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, int value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * This method will write to <b>SharedPreferences</b>.
     *
     * @param key   = String <b>key</b> to store in <b>SharedPreferences</b>.
     * @param value = long <b>value</b> to store in <b>SharedPreferences</b>.
     */
    public void writeSharedPreferences(String key, long value) {
        SharedPreferences.Editor editor = readSharedPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * This method will return instance of <b>SmartDataHelper</b> which is
     * currently being used by the SmartFramework.<br>
     * This method will return <b>null</b>, if <b>isDBEnabled</b> flag is false
     * in <b>ApplicationConfiguration</b>.
     *
     * @return dataHelper = Instance of <b>SmartDataHelper</b>.
     */
    public SmartDataHelper getDataHelper() {
        if (IS_DB_ENABLE)
            return dataHelper;
        else
            return null;
    }

    @Override
    public void onTerminate() {
        if (IS_DB_ENABLE)
            dataHelper.getDB().close();
        super.onTerminate();
    }

    public boolean isAppForground() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> i = l.iterator();
        while (i.hasNext()) {
            RunningAppProcessInfo info = i.next();
            if (info.uid == getApplicationInfo().uid && info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    android.util.Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
