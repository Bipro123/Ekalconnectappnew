package com.connectapp.user.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.ImageAdapter;
import com.connectapp.user.adapter.SubmissionsAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.constant.StaticConstants;
import com.connectapp.user.data.ImageClass;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.syncadapter.OfflineDB;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UnSyncedDataActivity extends AppCompatActivity implements DBConstants, ServerResponseCallback {

    private Context mContext;
    private ListView lv_submission;
    private SubmissionsAdapter adapter;
    private Dialog mDialog;
    private ArrayList<OfflineSubmission> submissionList = new ArrayList<OfflineSubmission>();
    private VolleyTaskManager volleyTaskManager;
    private OfflineSubmission offlineSubmission;


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
        setContentView(R.layout.activity_submissions);
        mContext = UnSyncedDataActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Unsynced Data");
        lv_submission = (ListView) findViewById(R.id.lv_submission);
        volleyTaskManager = new VolleyTaskManager(mContext);
        try {
            fetchOfflineRows();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adapter = new SubmissionsAdapter(mContext, submissionList);
            lv_submission.setAdapter(adapter);
            lv_submission.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    offlineSubmission = submissionList.get(position);
                    showSubmittedFormData(offlineSubmission);
                }
            });
        }
    }

    private void fetchOfflineRows() {
        OfflineDB mDb = new OfflineDB(mContext);
        SQLiteDatabase database = mDb.getReadableDatabase();
        try {
            Cursor cur = database.query(OFFLINE_TABLE, null, null, null, null, null, null);
            System.out.println("Count: " + cur.getCount());
            if (cur != null) {
                while (cur.moveToNext()) {
                    submissionList.add(Constant.getOfflineSubmissionFromCursor(cur));
                }
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("InflateParams")
    private void showSubmittedFormData(final OfflineSubmission submission) {
        mDialog = new Dialog(UnSyncedDataActivity.this);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.unsynced_submission, null);
        ArrayList<ImageClass> imagesList = new ArrayList<ImageClass>();
        ImageClass imageClass = new ImageClass();
        imageClass.setBase64value(submission.getBase64Image());
        imagesList.add(imageClass);
        ViewPager vp_selectedImages = (ViewPager) view.findViewById(R.id.vp_selectedImages);
        ImageAdapter adapter = new ImageAdapter(this, imagesList);
        vp_selectedImages.setAdapter(adapter);
        ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
        if (imagesList.size() == 0) {
            vp_selectedImages.setBackgroundResource(R.drawable.no_image);
        } else {
            vp_selectedImages.setBackgroundColor(Color.parseColor("#D7D7D7"));
            vp_selectedImages.setCurrentItem(imagesList.size() - 1);
        }
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView tv_schoolrath_label = (TextView) view.findViewById(R.id.tv_schoolrath_label);
        TextView tv_schoolCode = (TextView) view.findViewById(R.id.tv_schoolCode);
        TextView tv_villageName = (TextView) view.findViewById(R.id.tv_comments);
        TextView tv_comments = (TextView) view.findViewById(R.id.tv_pictureCategory);
        String schoolCode = submission.getSchoolCode().trim();
        if (!schoolCode.equalsIgnoreCase(StaticConstants.SCHOOL_CODE_DEFAULT)) {
            tv_schoolCode.setText(submission.getSchoolCode());
            tv_schoolrath_label.setText("School Code : ");
        } else if (!schoolCode.equalsIgnoreCase(StaticConstants.RATH_NUMBER_DEFAULT)) {
            tv_schoolCode.setText(submission.getRathNumber());
            tv_schoolrath_label.setText("Rath Number : ");
        }
        tv_villageName.setText(submission.getVillageName());
        tv_comments.setText(submission.getComments());
        TextView tv_latitude = (TextView) view.findViewById(R.id.tv_latitude);
        TextView tv_longitude = (TextView) view.findViewById(R.id.tv_longitude);
        tv_latitude.setText("Latitude:   " + submission.getLatitude());
        tv_longitude.setText("Longitude:  " + submission.getLongitude());
        Button btn_submitUnsyncedData = (Button) view.findViewById(R.id.btn_submitUnsyncedData);
        btn_submitUnsyncedData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put("muID", "" + submission.getMuId());
                requestMap.put("threadID", "" + submission.getThreadID());
                requestMap.put("image", "" + submission.getBase64Image());
                requestMap.put("lat", "" + "" + submission.getLatitude());
                requestMap.put("long", "" + "" + submission.getLongitude());
                requestMap.put("date", "" + submission.getDate());
                requestMap.put("time", "" + submission.getTime());
                Log.e("postOfflineData", "------------------------->>>>>>>>>>>>>--------------------------------");
                Log.e("postOfflineData", "TIME: " + submission.getTime());
                Log.e("postOfflineData", "VILLAGE NAME: " + submission.getVillageName());
                if (submission.getThreadID().equalsIgnoreCase("6")) {
                    volleyTaskManager.urlusersubmission= Consts.USER_SUBMISSION_URL;
                    requestMap.put("sCode", submission.getSchoolCode());
                    //Tag Mismatch Village becomes comments
                    requestMap.put("comments", submission.getVillageName());
                    //Tag Mismatch Comments becomes piccat
                    requestMap.put("piccat", "" + submission.getComments());
                }
                else if (submission.getThreadID().equalsIgnoreCase("8")) {
                    volleyTaskManager.urlusersubmission= Consts.USER_SUBMISSION_URL2;
                    requestMap.put("sCode", submission.getSchoolCode());
                    //Tag Mismatch Village becomes comments
                    requestMap.put("comments", submission.getVillageName());
                    //Tag Mismatch Comments becomes piccat
                    requestMap.put("piccat", "" + submission.getComments());
                }
                else if (submission.getThreadID().equalsIgnoreCase("9")) {
                    volleyTaskManager.urlusersubmission= Consts.USER_SUBMISSION_URL2;
                    requestMap.put("sCode", submission.getSchoolCode());
                    //Tag Mismatch Village becomes comments
                    requestMap.put("comments", submission.getVillageName());
                    //Tag Mismatch Comments becomes piccat
                    requestMap.put("piccat", "" + submission.getComments());
                    requestMap.put("muID", "" + submission.getMuId());
                    requestMap.put("threadID", "" + submission.getThreadID());
                    requestMap.put("image", "" + submission.getBase64Image());
                    requestMap.put("lat", "" + "" + submission.getLatitude());
                    requestMap.put("long", "" + "" + submission.getLongitude());

                    requestMap.put("villageName2", "" + submission.getVillageName2());
                    requestMap.put("Anchal", "" + submission.getAnchal());
                    requestMap.put("dateofexamination", "" + submission.getDATE_OF_EXAMINATION());
                    requestMap.put("patientname", "" + submission.getPatientName());
                    requestMap.put("headfamily", "" + submission.getHEAD_OF_FAMILY());
                    requestMap.put("age", "" + "" + submission.getAGE());
                    requestMap.put("visionvr", "" + "" + submission.getVISION_VR());
                    requestMap.put("gender", "" + submission.getGENDER());
                    requestMap.put("glasses", "" + submission.getGLASSES());
                    requestMap.put("visionvl", "" + submission.getVISION_VL());
                    requestMap.put("historycomplaints", "" + "" + submission.getHISTORY_COMPLAINTS());
                    requestMap.put("pasthistory", "" + "" + submission.getPAST_HISTORY());
                    requestMap.put("presenthistory", "" + submission.getPRESENT_HISTORY());
                    requestMap.put("bpsystolic", "" + submission.getBP_SYSTOLIC());
                    requestMap.put("bpdiastolic", "" + submission.getBP_DIASTOLIC());
                    requestMap.put("bmiheight", "" + "" + submission.getBMI_HEIGHT());
                    requestMap.put("bmiweight", "" + "" + submission.getBMI_WEIGHT());
                    requestMap.put("bmiobesity", "" + submission.getBMI_OBESITY());
                    requestMap.put("sugarfasting", "" + submission.getSUGAR_FASTING());
                    requestMap.put("sugarpp", "" + submission.getSUGAR_PP());
                    requestMap.put("sugarrandom", "" + "" + submission.getSUGAR_RANDOM());
                    requestMap.put("medicine", "" + "" + submission.getMEDICINE());
                    requestMap.put("amount", "" + "" + submission.getAMOUNT());
                    requestMap.put("historytaking", "" + "" + submission.getHISTORY_TAKING());
                    requestMap.put("statenew", "" + "" + submission.getSTATE());
//
//
//
                }
                else if (submission.getThreadID().equalsIgnoreCase("7")) {
                    volleyTaskManager.urlusersubmission= Consts.USER_SUBMISSION_URL;
                    requestMap.put("rathcat", "" + submission.getComments());
                    requestMap.put("rCode", "" + submission.getRathNumber());
                    requestMap.put("sCode", "");
                    requestMap.put("comments", submission.getVillageName());
                } else {
                    requestMap.put("keyWords", "" + submission.getKeywords());
                }
                volleyTaskManager.doPostFormData(requestMap, true);
            }
        });
        mDialog.setCancelable(false);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        mDialog.getWindow().setAttributes(lp);
    }

    public void onDataSettingClick(View v) {
    }

    public void onCloseClick(View v) {
        finish();
    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.e("Response", "" + resultJsonObject);
        if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {
            try {
                if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
                    deleteFromDatabase(offlineSubmission.getDate(), offlineSubmission.getTime());
                    Toast.makeText(mContext, "Data synced successfully!", Toast.LENGTH_LONG).show();
                    finish();
                    // fetchOfflineRows();
                    // if(submissionList.size()>0) adapter.notifyDataSetChanged(submissionList);else finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mDialog.isShowing()) mDialog.dismiss();
            }
        }
    }

    @Override
    public void onError(VolleyError error) {
        Log.e("Response", "Error");
    }

    private void deleteFromDatabase(String date, String time) {
        Log.e("deleteFromDatabase", "---------------------------------------------\n");
        Log.e("deleteFromDatabase", "delete from database");
        Log.e("deleteFromDatabase", "---------------------------------------------\n\n");
        OfflineDB mdb = new OfflineDB(mContext);
        boolean status = mdb.deleteRow(date, time);
        if (status) {
            Log.e("deleteFromDatabase", "Deleted!!");
        } else Log.e("deleteFromDatabase", "NOT DONE DELETE");
    }
}
