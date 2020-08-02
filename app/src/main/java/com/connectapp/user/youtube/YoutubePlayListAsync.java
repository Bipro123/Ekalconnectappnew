package com.connectapp.user.youtube;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import com.connectapp.user.data.Video;
import com.connectapp.user.volley.AppController;
import com.connectapp.user.volley.ServerResponseCallback;

public class YoutubePlayListAsync extends AsyncTask<Void, Void, ArrayList<Video>> implements OnCancelListener {

	public YoutubePlayListListener mListener;
	private String playlistId;
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private static String tag_json_obj = "jobj_req";
	private static String baseURL = Consts.BASE_URL;


	public YoutubePlayListAsync(Context mContext, String playlistId) {

		this.playlistId = playlistId;
		this.mContext = mContext;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("Loading...");
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mProgressDialog.show();
	}

	@Override
	protected ArrayList<Video> doInBackground(Void... params) {
		final ArrayList<Video> videoList = new ArrayList<Video>();

		makeJsonObjReq(new HashMap<String, String>(), new ServerResponseCallback() {

			@Override
			public void onSuccess(JSONObject resultJsonObject) {
				// TODO Auto-generated method stub

				try {
					JSONArray jsonArray = resultJsonObject.optJSONArray("items");
					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject jsonObject = jsonArray.getJSONObject(i);

						JSONObject snippetObj = new JSONObject(jsonObject.optString("snippet"));
						String title = snippetObj.optString("title");

						String description = snippetObj.optString("description");
						JSONObject thumbnailsObject = new JSONObject(snippetObj.optString("thumbnails"));

						JSONObject mediumObject = new JSONObject(thumbnailsObject.optString("medium"));

						String mediumThumbNailUrl = mediumObject.optString("url");

						JSONObject resourceIdObject = new JSONObject(snippetObj.optString("resourceId"));
						String videoId = resourceIdObject.optString("videoId");

						Drawable thumbDrawable = LoadImageFromWebOperations(mediumThumbNailUrl);
						Video video = new Video(title, videoId, thumbDrawable, description);
						videoList.add(video);

					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("IOException", "" + e);
				}
			}

			@Override
			public void onError(VolleyError error) {
				Log.d("TAG", "Do Something");

			}
		}, "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + playlistId
				+ "&maxResults=25&key=AIzaSyC-YyIxbDLPMw0_nkLjj_BfpVozRf84pEM");
		/*// Get a httpclient to talk to the internet
		HttpClient client = new DefaultHttpClient();
		// Perform a GET request to YouTube for a JSON list of all the videos by a specific user
		HttpUriRequest request = new HttpGet("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="
				+ playlistId + "&maxResults=25&key=AIzaSyAKgCx_f4jAfwjgrIbV5ZCXqyuwpk-oK5o");

		HttpResponse response = client.execute(request);
		String jsonString = StreamUtils.convertToString(response.getEntity().getContent());
		JSONObject json = new JSONObject(jsonString);

		JSONArray jsonArray = new JSONArray(json.optString("items"));
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONObject snippetObj = new JSONObject(jsonObject.optString("snippet"));
			String title = snippetObj.optString("title");

			String description = snippetObj.optString("description");
			JSONObject thumbnailsObject = new JSONObject(snippetObj.optString("thumbnails"));

			JSONObject mediumObject = new JSONObject(thumbnailsObject.optString("medium"));

			String mediumThumbNailUrl = mediumObject.optString("url");

			JSONObject resourceIdObject = new JSONObject(snippetObj.optString("resourceId"));
			String videoId = resourceIdObject.optString("videoId");

			Drawable thumbDrawable = LoadImageFromWebOperations(mediumThumbNailUrl);
			Video video = new Video(title, videoId, thumbDrawable, description);
			videoList.add(video);

		}*/

		return videoList;
	}

	@Override
	protected void onPostExecute(ArrayList<Video> result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		mListener.videoPlaylistAsyncCallback(result);
	}

	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, url);
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		mProgressDialog.dismiss();

	}

	/**
	 * 
	 * Making json object request
	 * */
	private void makeJsonObjReq(final Map<String, String> paramsMap, final ServerResponseCallback mListener, String url) {

		Log.d("JSONObject", new JSONObject(paramsMap).toString());

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url, new JSONObject(paramsMap),
				new Response.Listener<JSONObject>() {

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
