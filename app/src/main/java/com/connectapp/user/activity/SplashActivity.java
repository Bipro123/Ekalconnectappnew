package com.connectapp.user.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.firebase.Config;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends Activity implements ServerResponseCallback {

    private TextView tv_splash, tv_subText;
    private Typeface custom_font;
    private Account appAccount;
    private VolleyTaskManager volleyTaskManager;
    private String TAG = getClass().getSimpleName().toString();
    private Context mContext;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;

        //Log.v(null, null);
        tv_splash = (TextView) findViewById(R.id.tv_splash);

        tv_subText = (TextView) findViewById(R.id.tv_subText);

        custom_font = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

        tv_splash.setTypeface(custom_font);

        tv_subText.setTypeface(custom_font);

        // Fetches reg id from shared preferences

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        initializeSyncAdapter();
        volleyTaskManager = new VolleyTaskManager(mContext);
        new SplashTimerTask().execute();

    }


    /**
     * The Splash Timer. duration ---> 2345ms
     *
     * @params {@link AsyncTask}
     */
    private class SplashTimerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(2345);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (checkAndRequestPermissions()) {
                //all permission granted
                moveToNxtControl();
            } else {
                //require all permission.
            }
        }

    }


    /**
     * Open RegistrationActivity.
     */
    private void openRegistrationActivity() {

        Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Open LoginActivity.
     */
    public void openLoginActivity() {

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * Open FireBaseActivity.
     */
    private void openMainActivity() {

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Initilaizes the sync adapter with proper Authentication through
     * authentication service.
     **/
    private void initializeSyncAdapter() {

        Log.e("Splash Activity", "------------>> Sync Adapter initialized  <<--------------");
        appAccount = new Account(Constant.ACCOUNT_NAME, Constant.ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccountExplicitly(appAccount, null, null);
        ContentResolver.setIsSyncable(appAccount, Constant.PROVIDER, 1);
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.setSyncAutomatically(appAccount, Constant.PROVIDER, true);
    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {

        Log.e(TAG, "Result \n" + resultJsonObject);
        // TODO get tag isForced
        if (resultJsonObject.optString("code").trim().equalsIgnoreCase("404")) {

            // Application version is Up to date.
            proceedNormally();

        } else if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {

            // showVersionUpdateDialog();

            // Older version is installed
            if (resultJsonObject.optString("isForced").trim().equalsIgnoreCase("1"))
                Util.showMessageWithOkUpdate(SplashActivity.this, "A new version has arrived. Please tap UPDATE to update.",
                        new AlertDialogCallBack() {

                            @Override
                            public void onSubmit() {
                                openPlaystore();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            else
                Util.showMessageWithOkContinueUpdate(SplashActivity.this, "A new version has arrived. Please tap UPDATE to update, or SKIP to continue.",
                        new AlertDialogCallBack() {

                            @Override
                            public void onSubmit() {
                                openPlaystore();
                            }

                            @Override
                            public void onCancel() {
                                // Application version is Up to date.
                                proceedNormally();
                            }
                        });
        } else {
            // OFFLINE LOGIN -->> Enter in Offline Mode
            UserClass userClass = Util.fetchUserClass(mContext);
            if (userClass == null)
                userClass = new UserClass();
            userClass.isOfflineLogin = true;
            Util.saveUserClass(mContext, userClass);
            proceedNormally();
        }

    }

  /*  private void showVersionUpdateDialog() {

        Dialog dialog = new Dialog(mContext);
        dialog.show();
    }*/

    @Override
    public void onError(VolleyError error) {

       /* Util.showCallBackMessageWithOkCancel(mContext, "Server not responding. Tap ok to try again.\nor cancel to exit.",
                new AlertDialogCallBack() {

                    @Override
                    public void onSubmit() {
                        getApplicationVersion();

                    }

                    @Override
                    public void onCancel() {
                        finish();

                    }
                });*/
        // OFFLINE LOGIN -->> Enter in Offline Mode
        UserClass userClass = Util.fetchUserClass(mContext);
        if (userClass == null)
            userClass = new UserClass();
        userClass.isOfflineLogin = true;
        Util.saveUserClass(mContext, userClass);
        proceedNormally();

    }

    /**
     * Method to fetch the current Application version.
     */
    private void getApplicationVersion() {
        try {

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionCode = pInfo.versionCode;

            Log.e(TAG, "version code: " + versionCode);
            volleyTaskManager.doGetAppVersionCodeFromPlaystore("" + versionCode);

        } catch (NameNotFoundException e) {

            // If Exception Occurs then the Normal Splash will kick in
            Log.e(TAG, "Package Name not Found!");
            e.printStackTrace();

            proceedNormally();
        }
    }

    /**
     * This method opens up the default playstore app.
     */
    private void openPlaystore() {

        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                    + appPackageName)));
        }
        finish();
    }

    /**
     * Proceed forward to the respective Activities.
     */
    private void proceedNormally() {
        if (Util.fetchUserClass(SplashActivity.this) == null || !Util.fetchUserClass(mContext).isRegistered) {

            openRegistrationActivity();

        } else {

            Log.e("User logged in:", Util.fetchUserClass(SplashActivity.this).getIsLoggedin() + "");
            if (Util.fetchUserClass(SplashActivity.this).getIsLoggedin()) {

                openMainActivity();
            } else {
                openLoginActivity();
            }
        }
    }


    /*************************  App Permissions  *****************************/
    private boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int phonestate = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int externalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // Remove SMS and CALL Phone Permission
       // int callPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        //int sendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int getAccounts = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
            //Log.e("Permission", "CAMERA");
        }
        if (phonestate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            // Log.e("Permission", "READ_PHONE_STATE");
        }
        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            // Log.e("Permission", "ACCESS_FINE_LOCATION");
        }
        if (externalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //  Log.e("Permission", "WRITE_EXTERNAL_STORAGE");
        }
        // Remove SMS and CALL Phone Permission
        /*if (callPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
            //Log.e("Permission", "CALL_PHONE");
        }*/
        /*if (systemAlertWindow != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
            Log.e("Permission","SYSTEM_ALERT_WINDOW");
        }*/
        // Remove SMS and CALL Phone Permission
       /* if (sendSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
            //Log.e("Permission", "SEND_SMS");
        }*/
       /* if (syncSettings != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_SYNC_SETTINGS);
            Log.e("Permission","WRITE_SYNC_SETTINGS");
        }*/
        if (getAccounts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
            //Log.e("Permission", "GET_ACCOUNTS");
        }
        // Log.e("Permission", "Size: " + listPermissionsNeeded.size());
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Remove SMS and CALL Phone Permission
                //perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SYSTEM_ALERT_WINDOW, PackageManager.PERMISSION_GRANTED);
                // Remove SMS and CALL Phone Permission
                //perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_SYNC_SETTINGS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            // Remove SMS and CALL Phone Permission
                            //&& perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED
                            // Remove SMS and CALL Phone Permission
                            //&& perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_SYNC_SETTINGS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                            ) {

                        // here you can do your logic all Permission Success Call
                        moveToNxtControl();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Some Permissions are required for ConnectApp",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    dialog.dismiss();
                                                    finish();
                                                    break;

                                            }
                                        }
                                    });
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                        }
                    }
                }
            }
        }

    }

    private void moveToNxtControl() {
        if (Util.isInternetAvailable(SplashActivity.this)) {
            UserClass userClass = Util.fetchUserClass(mContext);
            if (userClass == null)
                userClass = new UserClass();
            userClass.isOfflineLogin = false;
            Util.saveUserClass(mContext, userClass);
            getApplicationVersion();
        } else {
            UserClass userClass = Util.fetchUserClass(mContext);
            if (userClass == null)
                userClass = new UserClass();
            userClass.isOfflineLogin = true;
            Util.saveUserClass(mContext, userClass);
            proceedNormally();
        }
    }


    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //Utils.startInstalledAppDetailsActivity(MainActivity.this);
                        Util.grantPermissionRedirect(mContext);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        dialog.create().dismiss();
                        finish();
                    }
                });
        dialog.show();
    }

}
