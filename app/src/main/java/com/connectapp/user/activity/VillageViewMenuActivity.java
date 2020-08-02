package com.connectapp.user.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.util.Util;

public class VillageViewMenuActivity extends AppCompatActivity implements OnClickListener {

    private Context mContext;

    //private ListView lv_schools;

    private TextView tvCountryCode;
    private TextView tv_stateCode;
    private EditText et_anchal;
    private EditText et_sankul;
    private EditText et_sanch;
    private EditText et_upsanch;
    private EditText et_village;


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
        setContentView(R.layout.activity_menu_school);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Village Camp");
        mContext = VillageViewMenuActivity.this;
        //lv_schools = (ListView) findViewById(R.id.lv_menu_school);

        //SchoolViewMenuAdapter adapter = new SchoolViewMenuAdapter(mContext, SchoolViewMenuActivity.this);
        //lv_schools.setAdapter(adapter);
		/*lv_schools.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position) {
				case 0:

					Intent intent = new Intent(mContext, WebViewActivity.class);
					intent.putExtra("loadUrl", "http://connectapp.net/dev/fts/portal/schools/");
					intent.putExtra("title", "School");
					startActivity(intent);
					break;
				case 1:

					break;

				default:
					break;
				}
			}
		});*/
        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
        tv_stateCode = (TextView) findViewById(R.id.tv_stateCode);
        et_anchal = (EditText) findViewById(R.id.et_anchal);
        et_sankul = (EditText) findViewById(R.id.et_sankul);
        et_sanch = (EditText) findViewById(R.id.et_sanch);
        et_upsanch = (EditText) findViewById(R.id.et_upsanch);
        et_village = (EditText) findViewById(R.id.et_village);

        tv_stateCode.setFocusable(true);
        tv_stateCode.requestFocus();
        tv_stateCode.setCursorVisible(true);
        tv_stateCode.setOnClickListener(this);
        setTextWatcher();
        RobotoTextView submit = (RobotoTextView) findViewById(R.id.submit);
		/*ImageButton ib_closeWindow = (ImageButton) schoolCodeDialog.findViewById(R.id.ib_closeWindow);
		ib_closeWindow.setOnClickListener(new OnClickListener() {

 			@Override
			public void onClick(View v) {
				schoolCodeDialog.dismiss();
			}
		});*/

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String countryCode = tvCountryCode.getText().toString().trim();
                String stateCode = tv_stateCode.getText().toString().trim();
                String anchal = et_anchal.getText().toString().trim();
                String sankul = et_sankul.getText().toString().trim();
                String sanch = et_sanch.getText().toString().trim();
                String upsanch = et_upsanch.getText().toString().trim();
                String village = et_village.getText().toString().trim();

                if (stateCode.isEmpty() || stateCode.equalsIgnoreCase("-")) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the State Code.");
                } else if (anchal.isEmpty()) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the Anchal.");
                } else if (sankul.isEmpty()) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the Sankul.");
                } else if (sanch.isEmpty()) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the Sanch.");
                } else if (upsanch.isEmpty()) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the Up-Sanch.");
                } else if (village.isEmpty()) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the Village.");
                } else if (anchal.length() < 2) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the correct Anchal.");
                } else if (village.length() < 2) {
                    Util.showMessageWithOk(VillageViewMenuActivity.this, "Please enter the correct Village.");
                } else {
                    String enteredSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal)
                            .append(sankul).append(sanch).append(upsanch).append(village).toString();

                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("loadUrl", "http://connectapp.net/arogyaconnectapp/villages/"+enteredSchoolCode);
                    intent.putExtra("title", "Village");

                    startActivity(intent);
                }

				/*if (enteredSchoolCode.isEmpty()) {
					Toast.makeText(mContext, "Please enter the School Code.", Toast.LENGTH_LONG).show();
				} else if (enteredSchoolCode.length() < 11) {
					Toast.makeText(mContext, "Please enter the correct School Code.", Toast.LENGTH_LONG).show();
				} */

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stateCode:
                startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);
                break;

            default:
        }

    }

    private void setTextWatcher() {
        et_anchal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TextWatcher", "anchal on text changed count " + s.length());
                if (s.length() == 2)
                    et_sankul.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_sankul.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1)
                    et_sanch.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_sanch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1)
                    et_upsanch.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_upsanch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1)
                    et_village.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_village.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) {
                    et_village.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Util.hideSoftKeyboard(mContext, et_village);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("------>> onActivityResult CALLED  >>---------------");
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            this.tv_stateCode.setText(data.getStringExtra(StateCodeActivity.RESULT_STATECODE));
            this.et_anchal.requestFocus();
            Util.showSoftKeyboard(this.mContext, this.et_anchal);
        }
    }

}