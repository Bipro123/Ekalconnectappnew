package com.connectapp.user.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.ViewPager;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.ImageAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.constant.StaticConstants;
import com.connectapp.user.data.ImageClass;
import com.connectapp.user.data.PrefUtils;
import com.connectapp.user.data.RathClass;
import com.connectapp.user.data.Thread;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.dropDownActivity.PictureCategoryActivity;
import com.connectapp.user.dropDownActivity.RathNumberActivity;
import com.connectapp.user.dropDownActivity.RathPictureCategoryActivity;
import com.connectapp.user.location.FetchCordinates;
import com.connectapp.user.location.FetchCordinates.GPSLocationListener;
import com.connectapp.user.location.FusedLocationCallback;
import com.connectapp.user.location.LocationCallback;
import com.connectapp.user.location.StaticVariables;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author raisahab.ritwik
 * <p>
 * Rath Form Activity.
 * </p>
 */
public class RathFormActivity extends AppCompatActivity implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener,
        ServerResponseCallback, FusedLocationCallback, LocationCallback, OnClickListener {
    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int PICTURE_GALLERY_REQUEST = 2572;
    private static final int PICTURE_CATEGORY_REQUEST = 11;
    private static final int STATE_CODE_REQUEST = 12;
    private static final int RATH_NUMBER_REQUEST = 13;
    private static Uri mCapturedImageURI;
    private String TAG = getClass().getSimpleName();
    private TextView dropDownActivity_rathNumber;
    private TextView dropDownActivity_pictureCategory;
    private EditText et_comments;

    private FusedLocationProviderApi fusedLocationProviderApi;
    private String geoAddress = "";
    private ContentValues historyCV;
    private int imageCount = 0;
    private ArrayList<ImageClass> imagesList;
    private LinearLayout ll_dynamicField;
    private Context mContext;
    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ProgressDialog mProgressDialog;

    private Dialog mDialog;
    private ProgressBar progressBarFetchLoc;

    private AlertDialog systemAlertDialog;
    private Thread thread;
    private TextView tv_imageProgress;
    private View v_swipeLeft;
    private View v_swipeRight;
    private VolleyTaskManager volleyTaskManager;
    private ViewPager vp_selectedImages;
    private boolean isSubmitService = false;
    private boolean isRathNumberService = false;
    private FetchCordinates mtask;

    ///-----------------
    public double lati = 0.0;
    public double longi = 0.0;
    public double networklat;
    public double netwoklong;
    public Location mLocation = new Location("0");

    public LocationManager mLocationManager;

    public GPSLocationListener gpsLocationListener;

    private boolean isGPSLocation = false;
    private ImageButton ib_reject_picture;
    private String imageFileName = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_help) {
            startActivity(new Intent(mContext, GPSTutorialActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* AlertDialog Callback saved Off-line*/
    class SavedOffline implements AlertDialogCallBack {
        SavedOffline() {
        }

        public void onSubmit() {
            RathFormActivity.this.finish();
            RathFormActivity.this.mCurrentLocation = null;
        }

        public void onCancel() {
        }
    }

    /* AlertDialog Callback Submission Complete */
    class SubmissionComplete implements AlertDialogCallBack {
        SubmissionComplete() {
        }

        public void onSubmit() {
            RathFormActivity.this.clearForm();
            RathFormActivity.this.finish();
            RathFormActivity.this.mCurrentLocation = null;
        }

        public void onCancel() {
        }
    }
    // TODO -- SHOW HELP ICON
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rath_form);
        mContext = RathFormActivity.this;
        dropDownActivity_pictureCategory = (TextView) findViewById(R.id.dropDownActivity_pictureCategory);
        dropDownActivity_pictureCategory.setOnClickListener(this);
        volleyTaskManager = new VolleyTaskManager(this.mContext);
        vp_selectedImages = (ViewPager) findViewById(R.id.vp_selectedImages);
        tv_imageProgress = (TextView) findViewById(R.id.tv_imageProgress);
        v_swipeLeft = findViewById(R.id.v_swipeLeft);
        v_swipeRight = findViewById(R.id.v_swipeRight);
        ib_reject_picture = (ImageButton) findViewById(R.id.ib_reject_picture);
        imagesList = new ArrayList<ImageClass>();
        mProgressDialog = new ProgressDialog(this.mContext);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);


        // Display metrics for window size
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        //Create Dialog
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog_fetch_location);

        Button btn_cancel_getLocation = (Button) mDialog.findViewById(R.id.btn_cancel);
        btn_cancel_getLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGPSLocation) {
                    mtask.cancel(true);
                    isGPSLocation = false;
                }
                hideDialog();
            }
        });
        progressBarFetchLoc = (ProgressBar) mDialog.findViewById(R.id.progressBar_location);

        mDialog.getWindow().setLayout((6 * width) / 7, ViewGroup.LayoutParams.WRAP_CONTENT);

        thread = new Thread();
        thread = (Thread) getIntent().getExtras().get("thread");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rath");
        initView();
    }

    private void initView() {
        ll_dynamicField = (LinearLayout) findViewById(R.id.ll_dynamicField);
        if (thread.getThreadID().equalsIgnoreCase("7")) {
            ll_dynamicField.setVisibility(View.VISIBLE);
            et_comments = (EditText) findViewById(R.id.et_comments);
            dropDownActivity_rathNumber = (TextView) findViewById(R.id.dropDownActivity_rathNumber);
            dropDownActivity_rathNumber.setOnClickListener(this);
        }
        mCurrentLocation = null;
        ib_reject_picture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesList.clear();
                if (!TextUtils.isEmpty(imageFileName.trim())) {
                    deleteSelectedImageFile(imageFileName);
                    imageFileName = "";
                }
                imageUpdateOnView();

            }
        });
        checkingLocation();
    }

    @SuppressLint({"InflateParams"})
    public void onPictureClick(View v) {
        if (this.imagesList.size() < 1) {
            this.mProgressDialog.setMessage("Please wait...");
            this.mProgressDialog.setCancelable(true);
            showProgressDialog();
            cameraSelectedPic();
            return;
        }
        Util.showMessageWithOk(RathFormActivity.this, "Maximum number of images has already been selected!");
    }

    private void showProgressDialog() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Method to select picture from gallery.
     */
    protected void populatingSelectedPic() {
        Log.e(TAG, "selected from gallery");
        Intent albumIntent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
        albumIntent.setType("image/*");
        startActivityForResult(albumIntent, PICTURE_GALLERY_REQUEST);
    }

    /**
     * Method to capture image from camera.
     */
    protected void cameraSelectedPic() {
        Log.e(TAG, "selected from camera");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ConnectAppImages");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "RATH_" + timeStamp + ".png");
        imageFileName = "RATH_" + timeStamp + ".png";
        Log.e("FileName", "imageFileName: " + imageFileName);
        Uri uriSavedImage = Uri.fromFile(image);
        mCapturedImageURI = uriSavedImage;
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAMERA_PIC_REQUEST);

    }

    /**
     * Fetch image path.
     */
    private void processImagePath(String picturePath) {
        Options opt = new Options();
        opt.inScaled = true;
        int bitWidth = BitmapFactory.decodeFile(picturePath).getWidth();
        int bitHeight = BitmapFactory.decodeFile(picturePath).getHeight();
        System.out.println("width : " + bitWidth + " bitHeight : " + bitHeight);
        if (bitWidth <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitHeight <= 1536) {
            opt.inSampleSize = 4;
        } else if ((bitHeight <= 1536 || bitHeight > 1944)
                && (bitWidth <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitWidth > 2592)) {
            opt.inSampleSize = 8;
        } else {
            opt.inSampleSize = 6;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, opt);
        if (bitmap != null) {
            try {
                int orientation = new ExifInterface(picturePath).getAttributeInt("Orientation", 1);
                Log.e("orientation", new StringBuilder(String.valueOf(orientation)).append("<<<").toString());
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case CompletionEvent.STATUS_FAILURE /*1*/:
                        Log.e("Case:", "1");
                        break;
                    case CompletionEvent.STATUS_CONFLICT /*2*/:
                        Log.e("Case:", "2");
                        break;
                    case CompletionEvent.STATUS_CANCELED /*3*/:
                        Log.e("Case:", "3");
                        matrix.postRotate(BitmapDescriptorFactory.HUE_CYAN);
                        break;
                    case GeofencingRequest.INITIAL_TRIGGER_DWELL /*4*/:
                        Log.e("Case:", "4");
                        break;
                    case DetectedActivity.TILTING /*5*/:
                        Log.e("Case:", "5");
                        break;
                    case Quest.STATE_FAILED /*6*/:
                        Log.e("Case:", "6");
                        matrix.postRotate(90.0f);
                        break;
                    case DetectedActivity.WALKING /*7*/:
                        Log.e("Case:", "7");
                        break;
                    case DetectedActivity.RUNNING /*8*/:
                        Log.e("Case:", "8");
                        matrix.postRotate(-90.0f);
                        break;
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageClass imageClass = new ImageClass();
            imageClass.setBase64value(Util.getBase64StringFromBitmap(bitmap));
            imageClass.setImageCount(this.imageCount + 1);
            imagesList.add(imageClass);
            imageCount++;
        } else {
            Toast.makeText(this, new StringBuilder(String.valueOf(picturePath)).append("not found").toString(), Toast.LENGTH_LONG).show();
        }
        imageUpdateOnView();
    }

    private void imageUpdateOnView() {
        vp_selectedImages.setAdapter(new ImageAdapter(this, imagesList));
        if (imagesList.size() == 0) {
            vp_selectedImages.setBackgroundResource(R.drawable.default_empty);
            ib_reject_picture.setVisibility(View.INVISIBLE);
        } else {
            vp_selectedImages.setBackgroundColor(Color.parseColor("#D7D7D7"));
            vp_selectedImages.setCurrentItem(imagesList.size() - 1);
            ib_reject_picture.setVisibility(View.VISIBLE);
        }
        if (imagesList.size() <= 1) {
            tv_imageProgress.setText("[Image added " + imagesList.size() + "/1]");
            v_swipeRight.setVisibility(View.INVISIBLE);
            v_swipeLeft.setVisibility(View.INVISIBLE);
            return;
        }
        tv_imageProgress.setText("Slide to view other images\n[Images added " + imagesList.size() + "/1]");
        v_swipeRight.setVisibility(View.VISIBLE);
        v_swipeLeft.setVisibility(View.VISIBLE);
    }

    /**
     * Check if the GPS is on or not.
     */
    private void checkingLocation() {
        if (isGooglePlayServicesAvailable()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")) {
                Log.e("GPS Connection Found:", "true");
                if (mCurrentLocation == null) {
                    mProgressDialog.setMessage("Fetching present location...");
                    mProgressDialog.setCancelable(true);
                    createLocationRequest();
                    return;
                }
                return;
            }
            Log.e(this.TAG, "NO LOCATION FOUND!");
            if (systemAlertDialog == null) {
                systemAlertDialog = Util.showSettingsAlert(getApplicationContext(), systemAlertDialog);
                return;
            } else if (!systemAlertDialog.isShowing()) {
                systemAlertDialog = Util.showSettingsAlert(getApplicationContext(), systemAlertDialog);
                return;
            } else {
                return;
            }
        }
        GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), this, 10).show();
    }

    private boolean isGooglePlayServicesAvailable() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0) {
            return true;
        }
        return false;
    }


    /**
     * Start fetching location.
     */
    protected void createLocationRequest() {
       /* if (Util.isInternetAvailable(this.mContext)) {
            System.out.println("Fused Location called!");
            this.mProgressDialog.setMessage("Fetching present location...");
            this.mProgressDialog.setCancelable(true);
            showDialog();
            //showProgressDialog();
            this.mLocationRequest = LocationRequest.create();
            this.mLocationRequest.setPriority(100);
            this.mLocationRequest.setNumUpdates(1);
            this.mLocationRequest.setInterval(5000);
            this.mLocationRequest.setFastestInterval(1000);
            this.fusedLocationProviderApi = LocationServices.FusedLocationApi;
            if (this.mGoogleApiClient == null) {
                this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                this.mGoogleApiClient.connect();
                return;
            }
            return;
        }*/
        /*gpsLocationListener = new GPSLocationListener();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);*/


        FetchCordinates mtask = new FetchCordinates(mContext);
        mtask.mListener = this;

        isGPSLocation = true;
        showDialog();
        mtask.execute(new String[0]);
        this.mtask = mtask;
        this.mtask.isFetchCancelled = false;
    }

    //TODO
    public void getLocation(Location mLocation, boolean isSuccess) {

        hideDialog();
        if (isSuccess) {
            mCurrentLocation = mLocation;
            Log.e("Latitude", "" + mLocation.getLatitude());
            Log.e("Longitude", "" + mLocation.getLongitude());

            /*if (Util.isInternetAvailable(mContext)) {

                isRathNumberService = true;
                volleyTaskManager.doGetFetchRathNumbers();

            }*/
        } else if (!isSuccess && mtask.isFetchCancelled) {
            // Cancelled by user.
            //clearForm();
            finish();
            mCurrentLocation = null;
        } else {
            Util.showCallBackMessageWithOkCancelGPS(mContext,
                    "Location not found! Please do not stay indoor. Tap OK to try again or CANCEL to exit.",
                    new AlertDialogCallBack() {

                        @Override
                        public void onSubmit() {
                            Intent intent = new Intent(mContext, RathFormActivity.class);
                            intent.putExtra("thread", thread);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancel() {
                            finish();

                        }
                    });
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult.toString());
        Toast.makeText(this, "Connection failed: " + connectionResult.toString(), Toast.LENGTH_LONG).show();
        hideDialog();
    }

    public void onConnected(Bundle arg0) {
        Log.e(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest,
                (LocationListener) this);
        Log.e(TAG, "Location update started ..............: ");
    }

    public void onConnectionSuspended(int arg0) {
        hideDialog();
    }

    public void onLocationChanged(Location location) {
        Log.e(TAG, "Firing onLocationChanged..............................................");
        Log.e(TAG, "Lat: " + location.getLatitude());
        Log.e(TAG, "Lon: " + location.getLongitude());
        Log.e(TAG, "Accuray: " + location.getAccuracy());
        hideDialog();
        mCurrentLocation = location;
        Log.e("onLocationChanged", "Geo Address: " + geoAddress);
    }

    protected void onResume() {
        super.onResume();
        System.out.println("------>> ON RESUME CALLED  >>---------------");
        if (!StaticVariables.isHelp && mCurrentLocation == null) {
            checkingLocation();
        }
    }

    /**
     * onclick listener for submit button
     */
    public void onSubmitClick(View v) {
        String rathNumber = dropDownActivity_rathNumber.getText().toString().trim();
        String comments = et_comments.getText().toString().trim();
        String pictureCategory = dropDownActivity_pictureCategory.getText().toString().trim();

        /*if (ll_dynamicField.getVisibility() == View.VISIBLE) {
            rathNumber = dropDownActivity_rathNumber.getText().toString().trim();
            comments = et_comments.getText().toString().trim();
        }*/
        if (imagesList.size() < 1) {
            Util.showMessageWithOk(RathFormActivity.this, "Please take a picture first.");
        } else if (rathNumber.length() < 1) {
            Util.showMessageWithOk(RathFormActivity.this, "Please enter the rath number.");
        } else if (pictureCategory.isEmpty()) {
            Util.showMessageWithOk(RathFormActivity.this, "Please select a picture category.");
        } else if (Util.isInternetAvailable(mContext)) {

            HashMap<String, String> formDataMap = new HashMap();
            formDataMap.put(DBConstants.MU_ID, Util.fetchUserClass(mContext).getUserId());
            formDataMap.put(com.connectapp.user.db.DBConstants.THREAD_ID, thread.getThreadID());
            formDataMap.put(DBConstants.IMAGE, ((ImageClass) imagesList.get(0)).getBase64value());
            if (mCurrentLocation.getLatitude()!= 0.0 && mCurrentLocation.getLongitude()!=0.0) {
                formDataMap.put("lat", "" + mCurrentLocation.getLatitude());
                formDataMap.put("long", "" + mCurrentLocation.getLongitude());
            } else {
                networklat=MainActivity.getLat();
                netwoklong=MainActivity.getlongi();
                formDataMap.put("lat",Double.toString(networklat));
                formDataMap.put("long",Double.toString(netwoklong));
            }
            formDataMap.put(DBConstants.DATE, Util.getDate());
            formDataMap.put(DBConstants.TIME, Util.getTime());
            formDataMap.put("sCode", "");
            if (thread.getThreadID().equalsIgnoreCase("7")) {
                formDataMap.put("rathcat", dropDownActivity_pictureCategory.getText().toString().trim());
                formDataMap.put("rCode", rathNumber);
                formDataMap.put(DBConstants.PICTURE_CATEGORY, comments);
            } else {
                formDataMap.put("keyWords", "12,13,14");
            }
            // Create content value for History
            historyCV = new ContentValues();
            historyCV.put(DBConstants.MU_ID, Util.fetchUserClass(mContext).getUserId());
            historyCV.put(DBConstants.THREAD_ID, thread.getThreadID());
            historyCV.put(DBConstants.IMAGE, ((ImageClass) imagesList.get(0)).getBase64value());
            if (mCurrentLocation.getLatitude()!= 0.0 && mCurrentLocation.getLongitude()!=0.0) {
                historyCV.put(DBConstants.LATITUDE, mCurrentLocation.getLatitude());
                historyCV.put(DBConstants.LONGITUDE, mCurrentLocation.getLongitude());
            } else {
                networklat=MainActivity.getLat();
                netwoklong=MainActivity.getlongi();
                historyCV.put(DBConstants.LATITUDE,Double.toString(networklat));
                historyCV.put(DBConstants.LONGITUDE, Double.toString(netwoklong));
            }
            historyCV.put(DBConstants.PICTURE_CATEGORY, dropDownActivity_pictureCategory.getText().toString().trim());
            historyCV.put(DBConstants.KEYWORDS, "12,13,14");
            historyCV.put(DBConstants.ADDRESS, geoAddress);
            historyCV.put(DBConstants.DATE, Util.getDate());
            historyCV.put(DBConstants.TIME, Util.getTime());
            historyCV.put(DBConstants.SCHOOL_CODE, StaticConstants.SCHOOL_CODE_DEFAULT);
            historyCV.put(DBConstants.RATH_NUMBER, rathNumber);
            historyCV.put(DBConstants.VILLAGE_NAME, comments);
            historyCV.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + "" + "\",\"village\":\"" + comments + "\"}");
            isSubmitService = true;
            volleyTaskManager.urlusersubmission= Consts.USER_SUBMISSION_URL;
            volleyTaskManager.doPostFormData(formDataMap, true);
        } else {

            // Save the filled data in the OFFLINE_TABLE - Sync Adapter
            Util.showCallBackMessageWithOkCallback_success(mContext,
                    "The data has been saved. It will be uploaded whenever Internet is available.", new SavedOffline(),
                    "No Internet");
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.MU_ID, Util.fetchUserClass(mContext).getUserId());
            cv.put(DBConstants.THREAD_ID, thread.getThreadID());
            cv.put(DBConstants.IMAGE, ((ImageClass) imagesList.get(0)).getBase64value());
            if (mCurrentLocation.getLatitude()!= 0.0 && mCurrentLocation.getLongitude()!=0.0) {
                cv.put(DBConstants.LATITUDE, mCurrentLocation.getLatitude());
                cv.put(DBConstants.LONGITUDE, mCurrentLocation.getLongitude());
            } else {
                networklat=MainActivity.getLat();
                netwoklong=MainActivity.getlongi();
                cv.put(DBConstants.LATITUDE, Double.toString(networklat));
                cv.put(DBConstants.LONGITUDE, Double.toString(netwoklong));
            }
            cv.put(DBConstants.PICTURE_CATEGORY, this.dropDownActivity_pictureCategory.getText().toString().trim());
            cv.put(DBConstants.KEYWORDS, "12,13,14");
            cv.put(DBConstants.ADDRESS, geoAddress);
            cv.put(DBConstants.DATE, Util.getDate());
            cv.put(DBConstants.TIME, Util.getTime());
            cv.put(DBConstants.SCHOOL_CODE, StaticConstants.SCHOOL_CODE_DEFAULT);
            cv.put(DBConstants.RATH_NUMBER, rathNumber);
            cv.put(DBConstants.VILLAGE_NAME, comments);
            cv.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + "" + "\",\"village\":\"" + comments + "\"}");
            getContentResolver().insert(Constant.CONTENT_URI, cv);
            clearForm();
        }
    }

    public void onSuccess(JSONObject resultJsonObject) {

        Log.e("isRathNumberService", "isRathNumberService: " + isRathNumberService);
        if (isRathNumberService) {
            isRathNumberService = false;
            Log.e(this.TAG, "" + resultJsonObject);
            if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
                JSONArray dataArray = resultJsonObject.optJSONArray("data");
                PrefUtils prefUtils = new PrefUtils();
                ArrayList<RathClass> rathClassArray = new ArrayList<RathClass>();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject jsonObject = dataArray.optJSONObject(i);
                    RathClass rathClass = new RathClass();
                    rathClass.setBhaag(jsonObject.optString("bhag").trim());
                    rathClass.setRathCode(jsonObject.optString("rathNo").trim());
                    rathClass.setRathName(jsonObject.optString("bhag").trim() + " " + jsonObject.optString("rathNo").trim());
                    rathClassArray.add(rathClass);
                }
                prefUtils.setRathClasses(rathClassArray);
                Util.savePrefUtilClass(mContext, prefUtils);
            }

        } else if (isSubmitService) {
            isSubmitService = false;
            Log.e(this.TAG, "" + resultJsonObject);
            if (resultJsonObject.toString() == null || resultJsonObject.toString().trim().isEmpty()) {
                Toast.makeText(this.mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                String result = "";
                String message = "";
                result = resultJsonObject.optString("code");
                message = resultJsonObject.optString("msg");
                Log.e(this.TAG, result);
                if (result.equalsIgnoreCase("200")) {
                    new HistoryDB().saveHistoryData(mContext, historyCV);
                    Util.showCallBackMessageWithOkCallback(mContext, "Submision Complete", new SubmissionComplete());
                    return;
                }
                Toast.makeText(mContext, " " + message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onError(VolleyError error) {
        if (isSubmitService) {
            isSubmitService = false;
            if (error instanceof TimeoutError || error instanceof ServerError || error instanceof NetworkError)
                saveOfflineData();
        } else if (isRathNumberService) {
            // TODO do respective changes for Rath Number Service- if required to.
            // No requirement as of now.
        }
    }


    /**
     * Clears all the fields that have been filled.
     */
    private void clearForm() {
        imageCount = 0;
        dropDownActivity_pictureCategory.setText("");
        tv_imageProgress.setText("[Image added 0/1]");
        imagesList.clear();
        if (thread.getThreadID().equalsIgnoreCase("7")) {
            et_comments.setText("");
        }
        imageUpdateOnView();
        onPause();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onDestroy() {
        super.onDestroy();
        System.out.println("On destroy");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        StaticVariables.isHelp = false;
    }

    public void getLocationAGPS(Location location) {
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dropDownActivity_rathNumber:
                startActivityForResult(new Intent(mContext, RathNumberActivity.class), RATH_NUMBER_REQUEST);
                break;
            case R.id.dropDownActivity_pictureCategory:
                startActivityForResult(new Intent(this.mContext, RathPictureCategoryActivity.class), PICTURE_CATEGORY_REQUEST);
                break;
            default:
                break;
        }
    }

    public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {

        //System.out.println("------>> onActivityResult CALLED  >>---------------");
        //If the request was for picture category and the result was ok. /* Scope for future enhancement*/
        if (requestCode == PICTURE_CATEGORY_REQUEST && resultCode == Activity.RESULT_OK) {
            dropDownActivity_pictureCategory.setText(data.getStringExtra(PictureCategoryActivity.PICTURE_CATEGORY));
            et_comments.requestFocus();
        }

        //If the request was for rath number and the result was ok.
        else if (requestCode == RATH_NUMBER_REQUEST && resultCode == Activity.RESULT_OK) {
            dropDownActivity_rathNumber.setText(data.getStringExtra(RathNumberActivity.RATH));
            dropDownActivity_pictureCategory.requestFocus();
        } else {
            // This means the request was to click picture
            hideProgressDialog();
            if (resultCode == Activity.RESULT_OK) {
                /*Uri selectedUri = null;
                switch (requestCode) {
				case CAMERA_PIC_REQUEST 1337:
					selectedUri = mCapturedImageURI;
					break;
				case PICTURE_GALLERY_REQUEST 2572:
					selectedUri = data.getData();
					break;
				}*/
                /*String[] filePathColumn = new String[] { "_data" };
                Cursor cursor = getContentResolver().query(selectedUri, filePathColumn, null, null, null);
				cursor.moveToFirst();
				String picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
				cursor.close();*/

                String picturePath = mCapturedImageURI.getPath();
                Log.e(this.TAG, "Picture path: " + picturePath);
                processImagePath(picturePath);
            } else if (!(requestCode == 11 || requestCode == 12)) {
                Log.e("DialogChoosePicture", "Warning: activity result not ok");
                Toast.makeText(this.mContext, "No image selected", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e("onSaveInstanceState", "onSaveInstanceState");
        Log.e("onSaveInstanceState", "Captured Uri" + mCapturedImageURI);
        System.out.println("------------------------------------\n");
        outState.putString("URI", "" + mCapturedImageURI);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("------------------------------------\n");
        Log.e("onRestoreInstanceState", "onRestoreInstanceState");
        Log.e("onRestoreInstanceState", "Captured Uri " + mCapturedImageURI);
        System.out.println("------------------------------------\n");

        System.out.println("Restored URI " + savedInstanceState.getString("URI"));

    }


    private void showDialog() {
        if (!mDialog.isShowing()) {
            mDialog.show();

            new CountDownTimer(120000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //this will be done every 1000 milliseconds ( 1 seconds )
                    int progress = (int) ((120000 - millisUntilFinished) / 1000);
                    progressBarFetchLoc.setProgress(progress);
                }

                @Override
                public void onFinish() {
                    hideDialog();
                }

            }.start();

        }
    }

    private void hideDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }

    private void deleteSelectedImageFile(String fileName) {
        try {
            Log.e("FileName", "Filename: " + fileName);
            /*File dir = new File(Environment.getExternalStorageDirectory() + "/ConnectAppImages");
            File[] files = dir.listFiles();
            int numberOfFiles = files.length;
            Log.e("Length", "Number of Files: " + numberOfFiles);*/
            File file = new File(Environment.getExternalStorageDirectory() + "/ConnectAppImages/" + fileName);
            if (file.exists()) {
                Log.e("File", "File exists.");
                file.delete();
            } else {
                Log.e("File", "File does not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveOfflineData() {
        String rathNumber = dropDownActivity_rathNumber.getText().toString().trim();
        String comments = et_comments.getText().toString().trim();
        String pictureCategory = dropDownActivity_pictureCategory.getText().toString().trim();
        // Save the filled data in the OFFLINE_TABLE - Sync Adapter
        Util.showCallBackMessageWithOkCallback_success(mContext,
                "The data has been saved. It will be uploaded whenever Internet is available.", new SavedOffline(),
                "No Internet");
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.MU_ID, Util.fetchUserClass(mContext).getUserId());
        cv.put(DBConstants.THREAD_ID, thread.getThreadID());
        cv.put(DBConstants.IMAGE, ((ImageClass) imagesList.get(0)).getBase64value());
        if (mCurrentLocation.getLatitude()!= 0.0 && mCurrentLocation.getLongitude()!=0.0) {
            cv.put(DBConstants.LATITUDE, mCurrentLocation.getLatitude());
            cv.put(DBConstants.LONGITUDE, mCurrentLocation.getLongitude());
        } else {
            networklat=MainActivity.getLat();
            netwoklong=MainActivity.getlongi();
            cv.put(DBConstants.LATITUDE, Double.toString(networklat));
            cv.put(DBConstants.LONGITUDE, Double.toString(netwoklong));
        }
        cv.put(DBConstants.PICTURE_CATEGORY, this.dropDownActivity_pictureCategory.getText().toString().trim());
        cv.put(DBConstants.KEYWORDS, "12,13,14");
        cv.put(DBConstants.ADDRESS, geoAddress);
        cv.put(DBConstants.DATE, Util.getDate());
        cv.put(DBConstants.TIME, Util.getTime());
        cv.put(DBConstants.SCHOOL_CODE, StaticConstants.SCHOOL_CODE_DEFAULT);
        cv.put(DBConstants.RATH_NUMBER, rathNumber);
        cv.put(DBConstants.VILLAGE_NAME, comments);
        cv.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + "" + "\",\"village\":\"" + comments + "\"}");
        getContentResolver().insert(Constant.CONTENT_URI, cv);
        clearForm();
    }

}

