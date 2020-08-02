package com.connectapp.user.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.ImageAdapter;
import com.connectapp.user.adapter.SubmissionsAdapter;
import com.connectapp.user.constant.StaticConstants;
import com.connectapp.user.data.ImageClass;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.syncadapter.DBConstants;

import java.util.ArrayList;
import java.util.Collections;

/**
 * All the submissions that have been made (School submissions/Rath
 * submissions).
 *
 * @author ritwik.rai
 **/
public class HistoryActivity extends AppCompatActivity implements DBConstants {

    private Context mContext;
    private ListView lv_submission;
    private SubmissionsAdapter adapter;
    private ArrayList<OfflineSubmission> submissionList = new ArrayList<OfflineSubmission>();


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
        mContext = HistoryActivity.this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");
        lv_submission = (ListView) findViewById(R.id.lv_submission);

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

                    showSubmittedFormData(position);

                }

            });
        }

    }

    /**
     * fetches offline history data.
     */
    private void fetchOfflineRows() {

        submissionList = new HistoryDB().getHistory(mContext);
        // Reverse the History List
        Collections.reverse(submissionList);

    }

    /**
     * Display the submitted Data - history
     */
    @SuppressLint("InflateParams")
    private void showSubmittedFormData(int position) {
        final Dialog customDialog = new Dialog(HistoryActivity.this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.offline_submission, null);

        ArrayList<ImageClass> imagesList = new ArrayList<ImageClass>();
        ImageClass imageClass = new ImageClass();
        imageClass.setBase64value(submissionList.get(position).getBase64Image());
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
                customDialog.dismiss();
            }
        });

        TextView tv_schoolrath_label = (TextView) view.findViewById(R.id.tv_schoolrath_label);
        TextView tv_schoolCode = (TextView) view.findViewById(R.id.tv_schoolCode);
        TextView tv_villageName = (TextView) view.findViewById(R.id.tv_comments);
        TextView tv_comments = (TextView) view.findViewById(R.id.tv_pictureCategory);

        if (!submissionList.get(position).getSchoolCode().trim().equalsIgnoreCase(StaticConstants.SCHOOL_CODE_DEFAULT)) {

            tv_schoolCode.setText(submissionList.get(position).getSchoolCode());
            tv_schoolrath_label.setText("School Code : ");

        } else if (!submissionList.get(position).getRathNumber().trim().equalsIgnoreCase(StaticConstants.RATH_NUMBER_DEFAULT)) {

            tv_schoolCode.setText(submissionList.get(position).getRathNumber());
            tv_schoolrath_label.setText("Rath Number : ");
        }

        tv_villageName.setText(submissionList.get(position).getVillageName());
        tv_comments.setText(submissionList.get(position).getComments());

        TextView tv_latitude = (TextView) view.findViewById(R.id.tv_latitude);
        TextView tv_longitude = (TextView) view.findViewById(R.id.tv_longitude);

        tv_latitude.setText("Latitude:   " + submissionList.get(position).getLatitude());
        tv_longitude.setText("Longitude:  " + submissionList.get(position).getLongitude());

        customDialog.setCancelable(false);
        customDialog.setContentView(view);
        customDialog.setCanceledOnTouchOutside(false);

        // Start AlertDialog
        //customDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        customDialog.show();
        customDialog.getWindow().setAttributes(lp);

    }

    public void onDataSettingClick(View v) {
    }

    public void onCloseClick(View v) {
        finish();
    }
}
