package com.connectapp.user.dropDownActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.CommentsListArrayAdapter;
import com.connectapp.user.data.District;
import com.connectapp.user.data.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistrictListActivity extends Activity {

    private ListView listView_country;
    public static String DISTRICT_NAME = "distName";
    private String[] countrynames, countrycodes;
    private TypedArray imgs;
    private List<State> countryList;
    private ArrayList<District> districts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropdown_layout);
        listView_country = (ListView) findViewById(R.id.item_listView_dropDown);
        districts = (ArrayList<District>) getIntent().getSerializableExtra("district");
        populateCountryList();
        CommentsListArrayAdapter adapter = new CommentsListArrayAdapter(DistrictListActivity.this,
                countryList);

        listView_country.setAdapter(adapter);

        listView_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                State c = countryList.get(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(DISTRICT_NAME, c.getName());
                setResult(RESULT_OK, returnIntent);
                //imgs.recycle();
                // recycle images
                finish();
            }
        });

    }

    private void populateCountryList() {
        countryList = new ArrayList<State>();
        countrynames = new String[districts.size()];
        for (int i = 0; i < districts.size(); i++) {
            countrynames[i] = districts.get(i).getDistrictName();
        }
        //countrynames = getResources().getStringArray(R.array.comments_array);
        countrycodes = countrynames;
        //imgs = getResources().obtainTypedArray(R.array.country_flags);
        for (int i = 0; i < countrynames.length; i++) {
            countryList.add(new State(countrynames[i], countrycodes[i]));
        }

    }

    private String fetchPhoneCodeFromName(String countryName, List<State> countryList) {
        System.out.println("Country Name: " + countryName);
        HashMap<String, String> countryCodeMap = new HashMap<String, String>();
        for (int i = 0; i < countryList.size(); i++) {
            String[] countryArray = countryList.get(i).getName().split("\\,");
            //String countryNamefromMap = countryArray[1];

            //countryCodeMap.put(countryNamefromMap, countryList.get(i).getCode());

        }
        Log.v("Hashmap", "" + countryCodeMap.toString());
        System.out.println("" + countryCodeMap.get(countryName));
        return countryCodeMap.get(countryName).toString();
    }

}
