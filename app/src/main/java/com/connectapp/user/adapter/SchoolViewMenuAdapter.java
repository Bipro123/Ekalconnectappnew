package com.connectapp.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.activity.MainActivity;
import com.connectapp.user.activity.WebViewActivity;
import com.connectapp.user.util.Util;

public class SchoolViewMenuAdapter extends BaseAdapter {

	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;


	public SchoolViewMenuAdapter(Context mContext, Activity mActivity) {
		this.mContext = mContext;
		this.mActivity = mActivity;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View hView = convertView;
		if (convertView == null) {
			hView = mInflater.inflate(R.layout.list_item_menu_school, null);
			ViewHolder holder = new ViewHolder();
			//holder.rl_all_schools = (RelativeLayout) hView.findViewById(R.id.rl_all_schools);
			//holder.rl_school_code = (RelativeLayout) hView.findViewById(R.id.rl_school_code);
			hView.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) hView.getTag();
		//holder.cityName.setText("");
		/*if (position == 0) {
			holder.rl_school_code.setVisibility(View.GONE);
		} else if (position == 1) {
			holder.rl_all_schools.setVisibility(View.GONE);
		}*/

		// ************************************//
		// ******* CLICK EVENT FOR SUBMIT *****//
		// ************************************//

		/*holder.tvCountryCode = (TextView) hView.findViewById(R.id.tvCountryCode);
		holder.tv_stateCode = (TextView) hView.findViewById(R.id.tv_stateCode);
		holder.et_anchal = (EditText) hView.findViewById(R.id.et_anchal);
		holder.et_sankul = (EditText) hView.findViewById(R.id.et_sankul);
		holder.et_sanch = (EditText) hView.findViewById(R.id.et_sanch);
		holder.et_upsanch = (EditText) hView.findViewById(R.id.et_upsanch);
		holder.et_village = (EditText) hView.findViewById(R.id.et_village);

		holder.tv_stateCode.setFocusable(true);
		holder.tv_stateCode.requestFocus();
		holder.tv_stateCode.setCursorVisible(true);
		//holder.tv_stateCode.setOnClickListener(this);

		String countryCode = holder.tvCountryCode.getText().toString().trim();
		String stateCode =holder. tv_stateCode.getText().toString().trim();
		String anchal = holder.et_anchal.getText().toString().trim();
		String sankul = holder.et_sankul.getText().toString().trim();
		String sanch = holder.et_sanch.getText().toString().trim();
		String upsanch = holder.et_upsanch.getText().toString().trim();
		String village = holder.et_village.getText().toString().trim();

		if (stateCode.isEmpty() || stateCode.equalsIgnoreCase("-")) {
			Util.showMessageWithOk(mActivity, "Please enter the State Code.");
		} else if (anchal.isEmpty()) {
			Util.showMessageWithOk(mActivity, "Please enter the Anchal.");
		} else if (sankul.isEmpty()) {
			Util.showMessageWithOk(mActivity, "Please enter the Sankul.");
		} else if (sanch.isEmpty()) {
			Util.showMessageWithOk(mActivity, "Please enter the Sanch.");
		} else if (upsanch.isEmpty()) {
			Util.showMessageWithOk(mActivity, "Please enter the Up-Sanch.");
		} else if (village.isEmpty()) {
			Util.showMessageWithOk(mActivity, "Please enter the Village.");
		} else if (anchal.length() < 2) {
			Util.showMessageWithOk(mActivity, "Please enter the correct Anchal.");
		} else if (village.length() < 2) {
			Util.showMessageWithOk(mActivity, "Please enter the correct Village.");
		} else {
			String enteredSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal)
					.append(sankul).append(sanch).append(upsanch).append(village).toString();

			Intent intent = new Intent(mContext, SchoolViewActivity.class);
			intent.putExtra("loadUrl", "http://connectapp.net/dev/fts/portal/schools/" + enteredSchoolCode);
			mContext.startActivity(intent);
		}*/

		return hView;
	}


	class ViewHolder {
		RelativeLayout rl_school_code;
		//RelativeLayout rl_all_schools;

	}
}
