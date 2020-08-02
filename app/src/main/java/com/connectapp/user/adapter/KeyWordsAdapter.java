package com.connectapp.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.data.Keyword;

/**
 * @author ritwik 
 * 
 * */
/**
 * Keywords Adapter
 * */
public class KeyWordsAdapter extends BaseAdapter {

	//private Context mContext;
	private LayoutInflater mInflater;
	ArrayList<Keyword> listItem = new ArrayList<Keyword>();


	public KeyWordsAdapter(Context mContext, ArrayList<Keyword> listItem) {

		//this.mContext = mContext;
		this.listItem = listItem;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View hView = convertView;
		if (convertView == null) {
			hView = mInflater.inflate(R.layout.item_keyword, null);
			ViewHolder holder = new ViewHolder();
			holder.keyWord = (TextView) hView.findViewById(R.id.tv_name);

			hView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) hView.getTag();
		holder.keyWord.setText(listItem.get(position).getValue());
		return hView;
	}


	class ViewHolder {
		TextView keyWord;

	}
}
