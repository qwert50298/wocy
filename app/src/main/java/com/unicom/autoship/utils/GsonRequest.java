package com.unicom.autoship.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dyzhou on 2018/8/01
 */
public class GsonRequest<T> extends Request<T> {

    private final Response.Listener<T> mListener;

    private Gson mGson;

    private Class<T> mClass;


    private Map<String, String> params;

    /**
     * post
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> params,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mGson = new Gson();
        mClass = clazz;
        this.params = params;
        mListener = listener;
        this.setRetryPolicy(new DefaultRetryPolicy( 10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        Log.d(clazz.getSimpleName()+"post", params.toString());
    }


    /**
     * get
     */
    public GsonRequest(String url, Class<T> clazz,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mClass = clazz;
        mListener = listener;
        mGson = new Gson();
        this.setRetryPolicy(new DefaultRetryPolicy( 10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES+1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );

        Log.d(clazz.getSimpleName()+"Get", url.toString());
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
//        headers.put("Content-Type", "multipart/form-data");
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    "UTF-8");
            Log.d(mClass.getSimpleName()+"response", jsonString);
            return Response.success(mGson.fromJson(jsonString, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } finally {

        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

}
