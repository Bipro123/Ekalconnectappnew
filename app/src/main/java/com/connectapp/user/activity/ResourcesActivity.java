package com.connectapp.user.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.connectapp.user.R;
import com.connectapp.user.data.Video;
import com.connectapp.user.volley.VolleyTaskManager;
import com.connectapp.user.youtube.YoutubeAsyncTask;
import com.connectapp.user.youtube.YoutubePlayListListener;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ResourcesActivity extends AppCompatActivity implements YoutubePlayListListener, OnClickListener {

	private Context mContext;
	private LinearLayout ll_pdf_ekal_system, ll_vid_ev_mission, ll_vid_ev_story_one, ll_vid_ev_story_two, ll_vid_ev_pm_praise,
			ll_vid_ev_vs_modi;
	//private ListView listView_youtubePlaylist;
	private ProgressDialog mProgressDialog;
	private static String tag_json_obj = "jobj_req";

	private VolleyTaskManager volleyTaskManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_resources);

		mContext = ResourcesActivity.this;
		//Set the back button which navigates to previous state
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ll_pdf_ekal_system = (LinearLayout) findViewById(R.id.ll_pdf_ekal_system);

		//listView_youtubePlaylist = (ListView) findViewById(R.id.listView_youtubePlaylist);

		ll_pdf_ekal_system.setOnClickListener(this);

		permitMultiThreading();

		// TODO Comment out for youtube vids
		//getPlayList();

		/*volleyTaskManager = new VolleyTaskManager(mContext);

		volleyTaskManager.doGetYoutubePlaylist("PLh9bLT30viRllGYeW2eaVyfUMc79omX0d");*/
	}

	private void getPlayList() {
		YoutubeAsyncTask youtubeAsyncTask = new YoutubeAsyncTask(mContext,
				"https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="
						+ "PLh9bLT30viRllGYeW2eaVyfUMc79omX0d" + "&maxResults=25&key=AIzaSyC-YyIxbDLPMw0_nkLjj_BfpVozRf84pEM");
		youtubeAsyncTask.mListener = this;
		youtubeAsyncTask.execute();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_pdf_ekal_system:
			openPdf();
			break;

		default:
			break;
		}
	}

	private void openPdf() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("http://connectapp.net/site/ariUploads/ekalSystem.pdf"));
		startActivity(i);
	}

	/*@Override
	public void onSuccess(JSONObject resultJsonObject) {

		final ArrayList<Video> videoList = new ArrayList<Video>();
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
				Log.d("TAV", "Thumb Drawable: " + thumbDrawable);
				Video video = new Video(title, videoId, thumbDrawable, description);
				videoList.add(video);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (videoList.size() > 0) {
			YoutubeVideoAdapter mAdapter = new YoutubeVideoAdapter(ResourcesActivity.this, videoList);

			listView_youtubePlaylist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent i = new Intent(ResourcesActivity.this, PlayVideoActivity.class);
					i.putExtra("videocode", videoList.get(arg2).getUrl());
					i.putExtra("videoDescription", videoList.get(arg2).getVideoDescription());
					startActivity(i);

				}
			});

			listView_youtubePlaylist.setAdapter(mAdapter);
		}
	}

	@Override
	public void onError() {

	}*/

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

	private void permitMultiThreading() {
		int SDK_INT = android.os.Build.VERSION.SDK_INT;
		if (SDK_INT > 8) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	public void videoPlaylistAsyncCallback(final ArrayList<Video> result) {
		//TODO Comment out for youtube videos
		/*if (result != null && result.size() > 0) {
			YoutubeVideoAdapter mAdapter = new YoutubeVideoAdapter(ResourcesActivity.this, result);

			listView_youtubePlaylist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent i = new Intent(ResourcesActivity.this, PlayVideoActivity.class);
					i.putExtra("videocode", result.get(arg2).getUrl());
					i.putExtra("videoDescription", result.get(arg2).getVideoDescription());
					startActivity(i);

				}
			});

			listView_youtubePlaylist.setAdapter(mAdapter);
		} else {
			Toast.makeText(mContext, "Something went wrong. Please try after sometime.", Toast.LENGTH_SHORT).show();
		}*/
	}
}
