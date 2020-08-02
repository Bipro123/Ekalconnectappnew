package com.connectapp.user.volley;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface ServerStringResponseCallback {
    public void onSuccess(String resultJsonObject);


    /**
     * If there occurs any error while communicating with server
     *
     * @param error
     */
    public void ErrorMsg(VolleyError error);

}
