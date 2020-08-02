package com.connectapp.user.syncadapter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.volley.AppController;
import com.connectapp.user.volley.ServerResponseCallback;

@SuppressLint("ShowToast")
public class VolleySyncManager {
	private static String tag_json_obj = "jobj_req";
	private static String baseURL = Consts.BASE_URL;

	/**
	 * 
	 * Making json object request
	 * */
	public static void makeJsonObjReq(final Map<String, String> paramsMap,
			final ServerResponseCallback mListener) {

		Log.d("JSONObject", new JSONObject(paramsMap).toString());

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, Consts.USER_SUBMISSION_URL,
				new JSONObject(paramsMap), new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d("VolleySyncManager", response.toString());

						mListener.onSuccess(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("VolleySyncManager", "Error: " + error.getMessage());

						if (error instanceof TimeoutError || error instanceof NoConnectionError) {
							Log.d("error ocurred", "TimeoutError");
						} else if (error instanceof AuthFailureError) {
							Log.d("error ocurred", "AuthFailureError");
						} else if (error instanceof ServerError) {
							Log.d("error ocurred", "ServerError");
						} else if (error instanceof NetworkError) {
							Log.d("error ocurred", "NetworkError");
						} else if (error instanceof ParseError) {
							Log.d("error ocurred", "ParseError");
							error.printStackTrace();
						}
						mListener.onError(error);
					}
				}) {

			/**
			 * Passing some request headers
			 * */
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json; charset=utf-8");
				return headers;
			}

			@Override
			protected Map<String, String> getParams() {

				return paramsMap;
			}

		};

		jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

	}

}
