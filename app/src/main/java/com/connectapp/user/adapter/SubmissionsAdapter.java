package com.connectapp.user.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.data.OfflineSubmission;

@SuppressLint("InflateParams")
public class SubmissionsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    ArrayList<OfflineSubmission> submissionList = new ArrayList<OfflineSubmission>();

    public SubmissionsAdapter(Context mContext, ArrayList<OfflineSubmission> submissionList) {

        this.mContext = mContext;
        this.submissionList = submissionList;
        mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return submissionList.size();
    }

    @Override
    public Object getItem(int position) {
        return submissionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View hView = convertView;

        if (convertView == null) {

            hView = mInflater.inflate(R.layout.list_item_unsynced_data, null);
            ViewHolder holder = new ViewHolder();
            holder.date = (TextView) hView.findViewById(R.id.tv_date);
            holder.time = (TextView) hView.findViewById(R.id.tv_time);
            hView.setTag(holder);

        }

        ViewHolder holder = (ViewHolder) hView.getTag();
        holder.date.setText(submissionList.get(position).getDate());
        holder.time.setText(submissionList.get(position).getTime());

        return hView;

    }

    class ViewHolder {

        TextView date;
        TextView time;

    }

    public void notifyDataSetChanged(ArrayList<OfflineSubmission> submissionList) {
        this.submissionList = submissionList;
        notifyDataSetChanged();
    }
}
