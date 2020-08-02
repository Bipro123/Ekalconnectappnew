package com.connectapp.user.members;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.data.Member;
import com.connectapp.user.util.ImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MemberViewActivity extends AppCompatActivity {

	private Context mContext;
	private String memberName = "";
	private ImageView user_profile_photo;
	private TextView tv_user_name, tv_designation;
	private TextView tv_spouse, tv_contact_number, tv_mobile, tv_email, tv_address;
	private Toolbar toolbar;


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_user_profile);

		mContext = MemberViewActivity.this;

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// This is for custom navigation back button.
		//toolbar.setNavigationIcon(android.R.drawable.title_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		View logo = getLayoutInflater().inflate(R.layout.toolbar_member_view, null);
		logo.findViewById(R.id.home).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MembersDirectory.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		toolbar.addView(logo);

		Member member = (Member) getIntent().getExtras().get("member");
		tv_user_name = (TextView) findViewById(R.id.tv_user_name);
		tv_designation = (TextView) findViewById(R.id.tv_designation);

		Log.d("Member", "Name: " + member.name);

		user_profile_photo = (ImageView) findViewById(R.id.ib_profile_pic);
		tv_user_name = (TextView) findViewById(R.id.tv_user_name);

		tv_spouse = (TextView) findViewById(R.id.tv_spouse);
		tv_contact_number = (TextView) findViewById(R.id.tv_contact_number);
		tv_mobile = (TextView) findViewById(R.id.tv_mobile);
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_address = (TextView) findViewById(R.id.tv_address);
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
		ImageUtil.displayRoundImage(user_profile_photo, member.picUrl, null);
		tv_user_name.setText(member.name);
		tv_designation.setText(member.designation);

		tv_spouse.setText(Html.fromHtml("<b>" + "Name of the Spouse:  " + "</b> " + "<br/>" + member.spouseName));
		tv_contact_number.setText(Html.fromHtml("<b>" + "Contact Number:  " + "</b> " + "<br/>" + member.contactNo));

		tv_mobile.setText(member.mobile);
		tv_email.setText(member.email);

		tv_address.setText(Html.fromHtml("<b>" + "Address:  " + "</b> " + "<br/>" + member.add1 + "<br/>" + member.add2 + "<br/>"
				+ member.add3 + "<br/>" + member.town + "<br/>" + member.pin));

		tv_mobile.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					String phone = tv_mobile.getText().toString();
					if (event.getRawX() <= tv_mobile.getTotalPaddingLeft()) {

						if (phone != null && phone.trim().length() > 0) {
							Intent callMemberIntent = new Intent(Intent.ACTION_DIAL);
							callMemberIntent.setData(Uri.parse("tel:" + phone.trim()));
							startActivity(callMemberIntent);
						} else {
							//Do something -- future implementation

						}
						return true;
					}
					if (event.getRawX() >= tv_mobile.getTotalPaddingLeft()) {

						if (phone != null && phone.trim().length() > 0) {

							Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
							smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
							smsIntent.setData(Uri.parse("sms:" + phone.trim()));
							startActivity(smsIntent);

						} else {
							//Do something -- future implementation

						}
						return true;
					}

				}
				return false;
			}
		});
		tv_email.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					if (event.getRawX() >= tv_mobile.getTotalPaddingLeft()) {

						String email = tv_email.getText().toString();
						if (email != null && !email.trim().isEmpty()) {
							Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"
									+ tv_email.getText().toString().trim()));
							intent.putExtra(Intent.EXTRA_SUBJECT, "");
							intent.putExtra(Intent.EXTRA_TEXT, "");
							try {
								startActivity(intent);
							} catch (android.content.ActivityNotFoundException ex) {
								Toast.makeText(mContext, "Mail account not present.", Toast.LENGTH_SHORT).show();
							}
						}
						return true;
					}

				}
				return false;
			}
		});

	}

}
