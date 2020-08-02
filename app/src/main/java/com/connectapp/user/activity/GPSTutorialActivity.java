package com.connectapp.user.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.connectapp.user.R;
import com.connectapp.user.adapter.GPSTutorialAdapter;
import com.connectapp.user.location.StaticVariables;

public class GPSTutorialActivity extends AppCompatActivity {
    ViewPager myPager;

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
        setContentView(R.layout.dialog_gps_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("How to turn on the GPS?");
        StaticVariables.isHelp = true;
        GPSTutorialAdapter adapter = new GPSTutorialAdapter();
        myPager = (ViewPager) findViewById(R.id.viewpager_layout);

        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);

    }

    public void jumpToNextPage(View view) {

        myPager.setCurrentItem(myPager.getCurrentItem() + 1, true);
    }

    public void jumpToPreviousPage(View view) {

        myPager.setCurrentItem(myPager.getCurrentItem() - 1, true);
    }
}
