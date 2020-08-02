package com.connectapp.user.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.ImageAdapter;
import com.connectapp.user.constant.StaticConstants;
import com.connectapp.user.data.District;
import com.connectapp.user.data.ImageClass;
import com.connectapp.user.data.State;
import com.connectapp.user.dropDownActivity.DemoDistListActivity;
import com.connectapp.user.dropDownActivity.DemoTehsilListActivity;
import com.connectapp.user.dropDownActivity.DemoVillageListActivity;
import com.connectapp.user.dropDownActivity.DistrictListActivity;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.dropDownActivity.StateListActivity;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RevenueVillageActivity extends AppCompatActivity implements View.OnClickListener, ServerResponseCallback {

    private Context mContext;
    private TextView dropDownActivity_state;
    private TextView dropDownActivity_district, dropDownActivity_village, dropDownActivity_tehsil;
    //private EditText et_village_name;
    private static final int STATE_REQ_CODE = 3424;
    private static final int DISTRICT_REQ_CODE = 3425;
    private static final int TEHSIL_REQ_CODE = 3426;
    private static final int VILLAGE_REQ_CODE = 3427;
    private static final int SANKUL_REQ_CODE = 3428;
    private VolleyTaskManager volleyTaskManager;
    private ArrayList<District> districts = new ArrayList<>();
    private CheckBox chkbx_projOne;
    private String schoolCode = "";// Temp flag schoolcode
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_village);
        mContext = RevenueVillageActivity.this;
        parentView = this.getCurrentFocus();

        volleyTaskManager = new VolleyTaskManager(mContext);
        dropDownActivity_state = (TextView) findViewById(R.id.dropDownActivity_state);
        dropDownActivity_district = (TextView) findViewById(R.id.dropDownActivity_district);
        dropDownActivity_tehsil = (TextView) findViewById(R.id.dropDownActivity_tehsil);
        dropDownActivity_village = (TextView) findViewById(R.id.dropDownActivity_village);
        chkbx_projOne = (CheckBox) findViewById(R.id.chkbx_projOne);

        dropDownActivity_state.setOnClickListener(this);
        dropDownActivity_district.setOnClickListener(this);
        dropDownActivity_tehsil.setOnClickListener(this);
        dropDownActivity_village.setOnClickListener(this);

        chkbx_projOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (chkbx_projOne.isChecked()) {
                    showSchoolCodeDialog();
                }
            }
        });
    }

    private TextView tvCountryCode;
    private TextView tv_stateCode;
    private EditText et_anchal;
    private EditText et_sankul;
    private EditText et_sanch;
    private EditText et_upsanch;
    private EditText et_village;
    boolean checkboxStatus = false; // Initializing just to ensure safety (Not Mandatory)

    private void showSchoolCodeDialog() {
        checkboxStatus = false;
        final Dialog customDialog = new Dialog(RevenueVillageActivity.this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_schoolcode, null);
        ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);
        tvCountryCode = (TextView) view.findViewById(R.id.tvCountryCode);
        tv_stateCode = (TextView) view.findViewById(R.id.tv_stateCode);
        et_anchal = (EditText) view.findViewById(R.id.et_anchal);
        et_sankul = (EditText) view.findViewById(R.id.et_sankul);
        et_sanch = (EditText) view.findViewById(R.id.et_sanch);
        et_upsanch = (EditText) view.findViewById(R.id.et_upsanch);
        et_village = (EditText) view.findViewById(R.id.et_village);
        RobotoTextView submit = (RobotoTextView) view.findViewById(R.id.submit);
        tv_stateCode.setFocusable(true);
        tv_stateCode.requestFocus();
        tv_stateCode.setCursorVisible(true);
        tv_stateCode.setOnClickListener(this);
        setTextWatcher();
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkbx_projOne.setChecked(checkboxStatus);
                Util.hideSoftKeyboard(mContext, v);
                customDialog.dismiss();

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = tvCountryCode.getText().toString().trim();
                String stateCode = tv_stateCode.getText().toString().trim();
                String anchal = et_anchal.getText().toString().trim();
                String sankul = et_sankul.getText().toString().trim();
                String sanch = et_sanch.getText().toString().trim();
                String upsanch = et_upsanch.getText().toString().trim();
                String village = et_village.getText().toString().trim();

                if (stateCode.isEmpty() || stateCode.equalsIgnoreCase("-")) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the State Code.");
                } else if (anchal.isEmpty()) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the Anchal.");
                } else if (sankul.isEmpty()) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the Sankul.");
                } else if (sanch.isEmpty()) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the Sanch.");
                } else if (upsanch.isEmpty()) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the Up-Sanch.");
                } else if (village.isEmpty()) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the Village.");
                } else if (anchal.length() < 2) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the correct Anchal.");
                } else if (village.length() < 2) {
                    Util.showMessageWithOk(RevenueVillageActivity.this, "Please enter the correct Village.");
                } else {
                    String enteredSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal)
                            .append(sankul).append(sanch).append(upsanch).append(village).toString();
                    schoolCode = enteredSchoolCode;
                    checkboxStatus = true;
                    customDialog.dismiss();
                }
            }
        });
        customDialog.setCancelable(false);
        customDialog.setContentView(view);
        customDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        customDialog.show();
        customDialog.getWindow().setAttributes(lp);

    }


    public void onNextClick(View view) {
        String stateName = dropDownActivity_state.getText().toString().trim();
        String districtName = dropDownActivity_district.getText().toString().trim();
        String tehsil = dropDownActivity_tehsil.getText().toString().trim();
        String villageName = dropDownActivity_village.getText().toString().trim();
        if (TextUtils.isEmpty(stateName)) {
            Toast.makeText(mContext, "Please select a state.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(districtName)) {
            Toast.makeText(mContext, "Please select the district.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(tehsil)) {
            Toast.makeText(mContext, "Please select the tehsil.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(villageName)) {
            Toast.makeText(mContext, "Please select the village.", Toast.LENGTH_SHORT).show();
            return;
        }
        Util.savePersonCount(mContext, 0);
        Intent intent = new Intent(mContext, VillageFormActivity.class);
        intent.putExtra("state", stateName);
        intent.putExtra("district", districtName);
        intent.putExtra("village", villageName);
        intent.putExtra("personCount", 0);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dropDownActivity_state:
                startActivityForResult(new Intent(this.mContext, StateListActivity.class), STATE_REQ_CODE);
                break;

            case R.id.dropDownActivity_district:
                /*String selectedState = dropDownActivity_state.getText().toString().trim();
                if (TextUtils.isEmpty(selectedState)) {
                    Toast.makeText(mContext, "Please select a state.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.isEmpty(selectedState) && districts.size() < 1) {
                    Toast.makeText(mContext, "Districts not found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this.mContext, DistrictListActivity.class);
                intent.putExtra("district", districts);
                startActivityForResult(intent, DISTRICT_REQ_CODE);*/
                startActivityForResult(new Intent(this.mContext, DemoDistListActivity.class), DISTRICT_REQ_CODE);
                break;
            case R.id.dropDownActivity_tehsil:
                startActivityForResult(new Intent(this.mContext, DemoTehsilListActivity.class), TEHSIL_REQ_CODE);
                break;
            case R.id.dropDownActivity_village:
                startActivityForResult(new Intent(this.mContext, DemoVillageListActivity.class), VILLAGE_REQ_CODE);
                break;
            case R.id.tv_stateCode:
                startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), SANKUL_REQ_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STATE_REQ_CODE && resultCode == Activity.RESULT_OK) {
            State selectedState = (State) data.getSerializableExtra(StateListActivity.STATE);
            dropDownActivity_state.setText("" + selectedState.getName().trim());
            String stateCode = selectedState.getCode();
            dropDownActivity_district.setText("");
            dropDownActivity_tehsil.setText("");
            dropDownActivity_village.setText("");
            // getDistrictFromStateCode(stateCode);

        } else if (requestCode == DISTRICT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            //dropDownActivity_district.setText(data.getStringExtra(DistrictListActivity.DISTRICT_NAME));
            dropDownActivity_district.setText(data.getStringExtra(DemoDistListActivity.DISTRICT));
            dropDownActivity_tehsil.setText("");
            dropDownActivity_village.setText("");
        } else if (requestCode == TEHSIL_REQ_CODE && resultCode == Activity.RESULT_OK) {
            //dropDownActivity_district.setText(data.getStringExtra(DistrictListActivity.DISTRICT_NAME));
            dropDownActivity_tehsil.setText(data.getStringExtra(DemoTehsilListActivity.TEHSIL));
            dropDownActivity_village.setText("");
        } else if (requestCode == VILLAGE_REQ_CODE && resultCode == Activity.RESULT_OK) {
            //dropDownActivity_district.setText(data.getStringExtra(DistrictListActivity.DISTRICT_NAME));
            dropDownActivity_village.setText(data.getStringExtra(DemoVillageListActivity.VILLAGE));
        } else if (requestCode == SANKUL_REQ_CODE && resultCode == Activity.RESULT_OK) {
            this.tv_stateCode.setText(data.getStringExtra(StateCodeActivity.RESULT_STATECODE));
            this.et_anchal.requestFocus();
            Util.showSoftKeyboard(this.mContext, this.et_anchal);
        }
    }

    private void getDistrictFromStateCode(String stateCode) {
        volleyTaskManager.doGet("http://districts.gov.in/doi_service/rest.php/district/" + stateCode + "/json");
    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.e("getDistrict", "District" + resultJsonObject);
        JSONArray categoriesArray = resultJsonObject.optJSONArray("categories");
        ArrayList<District> districts = new ArrayList<>();
        for (int i = 0; i < categoriesArray.length(); i++) {
            JSONObject districtJSON = categoriesArray.optJSONObject(i);
            JSONObject category = districtJSON.optJSONObject("category");
            Log.e("category", "category" + category);
            if (category.has("district_name")) {
                District district = new District();
                district.setDistrictID(category.optString("district_id"));
                district.setDistrictName(category.optString("district_name"));
                districts.add(district);
            }
        }
        this.districts = districts;
    }

    @Override
    public void onError(VolleyError error) {

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


}
