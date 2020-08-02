package com.connectapp.user.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.RathViewMenuAdapter;
import com.connectapp.user.dropDownActivity.RathNumberActivity;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.util.Util;

public class RathViewMenuActivity extends AppCompatActivity {

	private Context mContext;

	private ListView lv_rath;

	private TextView dropDownActivity_rathNumber;

	/*private TextView tvCountryCode;
	private TextView tv_stateCode;
	private EditText et_anchal;
	private EditText et_sankul;
	private EditText et_sanch;
	private EditText et_upsanch;
	private EditText et_village;*/
	private static final int RATH_NUMBER_REQUEST = 13;


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
		setContentView(R.layout.activity_menu_rath);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("View Rath");
		mContext = RathViewMenuActivity.this;
		lv_rath = (ListView) findViewById(R.id.lv_menu_rath);

		RathViewMenuAdapter adapter = new RathViewMenuAdapter(mContext, RathViewMenuActivity.this);
		lv_rath.setAdapter(adapter);
		lv_rath.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
					case 0:

						Intent intent = new Intent(mContext, WebViewActivity.class);
						intent.putExtra("loadUrl", "http://connectapp.net/dev/fts/portal/rath/");
						intent.putExtra("title", "Rath");
						startActivity(intent);
						break;
					case 1:

						break;

					default:
						break;
				}
			}
		});
		dropDownActivity_rathNumber = (TextView) findViewById(R.id.dropDownActivity_rathNumber);
		RobotoTextView submit = (RobotoTextView) findViewById(R.id.submit);
		dropDownActivity_rathNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(mContext, RathNumberActivity.class), RATH_NUMBER_REQUEST);

			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String rathNumber = dropDownActivity_rathNumber.getText().toString().trim();
				if (rathNumber.length() < 0) {
					Util.showMessageWithOk(RathViewMenuActivity.this, "Please select a rath number.");
				} else {
					Intent intent = new Intent(mContext, WebViewActivity.class);
					intent.putExtra("loadUrl", "http://connectapp.net/dev/fts/portal/rath/" + rathNumber);
					intent.putExtra("title", "Rath");
					startActivity(intent);
				}

				/*if (enteredSchoolCode.isEmpty()) {
					Toast.makeText(mContext, "Please ent
					er the School Code.", Toast.LENGTH_LONG).show();
				} else if (enteredSchoolCode.length() < 11) {
					Toast.makeText(mContext, "Please enter the correct School Code.", Toast.LENGTH_LONG).show();
				} */

			}
		});
	}

	public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("------>> onActivityResult CALLED  >>---------------");
		if (requestCode == RATH_NUMBER_REQUEST && resultCode == Activity.RESULT_OK) {
			this.dropDownActivity_rathNumber.setText(data.getStringExtra(RathNumberActivity.RATH));
		}

	}

}