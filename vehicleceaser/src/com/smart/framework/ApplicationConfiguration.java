package com.smart.framework;

import android.content.Context;
import android.util.Log;


import java.io.File;

/**
 * This Class Contains All Method Related To ApplicationConfiguration.
 *
 * @author tasol
 *         NOTE : currently this class not used,future development
 */

public class ApplicationConfiguration implements SmartApplicationConfiguration, SmartVersionHandler {

    private static final String TAG = "ApplicationConfig";

    @Override
    public String getGCMProjectId() {
        return "725447998213";
    }

    @Override
    public String getCrashHandlerFileName() {
        return getAppName() + "log.file";
    }

    @Override
    public boolean IsCrashHandlerEnabled() {
        return false;
    }

    @Override
    public String getAppName() {
        return "BaseApp";
    }

    @Override
    public boolean IsSharedPreferenceEnabled() {
        return true;
    }

    @Override
    public String getSecurityKey() {
        return "901f15a565f8eac8265bacede4b1c17";
    }

    @Override
    public String getDatabaseName() {
        return "BaseApp";
    }

    @Override
    public int getDatabaseVersion() {
        return 1;
    }

    @Override
    public String getFacebookAppID() {
        return "431844313570473";
    }

    @Override
    public String getDomain() {
        return "http://tasolglobal.com/demonstrator/public/webservice";
    }

    @Override
    public String getTwitterConsumerKey() {
        return "ACGuGZRQI4rASvX4uHgDw";
    }

    @Override
    public String getTwitterSecretKey() {
        return "n2zv5dXGbvav3FCb63sk3rIYH8zz74is69dUkINlsgg";
    }

    @Override
    public String getFontName() {
        return "fonts/Roboto-Regular.ttf";
    }

    @Override
    public String getBoldFontName() {
        return "fonts/Roboto-Bold.ttf";
    }

    @Override
    public String getGCMID() {
        return "265622724567";
    }

    @Override
    public String getGCMVersion() {
        return "4030500";
    }

    @Override
    public String getMapAPIKey() {
        return "AIzaSyAmf-z2ZOnfA5p7xLvkiYltjyTeY3_rBa8";
    }

    @Override
    public String getAppMetadataStoragePath(Context context, String extension) {
        if (shouldStoreAppDataInExternalStorage()) {
            String directoryPath = SmartUtils.createExternalDirectory(SmartUtils.removeSpecialCharacter(getAppName()));
            if (directoryPath != null) {
                StringBuilder fileName = new StringBuilder(directoryPath).append(File.separator).append(SmartUtils.removeSpecialCharacter(getAppName())).append(System.currentTimeMillis()).append(".").append(extension);
                return fileName.toString();
            } else {
                Log.w(TAG, "Failed to create external directory");
            }
        } else {
            StringBuilder fileName = new StringBuilder(context.getFilesDir().getAbsolutePath()).append(File.separator).append(SmartUtils.removeSpecialCharacter(getAppName())).append(System.currentTimeMillis()).append(".").append(extension);
            return fileName.toString();
        }
        return null;
    }

    @Override
    public boolean shouldStoreAppDataInExternalStorage() {
        return true;
    }

    @Override
    public boolean isDBEnable() {
        return true;
    }


    @Override
    public boolean isDebugOn() {
        return true;
    }

    @Override
    public boolean isHttpAccessAllow() {
        return false;
    }


    @Override
    public boolean isCrashHandlerEnable() {
        return false;
    }

    @Override
    public boolean isSharedPrefrenceEnable() {
        return true;
    }



    @Override
    public String getDatabaseSQL() {
        return "BaseApp" + ".sql";
    }

    @Override
    public void onInstalling(SmartApplication smartApplication) {
    }

    @Override
    public void onUpgrading(int oldVersion, int newVersion, SmartApplication smartApplication) {
    }
}
