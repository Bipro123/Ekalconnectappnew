package com.connectapp.user.members;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

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
import com.connectapp.user.db.MembersDB;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

public class MembersDirectory extends AppCompatActivity implements OnClickListener, ServerResponseCallback, DBConstants {

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
    private boolean isCheckUpdate = false;
    private boolean isFetchMembers = false;
    static int serverCurrentVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_directory);
        mContext = MembersDirectory.this;

        tv_notification_on_update = (TextView) findViewById(R.id.tv_notification_on_update);
        volleyTaskManager = new VolleyTaskManager(mContext);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Preparing Members Directory! Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FTS Members Directory");

		/*	if (Util.fetchUserClass(mContext).getIsFirstTimeAccess()) {
				// Accessing the members directory for the first time
				fetchMembersDirectory();
			} else {*/

        // CHECK UPDATES
        checkUpdates();

        /*}
         */
        // Set blink animation for tv_notification_on_update
        /*anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(750); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tv_notification_on_update.startAnimation(anim);*/


        tv_notification_on_update.setOnClickListener(this);
    }

    private void checkUpdates() {

        if (Util.isInternetAvailable(mContext)) {
            isCheckUpdate = true;
            volleyTaskManager.doPostCheckUpdates(new HashMap<String, String>(), true);
        } else {
            tv_notification_on_update.setVisibility(View.GONE);
            fetchMembersDirectory();
        }
        UserClass mUserClass = Util.fetchUserClass(mContext);
        if (mUserClass != null && mUserClass.getIsMembersDirectoryComplete()) {
            showCityList();
        }
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
            java.lang.reflect.Field mCursorDrawableRes = android.widget.TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void getMembersDirectoy(int currentNode) {

        volleyTaskManager.doGetMembersDirectory(currentNode);

    }

    private void fetchMembersDirectory() {
        UserClass userClass = Util.fetchUserClass(mContext);

        Log.e("TAG", "Current City Index: " + userClass.getCurrentCityIndex());
        if (userClass != null && userClass.getCurrentCityIndex() == -1) {
            Log.e("TAG", "Current City Index: " + userClass.getCurrentCityIndex());
            pDialog.show();
            cityMap = userClass.getCityName();
            if (cityMap == null)
                cityMap = new HashMap<>();
            isFetchMembers = true;
            volleyTaskManager.doGetMembersDirectory(0);

        } else if (userClass != null && userClass.getCurrentCityIndex() != -1) {
            Log.e("TAG", "Current City Index: " + userClass.getCurrentCityIndex());
            if (!userClass.getIsMembersDirectoryComplete()) {
                pDialog.show();
                cityMap = userClass.getCityName();
                currentNode = Util.fetchUserClass(mContext).getCurrentCityIndex();
                isFetchMembers = true;
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

        if (isCheckUpdate) {
            Log.e("Result", "" + resultJsonObject);
            isCheckUpdate = false;
            if (resultJsonObject.optString("status").trim().equalsIgnoreCase("200")) {
                UserClass userClass = Util.fetchUserClass(mContext);
                serverCurrentVersion = Integer.parseInt(resultJsonObject.optString("currentVersion").trim());
                if (userClass.getCurrentMemebersDirVersion() < serverCurrentVersion && !Util.fetchUserClass(mContext).getIsFirstTimeAccess()) {
                    tv_notification_on_update.setVisibility(View.VISIBLE);
                }
            }
            Log.e("onSuccess", "First Time access: " + Util.fetchUserClass(mContext).getIsFirstTimeAccess());
            Log.e("onSuccess", "is Mem dir complete: " + Util.fetchUserClass(mContext).getIsMembersDirectoryComplete());
            if (Util.fetchUserClass(mContext).getIsFirstTimeAccess() && Util.isInternetAvailable(mContext)) {
                // Accessing the members directory for the first time
                fetchMembersDirectory();
            } else if (!Util.fetchUserClass(mContext).getIsFirstTimeAccess() && !Util.fetchUserClass(mContext).getIsMembersDirectoryComplete()) {
                fetchMembersDirectory();
            }

        } else if (isFetchMembers) {
            isFetchMembers = false;
            //Log.e(TAG, "onSuccess: " + resultJsonObject);
            if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {

                int memberCount = Integer.parseInt(resultJsonObject.optString("memberCount").trim());
                int cityCount = Integer.parseInt(resultJsonObject.optString("cityCount").trim());

                Log.e("Member", "Member count: " + memberCount);
                Log.e("Member", "City Count: " + cityCount);
                mUserClass = Util.fetchUserClass(mContext);

                //PARSING RESPONSE
                JSONArray responseArray = resultJsonObject.optJSONArray("dir");
                Log.e("Response Array Length", "Response Array length: " + responseArray.length());

                String cityName = responseArray.optJSONObject(0).optString("cityName").trim();
                JSONArray memberArray = responseArray.optJSONObject(0).optJSONArray("members");
                Log.e("member Array Length", "member Array length: " + memberArray.length());

                // Parse members
                Log.e("City name", "City Name: " + cityName);
                parseAndStoreMember(memberArray, cityName);

                // SAVE CITY NAME
                cityMap.put("" + currentNode, "" + cityName.trim());

                int currentMemberCount = mUserClass.getCurrentMemberCount() + memberArray.length();

                Log.e("Current Member", "current member count: " + currentMemberCount);
                Log.e("total count", "Total member count: " + memberCount);

                double ratio = ((double) currentMemberCount / (double) memberCount) * 100;
                Log.e("Ratio", "Ratio: " + ratio);

                int progress = (int) ratio;

                Log.e("PROGRESS", "Progress: " + progress);
                pDialog.setProgress(progress);

                mUserClass.setCurrentMemberCount(currentMemberCount);
                mUserClass.setTotalMemberCount(memberCount);
                mUserClass.setTotalCityCount(cityCount);
                mUserClass.setCityName(cityMap);

                // Move to the next node
                currentNode++;
                mUserClass.setCurrentCityIndex(currentNode);

                Util.saveUserClass(mContext, mUserClass);
                if (currentNode < cityCount)// Check if the last Node has already been called.
                {
                    mUserClass.setIsMembersDirectoryComplete(false);

                    isFetchMembers = true;
                    volleyTaskManager.doGetMembersDirectory(currentNode);
                } else if (currentNode == cityCount) {

                    Log.e("TAG", "Server current version: " + serverCurrentVersion);
                    // ALL PROCESS COMPLETE
                    mUserClass.setIsMembersDirectoryComplete(true);
                    pDialog.dismiss();
                    mUserClass.setIsFirstTimeAccess(false);
                    mUserClass.setCurrentMemebersDirVersion(serverCurrentVersion);
                    findViewById(R.id.llAlert).setVisibility(View.GONE);
                    tv_notification_on_update.setVisibility(View.GONE);
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
                                isFetchMembers = true;
                                volleyTaskManager.doGetMembersDirectory(currentNode);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        }
    }

    @Override
    public void onError(VolleyError error) {
        pDialog.dismiss();
        tv_notification_on_update.setVisibility(View.GONE);
        if (mUserClass != null && mUserClass.getIsMembersDirectoryComplete()) {
            showCityList();
        } else {
            Toast.makeText(mContext, "No intenet Connection.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void showCityList() {

        lv_contact = (ListView) findViewById(R.id.lv_contact);
        HashMap<String, String> cityMap = Util.fetchUserClass(mContext).getCityName();
        if (cityMap != null) {
            CityListAdapter adapter = new CityListAdapter(mContext, cityMap);
            lv_contact.setAdapter(adapter);
            lv_contact.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, MemberListActivity.class);
                    intent.putExtra("cityName", Util.fetchUserClass(mContext).getCityName().get("" + position));
                    startActivity(intent);
                }
            });
        } else {
            //TODO Handle this case
        }

        if (Util.isInternetAvailable(mContext)) {
            // TODO Check whether the members directory is updated or not

        } else {
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
            mContentValue.put(SEQUENCE, memberObj.optString("sequence").trim());
            new MembersDB().saveMembersData(mContext, mContentValue);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_notification_on_update:

                Util.showCallBackMessageWithOkCancel(mContext,
                        "New updates of members directory are available. Tap OK to update or cancel to close this dialog.",
                        new AlertDialogCallBack() {

                            @Override
                            public void onSubmit() {
                                boolean isTableCleared = new MembersDB().clearTable(mContext);
                                if (isTableCleared) {
                                    reFetchMembersDirectory();
                                } else {
                                    Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
                                }
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

    private void reFetchMembersDirectory() {
        UserClass userClass = Util.fetchUserClass(mContext);
        userClass.setCurrentCityIndex(-1);
        userClass.setCurrentMemberCount(0);
        userClass.setCityName(new HashMap<String, String>());
        userClass.setIsMembersDirectoryComplete(false);
        currentNode = 0;
        Util.saveUserClass(mContext, userClass);
        fetchMembersDirectory();
    }


}
