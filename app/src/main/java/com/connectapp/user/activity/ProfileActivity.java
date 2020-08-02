package com.connectapp.user.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.ServerResponseStringCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONObject;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements ServerResponseCallback {

    private Context mContext;
    private EditText et_name, et_phone, et_email;
    private VolleyTaskManager volleyTaskManager;
    //private boolean isFetchProfileService = false, isUpdateEmail = false, isUpdatePhone = false, isupdateProfile = false;
    private boolean isFetchProfileService = false, isUpdatePhone = false, isupdateProfile = false;
    private String phoneNumber = "", emailID = "";
    private String newEmail = "", newPhoneNumber = "";
    private Dialog editPhoneDialog;
    private Dialog editEmailDialog;
    private LinearLayout llParent;
    private String fetchedPhNumber = "";
    private boolean isUpdateEmail = false;


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
        setContentView(R.layout.activity_edit_profile);
        mContext = ProfileActivity.this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("  Edit Profile");

        et_name = (EditText) findViewById(R.id.et_name);
        // tv_phone = findViewById(R.id.tv_phone);
        // tv_email = findViewById(R.id.tv_email);

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);

        llParent = (LinearLayout) findViewById(R.id.llParent);

        llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideSoftKeyboard(mContext, view);
            }
        });

        volleyTaskManager = new VolleyTaskManager(mContext);

        fetchUserProfile();

    }

    /**
     * Method call to fetch user profile.
     */
    private void fetchUserProfile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
        volleyTaskManager.doFetchUserProfile(hashMap, true);
        isFetchProfileService = true;
    }

    /**
     * Method call for Edit Phone icon.
     */
    public void onEditPhoneClick(View view) {
        //showEditPhoneDialog();
    }

    /**
     * Method call for Edit Email icon.
     */
    public void onEditEmailClick(View view) {
        //showEditEmailDialog();
    }

    /**
     * This method opens a dialog to edit Email Address.
     */
    private void showEditEmailDialog() {

        editEmailDialog = new Dialog(mContext);

        editEmailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editEmailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_edit_email, null);

        final EditText et_newEmail = (EditText) view.findViewById(R.id.et_newEmail);
        final Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String newEmail = et_newEmail.getText().toString().trim();

                if (TextUtils.isEmpty(newEmail)) {
                    Toast.makeText(mContext, "Please enter an email id.", Toast.LENGTH_LONG).show();
                    return;
                } else if (!Util.isValidEmail(newEmail)) {

                    Toast.makeText(mContext, "Please enter a valid e-mail id.", Toast.LENGTH_LONG).show();
                    return;
                }

                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
                requestMap.put("email", "" + newEmail);
                requestMap.put("userPhone", "" + phoneNumber);
                ProfileActivity.this.newEmail = newEmail;
                isUpdateEmail = true;
                volleyTaskManager.doUpdateUserProfile(requestMap, true);
            }
        });

        editEmailDialog.setCancelable(true);
        editEmailDialog.setContentView(view);
        editEmailDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(editEmailDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        editEmailDialog.show();
        editEmailDialog.getWindow().setAttributes(lp);

    }

    /**
     * This method opens the dialog to edit the phone number.
     */
    private void showEditPhoneDialog() {
        editPhoneDialog = new Dialog(mContext);

        editPhoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editPhoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_edit_phone, null);

        final EditText et_newPho = (EditText) view.findViewById(R.id.et_newPho);
        Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newPhone = et_newPho.getText().toString().trim();

                if (TextUtils.isEmpty(newPhone)) {
                    Toast.makeText(mContext, "Please enter a phone number", Toast.LENGTH_LONG).show();
                    return;
                } else if (newPhone.length() < 10) {
                    Toast.makeText(mContext, "Please enter a correct phone number.", Toast.LENGTH_LONG).show();
                    return;
                }

                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
                requestMap.put("email", "" + emailID);
                requestMap.put("userPhone", "" + newPhone.trim());
                ProfileActivity.this.newPhoneNumber = newPhone;
                volleyTaskManager.doUpdateUserProfile(requestMap, true);
            }
        });

        editPhoneDialog.setCancelable(true);
        editPhoneDialog.setContentView(view);
        editPhoneDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(editPhoneDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        editPhoneDialog.show();
        editPhoneDialog.getWindow().setAttributes(lp);

    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.e("Response", "Response: " + resultJsonObject);
        if (isFetchProfileService) {
            isFetchProfileService = false;
            if (resultJsonObject.optString("code").equalsIgnoreCase("200")) {
                JSONObject data = resultJsonObject.optJSONObject("data");
                String name = data.optString("userName");
                String phone = data.optString("Phone");
                String email = data.optString("email");
                // emailID = email;
                // phoneNumber = phone;
                fetchedPhNumber = phone;
                updateView(name, phone, email);


            } else if (resultJsonObject.optString("code").equalsIgnoreCase("404")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else if (resultJsonObject.optString("code").equalsIgnoreCase("400")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            }

        } /*else if (isUpdateEmail) {
            isUpdateEmail = false;
            if (resultJsonObject.optString("code").equalsIgnoreCase("200")) {
                //Assign value to EmailID
                emailID = newEmail;
                // Update View
                tv_email.setText("" + emailID);
                // Display Success message
                Toast.makeText(mContext, "Email ID Ipdated successfully", Toast.LENGTH_SHORT).show();
                // Close dialog window
                if (editEmailDialog != null && editEmailDialog.isShowing())
                    editEmailDialog.dismiss();

            } else if (resultJsonObject.optString("code").trim().equalsIgnoreCase("400")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            }
        } else if (isUpdatePhone) {
            isUpdatePhone = false;
            if (resultJsonObject.optString("code").equalsIgnoreCase("200")) {
                // Assign value to phoneNumber
                phoneNumber = newPhoneNumber;
                // Update View
                tv_phone.setText(phoneNumber);
                // Display success message
                Toast.makeText(mContext, "Phone number updated successfully.", Toast.LENGTH_SHORT).show();
                // Close dialog window
                if (editPhoneDialog != null && editPhoneDialog.isShowing())
                    editPhoneDialog.dismiss();

            } else if (resultJsonObject.optString("code").trim().equalsIgnoreCase("400")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            }
        }*/ else if (isupdateProfile) {
            isupdateProfile = false;
            if (resultJsonObject.optString("code").equalsIgnoreCase("200")) {

                UserClass userClass = Util.fetchUserClass(mContext);
                userClass.setName("" + et_name.getText().toString().trim());
                Util.saveUserClass(mContext, userClass);

                // Display success message
                //Toast.makeText(mContext, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                Util.showCallBackMessageWithOkCallback(mContext, "Profile updated successfully.", new AlertDialogCallBack() {
                    @Override
                    public void onSubmit() {
                        if (isUpdateEmail) {

                            UserClass mUser = Util.fetchUserClass(mContext);
                            mUser.setIsLoggedin(false);
                            Util.saveUserClass(mContext, mUser);

                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else
                            finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

            } else if (resultJsonObject.optString("code").trim().equalsIgnoreCase("400")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            }
        }
    }

    @Override
    public void onError(VolleyError error) {
        String errorMsg = "";
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            errorMsg = mContext.getString(R.string.response_timeout);
        } else if (error instanceof AuthFailureError) {
            errorMsg = mContext.getString(R.string.auth_failure);
        } else if (error instanceof ServerError) {
            errorMsg = mContext.getString(R.string.server_error);
        } else if (error instanceof NetworkError) {
            errorMsg = mContext.getString(R.string.network_error);
        } else if (error instanceof ParseError) {
            errorMsg = mContext.getString(R.string.parse_error);
        }
        Util.showCallBackMessageWithOkCallback(mContext, "" + errorMsg, new AlertDialogCallBack() {
            @Override
            public void onSubmit() {
                finish();
            }

            @Override
            public void onCancel() {

            }
        });

    }

    private void updateView(String name, String phone, String email) {

        et_name.setText("" + name);
        //tv_phone.setText( phone);
        et_phone.setText(phone);
        //tv_email.setText("" + email);
        et_email.setText("" + email);
    }


    public void onSubmitClick(View view) {
        if (Util.isInternetAvailable(mContext)) {
            String userName = et_name.getText().toString().trim();
            String phone = et_phone.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                Util.showMessageWithOk(ProfileActivity.this, "Please enter your name.");
                et_name.requestFocus();
                return;
            } else if (TextUtils.isEmpty(phone)) {
                Util.showMessageWithOk(ProfileActivity.this, "Please enter your phone number.");
                et_phone.requestFocus();
                return;
            } else if (TextUtils.isEmpty(email)) {
                Util.showMessageWithOk(ProfileActivity.this, "Please enter your email address.");
                et_email.requestFocus();
                return;
            } else if (!Util.isValidEmail(email)) {
                Util.showMessageWithOk(ProfileActivity.this, "Please enter a valid email address.");
                et_email.requestFocus();
                return;
            } else if (phone.length() < 10) {
                Util.showMessageWithOk(ProfileActivity.this, "Please enter a valid phone number.");
                et_phone.requestFocus();
                return;
            }
            // Check whether phone number has been edited
            if (!phone.equalsIgnoreCase(fetchedPhNumber))
                isUpdateEmail = true;
            else
                isUpdateEmail = false;

            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
            requestMap.put("email", "" + et_email.getText().toString().trim());
            requestMap.put("userPhone", "" + et_phone.getText().toString().trim());
            requestMap.put("userName", "" + et_name.getText().toString().trim());
            isupdateProfile = true;
            volleyTaskManager.doUpdateUserProfile(requestMap, true);
        } else
            Util.showMessageWithOk(ProfileActivity.this, "No Internet connection.");

    }

    public void onCancelClick(View view) {
        finish();
    }


}
