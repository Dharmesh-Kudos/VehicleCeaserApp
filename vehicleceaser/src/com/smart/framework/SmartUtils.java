package com.smart.framework;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidquery.AQuery;
import com.smart.customviews.CustomClickListner;
import com.smart.customviews.SmartDatePickerView;
import com.vehicleceaser.R;
import com.vehicleceaser.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tasol on 23/5/15.
 */

public class SmartUtils implements Constants {

    private static final String TAG = "SmartUtil";
    private static boolean isNetworkAvailable;
    private static ProgressDialog progressDialog;
    private static Dialog loadingDialog;
    private static Geocoder geocoder;
    private static String imgPath;
    private static int mYear;
    private static int mMonth;
    private static int mDay;


    public static boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    public static void setNetworkStateAvailability(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        isNetworkAvailable = true;
                        return;
                    }
                }
            }
        }

        isNetworkAvailable = false;
    }


    /**
     * This method used to get date from string.
     *
     * @param strDate represented date
     * @return represented {@link Date}
     */
    public static Calendar getDateFromString(String strDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calender = Calendar.getInstance();
        Date date;
        try {
            date = dateFormat.parse(strDate);
            calender.setTime(date);
            return calender;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    /**
     * This method used to email validator.
     *
     * @param mailAddress represented email
     * @return represented {@link Boolean}
     */
    public static boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }


    public static void onShareClick(Context context, final String shareLink) {
        Resources resources = context.getResources();

        Intent emailIntent = new Intent();
//        Uri fileUri = null;
//        if (!fromBlog) {//if want to share docx and pdf file to Gmail
//            emailIntent.setAction(Intent.ACTION_SEND);
//            fileUri = FileProvider.getUriForFile(context, "com.documaker.fileprovider", theFile);
//            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            emailIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
//            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//            emailIntent.setType("message/rfc822");
//
//        } else {//If only wants to send some text to Gmail
//            emailIntent.setAction(Intent.ACTION_SEND);
//            emailIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
//            emailIntent.setType("message/rfc822");
//        }
        emailIntent.setAction(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
        emailIntent.setType("message/rfc822");


        PackageManager pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, "Share via...");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (int i = 0; i < resInfo.size(); i++) {
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("twitter") || packageName.contains("facebook")
                    || packageName.contains("plus") || packageName.contains("whatsapp")
                    || packageName.contains("mms")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);

//                if (!fromBlog) {
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                } else {
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, shareLink);
//                }

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareLink);

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        context.startActivity(openInChooser);
    }


    /**
     * This method used to get time from string.
     *
     * @param strTime represented time
     * @return represented {@link Date}
     */
    public static Calendar getTimeFromString(String strTime, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date;
        Calendar calnder = Calendar.getInstance();
        try {
            date = dateFormat.parse(strTime);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }

    /**
     * This method used to get date dialog.
     *
     * @param strDate  represented date
     * @param restrict represented isRestrict
     */
    public static void getDateDialog(Context context, final String strDate,
                                     boolean restrict, final CustomClickListner target, final String format) {

        Calendar date = getDateFromString(strDate, format);
        Calendar today = Calendar.getInstance();

        if (restrict && date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            date.add(Calendar.YEAR, -18);
        }

        SmartDatePickerView dateDlg = new SmartDatePickerView(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Time chosenDate = new Time();
                chosenDate.set(dayOfMonth, monthOfYear, year);
                long dt = chosenDate.toMillis(true);
                CharSequence strDate = DateFormat.format(format, dt);
                target.onClick(strDate.toString());
            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), restrict);

        dateDlg.show();

    }

    /**
     * This method used to get date-time dialog.
     *
     * @param strDate represented date-time
     * @param target  represented {@link CustomClickListner}
     */
    public static void getDateTimeDialog(final Context context, final String strDate,
                                         final CustomClickListner target, final String format) {
        final Calendar date = getDateFromString(strDate, format);
        DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final int y = year;
                final int m = monthOfYear;
                final int d = dayOfMonth;

                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Time chosenDate = new Time();
                        chosenDate.set(0, minute, hourOfDay, d, m, y);
                        long dt = chosenDate.toMillis(true);
                        CharSequence strDate = DateFormat.format(format, dt);
                        target.onClick(strDate.toString());
                    }
                }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true).show();

            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));

        dateDialog.show();

    }

    /**
     * This method used to get time dialog.
     *
     * @param strTime represented time
     * @param target  represented {@link CustomClickListner}
     */
    public static void getTimeDialog(Context context, final String strTime, final CustomClickListner target, final String format) {

        Calendar date = getTimeFromString(strTime, format);
        TimePickerDialog timeDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                String dateString = new SimpleDateFormat(format).format(date);
                target.onClick(dateString);
            }
        }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

        timeDialog.show();

    }


    public static void showLoadingDialog(final Context context) {
        try {
            hideLoadingDialog();
            loadingDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            loadingDialog.setContentView(R.layout.wish_loading_dialog);
            loadingDialog.setCancelable(true);
            loadingDialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog can be set cancellable by passing appropriate flag in
     * parameter. User can dismiss the current progress dialog by pressing back
     * SmartButton if the flag is set to <b>true</b>; This method can also be
     * called from non UI threads.
     *
     * @param context = Context context will be current activity's context.
     *                <b>Note</b> : A new progress dialog will be generated on
     *                screen each time this method is called.
     */
    public static void showProgressDialog(final Context context, String msg, final boolean isCancellable) {
        Log.d("@@@SmartUtils", "ShowPD");
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        }
        progressDialog = ProgressDialog.show(context, "", "");
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(isCancellable);
        ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_ATOP);
        ((TextView) progressDialog.findViewById(R.id.txtMessage)).setText(msg == null || msg.trim().length() <= 0 ? context.getString(R.string.dialog_loading_please_wait) : msg);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * This method will hide existing progress dialog.<br>
     * It will not throw any Exception if there is no progress dialog on the
     * screen and can also be called from non UI threads.
     */
    static public void hideProgressDialog() {
        Log.d("@@@SmartUtils", "HidePD");
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        } catch (Throwable e) {
            progressDialog = null;
        }
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }


    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title  = String title will be the title of OK dialog.
     * @param msg    = String msg will be the message in OK dialog.
     * @param target = String target is AlertNewtral callback for OK SmartButton
     *               click action.
     */
    static public void getConfirmDialog(final Context context, final String title, final String msg, final String positiveBtnCaption,
                                        final String negativeBtnCaption, final boolean isCancelable, final AlertMagnatic target) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(title).setMessage(msg).setCancelable(false)
                        .setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                target.PositiveMethod(dialog, id);
                            }
                        })
                        .setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                target.NegativeMethod(dialog, id);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setCancelable(isCancelable);
                alert.show();
            }
        });


    }


    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title         = String title will be the title of OK dialog.
     * @param msg           = String msg will be the message in OK dialog.
     * @param buttonCaption = String SmartButtonCaption will be the name of OK
     *                      SmartButton.
     * @param target        = String target is AlertNewtral callback for OK SmartButton
     *                      click action.
     */
    static public void getOKDialog(final Context context, final String title, final String msg, final String buttonCaption,
                                   final boolean isCancelable, final AlertNeutral target) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title).setMessage(msg)
                        .setCancelable(false)
                        .setNeutralButton(buttonCaption, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                target.NeutralMathod(dialog, id);
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setCancelable(isCancelable);
                alert.show();
            }
        });

    }


    /**
     * This method will show short length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void ting(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will show long length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void tong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    // Audio, Image and Video

    /**
     * This method used to decode file from string path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * This method used to decode file from uri path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(Context context, Uri path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(getAbsolutePath(context, path), o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(getAbsolutePath(context, path), o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    static public Uri getVideoPlayURI(String videoUrl) {
        String video_id = "";
        if (videoUrl != null && videoUrl.trim().length() > 0) {
            System.out.println("VIDEOURL" + videoUrl);
            String expression = "(?:http|https|)(?::\\/\\/|)(?:www.|m.)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{11})[a-z0-9;:@?&%=+\\/\\$_.-]*";
            CharSequence input = videoUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                System.out.println("DATA" + matcher.group(1));
                String groupIndex1 = matcher.group(1);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        System.out.println("VIDEOID" + video_id);
        if (video_id.trim().length() > 0) {
            return Uri.parse("ytv://" + video_id);
        } else {
            return Uri.parse("mp4://" + videoUrl);
        }
    }

    //General Methods

    /**
     * This method will return android device UDID.
     *
     * @return DeviceID = String DeviceId will be the Unique Id of android
     * device.
     */
    static public String getDeviceUDID(Context context) {
        try {
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "0000";
    }


    public static String getB64Auth(String userName, String password) {
        String source = userName + ":" + password;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }


    static public String getAbsolutePath(Context context, Uri uri) {
        String path = "";
        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = ((Activity) context).managedQuery(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (path == null || path.length() <= 0) {

            try {
                Uri originalUri = uri;

                String id = originalUri.getLastPathSegment().split(":")[1];
                final String[] imageColumns = {MediaStore.Images.Media.DATA};
                final String imageOrderBy = null;

                Uri finalUri = getUri();

                Cursor imageCursor = ((Activity) context).managedQuery(finalUri, imageColumns,
                        MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

                if (imageCursor.moveToFirst()) {
                    path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (path == null || path.length() <= 0) {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {"_data"};
                Cursor cursor = null;

                try {
                    cursor = context.getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                path = uri.getPath();
            }
        }

        if (path == null || path.length() <= 0) {
            path = getPath(uri, context);
        }

        return path;
    }

    static public Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    static public String getPath(final Uri uri, Context context) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * This method used to hide soft keyboard.
     */
    static public void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method used to show soft keyboard.
     */
    static public void showSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method used to convert json to map.
     *
     * @param object represented json object
     * @return represented {@link Map <String, String>}
     * @throws JSONException represented {@link JSONException}
     */
    static public Map<String, String> jsonToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    /**
     * This method used to convert json to Object.
     *
     * @param json represented json object
     * @return represented {@link Object}
     * @throws JSONException represented {@link JSONException}
     */
    static public Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return jsonToMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    /**
     * This method used to convert json array to List.
     *
     * @param array represented json array
     * @return represented {@link List}
     * @throws JSONException represented {@link JSONException}
     */
    static public List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    /**
     * This method used to string array from string with (,) separated.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final String value) {
        try {
            if (value.length() > 0) {
                final JSONArray temp = new JSONArray(value);
                Log.v("@@@@DATATATTAA", temp.toString());
                int length = temp.length();
                if (length > 0) {
                    final String[] recipients = new String[length];
                    for (int i = 0; i < length; i++) {
                        recipients[i] = temp.getString(i).equalsIgnoreCase("null") ? "1" : temp.getString(i);
                    }
                    return recipients;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * This method used to string array from arraylist.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final ArrayList<String> value) {
        try {
            String[] array = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                array[i] = value.get(i);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public void exportDatabse(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }


    static public void setAuthPermission() {

        AQuery.setAuthHeader(SmartUtils.getB64Auth(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HTTP_ACCESSS_USERNAME, ""),
                SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HTTP_ACCESSS_PASSWORD, "")));
    }

    static public String removeSpecialCharacter(String string) {

        return string.replaceAll("[ ,]", "_");
    }

    static public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    static public String createExternalDirectory(String directoryName) {

        if (SmartUtils.isExternalStorageAvailable()) {

            File file = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory may exist");
            }
            return file.getAbsolutePath();
        } else {

            Log.e(TAG, "External storage is not available");
        }
        return null;
    }


    static public void clearActivityStack(Activity currentActivity, Intent intent) {
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        currentActivity.startActivity(mainIntent);
    }

    static public int convertSizeToDeviceDependent(Context context, int value) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    static public void showSnackBar(Context context, String message, int length) {
        Snackbar snackbar = Snackbar.make(((SmartActivity) context).getSnackBarContainer(), message, length);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.accent));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(15);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
        ((SmartActivity) context).setSnackbar(snackbar);
    }

    static public void hideSnackBar(Context context) {
        if (((SmartActivity) context).getSnackbar() != null) {
            ((SmartActivity) context).getSnackbar().dismiss();
        }
    }

    public static boolean isOSPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static String format(String string, String inputFormat, String outputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat(outputFormat);
        try {
            Log.v("@@@@DATATATA", inputTimeFormat.parse(string).toString());
            return outputTimeFormat.format(inputTimeFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEqualDates(String start, String end, String inputFormat, String outputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat(outputFormat);
        try {
            Date sDate = inputTimeFormat.parse(start);
            String startDate = outputTimeFormat.format(sDate);
            Log.v("@@@@STARTDATE::", startDate.toString());

            Date eDate = inputTimeFormat.parse(end);
            String endDate = outputTimeFormat.format(eDate);
            Log.v("@@@@ENDDATE::", endDate.toString());

            return endDate.equals(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date format(String string, String inputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        try {
            return inputTimeFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTimePassed(String string, String hours24) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        String str = inputParser.format(now.getTime());
        Date currentDate = parseDate(str, hours24);
        Date inputDate = parseDate(string, hours24);

        return currentDate.after(inputDate);
    }

    private static Date parseDate(String date, String hours24) {
        SimpleDateFormat inputParser = new SimpleDateFormat(hours24);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

//    public static void stopLocationUpdates(com.google.android.gms.location.LocationListener locationListener, GoogleApiClient googleApiClient) {
//        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
//    }

    static public void removeCookie() {
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().remove(Constants.SP_COOKIES);
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().commit();
    }

    static public String validateResponse(Context context, JSONObject response, String errorMessage) {
        if (response.has("php_server_error")) {
            try {
                System.out.println("WSPHP SERVER_WARNINGS/ERRORS" + response.getString("php_server_error"));
                response.remove("php_server_error");
            } catch (Exception e) {
            }
        }

        if (response.has("code")) {
            try {
                if (response.has("message") && response.getString("message").length() > 0) {
                    errorMessage = response.getString("message");
                } else {
                    try {
                        int code = Integer.parseInt(response.getString("code"));
                        errorMessage = context.getString(context.getResources().getIdentifier("code" + code, "string", context.getPackageName()));
                    } catch (Exception e) {
                    }
                }
            } catch (Throwable e) {
            }
        } else {
            errorMessage = "Invalid Response";
        }

        if (response.has("notification")) {
            try {
                JSONObject obj = response.getJSONObject("notification");
                if (obj.has("friendNotification")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FRIEND_NOTIFICATION, obj.getString("friendNotification"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FRIEND_NOTIFICATION, "0");
                }
                if (obj.has("messageNotification")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_MESSAGE_NOTIFICATION, obj.getString("messageNotification"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_MESSAGE_NOTIFICATION, "0");
                }
                if (obj.has("globalNotification")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_GLOBAL_NOTIFICATION, obj.getString("globalNotification"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_GLOBAL_NOTIFICATION, "0");
                }
            } catch (Exception e) {
            }
        }

        removeUnnacessaryFields(response);

        return errorMessage;
    }

    static public int getResponseCode(JSONObject response) {
        if (response.has("status_code")) {
            try {
                int code = Integer.parseInt(response.getString("status_code"));
                return code;
            } catch (Throwable e) {
                e.printStackTrace();
                return 108;
            }
        }
        return 108;
    }

    static public boolean isSessionExpire(JSONObject response) {
        if (response != null && response.has("status_code")) {
            try {
                if (response.getInt("status_code") == 704) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    static private void removeUnnacessaryFields(JSONObject data) {
        data.remove("code");
        data.remove("full");
        data.remove("notification");
        data.remove("pushNotificationData");
        data.remove("timeStamp");
        data.remove("unreadMessageCount");
    }

    /**
     * This method used to auto login user params.
     *
     * @return represented {@link JSONObject}
     */
    static public JSONObject getLoginParams(Context mContext) {
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGIN_REQ_OBJECT, ""));
            JSONObject taskData = loginParams.getJSONObject("taskData");

            String udid = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_GCM_REGID, "");
            if (udid.length() > 0) {
                taskData.put("devicetoken", udid);
            }
        } catch (Exception e) {
        }
        return loginParams;
    }


    /**
     * This method used to get latitude-longitude from address.
     *
     * @param address represented address
     * @return represented {@link Address}
     */
    public static Address getLatLongFromAddress(Context context, String address) {
        if (address != null && address.length() > 0) {
            geocoder = new Geocoder(context);

            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(address, 1);
                return list.get(0);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void chooseDate(Context context, final CustomClickListner target) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        if ((monthOfYear + 1) > 9) {//if month is 10,11,12
                            if (dayOfMonth > 9) {//if day is 10-31
                                target.onClick(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            } else {//if day is 1-9
                                target.onClick(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                            }
                        } else {//if month is 1-9
                            if (dayOfMonth > 9) {//if day is 10-31
                                target.onClick(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                            } else {//if day is 1-9
                                target.onClick(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                            }
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    public static void makeWSCall(Context context, int apiType, String url_api, JSONObject jsonObject, final com.vehicleceaser.CustomClickListner target) {

        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(apiType, url_api, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equals("1")) {
                                target.onClick(response, "success");
                            } else {
                                target.onClick(response, "nothing");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject objError = new JSONObject();
                        try {
                            objError.put("error", error.toString());
                            target.onClick(objError, "error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }) {

        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjRequest);

    }
}
