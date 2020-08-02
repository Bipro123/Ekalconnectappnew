package com.connectapp.user.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.connectapp.user.data.Thread;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.location.FetchCordinates;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class  IvanFormActivity extends AppCompatActivity implements LocationListener, ConnectionCallbacks,
        OnConnectionFailedListener, ServerResponseCallback, FusedLocationCallback, LocationCallback, OnClickListener {
    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int PICTURE_GALLERY_REQUEST = 2572;
    private static Uri mCapturedImageURI;
    private String TAG = getClass().getSimpleName();
    private EditText et_anchal;
    private EditText et_sanch;
    private EditText et_sankul;
    private EditText et_upsanch;
    private EditText et_village;
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
    private TextView tvCountryCode;
    private TextView tv_imageProgress;
    private TextView tv_stateCode;
    private View v_swipeLeft;
    private View v_swipeRight;
    private VolleyTaskManager volleyTaskManager;
    private ViewPager vp_selectedImages;
    private TextView et_date_of_determination;
    private FetchCordinates mtask;
    private int day,year,month;
    private Calendar mcalendar;
    private Spinner spin_gender;
    private Spinner spin_glasses;
    private boolean isGPSLocation = false;
    private ImageButton ib_reject_picture;
    private String imageFileName = "";
    public double networklat;
    public double netwoklong;
    private EditText et_villagename;
    private EditText et_patientname ;
    private EditText et_headfamily ;
    private EditText et_age ;
    private EditText et_history_taking ;
    private EditText et_history_complaints ;
    private EditText et_vision_VR ;
    private EditText et_vision_VRWO ;
    private EditText et_vision_VL ;
    private EditText et_vision_VLWO;
    private EditText et_BP_systolic ;
    private EditText et_BP_Diastolic ;
    private EditText et_BMI_Height ;
    private EditText et_BMI_Weight ;
    private EditText et_anchal_name;

    private EditText et_BMI_Obesity ;
    private EditText et_Sugar_Fasting ;
    private EditText et_Sugar_PP ;
    private EditText et_Sugar_Random ;
    private EditText et_past_history;
    private EditText et_present_history;
    private Spinner et_medicine ;
    private EditText et_amount ;
    DatePickerDialog datePickerDialog;
    // New//
    private EditText et_registerno ;
    private EditText et_adharnum ;
    private EditText et_mobilenum ;
    private EditText et_occupation ;
    private EditText et_illcause ;
    private EditText et_illduration ;

    private Spinner et_diabetic ;

    private Spinner et_cardiac ;
    private Spinner et_oneye ;
    private Spinner et_hypernation ;
    private Spinner et_asthama ;
    private Spinner et_eyedrop;
    private Spinner spin_state;
    private EditText et_others;

    private EditText et_rsph ;
    private EditText et_rcycl ;
    private EditText et_reyeaxis ;
    private EditText et_lsph ;
    private EditText et_lcycl;
    private EditText et_leyeaxis;
    private EditText et_near ;
    private EditText et_instruct ;
    Double weight ;
    Double height;
    int cifras;
//    String [] ft;
//    String [] inch;


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
            IvanFormActivity.this.finish();
            IvanFormActivity.this.mCurrentLocation = null;
        }

        public void onCancel() {
        }
    }

    /* AlertDialog Callback Submission Complete */
    class SubmissionComplete implements AlertDialogCallBack {
        SubmissionComplete() {
        }

        public void onSubmit() {
            IvanFormActivity.this.clearForm();
            IvanFormActivity.this.finish();
            IvanFormActivity.this.mCurrentLocation = null;
        }

        public void onCancel() {
        }
    }

    // TODO -- SHOW HELP ICON
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }
*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivan_form);
        mContext = IvanFormActivity.this;
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
        getSupportActionBar().setTitle("Eye Van");
        initView();

    }

    private void initView() {
        ll_dynamicField = (LinearLayout) findViewById(R.id.ll_dynamicField);
        String[] gender = { "Select Gender","Male", "Female"};
        String[] glasses = { "Option","Yes", "No"};
        if (thread.getThreadID().equalsIgnoreCase("9")) {
            ll_dynamicField.setVisibility(View.VISIBLE);
            tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
            tv_stateCode = (TextView) findViewById(R.id.tv_stateCode);
            et_anchal = (EditText) findViewById(R.id.et_anchal);
            et_sankul = (EditText) findViewById(R.id.et_sankul);
            et_sanch = (EditText) findViewById(R.id.et_sanch);
            et_upsanch = (EditText) findViewById(R.id.et_upsanch);
            et_village = (EditText) findViewById(R.id.et_village);

            et_villagename = (EditText) findViewById(R.id.et_villagename);
            et_patientname = (EditText) findViewById(R.id.et_patientname);
            et_headfamily = (EditText) findViewById(R.id.et_headfamily);
            et_age = (EditText) findViewById(R.id.et_age);
//            et_history_taking = (EditText) findViewById(R.id.et_history_taking);
            et_history_complaints = (EditText) findViewById(R.id.et_history_complaints);
            et_vision_VR = (EditText) findViewById(R.id.et_vision_VR);
            et_vision_VRWO=(EditText) findViewById(R.id.et_vision_VRWO) ;
            et_vision_VL = (EditText) findViewById(R.id.et_vision_VL);
            et_vision_VLWO=(EditText) findViewById(R.id.et_vision_VLWO) ;
//            et_history_taking = (EditText) findViewById(R.id.et_history_taking);
            et_history_complaints = (EditText) findViewById(R.id.et_history_complaints);
            et_vision_VR = (EditText) findViewById(R.id.et_vision_VR);
            et_past_history=(EditText) findViewById(R.id.et_past_history);
            et_present_history=(EditText) findViewById(R.id.et_present_history);
            et_BP_systolic = (EditText) findViewById(R.id.et_BP_systolic);
            et_BP_Diastolic = (EditText) findViewById(R.id.et_BP_Diastolic);
            et_BMI_Height = (EditText) findViewById(R.id.et_BMI_Height);
            et_BMI_Weight = (EditText) findViewById(R.id.et_BMI_Weight);
            et_anchal_name = (EditText) findViewById(R.id.et_anchalname);
            et_BMI_Obesity = (EditText) findViewById(R.id.et_BMI_Obesity);
            et_BMI_Obesity.setEnabled(false);
            et_Sugar_Fasting = (EditText) findViewById(R.id.et_Sugar_Fasting);
            et_Sugar_PP = (EditText) findViewById(R.id.et_Sugar_PP);
            et_Sugar_Random = (EditText) findViewById(R.id.et_Sugar_Random);

            et_medicine = (Spinner) findViewById(R.id.spin_medtabsyrup);
            ArrayAdapter<CharSequence> adapter_medicine = ArrayAdapter.createFromResource(this,
                    R.array.med_tab_syrup, android.R.layout.simple_spinner_item);
            adapter_medicine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_medicine.setAdapter(adapter_medicine);
            et_eyedrop = (Spinner) findViewById(R.id.spin_eyedrop);
            ArrayAdapter<CharSequence> adapter_eye = ArrayAdapter.createFromResource(this,
                    R.array.eye_drop_array, android.R.layout.simple_spinner_item);
            adapter_medicine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_eyedrop.setAdapter(adapter_eye);

            et_amount = (EditText) findViewById(R.id.et_amount);


            et_registerno = (EditText) findViewById(R.id.et_registerno);
            et_adharnum = (EditText) findViewById(R.id.et_adharnum);
            et_mobilenum = (EditText) findViewById(R.id.et_mobilenum);

            et_occupation = (EditText) findViewById(R.id.et_occupation);
            et_illcause = (EditText) findViewById(R.id.et_illcause);
            et_illduration = (EditText) findViewById(R.id.et_illduration);
            et_diabetic = (Spinner) findViewById(R.id.spin_diabetic);
            ArrayAdapter<CharSequence> adapter_diabetic = ArrayAdapter.createFromResource(this,
                    R.array.option_array, android.R.layout.simple_spinner_item);
            adapter_diabetic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_diabetic.setAdapter(adapter_diabetic);
            et_cardiac=(Spinner) findViewById(R.id.spin_cardiac);
            ArrayAdapter<CharSequence> adapter_cardiac = ArrayAdapter.createFromResource(this,
                    R.array.option_array, android.R.layout.simple_spinner_item);
            adapter_cardiac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_cardiac.setAdapter(adapter_cardiac);
            et_oneye=(Spinner) findViewById(R.id.spin_oneeyed);
            ArrayAdapter<CharSequence> adapter_oneeyed = ArrayAdapter.createFromResource(this,
                    R.array.option_array, android.R.layout.simple_spinner_item);
            adapter_oneeyed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_oneye.setAdapter(adapter_oneeyed);
            et_hypernation = (Spinner) findViewById(R.id.spin_hypertension);
            ArrayAdapter<CharSequence> adapter_hypertension = ArrayAdapter.createFromResource(this,
                    R.array.option_array, android.R.layout.simple_spinner_item);
            adapter_hypertension.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_hypernation.setAdapter(adapter_hypertension);
            et_asthama = (Spinner) findViewById(R.id.spin_asthamatic);
            ArrayAdapter<CharSequence> adapter_asthama = ArrayAdapter.createFromResource(this,
                    R.array.option_array, android.R.layout.simple_spinner_item);
            adapter_asthama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            et_asthama.setAdapter(adapter_hypertension);
            et_others = (EditText) findViewById(R.id.et_others);
//            et_rsph = (EditText) findViewById(R.id.et_rsph);
//            et_rcycl = (EditText) findViewById(R.id.et_rcycl);
//            et_reyeaxis = (EditText) findViewById(R.id.et_reyeaxis);
//            et_lsph = (EditText) findViewById(R.id.et_lsph);
//            et_lcycl = (EditText) findViewById(R.id.et_lcycl);
//            et_leyeaxis = (EditText) findViewById(R.id.et_leyeaxis);
//
//            et_near = (EditText) findViewById(R.id.et_near);
//            et_instruct = (EditText) findViewById(R.id.et_instruct);





            spin_gender=(Spinner) findViewById(R.id.spin_gender);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.Gender_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_gender.setAdapter(adapter);
            spin_state=(Spinner) findViewById(R.id.spin_state);
            ArrayAdapter<CharSequence> adapter_state = ArrayAdapter.createFromResource(this,
                    R.array.state_spinner_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_state.setAdapter(adapter_state);
            spin_glasses=(Spinner) findViewById(R.id.spin_glasses);
            ArrayAdapter<CharSequence> adapter_glasses = ArrayAdapter.createFromResource(this,
                    R.array.option_array, android.R.layout.simple_spinner_item);
            adapter_glasses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin_glasses.setAdapter(adapter_glasses);
            et_date_of_determination=(TextView)findViewById(R.id.et_date_of_determination);
            et_date_of_determination.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    final Calendar c;
                    c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    //Dialog DatePicker
                    datePickerDialog = new DatePickerDialog(IvanFormActivity.this, new
                            DatePickerDialog.OnDateSetListener(){
                                @Override
                                public void onDateSet(DatePicker view,int year, int monthOfYear, int dayOfMonth){
                                    et_date_of_determination.setText(dayOfMonth + "/" +(monthOfYear + 1) + "/" + year);
                                }
                            },mYear,mMonth,mDay);
                    datePickerDialog.show();
                }

            });
            tv_stateCode.setFocusable(true);
            tv_stateCode.requestFocus();
            tv_stateCode.setCursorVisible(true);
            tv_stateCode.setOnClickListener(this);
            setTextWatcher();
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



    private void setTextWatcher() {
        et_anchal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e(TAG, "andchal on text changed count " + s.length());
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







        et_BMI_Weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here

                // yourEditText...

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ft=Double.valueOf(et_BMI_Height.getText().toString());
//                inch=ft[1].split("inch");
//                Toast.makeText(mContext, ft[0], Toast.LENGTH_SHORT).show();
//                Toast.makeText(mContext, inch[0], Toast.LENGTH_SHORT).show();
                try {

                    weight = Double.valueOf(et_BMI_Weight.getText().toString());
                    height = (Double.valueOf(et_BMI_Height.getText().toString()))/100;
//                    Toast.makeText(mContext, weight.toString(), Toast.LENGTH_SHORT).show();
                    double obesity = weight / (height * height);
                    cifras = (int) Math.pow(10, 1);
                    et_BMI_Obesity.setText(Double.toString(Math.ceil(obesity*cifras)/cifras));
                } catch (NumberFormatException e) {
                    et_BMI_Obesity.setText("");
                }
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

    @SuppressLint({"InflateParams"})
    public void onPictureClick(View v) {
        if (imagesList.size() < 1) {
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(true);
            showProgressDialog();
            cameraSelectedPic();
            return;
        }
        Util.showMessageWithOk(IvanFormActivity.this, "Maximum number of images has already been selected!");
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

    protected void populatingSelectedPic() {
        Log.e(this.TAG, "selected from gallery");
        Intent albumIntent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
        albumIntent.setType("image/*");
        startActivityForResult(albumIntent, PICTURE_GALLERY_REQUEST);
    }

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

        File image = new File(imagesFolder, "SCHOOL_" + timeStamp + ".png");
        imageFileName = "SCHOOL_" + timeStamp + ".png";
        Uri uriSavedImage = Uri.fromFile(image);
        mCapturedImageURI = uriSavedImage;
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAMERA_PIC_REQUEST);
    }

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

    private void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            FileWriter writer = new FileWriter(new File(root, sFileName));
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    protected void createLocationRequest() {
        /*
        // For testing INDOORS
        if (Util.isInternetAvailable(this.mContext)) {
            System.out.println("Fused Location called!");
            this.mProgressDialog.setMessage("Fetching present location...");
            this.mProgressDialog.setCancelable(true);
            //showProgressDialog();
             showDialog();
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
            Log.d("Latitude", "" + mLocation.getLatitude());
            Log.d("Longitude", "" + mLocation.getLongitude());
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
                            Intent intent = new Intent(mContext, IvanFormActivity.class);
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
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
        Toast.makeText(this, "Connection failed: " + connectionResult.toString(), Toast.LENGTH_LONG).show();
        hideDialog();
    }

    public void onConnected(Bundle arg0) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        this.fusedLocationProviderApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest,
                (LocationListener) this);
        Log.d(this.TAG, "Location update started ..............: ");
    }

    public void onConnectionSuspended(int arg0) {
        hideDialog();
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        Log.d(TAG, "Lat: " + location.getLatitude());
        Log.d(TAG, "Lon: " + location.getLongitude());
        Log.d(TAG, "Accuray: " + location.getAccuracy());
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

    public void onSubmitClick(View v) {

        String countryCode = "";
        String villageName = "";
        String stateCode = tv_stateCode.getText().toString().trim();
        String anchal = et_anchal.getText().toString().trim();
        String sankul = et_sankul.getText().toString().trim();
        String sanch = et_sanch.getText().toString().trim();
        String upsanch = et_upsanch.getText().toString().trim();
        String village = et_village.getText().toString().trim();
        String completeSchoolCode = "";
        String villagename2 = et_villagename.getText().toString().trim();
        String anchalname = et_anchal_name.getText().toString().trim();
        String patientname= et_patientname.getText().toString().trim();
        String headoffamily = et_headfamily.getText().toString().trim();
        String age = et_age.getText().toString().trim();
//        String history_taking = et_history_taking.getText().toString().trim() ;
        String vision_VR = et_vision_VR.getText().toString().trim();
        String vision_VRWO=et_vision_VRWO.getText().toString().trim();
        String gender=spin_gender.getSelectedItem().toString();
        String state=spin_state.getSelectedItem().toString();
        String glasses=spin_glasses.getSelectedItem().toString();
        String date_examination=et_date_of_determination.getText().toString();
        String vision_VL = et_vision_VL.getText().toString().trim();
        String vision_VLWO=et_vision_VLWO.getText().toString().trim();
        String history_complaints = et_history_complaints.getText().toString().trim();
        String past_history = et_past_history.getText().toString().trim();
        String present_history = et_present_history.getText().toString().trim();
        String BP_systolic = et_BP_systolic.getText().toString().trim();
        String BP_Diastolic = et_BP_Diastolic.getText().toString().trim();
        String BMI_Height = et_BMI_Height.getText().toString().trim();
        String BMI_Weight = et_BMI_Weight.getText().toString().trim();
        String BMI_Obesity = et_BMI_Obesity.getText().toString().trim();
        String Sugar_Fasting = et_Sugar_Fasting.getText().toString().trim();
        String Sugar_PP = et_Sugar_PP.getText().toString().trim();
        String Sugar_Random = et_Sugar_Random.getText().toString().trim();
        String medicine = et_medicine.getSelectedItem().toString();
        String eyedrop = et_eyedrop.getSelectedItem().toString();
        String amount = et_amount.getText().toString().trim();

        String registerno=et_registerno.getText().toString().trim();
        String adharnum=et_adharnum.getText().toString().trim();
        String mobilenum=et_mobilenum.getText().toString().trim();

        String occupation=et_occupation.getText().toString().trim();
        String illcause=et_illcause.getText().toString().trim();
        String illduration=et_illduration.getText().toString().trim();
        String diabetic=et_diabetic.getSelectedItem().toString();
        String cardiac=et_cardiac.getSelectedItem().toString();
        String oneye=et_oneye.getSelectedItem().toString();
        String hypernation=et_hypernation.getSelectedItem().toString();
        String asthama=et_asthama.getSelectedItem().toString();
        String others=et_others.getText().toString().trim();
//        String rsph=et_rsph.getText().toString().trim();
//        String rcycl=et_rcycl.getText().toString().trim();
//        String reyeaxis=et_reyeaxis.getText().toString().trim();
//        String lsph=et_lsph.getText().toString().trim();
//        String lcycl=et_lcycl.getText().toString().trim();
//        String leyeaxis=et_leyeaxis.getText().toString().trim();
//
//        String near=et_near.getText().toString().trim();
//        String instruct=et_instruct.getText().toString().trim();
        if (ll_dynamicField.getVisibility() == View.VISIBLE) {
            countryCode = tvCountryCode.getText().toString().trim();
        }
        if (imagesList.size() < 1) {
            Util.showMessageWithOk(IvanFormActivity.this, "Please take a picture first.");
        }
        //else if (stateCode.isEmpty() || stateCode.equalsIgnoreCase("-")) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the State Code.");
//        } else if (anchal.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the Anchal.");
//        } else if (sankul.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the Sankul.");
//        } else if (sanch.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the Sanch.");
//        } else if (upsanch.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the Up-Sanch.");
//        } else if (village.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the Village.");
//        } else if (anchal.length() < 2) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the correct Anchal.");

//       else if (village.length() < 2) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Please enter the correct Village.");
//        else if (anchalname.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Anchal Name is empty");
//        }
//         else if (villagename2.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Village Name is empty");
////        }else if (patientname.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Patient Name is empty");
//        }else if (headoffamily.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Head of Family is empty");
//        }else if (age.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Age is empty");
//        }else if (past_history.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Past history is empty");
//        }else if (vision_VL.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Vision VL is empty");
//        }else if (vision_VR.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Vision VR is empty");
//        }
//        else if (present_history.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Present History is empty");
//        }else if (BP_systolic.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "BP Systolic is empty");
//        }else if (BMI_Height.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "BMI height is empty");
//        }else if (BMI_Weight.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "BMI Weight is empty");
//        }else if (BMI_Obesity.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "BMI Obesity is Empty");
//        }else if (Sugar_Fasting.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Sugar Fasting is Empty");
//        }else if (Sugar_PP.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Sugar PP is empty");
//        }else if (amount.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Amount Paid is empty");
//        }else if (vision_VLWO.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Amount Paid is empty");
//        }else if (vision_VRWO.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Amount Paid is empty");
//        }
//        else if (Sugar_Random.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Sugar Random is Empty Paid is empty");
//        }
//        else if (medicine.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Name of Medicine is empty");
//        }
//        else if (BP_Diastolic.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "BP Diastolic is empty");
//        }
//        else if (gender=="Please Select") {
//            Util.showMessageWithOk(IvanFormActivity.this, "Selected Gender Properly");
//        }
//        else if (date_examination.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Date of Examination is empty");
//        }
//        else if (glasses=="Please Select") {
//            Util.showMessageWithOk(IvanFormActivity.this, "Select Glasses Properly");
//        }
//        else if (registerno.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Registration No is empty");
//        }
//        else if (mobilenum.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Mobile Number is empty");
//        }
//        else if (occupation.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Occupation is empty");
//        }
//        else if (illcause.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Illness cause is empty");
//        }
//        else if (illduration.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Illness Duration is empty");
//        }else if (diabetic.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Diabetic is empty");
//        }
//        else if (cardiac.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Cardiac is empty");
//        }
//        else if (oneye.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "One eye is empty");
//        }
//        else if (hypernation.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Hypertention is empty");
//        }
//        else if (asthama.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Asthama is empty");
//        }
//        else if (others.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Others is empty");
//        }
//        else if (rsph.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "right sph is empty");
//        }
//        else if (rcycl.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "right cycl is empty");
//        }
//        else if (reyeaxis.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "right axis is empty");
//        }
//        else if (lsph.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "left sph is empty");
//        }
//        else if (lcycl.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "left cycl is empty");
//        }
//        else if (leyeaxis.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "left axis is empty");
//        }
//        else if (near.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Near is empty");
//        }
//        else if (instruct.isEmpty()) {
//            Util.showMessageWithOk(IvanFormActivity.this, "Instruction is empty");
//        }



        else if (Util.isInternetAvailable(this.mContext)) {
            completeSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal).append(sankul)
                    .append(sanch).append(upsanch).append(village).toString();
            HashMap<String, String> formDataMap = new HashMap();
            formDataMap.put(DBConstants.MU_ID, Util.fetchUserClass(this.mContext).getUserId());
            formDataMap.put(com.connectapp.user.db.DBConstants.THREAD_ID, this.thread.getThreadID());
            formDataMap.put(DBConstants.IMAGE, ((ImageClass) this.imagesList.get(0)).getBase64value());
            formDataMap.put(DBConstants.VILLAGE_NAME2, villagename2);
            formDataMap.put(DBConstants.PATIENT_NAME, patientname);
            formDataMap.put(DBConstants.STATE, state);
            formDataMap.put(DBConstants.ANCHAL_DATA, anchalname);
            formDataMap.put(DBConstants.HEAD_OF_FAMILY, headoffamily);
            formDataMap.put(DBConstants.AGE, age);
//            formDataMap.put(DBConstants.HISTORY_TAKING, history_taking);
            formDataMap.put(DBConstants.VISION_VR, vision_VR);
            formDataMap.put(DBConstants.VISION_VRWO, vision_VRWO);
            formDataMap.put(DBConstants.GENDER, gender);
            formDataMap.put(DBConstants.GLASSES, glasses);
            formDataMap.put(DBConstants.DATE_OF_EXAMINATION, date_examination);
            formDataMap.put(DBConstants.VISION_VL, vision_VL);
            formDataMap.put(DBConstants.VISION_VLWO, vision_VLWO);
            formDataMap.put(DBConstants.HISTORY_COMPLAINTS, history_complaints);
            formDataMap.put(DBConstants.PAST_HISTORY, past_history);
            formDataMap.put(DBConstants.PRESENT_HISTORY, present_history);
            formDataMap.put(DBConstants.BP_SYSTOLIC, BP_systolic);
            formDataMap.put(DBConstants.BP_DIASTOLIC, BP_Diastolic);
            formDataMap.put(DBConstants.BMI_HEIGHT, BMI_Height);
            formDataMap.put(DBConstants.BMI_WEIGHT, BMI_Weight);
            formDataMap.put(DBConstants.BMI_OBESITY, BMI_Obesity);
            formDataMap.put(DBConstants.SUGAR_FASTING, Sugar_Fasting);
            formDataMap.put(DBConstants.SUGAR_PP, Sugar_PP);
            formDataMap.put(DBConstants.SUGAR_RANDOM, Sugar_Random);
            formDataMap.put(DBConstants.MEDICINE, medicine);
            formDataMap.put(DBConstants.EYE_DROP, eyedrop);
            formDataMap.put(DBConstants.AMOUNT, amount);

            formDataMap.put(DBConstants.REGISTRATION_CODE, registerno);
            formDataMap.put(DBConstants.AADHAR_NUMBER, adharnum);
            formDataMap.put(DBConstants.PAITENT_MOBILE, mobilenum);
            formDataMap.put(DBConstants.OCCUPATION, occupation);
            formDataMap.put(DBConstants.ILLNESS_CAUSE,illcause);
            formDataMap.put(DBConstants.DURATION, illduration);
            formDataMap.put(DBConstants.DIABETIC, diabetic);
            formDataMap.put(DBConstants.CARDIAC, cardiac);
            formDataMap.put(DBConstants.ONE_EYED, oneye);
            formDataMap.put(DBConstants.HYPERNATION, hypernation);
            formDataMap.put(DBConstants.ASTHAMATIC, asthama);
            formDataMap.put(DBConstants.OTHERS, others);
//            formDataMap.put(DBConstants.SPH_RIGHT, rsph);
//            formDataMap.put(DBConstants.CYCL_RIGHT, rcycl);
//            formDataMap.put(DBConstants.AXIS_RIGHT,reyeaxis);
//            formDataMap.put(DBConstants.SPH_LEFT, lsph);
//            formDataMap.put(DBConstants.CYCL_LEFT, lcycl);
//            formDataMap.put(DBConstants.AXIS_LEFT, leyeaxis);
//            formDataMap.put(DBConstants.NEAR, near);
//            formDataMap.put(DBConstants.INSTRUCTION, instruct);


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
            if (this.thread.getThreadID().equalsIgnoreCase("9")) {
                formDataMap.put("sCode", completeSchoolCode);
                Toast.makeText(mContext, completeSchoolCode, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext,villageName, Toast.LENGTH_SHORT).show();
            } else {
                formDataMap.put("keyWords", "12,13,14");
            }
            historyCV = new ContentValues();
            historyCV.put(DBConstants.MU_ID, Util.fetchUserClass(mContext).getUserId());
            historyCV.put(DBConstants.THREAD_ID, thread.getThreadID());
            historyCV.put(DBConstants.IMAGE, ((ImageClass) imagesList.get(0)).getBase64value());
            if (mCurrentLocation != null) {
                historyCV.put(DBConstants.LATITUDE, mCurrentLocation.getLatitude());
                historyCV.put(DBConstants.LONGITUDE, mCurrentLocation.getLongitude());
            } else {
                historyCV.put(DBConstants.LATITUDE, "");
                historyCV.put(DBConstants.LONGITUDE, "");
            }
            historyCV.put(DBConstants.KEYWORDS, "12,13,14");
            historyCV.put(DBConstants.ADDRESS, this.geoAddress);
            historyCV.put(DBConstants.DATE, Util.getDate());
            historyCV.put(DBConstants.TIME, Util.getTime());
            historyCV.put(DBConstants.SCHOOL_CODE, completeSchoolCode);
            historyCV.put(DBConstants.RATH_NUMBER, StaticConstants.RATH_NUMBER_DEFAULT);
            historyCV.put(DBConstants.VILLAGE_NAME, villageName);
            historyCV.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + countryCode + "\",\"village\":\"" + villageName + "\"}");
            historyCV.put(DBConstants.VILLAGE_NAME2, villagename2);
            historyCV.put(DBConstants.PATIENT_NAME, patientname);
            historyCV.put(DBConstants.ANCHAL_DATA, anchalname);
            historyCV.put(DBConstants.HEAD_OF_FAMILY, headoffamily);
            historyCV.put(DBConstants.AGE, age);
//            historyCV.put(DBConstants.HISTORY_TAKING, history_taking);
            historyCV.put(DBConstants.VISION_VR, vision_VR);
            historyCV.put(DBConstants.VISION_VRWO, vision_VRWO);
            historyCV.put(DBConstants.GENDER, gender);
            historyCV.put(DBConstants.GLASSES, glasses);
            historyCV.put(DBConstants.DATE_OF_EXAMINATION, date_examination);
            historyCV.put(DBConstants.VISION_VL, vision_VL);
            historyCV.put(DBConstants.HISTORY_COMPLAINTS, history_complaints);
            historyCV.put(DBConstants.PAST_HISTORY, past_history);
            historyCV.put(DBConstants.PRESENT_HISTORY, present_history);
            historyCV.put(DBConstants.BP_SYSTOLIC, BP_systolic);
            historyCV.put(DBConstants.BP_DIASTOLIC, BP_Diastolic);
            historyCV.put(DBConstants.BMI_HEIGHT, BMI_Height);
            historyCV.put(DBConstants.BMI_WEIGHT, BMI_Weight);
            historyCV.put(DBConstants.BMI_OBESITY, BMI_Obesity);
            historyCV.put(DBConstants.SUGAR_FASTING, Sugar_Fasting);
            historyCV.put(DBConstants.SUGAR_PP, Sugar_PP);
            historyCV.put(DBConstants.SUGAR_RANDOM, Sugar_Random);
            historyCV.put(DBConstants.MEDICINE, medicine);
            historyCV.put(DBConstants.EYE_DROP, eyedrop);
            historyCV.put(DBConstants.AMOUNT, amount);
            // POST FORM DATA

            historyCV.put(DBConstants.REGISTRATION_CODE, registerno);
            historyCV.put(DBConstants.AADHAR_NUMBER, adharnum);
            historyCV.put(DBConstants.PAITENT_MOBILE, mobilenum);
            historyCV.put(DBConstants.OCCUPATION, occupation);
            historyCV.put(DBConstants.ILLNESS_CAUSE,illcause);
            historyCV.put(DBConstants.DURATION, illduration);
            historyCV.put(DBConstants.DIABETIC, diabetic);
            historyCV.put(DBConstants.CARDIAC, cardiac);
            historyCV.put(DBConstants.ONE_EYED, oneye);
            historyCV.put(DBConstants.HYPERNATION, hypernation);
            historyCV.put(DBConstants.ASTHAMATIC, asthama);
//            historyCV.put(DBConstants.SPH_RIGHT, rsph);
//            historyCV.put(DBConstants.CYCL_RIGHT, rcycl);
//            historyCV.put(DBConstants.AXIS_RIGHT,reyeaxis);
            historyCV.put(DBConstants.OTHERS, others);
//            historyCV.put(DBConstants.SPH_LEFT, lsph);
//            historyCV.put(DBConstants.CYCL_LEFT, lcycl);
//            historyCV.put(DBConstants.AXIS_LEFT, leyeaxis);
//            historyCV.put(DBConstants.NEAR, near);
//            historyCV.put(DBConstants.INSTRUCTION, instruct);

            Log.e("respo", "" + formDataMap);
            volleyTaskManager.urlusersubmission= Consts.USER_SUBMISSION_URL2;
            volleyTaskManager.doPostFormData(formDataMap, true);
        } else {
            completeSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal).append(sankul)
                    .append(sanch).append(upsanch).append(village).toString();
            Toast.makeText(mContext, "This is it", Toast.LENGTH_SHORT).show();
            Util.showCallBackMessageWithOkCallback_success(mContext,
                    "The data has been saved. It will be uploaded whenever Internet is available.", new SavedOffline(),
                    "No Internet");
            ContentValues cv = new ContentValues();
            Toast.makeText(mContext, "CV is it", Toast.LENGTH_SHORT).show();
            cv.put(DBConstants.MU_ID, Util.fetchUserClass(mContext).getUserId());
            cv.put(DBConstants.THREAD_ID, thread.getThreadID());
            cv.put(DBConstants.IMAGE, ((ImageClass) imagesList.get(0)).getBase64value());
            if (mCurrentLocation.getLatitude()!= 0.0 && mCurrentLocation.getLongitude()!=0.0) {
                cv.put(DBConstants.LATITUDE, mCurrentLocation.getLatitude());
                cv.put(DBConstants.LONGITUDE, mCurrentLocation.getLongitude());
            } else {
                networklat=MainActivity.getLat();
                netwoklong=MainActivity.getlongi();
                cv.put(DBConstants.LATITUDE,Double.toString(networklat));
                cv.put(DBConstants.LONGITUDE,Double.toString(netwoklong));
            }
            cv.put(DBConstants.KEYWORDS, "12,13,14");
            cv.put(DBConstants.ADDRESS, geoAddress);
            cv.put(DBConstants.DATE, Util.getDate());
            cv.put(DBConstants.TIME, Util.getTime());
            cv.put(DBConstants.SCHOOL_CODE, completeSchoolCode);
            cv.put(DBConstants.RATH_NUMBER, StaticConstants.RATH_NUMBER_DEFAULT);
            cv.put(DBConstants.VILLAGE_NAME, villageName);
            cv.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + countryCode + "\",\"village\":\"" + villageName + "\"}");
            cv.put(DBConstants.VILLAGE_NAME2, villagename2);
            cv.put(DBConstants.PATIENT_NAME, patientname);
            cv.put(DBConstants.ANCHAL_DATA, anchalname);
            cv.put(DBConstants.HEAD_OF_FAMILY, headoffamily);
            cv.put(DBConstants.AGE, age);
//            cv.put(DBConstants.HISTORY_TAKING, history_taking);
            cv.put(DBConstants.VISION_VR, vision_VR);
            cv.put(DBConstants.GENDER, gender);
            cv.put(DBConstants.GLASSES, glasses);
            cv.put(DBConstants.DATE_OF_EXAMINATION, date_examination);
            cv.put(DBConstants.VISION_VL, vision_VL);
            cv.put(DBConstants.PAST_HISTORY, past_history);
            cv.put(DBConstants.PRESENT_HISTORY, present_history);
            cv.put(DBConstants.BP_SYSTOLIC, BP_systolic);
            cv.put(DBConstants.BP_DIASTOLIC, BP_Diastolic);
            cv.put(DBConstants.BMI_HEIGHT, BMI_Height);
            cv.put(DBConstants.BMI_WEIGHT, BMI_Weight);
            cv.put(DBConstants.BMI_OBESITY, BMI_Obesity);
            cv.put(DBConstants.SUGAR_FASTING, Sugar_Fasting);
            cv.put(DBConstants.SUGAR_PP, Sugar_PP);
            cv.put(DBConstants.SUGAR_RANDOM, Sugar_Random);
            cv.put(DBConstants.MEDICINE, medicine);
            cv.put(DBConstants.EYE_DROP, eyedrop);
            cv.put(DBConstants.AMOUNT, amount);

            cv.put(DBConstants.REGISTRATION_CODE, registerno);
            cv.put(DBConstants.AADHAR_NUMBER, adharnum);
            cv.put(DBConstants.PAITENT_MOBILE, mobilenum);
            cv.put(DBConstants.OCCUPATION, occupation);
            cv.put(DBConstants.ILLNESS_CAUSE,illcause);
            cv.put(DBConstants.DURATION, illduration);
            cv.put(DBConstants.DIABETIC, diabetic);
            cv.put(DBConstants.CARDIAC, cardiac);
            cv.put(DBConstants.ONE_EYED, oneye);
            cv.put(DBConstants.HYPERNATION, hypernation);
            cv.put(DBConstants.ASTHAMATIC, asthama);
            cv.put(DBConstants.OTHERS, others);
//            cv.put(DBConstants.SPH_RIGHT, rsph);
//            cv.put(DBConstants.CYCL_RIGHT, rcycl);
//            cv.put(DBConstants.AXIS_RIGHT,reyeaxis);
//            cv.put(DBConstants.SPH_LEFT, lsph);
//            cv.put(DBConstants.CYCL_LEFT, lcycl);
//            cv.put(DBConstants.AXIS_LEFT, leyeaxis);
//            cv.put(DBConstants.NEAR, near);
//            cv.put(DBConstants.INSTRUCTION, instruct);

            getContentResolver().insert(Constant.CONTENT_URI,cv);
            clearForm();
        }
    }


    public void onSuccess(JSONObject resultJsonObject) {
        Log.e(TAG, "" + resultJsonObject);
        if (resultJsonObject.toString() == null || resultJsonObject.toString().trim().isEmpty()) {
            Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String result = "";
            String message = "";
            result = resultJsonObject.optString("code");
            message = resultJsonObject.optString("msg");
            Log.e(this.TAG, result);
            if (result.equalsIgnoreCase("200")) {
                new HistoryDB().saveHistoryData(this.mContext, this.historyCV);
                Util.showCallBackMessageWithOkCallback(mContext, "Submision Complete", new SubmissionComplete());
                return;
            }
            Toast.makeText(mContext, " " + message, Toast.LENGTH_LONG).show();
            Log.d("messagesubmission", "onSuccess: "+message);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onError(VolleyError error) {

        if (error instanceof TimeoutError || error instanceof ServerError||error instanceof NetworkError)
            saveOfflineData();
    }

    private void clearForm() {
        imageCount = 0;
        tv_imageProgress.setText("[Image added 0/1]");
        imagesList.clear();
        if (thread.getThreadID().equalsIgnoreCase("9")) {
            tvCountryCode.setText("");
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

            case R.id.tv_stateCode:
                startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);
                break;

            default:
                break;
        }
    }

    public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
        //System.out.println("------>> onActivityResult CALLED  >>---------------");
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            tv_stateCode.setText(data.getStringExtra(StateCodeActivity.RESULT_STATECODE));
            et_anchal.requestFocus();
            Util.showSoftKeyboard(this.mContext, this.et_anchal);
        }else {
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
                Log.e(TAG, "Picture path: " + picturePath);
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
            File file = new File(Environment.getExternalStorageDirectory() + "/ConnectAppImages/" + fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveOfflineData() {
        String countryCode = "";
        String villageName = "";
        String stateCode = tv_stateCode.getText().toString().trim();
        String anchal = et_anchal.getText().toString().trim();
        String sankul = et_sankul.getText().toString().trim();
        String sanch = et_sanch.getText().toString().trim();
        String upsanch = et_upsanch.getText().toString().trim();
        String village = et_village.getText().toString().trim();
        String completeSchoolCode = "";
        String anchalname = et_anchal_name.getText().toString().trim();
        String villagename = et_villagename.getText().toString().trim();
        String patientname= et_patientname.getText().toString().trim();
        String headoffamily = et_headfamily.getText().toString().trim();
        String age = et_age.getText().toString().trim();
//        String history_taking = et_history_taking.getText().toString().trim() ;
        String vision_VR = et_vision_VR.getText().toString().trim();
        String gender=spin_gender.getSelectedItem().toString();
        String glasses=spin_glasses.getSelectedItem().toString();
        String date_examination=et_date_of_determination.getText().toString();
        String vision_VL = et_vision_VL.getText().toString().trim();
        String history_complaints = et_history_complaints.getText().toString().trim();
        String past_history = et_past_history.getText().toString().trim();
        String present_history = et_present_history.getText().toString().trim();
        String BP_systolic = et_BP_systolic.getText().toString().trim();
        String BP_Diastolic = et_BP_Diastolic.getText().toString().trim();
        String BMI_Height = et_BMI_Height.getText().toString().trim();
        String BMI_Weight = et_BMI_Weight.getText().toString().trim();
        String BMI_Obesity = et_BMI_Obesity.getText().toString().trim();
        String Sugar_Fasting = et_Sugar_Fasting.getText().toString().trim();
        String Sugar_PP = et_Sugar_PP.getText().toString().trim();
        String Sugar_Random = et_Sugar_Random.getText().toString().trim();
        String medicine = et_medicine.getSelectedItem().toString();
        String eyedrop = et_eyedrop.getSelectedItem().toString();
        String amount = et_amount.getText().toString().trim();

        String registerno=et_registerno.getText().toString().trim();
        String adharnum=et_adharnum.getText().toString().trim();
        String mobilenum=et_mobilenum.getText().toString().trim();

        String occupation=et_occupation.getText().toString().trim();
        String illcause=et_illcause.getText().toString().trim();
        String illduration=et_illduration.getText().toString().trim();
        String diabetic=et_diabetic.getSelectedItem().toString();
        String cardiac=et_cardiac.getSelectedItem().toString();
        String oneye=et_oneye.getSelectedItem().toString();
        String hypernation=et_hypernation.getSelectedItem().toString();
        String asthama=et_asthama.getSelectedItem().toString();
        String others=et_others.getText().toString().trim();
//        String rsph=et_rsph.getText().toString().trim();
//        String rcycl=et_rcycl.getText().toString().trim();
//        String reyeaxis=et_reyeaxis.getText().toString().trim();
//        String lsph=et_lsph.getText().toString().trim();
//        String lcycl=et_lcycl.getText().toString().trim();
//        String leyeaxis=et_leyeaxis.getText().toString().trim();
//
//        String near=et_near.getText().toString().trim();
//        String instruct=et_instruct.getText().toString().trim();


        if (ll_dynamicField.getVisibility() == View.VISIBLE) {
            countryCode = tvCountryCode.getText().toString().trim();
        }
        completeSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal).append(sankul)
                .append(sanch).append(upsanch).append(village).toString();
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
            cv.put(DBConstants.LATITUDE,Double.toString(networklat));
            cv.put(DBConstants.LONGITUDE,Double.toString(netwoklong));
        }
        cv.put(DBConstants.VILLAGE_NAME2, Util.fetchUserClass(this.mContext).getUserId());
        cv.put(DBConstants.PATIENT_NAME, patientname);
        cv.put(DBConstants.ANCHAL_DATA, anchalname);
        cv.put(DBConstants.HEAD_OF_FAMILY, headoffamily);
        cv.put(DBConstants.AGE, age);
//        cv.put(DBConstants.HISTORY_TAKING, history_taking);
        cv.put(DBConstants.VISION_VR, vision_VR);
        cv.put(DBConstants.GENDER, gender);
        cv.put(DBConstants.GLASSES, glasses);
        cv.put(DBConstants.DATE_OF_EXAMINATION, date_examination);
        cv.put(DBConstants.VISION_VL, vision_VL);
        cv.put(DBConstants.HISTORY_COMPLAINTS, history_complaints);
        cv.put(DBConstants.PAST_HISTORY, past_history);
        cv.put(DBConstants.PRESENT_HISTORY, present_history);
        cv.put(DBConstants.BP_SYSTOLIC, BP_systolic);
        cv.put(DBConstants.BP_DIASTOLIC, BP_Diastolic);
        cv.put(DBConstants.BMI_HEIGHT, BMI_Height);
        cv.put(DBConstants.BMI_WEIGHT, BMI_Weight);
        cv.put(DBConstants.BMI_OBESITY, BMI_Obesity);
        cv.put(DBConstants.SUGAR_FASTING, Sugar_Fasting);
        cv.put(DBConstants.SUGAR_PP, Sugar_PP);
        cv.put(DBConstants.SUGAR_RANDOM, Sugar_Random);
        cv.put(DBConstants.MEDICINE, medicine);
        cv.put(DBConstants.EYE_DROP, eyedrop);
        cv.put(DBConstants.AMOUNT, amount);
        cv.put(DBConstants.KEYWORDS, "12,13,14");
        cv.put(DBConstants.ADDRESS, geoAddress);
        cv.put(DBConstants.DATE, Util.getDate());
        cv.put(DBConstants.TIME, Util.getTime());
        cv.put(DBConstants.SCHOOL_CODE, completeSchoolCode);
        cv.put(DBConstants.RATH_NUMBER, StaticConstants.RATH_NUMBER_DEFAULT);
        cv.put(DBConstants.VILLAGE_NAME, villageName);
        cv.put(DBConstants.OTHER_DATA, "{\"sCode\":\"" + countryCode + "\",\"village\":\"" + villageName + "\"}");

        cv.put(DBConstants.REGISTRATION_CODE, registerno);
        cv.put(DBConstants.AADHAR_NUMBER, adharnum);
        cv.put(DBConstants.PAITENT_MOBILE, mobilenum);
        cv.put(DBConstants.OCCUPATION, occupation);
        cv.put(DBConstants.ILLNESS_CAUSE,illcause);
        cv.put(DBConstants.DURATION, illduration);
        cv.put(DBConstants.DIABETIC, diabetic);
        cv.put(DBConstants.CARDIAC, cardiac);
        cv.put(DBConstants.ONE_EYED, oneye);
        cv.put(DBConstants.HYPERNATION, hypernation);
        cv.put(DBConstants.ASTHAMATIC, asthama);
        cv.put(DBConstants.OTHERS, others);
//        cv.put(DBConstants.SPH_RIGHT, rsph);
//        cv.put(DBConstants.CYCL_RIGHT, rcycl);
//        cv.put(DBConstants.AXIS_RIGHT,reyeaxis);
//        cv.put(DBConstants.SPH_LEFT, lsph);
//        cv.put(DBConstants.CYCL_LEFT, lcycl);
//        cv.put(DBConstants.AXIS_LEFT, leyeaxis);
//        cv.put(DBConstants.NEAR, near);
//        cv.put(DBConstants.INSTRUCTION, instruct);

        getContentResolver().insert(Constant.CONTENT_URI, cv);

        clearForm();
    }
}
