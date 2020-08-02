package com.connectapp.user.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectapp.user.R;

/**
 * Shows city list on the Members Directory
 * 
 * @author raisahab.ritwik
 * */
public class CityListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private HashMap<String, String> cityMap = new HashMap<String, String>();


	public CityListAdapter(Context mContext, HashMap<String, String> cityMap) {
		this.mContext = mContext;
		this.cityMap = cityMap;
		mInflater = LayoutInflater.from(mContext);

		Log.d("CityAdapter", "Map: " + cityMap);
		Log.d("Size", "CityMap size: " + cityMap.size());
		Log.d("City Name", "First City Name: " + cityMap.get("" + 0));
	}

	@Override
	public int getCount() {
		return cityMap.size();
	}

	@Override
	public Object getItem(int position) {
		return cityMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View hView = convertView;
		if (convertView == null) {
			hView = mInflater.inflate(R.layout.list_item_mem_dir_city, null);
			ViewHolder holder = new ViewHolder();
			holder.cityName = (TextView) hView.findViewById(R.id.tv_item);
			hView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) hView.getTag();
		holder.cityName.setText(cityMap.get("" + position));

		return hView;
	}


	class ViewHolder {
		TextView cityName;

	}
}
