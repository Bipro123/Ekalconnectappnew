package com.connectapp.user.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.firebase.Config;
import com.connectapp.user.util.Util;
import com.connectapp.user.view.FloatLabeledEditText;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

/**
 * Login Activity Class.
 *
 * @author raisahab.ritwik
 */
public class LoginActivity extends Activity implements ServerResponseCallback {
    private FloatLabeledEditText etPassword;
    private Context mContext;
    private CheckBox chkbx_rememberMe;
    private UserClass user = new UserClass();
    private String TAG = "";
    private VolleyTaskManager volleyTaskManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;

        TAG = mContext.getClass().getSimpleName();

        volleyTaskManager = new VolleyTaskManager(mContext);

        etPassword = (FloatLabeledEditText) findViewById(R.id.etPassword);

        chkbx_rememberMe = (CheckBox) findViewById(R.id.chkbx_rememberMe);

        if (Util.fetchUserClass(LoginActivity.this) != null) {

            user = Util.fetchUserClass(LoginActivity.this);

        }

        if (user.getIsRemember()) {

            etPassword.setText(user.getPassword());
            //chkbx_rememberMe.setChecked(user.getIsRemember());

        }

        chkbx_rememberMe.setChecked(true);
    }

    /**
     * onClickListener of the SignIn Button.
     */
    public void onLoginClicked(View view) {

		/* if (etUserName.getVisibility() != View.GONE && etUserName.getText().toString().trim().isEmpty())

			Util.showMessageWithOk(mContext, "Please enter Username.");
		
		else */
        if (Util.isInternetAvailable(mContext)) {
            if (etPassword.getText().toString().trim().isEmpty())
                Util.showMessageWithOk(LoginActivity.this, "Please enter your phone number.");
            else {
                // Fetch reg id from shared preferences
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String deviceToken = pref.getString("regId", null);
                Log.e(TAG, "Firebase reg id: " + deviceToken);
                HashMap<String, String> requestMap = new HashMap<String, String>();
                requestMap.put("phone", etPassword.getText().toString().trim());
                // Device token for push notification
                requestMap.put("deviceToken", "" + deviceToken);

                volleyTaskManager.doLogin(requestMap, true);
                //	LoginTask loginTask = new LoginTask(mContext);
                //	loginTask.mListener = this;
                //	loginTask.execute(new JSONObject(requestMap).toString());

            }
        } else {
            Util.showMessageWithOk(LoginActivity.this, "No Internet.");
        }

    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.d(TAG, "" + resultJsonObject);

        if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {

            try {

                String result = "";

                result = resultJsonObject.optString("code");
                user.setMessage(resultJsonObject.optString("msg"));

                Log.v("TAG", "" + result);

                if (result.equalsIgnoreCase("200")) {

                    JSONObject resultDataObject = resultJsonObject.optJSONObject("userData");
                    user.setUserId(resultDataObject.optString("userid"));
                    user.setName(resultDataObject.optString("name"));
                    user.setEmail(resultDataObject.optString("email"));
                    user.setPhone(resultDataObject.optString("phone"));
                    user.setStatus(resultDataObject.optString("status"));
                    user.setIsRemember(chkbx_rememberMe.isChecked());
                    user.setUserName("");
                    user.setPassword(etPassword.getText().toString().trim());
                    user.setMessage(resultDataObject.optString("msg"));
                    user.setIsLoggedin(true);
                    user.isRegistered = true;
                    Util.saveUserClass(mContext, user);
                    openMenuActivity();

                } else {
                    Toast.makeText(mContext, " " + user.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onError(VolleyError error) {

    }

    // Open Menu Activity
    private void openMenuActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Open Privacy policy on browser
    public void onPrivacyPolicyClicked(View v) {
        String url = Consts.PRIVACY_POLICY_URL;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }
}
