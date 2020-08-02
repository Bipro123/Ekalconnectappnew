package com.connectapp.user.dropDownActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.adapter.CommentsListArrayAdapter;
import com.connectapp.user.data.State;

public class PictureCategoryActivity extends Activity {

	private ListView listView_country;
	public static String PICTURE_CATEGORY = "comments";
	private String[] countrynames, countrycodes;
	private TypedArray imgs;
	private List<State> countryList;
	public static int threadId=6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropdown_layout);
		listView_country = (ListView) findViewById(R.id.item_listView_dropDown);
		populateCountryList();
		CommentsListArrayAdapter adapter = new CommentsListArrayAdapter(PictureCategoryActivity.this,
				countryList);

		listView_country.setAdapter(adapter);

		listView_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				State c = countryList.get(position);
				Intent returnIntent = new Intent();
				returnIntent.putExtra(PICTURE_CATEGORY, c.getName());
				setResult(RESULT_OK, returnIntent);
				//imgs.recycle();
				// recycle images
				finish();
			}
		});

	}

	private void populateCountryList() {
		countryList = new ArrayList<State>();
		if(threadId==8)
		{
			countrynames = getResources().getStringArray(R.array.comments_arogya_array);
			countrycodes = getResources().getStringArray(R.array.comments_arogya_array);
		}
		else {
			countrynames = getResources().getStringArray(R.array.comments_array);
			countrycodes = getResources().getStringArray(R.array.comments_array);
		}
		//imgs = getResources().obtainTypedArray(R.array.country_flags);
		//Toast.makeText(this, Integer.toString(threadId), Toast.LENGTH_SHORT).show();
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
