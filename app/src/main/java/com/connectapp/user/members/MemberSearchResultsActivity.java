package com.connectapp.user.members;

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
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.adapter.SearchListAdapter;
import com.connectapp.user.data.Member;
import com.connectapp.user.db.MembersDB;

import java.util.ArrayList;

public class MemberSearchResultsActivity extends AppCompatActivity {

    private Context mContext;
    private ListView lv_search_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mContext = MemberSearchResultsActivity.this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("FTS Members");

        lv_search_result = (ListView) findViewById(R.id.lv_search_result);

        handleIntent(getIntent());
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

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            final ArrayList<Member> searchResult = new MembersDB().getSearchResult(mContext,
                    intent.getStringExtra(SearchManager.QUERY));
            Log.d("MemberSearchResActivity", "Size: " + searchResult.size());

            if (searchResult != null && searchResult.size() < 1) {
                Toast.makeText(mContext, "No Match found.", Toast.LENGTH_SHORT).show();

            } else {
                SearchListAdapter adapter = new SearchListAdapter(mContext, searchResult);
                lv_search_result.setAdapter(adapter);
                lv_search_result.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(mContext, MemberViewActivity.class);
                        intent.putExtra("member", searchResult.get(position));
                        startActivity(intent);
                    }
                });
            }

        }
    }
}
