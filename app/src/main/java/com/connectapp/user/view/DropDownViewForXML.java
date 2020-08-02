package com.connectapp.user.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.connectapp.user.R;

/**
 * @author st0le
 */
public class DropDownViewForXML extends TextView implements OnClickListener,
        OnItemClickListener, OnDismissListener, OnItemSelectedListener {

    private PopupWindow pw;
    private ListView lv;
    private PopupListItemAdapter adapter;
    private List<PopupListItem> list;
    private int selectedPosition;
    private OnClickListener onClickListener = null;
    private OnItemClickListener onItemClickListener;
    private OnItemSelectedListener onItemSelectedListener;
    private int popupHeight, popupWidth;
    private final float[] r = new float[] {2, 2, 2, 2, 2, 2, 2, 2};
    private Drawable listDrawable;
    private float textSize = 15;
    private Context mContext;

    public DropDownViewForXML(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DropDownViewForXML(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public DropDownViewForXML(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
    	if(!isInEditMode()){
    		listDrawable = getRoundDrawable();
//            setPadding(0, 2, 2, 2);
//            setGravity(Gravity.CENTER_VERTICAL);
//            setTextSize(textSize);
//            setTextColor(Color.WHITE);
    		 Log.d("dropDown", "adapter not null....");
    		
            super.setOnClickListener(this);
    	}
        popupWidth = 50;
        DisplayMetrics metrics = mContext.getApplicationContext().getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;
		
        /*if(Util.checkScreenRES(mContext)) {
			textSize = 13;
			popupHeight = 150;
		} else {
			textSize = 17;
			popupHeight = 223;
		}*/
        if((screenWidth == 1280 && screenHeight == 752) || (screenWidth == 800 && screenHeight == 1232)) {
        	textSize = 17;
        } else {
        	textSize = 13;
        }
        
    }

    public void setPopUpWidthAndHeight(int width,int height) {
    	popupHeight = height;
    	popupWidth =width;
    	}
    
    private Drawable getRoundDrawable() {
        ShapeDrawable drawable = new ShapeDrawable();
//      drawable.getPaint().setColor(drawable.getPaint().setColor(Color.WHITE));
        //drop_down_back Background
        drawable.getPaint().setColor(Color.parseColor("#ee202020"));
        drawable.setShape(new RoundRectShape(r, null, null));
        return drawable;
    }
    
    private void setAdapter(PopupListItemAdapter adapter) {
        if(adapter != null) {
            this.adapter = adapter;
            Log.d("dropDown", "adapter not null....");
            
            if(lv == null){
            	lv = new ListView(getContext());
            	Log.d("dropDown", "ListView null....");
            }
            lv.setCacheColorHint(0);
//          lv.setBackgroundDrawable(listDrawable);
            lv.setBackgroundResource(R.drawable.grey_border_box); // <-- uncomment this 
            lv.setAdapter(adapter);
            ColorDrawable green = new ColorDrawable(Color.parseColor("#ee333333"));
			lv.setDivider(green);
			lv.setDividerHeight(2);
//          lv.setDivider(getResources().getDrawable(R.drawable.line_orange));
            lv.setOnItemClickListener(this);
            lv.setOnItemSelectedListener(this);
            lv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                                                LayoutParams.MATCH_PARENT));
            selectedPosition = 0;
        }
        else {
            selectedPosition = -1;
            this.adapter = null;
        }
        refreshView();
    }

    private void refreshView() {
        if(adapter != null) {
            PopupListItem popupListItem = adapter.getItem(selectedPosition);
            setText(popupListItem.toString());
            if(popupListItem.getResId() != -1)
                setCompoundDrawablesWithIntrinsicBounds(popupListItem.getResId(), 0, 0, 0);
        }
    }

    @SuppressWarnings("deprecation")
	public void onClick(View v) {
        if(pw == null || !pw.isShowing()) {
            popupWidth = getMeasuredWidth();
            pw = new PopupWindow(v);
            pw.setContentView(lv);
            pw.setWidth(popupWidth);
//            pw.setHeight(popupHeight);
            pw.setHeight(LayoutParams.WRAP_CONTENT);
            pw.setBackgroundDrawable(new BitmapDrawable());
            pw.setOutsideTouchable(false);
            pw.setFocusable(true);
            pw.setClippingEnabled(true);
//            pw.showAsDropDown(v, v.getLeft(), v.getTop());
            pw.showAsDropDown(v, v.getScrollX(), v.getScrollY()+5);
            pw.setOnDismissListener(this);
        }
        if(onClickListener != null)
            onClickListener.onClick(v);
    }
    
    public void setHeight(int height){
    	popupHeight = height;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(pw != null)
            pw.dismiss();
        selectedPosition = arg2;
        refreshView();
        if(onItemClickListener != null)
            onItemClickListener.onItemClick(arg0, arg1, arg2, arg3);
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(onItemSelectedListener != null)
            onItemSelectedListener.onItemSelected(arg0, arg1, arg2, arg3);
    }

    private class PopupListItem {
        private final String text;
        private final int resId;

        public PopupListItem(String text) {
            this.text = text;
            this.resId = -1;
        }

        public PopupListItem(String text, int resId) {
            this.text = text;
            this.resId = resId;
        }

        public String getText() {
            return text;
        }

        public int getResId() {
            return resId;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private class PopupListItemAdapter extends ArrayAdapter<PopupListItem> {

        public PopupListItemAdapter(Context context, List<PopupListItem> objects) {
            super(context, android.R.layout.activity_list_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                convertView = newView();
                viewHolder = new ViewHolder();
                viewHolder.tvText = (TextView)convertView.findViewById(android.R.id.text1);
//              viewHolder.tvText.setTextColor(Color.BLACK);
                viewHolder.tvText.setTextColor(Color.parseColor("#333333"));
                viewHolder.tvText.setPadding(5, 10, 10, 5);
                viewHolder.ivIcon = (ImageView)convertView.findViewById(android.R.id.icon);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.tvText.setText(getItem(position).getText());
            int resId = getItem(position).getResId();
            if(resId != -1)
                viewHolder.ivIcon.setImageResource(resId);
            return convertView;
        }

        private class ViewHolder {
            TextView tvText;
            ImageView ivIcon;
        }

        private View newView() {
            LinearLayout ll_parent = new LinearLayout(getContext());
            ll_parent.setGravity(Gravity.CENTER_VERTICAL);
            ll_parent.setOrientation(LinearLayout.HORIZONTAL);

            ImageView ivIcon = new ImageView(getContext());
            ivIcon.setId(android.R.id.icon);
            ll_parent.addView(ivIcon);

            TextView tvText = new TextView(getContext());
            tvText.setTextColor(Color.BLACK);
            tvText.setTextSize(textSize);
            tvText.setMaxLines(2);
            tvText.setEllipsize(TruncateAt.MARQUEE);
            tvText.setMarqueeRepeatLimit(-1);
            tvText.setId(android.R.id.text1);
            ll_parent.addView(tvText);
            return ll_parent;
        }
    }

    public void setItems(String[] arr) {
        if(arr == null)
            throw new NullPointerException("Items Array is null.");
        if(list == null)
            list = new ArrayList<DropDownViewForXML.PopupListItem>();
        list.clear();
        for(String text: arr)
            list.add(new PopupListItem(text));
        adapter = new PopupListItemAdapter(getContext(), list);
        setAdapter(adapter);
        Log.d("dropDown", "setItems....");
        setText("");
//        refreshView();
    }

    public void setItems(String[] arr, int[] ico) {
        if(ico == null) {
            setItems(arr);
        }
        else {
            if(list == null)
                list = new ArrayList<DropDownViewForXML.PopupListItem>();
            list.clear();
            for(int i = 0; i < arr.length; i++) {
                if(i < ico.length)
                    list.add(new PopupListItem(arr[i], ico[i]));
                else
                    list.add(new PopupListItem(arr[i]));
            }
            adapter = new PopupListItemAdapter(getContext(), list);
            setAdapter(adapter);
        }
        refreshView();
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        textSize = size;
    }

    public void onDismiss() {
        pw = null;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        if(onItemSelectedListener != null)
            onItemSelectedListener.onNothingSelected(arg0);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        refreshView();
    }
}
