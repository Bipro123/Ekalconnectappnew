package com.connectapp.user.adapter;

import java.util.ArrayList;

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

@SuppressLint("InflateParams")
@SuppressWarnings("unused")
public class ThreadListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    ArrayList<com.connectapp.user.data.Thread> threadList = new ArrayList<com.connectapp.user.data.Thread>();

    public ThreadListAdapter(Context mContext, ArrayList<com.connectapp.user.data.Thread> threadList) {
        Log.d("ListAdapter", "Size: " + threadList.size());
        System.out.println("ListAdapter Size: " + threadList.size());
        this.mContext = mContext;
        this.threadList = threadList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return threadList.size();
    }

    @Override
    public Object getItem(int position) {
        return threadList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View hView = convertView;
        if (convertView == null) {
            hView = mInflater.inflate(R.layout.list_item_expandable_media, null);
            ViewHolder holder = new ViewHolder();
            holder.threadName = (TextView) hView.findViewById(R.id.tv_item);
            holder.iv_item_image = (ImageView) hView.findViewById(R.id.iv_item_image);
            hView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) hView.getTag();
        holder.threadName.setText(threadList.get(position).getThreadName());
        holder.iv_item_image.setImageResource(threadList.get(position).getThreadImage());
        /*if (threadList.get(position).getThreadName().trim().equalsIgnoreCase("Rath")) {
            holder.iv_item_image.setImageResource(R.drawable.ic_rath);
        } else if (threadList.get(position).getThreadName().trim().equalsIgnoreCase("Members Directory")) {
            holder.iv_item_image.setImageResource(R.drawable.ic_members);
        } else if (threadList.get(position).getThreadName().trim().equalsIgnoreCase("Resources")) {
            holder.iv_item_image.setImageResource(R.drawable.ic_resources_bg);
        } else if (threadList.get(position).getThreadName().trim().equalsIgnoreCase("Ekal Prayash")) {
            holder.iv_item_image.setImageResource(R.drawable.ic_ekal_prayash_bg);
        } else if (threadList.get(position).getThreadName().trim().equalsIgnoreCase("ConnectApp Chat")) {
            holder.iv_item_image.setImageResource(R.drawable.ic_chat_bg);
        }*/

        return hView;
    }

    class ViewHolder {
        TextView threadName;
        ImageView iv_item_image;

    }
}
