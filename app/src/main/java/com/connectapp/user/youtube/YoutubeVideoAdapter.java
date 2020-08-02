package com.connectapp.user.youtube;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.connectapp.user.R;
import com.connectapp.user.data.Video;
import com.connectapp.user.font.RobotoTextView;

public class YoutubeVideoAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ViewHolder viewHolder;
	ArrayList<Video> videoList;


	public YoutubeVideoAdapter(Context context, ArrayList<Video> videoList) {
		this.context = context;
		this.videoList = videoList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return (videoList.size());
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			row = inflater.inflate(R.layout.youtube_paylist_items, null);
			viewHolder = new ViewHolder(row);

			row.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) row.getTag();
		}

		viewHolder.title.setText(videoList.get(position).getTitle());
		viewHolder.videoThumb.setImageDrawable(videoList.get(position).getthumbDrawable());
		// setBitmapImage((videoList.get(position).getUrl()));
		return row;

	}


	class ViewHolder {
		public RobotoTextView title;
		public ImageView videoThumb;


		public ViewHolder(View row) {

			title = (RobotoTextView) row.findViewById(R.id.video_title);
			videoThumb = (ImageView) row.findViewById(R.id.iv_video_thumb);

		}

	}

}
