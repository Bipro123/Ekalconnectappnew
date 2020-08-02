package com.connectapp.user.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.connectapp.user.R;
import com.connectapp.user.syncadapter.DBConstants;

public class WebViewActivity extends AppCompatActivity implements DBConstants {

	private Context mContext;
	private WebView wv_schoolView;
	ProgressDialog mDialog;


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_school_view);
		mContext = WebViewActivity.this;

		wv_schoolView = (WebView) findViewById(R.id.wv_schoolView);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mDialog = new ProgressDialog(mContext);
		mDialog.setCancelable(true);
		mDialog.setMessage("Loading.. Please wait.");
		wv_schoolView.setWebViewClient(new myWebClient());
		String loadUrl = getIntent().getStringExtra("loadUrl");
		getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
		//wv_schoolView.loadDataWithBaseURL(null,loadUrl, "text/html", "UTF-8",null);
		wv_schoolView.loadUrl(loadUrl);
		wv_schoolView.getSettings().setJavaScriptEnabled(true);
		wv_schoolView.requestFocus();

		mDialog.setTitle(getResources().getString(R.string.app_name));

	}

	// ====================================
	// ===== WEB CLIENT ===================

	private class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			Log.v("URL", "" + url);

			mDialog.show();
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);

		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mDialog.dismiss();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mDialog.dismiss();

		}
	}

}
