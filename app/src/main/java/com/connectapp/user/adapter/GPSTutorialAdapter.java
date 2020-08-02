package com.connectapp.user.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.connectapp.user.R;

public class GPSTutorialAdapter extends PagerAdapter {
    public int getCount() {
        return 4;
    }

    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.layout.dialog_gps_image1;
                break;
            case 1:
                resId = R.layout.dialog_gps_image2;
                break;
            case 2:
                resId = R.layout.dialog_gps_image3;
                break;
            case 3:
                resId = R.layout.dialog_gps_image4;
                break;

        }
        View view = inflater.inflate(resId, null);
        ((ViewPager) collection).addView(view, 0);
        switch (position) {
            case 0:
                ImageView iv_previous = (ImageView) view.findViewById(R.id.iv_previous);
                iv_previous.setVisibility(View.GONE);
                break;
            case 3:
                ImageView iv_next = (ImageView) view.findViewById(R.id.iv_next);
                iv_next.setVisibility(View.GONE);
                break;
            default:
                break;

        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    //public boolean isViewFromObject(ViewGroup arg0, Object arg1) {
    // return arg0 == ((View) arg1);
    //}
    @Override
    public Parcelable saveState() {
        return null;
    }
}