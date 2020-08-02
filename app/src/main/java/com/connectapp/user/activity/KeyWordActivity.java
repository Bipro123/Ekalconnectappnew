package com.connectapp.user.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.KeyWordsAdapter;
import com.connectapp.user.data.Keyword;
import com.connectapp.user.data.Thread;
import com.connectapp.user.volley.ServerResponseCallback;

/**
 * @author ritwik.rai
 * */
public class KeyWordActivity extends Activity implements ServerResponseCallback {
	private ListView lv_keyword;
	private Context mContext;
	private String TAG = "";
	com.connectapp.user.data.Thread thread = new Thread();
	private ArrayList<Keyword> keywordList = new ArrayList<Keyword>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyword);
		initView();
	}

	/**
	 * Initialize Ui
	 * */
	private void initView() {

		lv_keyword = (ListView) findViewById(R.id.lv_keyword);

		mContext = KeyWordActivity.this;

		TAG = mContext.getClass().getSimpleName();
		thread = (Thread) getIntent().getExtras().get("threadAndKeywords");
		keywordList = thread.getKeywords();
		System.out.println("Keyword List size: " + keywordList.size());
		updateListUI(keywordList);

	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.d(TAG, "" + resultJsonObject);

		if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {

			keywordList.clear();
			JSONArray resultArray = resultJsonObject.optJSONArray("keywords");

			for (int i = 0; i < resultArray.length(); i++) {

				Keyword keyword = new Keyword();
				keyword.setId(resultArray.optJSONObject(i).optString("id"));
				keyword.setValue(resultArray.optJSONObject(i).optString("value"));
				keywordList.add(keyword);

			}

			updateListUI(keywordList);
		} else {

			Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public void onError(VolleyError error) {

	}

	private void updateListUI(ArrayList<Keyword> threadList) {

		KeyWordsAdapter adapter = new KeyWordsAdapter(mContext, threadList);
		lv_keyword.setAdapter(adapter);
		lv_keyword.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}

		});
	}

	public void onQuestionsClicked(View v) {

		Intent intent = new Intent(mContext, SchoolFormActivity.class);
		intent.putExtra("thread", thread);
		startActivity(intent);
		finish();
	}
}
