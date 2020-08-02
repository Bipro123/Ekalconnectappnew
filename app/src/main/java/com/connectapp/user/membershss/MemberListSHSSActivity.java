package com.connectapp.user.membershss;

import android.app.SearchManager;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.MemberListAdapter;
import com.connectapp.user.data.Member;
import com.connectapp.user.db.MembersSHSSDB;

import java.util.ArrayList;

public class MemberListSHSSActivity extends AppCompatActivity {

    private Context mContext;
    private ListView lv_members;
    private String cityName;
    private ArrayList<Member> members;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mContext = MemberListSHSSActivity.this;

        lv_members = (ListView) findViewById(R.id.lv_search_result);

        //Fetch Intent Data
        cityName = getIntent().getStringExtra("cityName").trim();
        Log.e("TAG", "CityName: " + cityName);

        members = new MembersSHSSDB().getMembers(mContext, cityName);
        Log.e("TAG", "member list size: " + members.size());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cityName);

        if (members.size() > 0) {
            MemberListAdapter adapter = new MemberListAdapter(mContext, members);
            lv_members.setAdapter(adapter);
            lv_members.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(mContext, MemberViewSHSSActivity.class);
                    intent.putExtra("member", members.get(position));
                    startActivity(intent);

                }
            });
        } else {
            // Retry and fetch the Members again
			/*UserClass userClass = Util.fetchUserClass(mContext);
			userClass.setCurrentCityIndex(-1);
			Util.saveUserClass(mContext, userClass);
			startActivity(new Intent(mContext, MembersSHSSDirectory.class));
			finish();*/
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
        inflater.inflate(R.menu.menu_main, menu);
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

}
