package com.smart.weservice;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SmartWebManager implements Constants {

    private static final int TIMEOUT = 100000;

    public enum REQUEST_METHOD_PARAMS {
        CONTEXT, PARAMS, REQUEST_TYPES, TAG, URL, TABLE_NAME,
        UN_NORMALIZED_FIELDS, RESPONSE_LISTENER, SHOW_SNACKBAR
    }

    public enum REQUEST_TYPE {JSON_OBJECT, JSON_ARRAY, IMAGE}

    private static SmartWebManager mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private String errorMessage = null;

    private SmartWebManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SmartWebManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SmartWebManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueueGet(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, String message, boolean isShowProgress) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest((String) requestParams.get(REQUEST_METHOD_PARAMS.URL), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("WSR : " + response);
//                if (SmartUtils.getResponseCode(response) == 200) {
//                    SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                ((OnResponseReceivedListenerJsonArray) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
//                } else {
//                    int responseCode = SmartUtils.getResponseCode(response);
//                    errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
//                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
//                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
//                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
//                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
            }

        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueGetJsonObject(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, String message, boolean isShowProgress) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, (String) requestParams.get(REQUEST_METHOD_PARAMS.URL), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("WSR : " + response);
//                if (SmartUtils.getResponseCode(response) == 200) {
//                    SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                try {
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
//                } else {
//                    int responseCode = SmartUtils.getResponseCode(response);
//                    errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
//                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
//                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
//                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
            }

        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueGetJsonObjectPost(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, String message, boolean isShowProgress, JSONObject object) {
        JsonObjectRequest jsObjReq = new JsonObjectRequest(Request.Method.POST,
                (String) requestParams.get(REQUEST_METHOD_PARAMS.URL), object,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (response.getString("message").trim().length() > 0) {
                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 208);
                            } else {
                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                SmartUtils.hideLoadingDialog();
                SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
            }
        }) {


        };
        jsObjReq.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsObjReq.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjReq);
    }

    public <T> void addToRequestQueueGetJsonArrayPost(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, String message, boolean isShowProgress, JSONArray object) {


        JsonArrayRequest jsObjReq = new JsonArrayRequest(Request.Method.POST,
                (String) requestParams.get(REQUEST_METHOD_PARAMS.URL), object,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response.getJSONObject(0), true, 208);

//                            if (response.getString("message").trim().length() > 0) {
//                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 208);
//                            } else {
//                                ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                SmartUtils.hideLoadingDialog();
                SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
            }
        }) {


        };
        jsObjReq.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsObjReq.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjReq);
    }


    public <T> void addToRequestQueueMultipart(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final String filePath,
                                               final String message, final boolean isShowSnackbar) {
        CustomJsonObjectRequestMultipart jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));
        // Log.v("@@@WsParams", jsonRequest.toString());
        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new CustomJsonObjectRequestMultipart(mCtx, (String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, filePath, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {
                                    addToRequestQueueMultipart(requestParams, filePath, message, isShowSnackbar);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        Log.v("@@@WsSessionParams", requestParamsForSession.toString());
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipart(requestParamsForSession, null, filePath, false);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            if (isShowSnackbar) {
                                SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            }
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("@@@@@WSResponse", error.toString());
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    if (isShowSnackbar) {
                        SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                    }
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueMultipartUpload(final HashMap<REQUEST_METHOD_PARAMS, Object> requestParams, final String[] filePath,
                                                     final String message, final boolean isShowSnackBar) {
        CustomJsonObjectRequestMultipart jsObjRequest = null;

        JSONObject jsonRequest = ((JSONObject) requestParams.get(REQUEST_METHOD_PARAMS.PARAMS));

        Log.v("@@@WsParams", jsonRequest.toString());

        if (requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES) == REQUEST_TYPE.JSON_OBJECT) {
            jsObjRequest = new CustomJsonObjectRequestMultipart(mCtx, (String) requestParams.get(REQUEST_METHOD_PARAMS.URL), jsonRequest, filePath, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("@@@@@WSResponse", response.toString());
                    if (SmartUtils.isSessionExpire(response)) {
                        SmartUtils.removeCookie();
                        final HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParamsForSession = new HashMap<>();
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_AUTOLOGIN);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, SmartUtils.getLoginParams((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)));
                        requestParamsForSession.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                            @Override
                            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                                if (responseCode == 200) {
                                    addToRequestQueueMultipartUpload(requestParams, filePath, message, isShowSnackBar);
                                }
                            }

                            @Override
                            public void onResponseError() {
                            }
                        });
                        SmartWebManager.getInstance((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT)).
                                addToRequestQueueMultipartUpload(requestParamsForSession, null, "", isShowSnackBar);
                    } else {
                        if (SmartUtils.getResponseCode(response) == 200) {
                            SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, true, 200);
                        } else {
                            int responseCode = SmartUtils.getResponseCode(response);
                            errorMessage = SmartUtils.validateResponse((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                            SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                            SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_LONG);
                            ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseReceived(response, false, responseCode);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER)).onResponseError();
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public interface OnResponseReceivedListener {
        void onResponseReceived(JSONObject tableRows, boolean isValidResponse, int responseCode);

        void onResponseError();
    }

    public interface OnResponseReceivedListenerJsonArray {
        void onResponseReceived(JSONArray tableRows, boolean isValidResponse, int responseCode);

        void onResponseError();
    }
}