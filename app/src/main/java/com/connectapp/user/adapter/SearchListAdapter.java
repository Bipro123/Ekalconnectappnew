package com.connectapp.user.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.data.Member;
import com.connectapp.user.util.ImageUtil;

@SuppressLint("InflateParams")
public class SearchListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	ArrayList<Member> searchResult = new ArrayList<Member>();


	public SearchListAdapter(Context mContext, ArrayList<Member> submissionList) {

		this.mContext = mContext;
		this.searchResult = submissionList;
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public int getCount() {
		return searchResult.size();
	}

	@Override
	public Object getItem(int position) {
		return searchResult.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View hView = convertView;

		if (convertView == null) {

			hView = mInflater.inflate(R.layout.item_search_result, null);
			ViewHolder holder = new ViewHolder();
			holder.memberName = (TextView) hView.findViewById(R.id.tv_memberName);
			holder.memberIcon = (ImageView) hView.findViewById(R.id.iv_profile_thumb);
			hView.setTag(holder);

		}

		ViewHolder holder = (ViewHolder) hView.getTag();
		holder.memberName.setText(searchResult.get(position).name);
		ImageUtil.displayRoundImage(holder.memberIcon, searchResult.get(position).picUrl, null);

		return hView;

	}


	class ViewHolder {

		TextView memberName;
		ImageView memberIcon;

	}
}
