package com.smart.weservice;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tasol on 12/10/15.
 */
public class CustomJsonObjectRequestMultipart extends JsonObjectRequest {

    private HttpEntity mHttpEntity;

    public CustomJsonObjectRequestMultipart(Context context, String url, JSONObject jsonRequest,
                                            String filePath, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        if (filePath != null && filePath.trim().length() > 0) {
            mHttpEntity = buildMultipartEntity(new File(filePath), jsonRequest);
        } else {
            mHttpEntity = buildMultipartEntity(jsonRequest);
        }
    }

    public CustomJsonObjectRequestMultipart(Context context, String url, JSONObject jsonRequest,
                                            String[] filePaths, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        mHttpEntity = buildMultipartEntity(filePaths, jsonRequest);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        Map headers = response.headers;
        String cookie = (String) headers.get("Set-Cookie");
        if (cookie != null && cookie.length() > 0) {
            Log.e("@@@@@GOT COOKIES", cookie);
        }
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(Constants.SP_COOKIES, cookie);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        return headers;
    }

    //Multipart string body
    private HttpEntity buildMultipartEntity(JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    //Multipart upload single image file
    private HttpEntity buildMultipartEntity(File file, JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody fileBody = new FileBody(file, ContentType.create("image/jpeg"), file.getName());
        builder.addPart("image", fileBody);
        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    //Multipart upload multiple images files
    private HttpEntity buildMultipartEntity(String[] filePaths, JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (int i = 0; i < 2; i++) {//Customised by dharmesh for Visitor POC
            File file = new File(filePaths[i]);
            Log.d("FILES UPLOADING...", String.valueOf(file));
            FileBody fileBody = new FileBody(file, ContentType.create("image/jpeg"), file.getName());
            builder.addPart("image" + (i), fileBody);
        }

        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        if (mHttpEntity != null) {
            return mHttpEntity.getContentType().getValue();
        }
        return null;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (mHttpEntity != null) {
                Log.d("request: multipart", mHttpEntity.toString());
                mHttpEntity.writeTo(bos);
            } else {
                return null;
            }
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    /**
     * A method used to get mime type of given file path of image or video.
     *
     * @param filePath
     * @return {@link String}
     */
    public String getMimeType(String filePath) {
        String type = null;
        String extension = getFileExtensionFromUrl(filePath);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * A method used to get extension type of given file path of image or video.
     *
     * @param url
     * @return {@link String}
     */
    public String getFileExtensionFromUrl(String url) {
        int dotPos = url.lastIndexOf('.');
        if (0 <= dotPos) {
            return (url.substring(dotPos + 1)).toLowerCase();
        }

        return "";
    }
}
