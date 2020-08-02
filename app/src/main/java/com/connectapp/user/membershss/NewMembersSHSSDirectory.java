package com.connectapp.user.membershss;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.CityListAdapter;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.db.DBConstants;
import com.connectapp.user.db.MembersSHSSDB;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class NewMembersSHSSDirectory extends AppCompatActivity implements OnClickListener, ServerResponseCallback, DBConstants {

	private Context mContext;
	private TextView tv_notification_on_update;
	private VolleyTaskManager volleyTaskManager;
	private int currentNode = 0;// Current Node meaning current city
	private String TAG = getClass().getSimpleName();
	// Progress Dialog
	private ProgressDialog pDialog;
	private UserClass mUserClass;
	private HashMap<String, String> cityMap = new HashMap<String, String>();
	private ListView lv_contact;
	//The blink animation
	private Animation anim;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_members_directory);
		mContext = NewMembersSHSSDirectory.this;

		tv_notification_on_update = (TextView) findViewById(R.id.tv_notification_on_update);
		volleyTaskManager = new VolleyTaskManager(mContext);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Preparing Directory for the first time. Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setCancelable(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("SHSS Members Directory");
		
		fetchMembersDirectory();
		
		checkUpdates();

		// Set blink animation for tv_notification_on_update
		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(750); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		tv_notification_on_update.startAnimation(anim);
		tv_notification_on_update.setOnClickListener(this);
	}

	private void checkUpdates() {

		if(Util.isInternetAvailable(mContext)){
			
			volleyTaskManager.doPostCheckUpdates(new HashMap<String, String>(), true);
		}
		else 
			tv_notification_on_update.setVisibility(View.GONE);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Inflate menu to add items to action bar if it is present.
		inflater.inflate(R.menu.menu_main, menu);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		android.widget.AutoCompleteTextView searchTextView = (android.widget.AutoCompleteTextView) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		try {
			java.lang.reflect.Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
			mCursorDrawableRes.setAccessible(true);
			mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
		} catch (Exception e) {
		}

		/*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String s) {
				Log.d(TAG, "onQueryTextSubmit ");
				ArrayList<String> searchResult = new MembersDB().getSearchResult(mContext, s);
				if (searchResult.size() < 1) {
					Toast.makeText(mContext, "No records found!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, searchResult.size() + " records found!", Toast.LENGTH_SHORT).show();
				}

				//Show in another view
				//customAdapter.swapCursor(cursor);

				return false;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				Log.d(TAG, "onQueryTextChange ");
				ArrayList<String> searchResult = new MembersDB().getSearchResult(mContext, s);
				if (searchResult != null) {

					Log.d("TAG", "Search result not null.");
					//customAdapter.swapCursor(cursor);
				}
				if (searchResult.size() < 1) {
					Toast.makeText(mContext, "No records found!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, searchResult.size() + " records found!", Toast.LENGTH_SHORT).show();
				}

				return false;
			}

		});*/

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Fetch members Directory.
	 * */
	private void fetchMembersDirectory() {
		UserClass userClass = Util.fetchUserClass(mContext);

		Log.d("TAG", "Current City Index: " + userClass.getCurrentCityIndexSHSS());
		if (userClass != null && userClass.getCurrentCityIndexSHSS() == -1) {
			Log.d("TAG", "Current City Index: " + userClass.getCurrentCityIndexSHSS());
			pDialog.show();
			cityMap = userClass.getCityNameSHSS();
			volleyTaskManager.doGetMembersDirectory(0);

		} else if (userClass != null && userClass.getCurrentCityIndexSHSS() != -1) {
			Log.d("TAG", "Current City Index: " + userClass.getCurrentCityIndexSHSS());
			if (!userClass.getIsMembersSHSSDirectoryComplete()) {
				pDialog.show();
				cityMap = userClass.getCityNameSHSS();
				currentNode = Util.fetchUserClass(mContext).getCurrentCityIndexSHSS();
				volleyTaskManager.doGetMembersDirectory(currentNode);
			} else {
				//Toast.makeText(mContext, "Members directory already Up-to-date.", Toast.LENGTH_LONG).show();
				// DONT SHOW PROGRESS- SHOW MEMBERS DIRECTORY
				showCityList();
			}
		}
	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		//Log.d(TAG, "onSuccess: " + resultJsonObject);
		if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {

			int memberCount = Integer.parseInt(resultJsonObject.optString("memberCount").trim());
			int cityCount = Integer.parseInt(resultJsonObject.optString("cityCount").trim());

			Log.d("Member", "Member count: " + memberCount);
			Log.d("Member", "City Count: " + cityCount);
			mUserClass = Util.fetchUserClass(mContext);

			//PARSING RESPONSE
			JSONArray responseArray = resultJsonObject.optJSONArray("dir");
			Log.d("Response Array Length", "Response Array length: " + responseArray.length());

			String cityName = responseArray.optJSONObject(0).optString("cityName").trim();
			JSONArray memberArray = responseArray.optJSONObject(0).optJSONArray("members");
			Log.d("member Array Length", "member Array length: " + memberArray.length());

			// Parse members
			Log.d("City name", "City Name: " + cityName);
			parseAndStoreMember(memberArray, cityName);

			// SAVE CITY NAME
			cityMap.put("" + currentNode, "" + cityName.trim());

			int currentMemberCount = mUserClass.getCurrentMemberCountSHSS() + memberArray.length();

			Log.d("Current Member", "current member count: " + currentMemberCount);
			Log.d("total count", "Total member count: " + memberCount);

			double ratio = ((double) currentMemberCount / (double) memberCount) * 100;
			Log.d("Ratio", "Ratio: " + ratio);

			int progress = (int) ratio;

			Log.d("PROGRESS", "Progress: " + progress);
			pDialog.setProgress(progress);

			mUserClass.setCurrentMemberCountSHSS(currentMemberCount);
			mUserClass.setTotalMemberCountSHSS(memberCount);
			mUserClass.setTotalCityCountSHSS(cityCount);
			mUserClass.setCityNameSHSS(cityMap);

			// Move to the next node
			currentNode++;
			mUserClass.setCurrentCityIndexSHSS(currentNode);

			Util.saveUserClass(mContext, mUserClass);
			if (currentNode < cityCount)// Check if the last Node has already been called.
			{
				mUserClass.setIsMembersSHSSDirectoryComplete(false);
				volleyTaskManager.doGetMembersDirectory(currentNode);
			} else if (currentNode == cityCount) {
				mUserClass.setIsMembersSHSSDirectoryComplete(true);
				pDialog.dismiss();
				Util.showCallBackMessageWithOkCallback(mContext, "Thank you! Please tap on ok to view members.",
						new AlertDialogCallBack() {

							@Override
							public void onSubmit() {
								// GOTO Members view
								showCityList();

							}

							@Override
							public void onCancel() {

							}
						});
				Toast.makeText(mContext, "All members have been updated.", Toast.LENGTH_SHORT).show();

			}

			Util.saveUserClass(mContext, mUserClass);

		} else {
			// Retry
			Util.showCallBackMessageWithOkCallback(mContext, "Something went wrong press ok to try again.",
					new AlertDialogCallBack() {

						@Override
						public void onSubmit() {
							volleyTaskManager.doGetMembersDirectory(currentNode);
						}

						@Override
						public void onCancel() {

						}
					});
		}
	}

	@Override
	public void onError(VolleyError error) {
		pDialog.dismiss();

	}

	private void showCityList() {

		lv_contact = (ListView) findViewById(R.id.lv_contact);
		HashMap<String, String> cityMap = Util.fetchUserClass(mContext).getCityNameSHSS();
		if (cityMap != null) {
			CityListAdapter adapter = new CityListAdapter(mContext, cityMap);
			lv_contact.setAdapter(adapter);
			lv_contact.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(mContext, MemberListSHSSActivity.class);
					intent.putExtra("cityName", Util.fetchUserClass(mContext).getCityNameSHSS().get("" + position));
					startActivity(intent);
				}
			});
		}

		else {
			//TODO Handle this case
		}
		
		if(Util.isInternetAvailable(mContext)){
			// TODO Check whether the members directory is updated or not
			
			
		}else{
			tv_notification_on_update.setVisibility(View.GONE);
			
		}

	}

	private void parseAndStoreMember(JSONArray memberArray, String cityName) {

		for (int i = 0; i < memberArray.length(); i++) {

			JSONObject memberObj = memberArray.optJSONObject(i);
			ContentValues mContentValue = new ContentValues();
			mContentValue.put(CITY, cityName.trim());
			mContentValue.put(ID, memberObj.optString("id").trim());
			mContentValue.put(NAME, memberObj.optString("name").trim());
			mContentValue.put(ID_NO, memberObj.optString("idNo").trim());
			mContentValue.put(SPOUSE_NAME, memberObj.optString("spouseName").trim());
			mContentValue.put(CONTACT_NO, memberObj.optString("contactNo").trim());
			mContentValue.put(MOBILE, memberObj.optString("mobile").trim());
			mContentValue.put(EMAIL, memberObj.optString("email").trim());
			mContentValue.put(DESIGNATION, memberObj.optString("designation").trim());
			mContentValue.put(ADD1, memberObj.optString("add1").trim());
			mContentValue.put(ADD2, memberObj.optString("add2").trim());
			mContentValue.put(ADD3, memberObj.optString("add3").trim());
			mContentValue.put(PIN, memberObj.optString("pin").trim());
			mContentValue.put(TOWN, memberObj.optString("city").trim());
			mContentValue.put(PIC, memberObj.optString("pic").trim());

			new MembersSHSSDB().saveMembersData(mContext, mContentValue);
			
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.tv_notification_on_update:
			
			Util.showCallBackMessageWithOkCancel(mContext, "New updates of members directory are available. Tap OK to update or cancel to close this dialog.", new AlertDialogCallBack() {
				
				@Override
				public void onSubmit() {
					
					reFetchMembersDirectory();
				}
				
				@Override
				public void onCancel() {
					
					
				}
			});
			break;

		default:
			break;
		}

	}
	
	private void reFetchMembersDirectory(){
		
	}
}
