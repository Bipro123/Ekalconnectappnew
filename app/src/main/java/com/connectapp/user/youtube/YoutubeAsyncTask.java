package com.connectapp.user.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.connectapp.user.data.Video;

public class YoutubeAsyncTask extends AsyncTask<Void, Void, Void> {

	private String requestUrl;
	private Context mContext;
	public YoutubePlayListListener mListener;
	private ArrayList<Video> videoList;
	private ProgressDialog mProgressDialog;


	public YoutubeAsyncTask(Context mContext, String requestUrl) {

		this.mContext = mContext;
		this.requestUrl = requestUrl;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("Loading videos! Please wait...");
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		// Will contain the raw JSON response as a string.
		String response = null;

		try {
			// Construct the URL for the OpenWeatherMap query
			// Possible parameters are avaiable at OWM's forecast API page, at
			// http://openweathermap.org/API#forecast
			URL url = new URL(requestUrl);

			// Create the request to OpenWeatherMap, and open the connection
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			// Read the input stream into a String
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				// Nothing to do.
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				// Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
				// But it does make debugging a *lot* easier if you print out the completed
				// buffer for debugging.
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				//TODO Return Error
				// Stream was empty.  No point in parsing.
				return null;
			} else {
				response = buffer.toString();
				parseResponse(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("PlaceholderFragment", "Error ", e);
			// If the code didn't successfully get the weather data, there's no point in attemping
			// to parse it.
			return null;
		} finally {

			//Close the connection
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("PlaceholderFragment", "Error closing stream", e);
				}
			}
		}
		return null;
	}

	private void parseResponse(String response) {

		videoList = new ArrayList<Video>();
		try {
			JSONObject resultJsonObject = new JSONObject(response);
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
				Log.d("TAV", "Thumb Drawable: " + thumbDrawable);
				Video video = new Video(title, videoId, thumbDrawable, description);
				videoList.add(video);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPostExecute(Void result) {

		super.onPostExecute(result);
		mListener.videoPlaylistAsyncCallback(videoList);
		mProgressDialog.dismiss();
	}

	private Drawable LoadImageFromWebOperations(String url) {

		Log.d("Thumnail", "Thumnail url: " + url);
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, url);
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
