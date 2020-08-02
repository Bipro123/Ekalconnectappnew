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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.StateListArrayAdapter;
import com.connectapp.user.data.State;

public class StateCodeActivity extends Activity {

	private ListView listView_country;
	public static String RESULT_STATECODE = "stateCode";
	private String[] countrynames, countrycodes;
	private TypedArray imgs;
	private List<State> countryList;
	AutoCompleteTextView act_state_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countrycode_layout);
		act_state_list = (AutoCompleteTextView) findViewById(R.id.act_country_list);
		listView_country = (ListView) findViewById(R.id.item_listView_dropDown);
		populateCountryList();
		StateListArrayAdapter adapter = new StateListArrayAdapter(StateCodeActivity.this, countryList);

		listView_country.setAdapter(adapter);

		listView_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				State c = countryList.get(position);
				Intent returnIntent = new Intent();
				returnIntent.putExtra(RESULT_STATECODE, c.getCode());
				setResult(RESULT_OK, returnIntent);
				//imgs.recycle();
				// recycle images
				finish();
			}
		});
		// Populate AutoComplete TextView
		ArrayAdapter<String> mAutoCompleteAdapter = new ArrayAdapter<String>(StateCodeActivity.this,
				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.state_name_array));
		act_state_list.setAdapter(mAutoCompleteAdapter);

		act_state_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra(RESULT_STATECODE,
						fetchPhoneCodeFromName(act_state_list.getText().toString().trim(), countryList));
				setResult(RESULT_OK, returnIntent);
				//imgs.recycle();
				// recycle images
				finish();
			}
		});

	}

	private void populateCountryList() {
		countryList = new ArrayList<State>();
		countrynames = getResources().getStringArray(R.array.state_name_array);
		countrycodes = getResources().getStringArray(R.array.state_array);
		//imgs = getResources().obtainTypedArray(R.array.country_flags);
		for (int i = 0; i < countrynames.length; i++) {
			countryList.add(new State(countrynames[i], countrycodes[i]));
		}

	}

	private String fetchPhoneCodeFromName(String countryName, List<State> countryList) {
		System.out.println("Country Name: " + countryName);
		HashMap<String, String> countryCodeMap = new HashMap<String, String>();
		for (int i = 0; i < countryList.size(); i++) {
//			String[] countryArray = countryList.get(i).getName().split("-");
//			String countryNamefromMap = countryArray[1];

			countryCodeMap.put(countryList.get(i).getName(), countryList.get(i).getCode());

		}
		Log.v("Hashmap", "" + countryCodeMap.toString());
		System.out.println("" + countryCodeMap.get(countryName));
		return countryCodeMap.get(countryName).toString();
	}

}
