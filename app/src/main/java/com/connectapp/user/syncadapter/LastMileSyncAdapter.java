package com.connectapp.user.syncadapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.android.volley.VolleyError;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.volley.ServerResponseCallback;

public class LastMileSyncAdapter extends AbstractThreadedSyncAdapter {

    private String TAG = getClass().getSimpleName().toString().trim();

    private ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();

    //private JsonParser jsonParser;
    private int dataCount = 0;


    public LastMileSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.e(TAG, "Constructor");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {

        Log.e(TAG, "--------------->> SynC Adapter Called  <<--------------");

        offlineSubmissions.clear();
        try {
            Cursor cur = provider.query(Constant.CONTENT_URI, null, null, null, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    offlineSubmissions.add(Constant.getOfflineSubmissionFromCursor(cur));
                }
                cur.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        postOfflineData();

    }

    private void postOfflineData() {

        Log.e("postOfflineData", "Submission size " + offlineSubmissions.size());
        Log.e("postOfflineData", "Data Count " + dataCount);

        if (offlineSubmissions.size() > 0 && dataCount < offlineSubmissions.size()) {
            HashMap<String, String> formDataMap = new HashMap<String, String>();

            formDataMap.put("muID", "" + offlineSubmissions.get(dataCount).getMuId());
            formDataMap.put("threadID", "" + offlineSubmissions.get(dataCount).getThreadID());
            formDataMap.put("image", "" + offlineSubmissions.get(dataCount).getBase64Image());
            formDataMap.put("lat", "" + "" + offlineSubmissions.get(dataCount).getLatitude());
            formDataMap.put("long", "" + "" + offlineSubmissions.get(dataCount).getLongitude());

            //formDataMap.put("address", "" + offlineSubmissions.get(dataCount).getAddress());
            formDataMap.put("date", "" + offlineSubmissions.get(dataCount).getDate());
            formDataMap.put("time", "" + offlineSubmissions.get(dataCount).getTime());
            Log.e("postOfflineData", "------------------------->>>>>>>>>>>>>--------------------------------");
            Log.e("postOfflineData", "TIME: " + offlineSubmissions.get(dataCount).getTime());
            Log.e("postOfflineData", "VILLAGE NAME: " + offlineSubmissions.get(dataCount).getVillageName());

            if (offlineSubmissions.get(dataCount).getThreadID().equalsIgnoreCase("6")) {

                //formDataMap.put("otherData", "" + offlineSubmissions.get(dataCount).getOtherData());
                //formDataMap.put("keyWords", "" + offlineSubmissions.get(dataCount).getKeywords());

                formDataMap.put("sCode", offlineSubmissions.get(dataCount).getSchoolCode());

                //TODO Village becomes comments
                //formDataMap.put("village", offlineSubmissions.get(dataCount).getVillageName());
                formDataMap.put("comments", offlineSubmissions.get(dataCount).getVillageName());

                //TODO Comments becomes piccat
                //formDataMap.put("comments", "" + offlineSubmissions.get(dataCount).getComments());
                formDataMap.put("piccat", "" + offlineSubmissions.get(dataCount).getComments());
            } else if (offlineSubmissions.get(dataCount).getThreadID().equalsIgnoreCase("7")) {

                formDataMap.put("rathcat", "" + offlineSubmissions.get(dataCount).getComments());
                formDataMap.put("rCode", "" + offlineSubmissions.get(dataCount).getRathNumber());
                formDataMap.put("sCode", "");
                formDataMap.put("comments", offlineSubmissions.get(dataCount).getVillageName());
            }
            else if (offlineSubmissions.get(dataCount).getThreadID().equalsIgnoreCase("8")) {

                formDataMap.put("sCode", offlineSubmissions.get(dataCount).getSchoolCode());

                //TODO Village becomes comments
                //formDataMap.put("village", offlineSubmissions.get(dataCount).getVillageName());
                formDataMap.put("comments", offlineSubmissions.get(dataCount).getVillageName());
            }
            else if (offlineSubmissions.get(dataCount).getThreadID().equalsIgnoreCase("9")) {

                formDataMap.put("sCode", offlineSubmissions.get(dataCount).getSchoolCode());

                //TODO Village becomes comments
                //formDataMap.put("village", offlineSubmissions.get(dataCount).getVillageName());
                formDataMap.put("comments", offlineSubmissions.get(dataCount).getVillageName());
            }
            else {
                formDataMap.put("keyWords", "" + offlineSubmissions.get(dataCount).getKeywords());

            }
            postData(formDataMap);
        } else
            dataCount = 0;
    }

    private void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postData(HashMap<String, String> formDataMap) {
        Log.e("postData", "" + formDataMap);
        Log.e("postData", "" + new JSONObject(formDataMap).toString());
        //generateNoteOnSD(getContext(), "OFFLINE NOTE", "" + new JSONObject(formDataMap).toString());
        VolleySyncManager.makeJsonObjReq(formDataMap, new ServerResponseCallback() {

            @Override
            public void onSuccess(JSONObject resultJsonObject) {
                Log.e("onSuccess", resultJsonObject.toString());
                if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {
                    try {
                        if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {

                            deleteFromDatabase(offlineSubmissions.get(dataCount).getDate(), offlineSubmissions.get(dataCount).getTime());
                            dataCount++;
                            postOfflineData();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

    }

    private void deleteFromDatabase(String date, String time) {
        Log.e("deleteFromDatabase", "---------------------------------------------\n");
        Log.e("deleteFromDatabase", "delete from database");
        Log.e("deleteFromDatabase", "---------------------------------------------\n\n");
        OfflineDB mdb = new OfflineDB(getContext());
        boolean status = mdb.deleteRow(date, time);
        if (status) {
            Log.e("deleteFromDatabase", "Deleted!!");
        } else
            Log.e("deleteFromDatabase", "NOT DONE DELETE");
    }

}
