package com.connectapp.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.connectapp.user.data.ImageClass;
import com.connectapp.user.util.Util;


// Adapter class to render the event details images with sliding control with view-pager

public class ImageAdapter extends PagerAdapter {
	Context context;

	ArrayList<ImageClass> imagesList = new ArrayList<ImageClass>();

	public ImageAdapter(Context context, ArrayList<ImageClass> imagesList) {
		this.context = context;
		this.imagesList = imagesList;
	}
	@Override
	public int getCount() {

		return imagesList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);
//		int padding = context.getResources().getDimensionPixelSize("5dp");
//		imageView.setPadding(padding, padding, padding, padding);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

		imageView.setImageBitmap(Util.getBitmapBase64FromString(imagesList.get(position).getBase64value()));

		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
}
