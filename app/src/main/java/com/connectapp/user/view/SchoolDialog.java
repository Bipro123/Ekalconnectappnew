package com.connectapp.user.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.util.Util;

public class SchoolDialog extends Activity implements View.OnClickListener {

    private Context mContext;
    private TextView tvCountryCode;
    private TextView tv_stateCode;
    private EditText et_anchal;
    private EditText et_sankul;
    private EditText et_sanch;
    private EditText et_upsanch;
    private EditText et_village;
    private static final int SANKUL_REQ_CODE = 3428;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_schoolcode);
        mContext = SchoolDialog.this;
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

        tv_stateCode.setFocusable(true);
        tv_stateCode.requestFocus();
        tv_stateCode.setCursorVisible(true);
        tv_stateCode.setOnClickListener(this);
        setTextWatcher();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_stateCode:
                startActivityForResult(new Intent(mContext, StateCodeActivity.class), SANKUL_REQ_CODE);
                break;
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
}
