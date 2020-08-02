package com.connectapp.user.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.DrawerAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.data.User;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.fragment.DashboardFragment;
import com.connectapp.user.chatmodel.UserChatClass;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.syncadapter.OfflineDB;
import com.connectapp.user.util.ImageUtil;
import com.connectapp.user.util.Util;
import com.connectapp.user.view.MaterialRippleLayout;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author raisahab.ritwik
 */
public class MainActivity extends AppCompatActivity implements DBConstants, OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private String TAG = getClass().getSimpleName();
    private ListView mDrawerList;
    private RobotoTextView rtv_userName;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
    private Context mContext;
    private Dialog customDialog;
    private Dialog unsyncedDataDialog;

    private VolleyTaskManager volleyTaskManager;

    private int dataCount = 0;
    private boolean isPostOfflineData = false;
    private UserClass userClass;
    private Dialog schoolCodeDialog;

    private TextView tvCountryCode;
    private TextView tv_stateCode;
    private EditText et_anchal;
    private EditText et_sankul;
    private EditText et_sanch;
    private EditText et_upsanch;
    private EditText et_village;

    //Firebase and GoogleApiClient
    public static GoogleApiClient mGoogleApiClient;
    public static FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final int RC_SIGN_IN = 9001;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInAccount acct;
    private DatabaseReference mDatabase;
    private LocationListener listener;
    private LocationManager locationManager;
    public boolean flag;
    String number1="not Present";
    String number2;
    public static double lat;
    public static double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_raga);
        mContext = MainActivity.this;

        userClass = Util.fetchUserClass(mContext);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ConnectApp");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = mDrawerTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.list_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {


            return;
        } else {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            number1 = tMgr.getLine1Number();
            /*Toast.makeText(mContext, "number not found", Toast.LENGTH_SHORT).show();*/
        }
        runtime_permission();
        /*if (flag) {
            Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
        }*/


        View headerView = getLayoutInflater().inflate(R.layout.header_navigation_drawer_social, mDrawerList, false);
        rtv_userName = (RobotoTextView) headerView.findViewById(R.id.tv_userName);
        ImageView iv_editProfile = (ImageView) headerView.findViewById(R.id.iv_editProfile);
        if (userClass != null)
            rtv_userName.setText(userClass.getName());

        ImageView iv = (ImageView) headerView.findViewById(R.id.image);

        ImageUtil.displayRoundImage(iv, "", null);

        mDrawerList.addHeaderView(headerView);// Add header before adapter (for
        // pre-KitKat)
        mDrawerList.setAdapter(new DrawerAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setSelectionAfterHeaderView();// Prevents list view to auto-scroll towards bottom.
        int color = getResources().getColor(R.color.material_grey_100);
        color = Color.argb(0xCD, Color.red(color), Color.green(color), Color.blue(color));
        mDrawerList.setBackgroundColor(color);
        mDrawerList.getLayoutParams().width = (int) getResources().getDimension(R.dimen.drawer_width_social);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(1);
        }

        iv_editProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isInternetAvailable(mContext))
                    startActivity(new Intent(mContext, ProfileActivity.class));
                else
                    Util.showMessageWithOk(MainActivity.this, "No Internet connection.");
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });


        // Check if the Login is not offline
        //if (!userClass.isOfflineLogin)

        // TODO -- Uncomment initializeFirebaseComponents() to enable Firebase initialization
        // Online Login so initialize firebase
        //initializeFirebaseComponents();

    }
    @SuppressLint("MissingPermission")
    private void enable_location() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat=location.getLatitude();
                longitude=location.getLongitude();
                /*Toast.makeText(mContext,"number="+number1+"Android Version="+ Build.VERSION.RELEASE+"Longitude="+longitude+"latitude="+lat+"UserID="+Util.fetchUserClass(mContext).getUserId(), Toast.LENGTH_LONG).show();*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i= new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager =(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

    }
    private boolean runtime_permission() {
        if(Build.VERSION.SDK_INT >= 23)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                enable_location();

            }
            else{
                runtime_permission();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the actio
        //
        // n bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } /*else if (item.getItemId() == R.id.menu_chat) {

            Util.showMessageWithOk(MainActivity.this, "ConnectApp Chat! Coming Soon.");
            *//*if (Util.isInternetAvailable(mContext)) {
                verifyUserLogin();
            } else {
                Util.showMessageWithOk(MainActivity.this, "You do not have an active internet connection.");
            }*//*
            return true;
        } */ else if (item.getItemId() == R.id.menu_edit_profile) {
            if (Util.isInternetAvailable(mContext))
                startActivity(new Intent(mContext, ProfileActivity.class));
            else
                Util.showMessageWithOk(MainActivity.this, "No Internet connection.");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("SignIn", "onConnectionFailed:" + connectionResult);
        Util.initToast(mContext, "Google Play Services error.");
        hideProgressDialog();
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);

            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }


    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public static double getLat() {

        return lat;
    }
    public static double getlongi(){
        return longitude;
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     **/
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void displayView(int position) {
        // update the main content by replacing fragments
        // You can change or Add a item in the left drawer from the left drawer class under Data Package
        Fragment fragment = null;
        switch (position) {

            case 1:
                // Main Dashboard fragment/Home
                fragment = new DashboardFragment();
                //Util.showMessageWithOk(mContext, "HOME");
                break;

            case 2:

                // UNSYNCED DATA
                showUnsyncedDataCount();
                break;

            case 3:
                // Submission History
                onHistoryClick();
                break;

            case 4:
                gotoSchoolViewMenu();
                //showSchoolCodeEntryMenu();
                break;

            case 5:
                gotoRathViewMenu();
                break;
            case 6:
                gotoVillageViewMenu();
                //showVillageCodeEntryMenu();
                break;
        /*case 6:
            // Resources and gallery
			onResourcesClick();
			break;*/
            case 7:
                String url = Consts.PRIVACY_POLICY_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            default:
                break;
        }

        if (fragment != null) {
            replaceFragment(fragment, position);
        } else {
            // error in creating fragment
            Log.e("FireBaseActivity", "Error in creating fragment");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void replaceFragment(Fragment fragment, int position) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    /**
     * Un-synced Data option
     */
    /*
	private void onDataSettingClick() {

	customDialog = new Dialog(mContext);

	customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

	LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
	View view = layoutInflater.inflate(R.layout.dialog_offline_data_status, null);

	ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);

	btn_close.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View v) {
	customDialog.dismiss();

	}
	});

	TextView tv_unsyncedData = (TextView) view.findViewById(R.id.tv_unsyncedData);

	offlineSubmissions.clear();
	try {
	fetchOfflineRows();

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	tv_unsyncedData.setText("Unsynced Data: (" + offlineSubmissions.size() + ")");

	}

	RelativeLayout rl_unsyncedDataCountLayout = (RelativeLayout) view.findViewById(R.id.rl_unsyncedDataCount);

	rl_unsyncedDataCountLayout.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
	if (offlineSubmissions.size() > 0) {
	customDialog.dismiss();
	startActivity(new Intent(mContext, UnSyncedDataActivity.class));
	}

	}
	});

	customDialog.setCancelable(false);
	customDialog.setContentView(view);
	customDialog.setCanceledOnTouchOutside(false);
	// Start AlertDialog
	customDialog.show();
	}*/
    private void showUnsyncedDataCount() {
        unsyncedDataDialog = new Dialog(mContext);
        unsyncedDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                mContext.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_unsynced_data_info, null);
        RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_text);
        RobotoTextView dialog_universal_info_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_ok);
        RobotoTextView dialog_universal_info_cancel = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_cancel);
        MaterialRippleLayout rippleLayoutCancel = (MaterialRippleLayout) view.findViewById(R.id.rippleLayoutCancel);

        offlineSubmissions.clear();
        try {
            fetchOfflineRows();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (offlineSubmissions.size() > 0) {
                unsyncedDataDialog.dismiss();
                startActivity(new Intent(mContext, UnSyncedDataActivity.class));
            } else {
                dialog_universal_info_text.setText("You have No Unsynced Data at the moment.");
                rippleLayoutCancel.setVisibility(View.GONE);

            }

        }

        dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (offlineSubmissions.size() > 0) {

                    unsyncedDataDialog.dismiss();
                    startActivity(new Intent(mContext, UnSyncedDataActivity.class));
                } else
                    unsyncedDataDialog.dismiss();
            }
        });
        dialog_universal_info_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                unsyncedDataDialog.dismiss();
            }
        });
        unsyncedDataDialog.setContentView(view);

        unsyncedDataDialog.show();
        if (offlineSubmissions.size() > 0)
            unsyncedDataDialog.dismiss();

    }

    private void onResourcesClick() {

        startActivity(new Intent(mContext, ResourcesActivity.class));

    }

    private void fetchOfflineRows() {
        OfflineDB mDb = new OfflineDB(mContext);
        SQLiteDatabase database = mDb.getReadableDatabase();

        try {
            Cursor cur = database.query(OFFLINE_TABLE, null, null, null, null, null, null);
            System.out.println("Count: " + cur.getCount());
            if (cur != null) {
                while (cur.moveToNext()) {
                    offlineSubmissions.add(Constant.getOfflineSubmissionFromCursor(cur));
                }
                cur.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onHistoryClick() {

        ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
        offlineSubmissions = new HistoryDB().getHistory(mContext);
        if (offlineSubmissions.size() > 0)
            startActivity(new Intent(mContext, HistoryActivity.class));
        else
            Util.showMessageWithOk(MainActivity.this, "You have no submission History!");

    }

    private void showSchoolCodeEntryMenu() {

        schoolCodeDialog = new Dialog(MainActivity.this);

        schoolCodeDialog.setContentView(R.layout.dialog_schoolcode_menu);
        // Set dialog title
        schoolCodeDialog.setTitle("Custom Dialog");

        tvCountryCode = (TextView) schoolCodeDialog.findViewById(R.id.tvCountryCode);
        tv_stateCode = (TextView) schoolCodeDialog.findViewById(R.id.tv_stateCode);
        et_anchal = (EditText) schoolCodeDialog.findViewById(R.id.et_anchal);
        et_sankul = (EditText) schoolCodeDialog.findViewById(R.id.et_sankul);
        et_sanch = (EditText) schoolCodeDialog.findViewById(R.id.et_sanch);
        et_upsanch = (EditText) schoolCodeDialog.findViewById(R.id.et_upsanch);
        et_village = (EditText) schoolCodeDialog.findViewById(R.id.et_village);

        tv_stateCode.setFocusable(true);
        tv_stateCode.requestFocus();
        tv_stateCode.setCursorVisible(true);
        tv_stateCode.setOnClickListener(this);
        setTextWatcher();
        RobotoTextView submit = (RobotoTextView) schoolCodeDialog.findViewById(R.id.submit);
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
                    Util.showMessageWithOk(MainActivity.this, "Please enter the State Code.");
                } else if (anchal.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Anchal.");
                } else if (sankul.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Sankul.");
                } else if (sanch.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Sanch.");
                } else if (upsanch.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Up-Sanch.");
                } else if (village.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Village.");
                } else if (anchal.length() < 2) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the correct Anchal.");
                } else if (village.length() < 2) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the correct Village.");
                } else {
                    String enteredSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal)
                            .append(sankul).append(sanch).append(upsanch).append(village).toString();
                    schoolCodeDialog.dismiss();

                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("loadUrl", "http://connectapp.net/dev/fts/portal/schools/" + enteredSchoolCode);
                    startActivity(intent);
                }

				/*if (enteredSchoolCode.isEmpty()) {
					Toast.makeText(mContext, "Please enter the School Code.", Toast.LENGTH_LONG).show();
				} else if (enteredSchoolCode.length() < 11) {
					Toast.makeText(mContext, "Please enter the correct School Code.", Toast.LENGTH_LONG).show();
				} */

            }
        });
        schoolCodeDialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(schoolCodeDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        schoolCodeDialog.show();
        schoolCodeDialog.getWindow().setAttributes(lp);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stateCode:
                startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);
                break;
            default:
                break;
        }
        startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);

    }

    private void setTextWatcher() {
        et_anchal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "anchal on text changed count " + s.length());
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

    private void gotoSchoolViewMenu() {
        Intent intent = new Intent(mContext, SchoolViewMenuActivity.class);
        startActivity(intent);

    }
    private void gotoVillageViewMenu() {
        Intent intent = new Intent(mContext, VillageViewMenuActivity.class);
        startActivity(intent);

    }

    private void gotoRathViewMenu() {
        Intent intent = new Intent(mContext, RathViewMenuActivity.class);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("------>> onActivityResult CALLED  >>---------------");
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            this.tv_stateCode.setText(data.getStringExtra(StateCodeActivity.RESULT_STATECODE));
            this.et_anchal.requestFocus();
            Util.showSoftKeyboard(this.mContext, this.et_anchal);
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Toast.makeText(mContext, "Google Account Verified.", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = result.getSignInAccount();

                showProgressDialog();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("Signin", "Google Sign In failed.");
                Toast.makeText(mContext, "Google Sign In failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Pass the activity result back to the Facebook SDK
            // mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //      ====================================================================
    //              ================ **** FIREBASE ****====================
    //      ====================================================================
    private void initializeFirebaseComponents() {

        //GOOGLE LOGIN

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, 0, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    String name = user.getDisplayName();
                    Toast.makeText(mContext, "" + user.getDisplayName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "something went wrong", Toast.LENGTH_LONG).show();
                }


            }
        };
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
    }

    public void showProgressDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void signOutFromFirebase() {
        mFirebaseAuth.signOut();
        // Google sign out
        //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        this.acct = acct;
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {

                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(mContext, "Authentication failed", Toast.LENGTH_SHORT).show();
                        } else {

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            addUserToDatabase(FirebaseAuth.getInstance().getCurrentUser());
                            String instanceId = FirebaseInstanceId.getInstance().getToken();

                            // Call update user API to update firebase id, email etc.
                            HashMap<String, String> requestMap = new HashMap<>();
                            requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
                            requestMap.put("emailID", "" + acct.getEmail());
                            requestMap.put("imageUrl", "" + mFirebaseAuth.getCurrentUser().getPhotoUrl());
                            requestMap.put("firebaseID", "" + mFirebaseAuth.getCurrentUser().getUid());
                            requestMap.put("firebaseInstanceID", "" + acct.getId());
                            // requestMap.put("deviceToken", "" + instanceId);
                            Log.e("firebaseId", "U Id: " + mFirebaseAuth.getCurrentUser().getUid());
                            Log.e("Request", "" + new JSONObject(requestMap));
                            // volleyTaskManager.doRegistration(requestMap, true);
                            //Toast.makeText(mContext,"Logging in! Please wait...",Toast.LENGTH_SHORT).show();
                            PostWithJsonWebTask.callPostWithJsonObjectWebtask(MainActivity.this, Consts.UPDATE_CHAT_PROFILE, requestMap, new ServerResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject resultJsonObject) {
                                    Log.e("onSuccess", "resultJsonObject: " + resultJsonObject);
                                    if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
                                        // Save userChat details
                                        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                                        if (userChatClass == null)
                                            userChatClass = new UserChatClass();

                                        userChatClass.displayName = "" + mFirebaseAuth.getCurrentUser().getDisplayName();
                                        userChatClass.profileUrl = "" + mFirebaseAuth.getCurrentUser().getPhotoUrl();
                                        userChatClass.setEmail("" + mFirebaseAuth.getCurrentUser().getEmail());
                                        userChatClass.firebaseId = "" + mFirebaseAuth.getCurrentUser().getUid();
                                        userChatClass.setUserId(Util.fetchUserClass(mContext).getUserId());
                                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                                        userChatClass.firebaseInstanceId = instanceId;
                                        Util.saveUserChatClass(mContext, userChatClass);
                                        // Goto ChatContactsActivity
                                        Intent intent = new Intent(mContext, ChatContactsActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Util.showMessageWithOk(MainActivity.this, "Something went wrong and please try again.");
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {

                                }
                            }, true, Request.Method.POST);

                        }
                    }
                });
    }

    /**
     * Add user to firebase User list [Table Name ==>> users]
     **/
    private void addUserToDatabase(FirebaseUser firebaseUser) {
        User user = new User(
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                firebaseUser.getUid(),
                firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString()
        );

        mDatabase.child("users")
                .child(user.getUid()).setValue(user);

        String instanceId = FirebaseInstanceId.getInstance().getToken();
        if (instanceId != null) {
            mDatabase.child("users")
                    .child(firebaseUser.getUid())
                    .child("instanceId")
                    .setValue(instanceId);
        }
    }

    /**
     * Verify user is logged in
     */
    private void verifyUserLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // User could not be verified.
            signOutFromFirebase();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);

        } else {
            // User verified.
           /* //User already logged in
            UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
            if (userChatClass == null)
                userChatClass = new UserChatClass();

            userChatClass.displayName = "" + mFirebaseAuth.getCurrentUser().getDisplayName();
            userChatClass.profileUrl = "" + mFirebaseAuth.getCurrentUser().getPhotoUrl();
            userChatClass.setEmail("" + mFirebaseAuth.getCurrentUser().getEmail());
            userChatClass.firebaseId = "" + mFirebaseAuth.getCurrentUser().getUid();
            userChatClass.setUserId(Util.fetchUserClass(mContext).getUserId());
            Util.saveUserChatClass(mContext, userChatClass);*/

            openChatPage();
        }
    }

    private void openChatPage() {
        {

            mDatabase = FirebaseDatabase.getInstance().getReference();
            addUserToDatabase(FirebaseAuth.getInstance().getCurrentUser());
            String instanceId = FirebaseInstanceId.getInstance().getToken();

            // Call update user API to update firebase id, email etc.
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
            requestMap.put("emailID", "" + mFirebaseAuth.getCurrentUser().getEmail());
            requestMap.put("imageUrl", "" + mFirebaseAuth.getCurrentUser().getPhotoUrl());
            requestMap.put("firebaseID", "" + mFirebaseAuth.getCurrentUser().getUid());
            requestMap.put("firebaseInstanceID", "" + FirebaseInstanceId.getInstance().getToken());
            // requestMap.put("deviceToken", "" + instanceId);
            Log.e("firebaseId", "U Id: " + mFirebaseAuth.getCurrentUser().getUid());
            Log.e("Request", "" + new JSONObject(requestMap));
            // volleyTaskManager.doRegistration(requestMap, true);
            //Toast.makeText(mContext,"Logging in! Please wait...",Toast.LENGTH_SHORT).show();
            PostWithJsonWebTask.callPostWithJsonObjectWebtask(MainActivity.this, Consts.UPDATE_CHAT_PROFILE, requestMap, new ServerResponseCallback() {
                @Override
                public void onSuccess(JSONObject resultJsonObject) {
                    Log.e("onSuccess", "resultJsonObject: " + resultJsonObject);
                    if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
                        // Save userChat details
                        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                        if (userChatClass == null)
                            userChatClass = new UserChatClass();

                        userChatClass.displayName = "" + mFirebaseAuth.getCurrentUser().getDisplayName();
                        userChatClass.profileUrl = "" + mFirebaseAuth.getCurrentUser().getPhotoUrl();
                        userChatClass.setEmail("" + mFirebaseAuth.getCurrentUser().getEmail());
                        userChatClass.firebaseId = "" + mFirebaseAuth.getCurrentUser().getUid();
                        userChatClass.setUserId(Util.fetchUserClass(mContext).getUserId());
                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                        userChatClass.firebaseInstanceId = instanceId;
                        Util.saveUserChatClass(mContext, userChatClass);
                        // Goto ChatContactsActivity
                        Intent intent = new Intent(mContext, ChatContactsActivity.class);
                        startActivity(intent);

                    } else {
                        Util.showMessageWithOk(MainActivity.this, "Something went wrong and please try again.");
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            }, true, Request.Method.POST);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO Uncomment for Firebase Chat
        /*mGoogleApiClient.stopAutoManage((FragmentActivity) mContext);
        mGoogleApiClient.disconnect();*/
    }

    @Override
    protected void onStart() {
        UserClass userClass = Util.fetchUserClass(mContext);
        if (userClass != null)
            rtv_userName.setText(userClass.getName());
        //Toast.makeText(mContext, "onStart", Toast.LENGTH_SHORT).show();
        super.onStart();

    }
}
