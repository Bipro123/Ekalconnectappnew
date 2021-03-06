package com.connectapp.user.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectapp.user.BuildConfig;
import com.connectapp.user.R;
import com.connectapp.user.adapter.ChatFirebaseAdapter;
import com.connectapp.user.callback.ClickListenerChatFirebase;
import com.connectapp.user.constant.StaticConstants;
import com.connectapp.user.data.ChatModel;
import com.connectapp.user.data.FileModel;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.data.UserModel;
import com.connectapp.user.chatmodel.UserChatClass;
import com.connectapp.user.util.FileOpen;
import com.connectapp.user.util.Util;
import com.connectapp.user.view.FullScreenImageActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

/*import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;*/

/**
 * Created by ritwik on 3/11/17.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ClickListenerChatFirebase, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;
    private static final int OTHER_FILE_REQUEST = 4;
    //File
    private File filePathImageCamera;
    private boolean isFileRequest = false;

    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage, btEmoji, buttonAttachFile;
  //  private EmojiconEditText edMessage;
    private View contentRoot;
  //  private EmojIconActions emojIcon;
    //  private LinearLayout llWelcomeMsg;
    //CLass Model
    private UserModel userModel;

    static final String CHAT_REFERENCE = "chatmodel";

    // Intent Data
    private String receiverEmail = "";
    private String receiverFirebaseID = "";
    private String receiverInstanceID = "";

    static final String TAG = ChatActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_chat_main);
        mContext = ChatActivity.this;
        String contactName = getIntent().getStringExtra("name");
        receiverEmail = getIntent().getStringExtra("email");
        Log.e("receiverEmail", "receiverEmail " + receiverEmail);
        receiverFirebaseID = getIntent().getStringExtra("firebaseId");
        Log.e("receiverFirebaseID", "receiverFirebaseID " + receiverFirebaseID);

        receiverInstanceID = getIntent().getStringExtra("firebaseInstanceID");
        Log.e("receiverInstanceID", "receiverInstanceID " + receiverInstanceID);
       // receiverInstanceID = "choP2nBMmM8:APA91bHYGvOkZaf2QVHDkshjb7Xg2-Hn-HO1x1oPH2dxCmnRBKByBVaU4Nki1IFZRNtcUs-cVB0wpXXDMq8WbyPg3dyPzFla8bLldbn5PfdpqtDWa3iyJevJKG5SNQZmuTMsqtNmJNkQxAu13IvTMrlQ7mN2iyX1ow";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("" + contactName);

        if (!Util.isInternetAvailable(this)) {
            Util.initToast(this, "You do not have an internet connection.");
            finish();
        } else {
            bindViews();
            verifyUserLogin();
            mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API).build();
        }
    }

    /**
     * Link views with Java API
     */
    private void bindViews() {
        contentRoot = findViewById(R.id.contentRoot);
       // edMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        buttonAttachFile = (ImageView) findViewById(R.id.buttonAttachFile);
        btSendMessage.setOnClickListener(this);
        buttonAttachFile.setOnClickListener(this);
        //btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        //llWelcomeMsg = (LinearLayout) findViewById(R.id.llWelcomeMsg);
        //emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
       // emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
            case R.id.buttonAttachFile:
                showAttachOptions();
                break;
        }
    }

    /**
     * Verify user is logged in
     */
    private void verifyUserLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // startActivity(new Intent(this, SigninActivity.class));
            finish();
        } else {
            userModel = new UserModel(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString(), mFirebaseUser.getUid());
            readMessagensFirebase();
        }
    }

    /**
     * Read collections chatmodel Firebase
     */
    private void readMessagensFirebase() {


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Log.e("Data", "Json: " + mFirebaseDatabaseReference.child("" + CHAT_REFERENCE).child("chatModel"));
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Data", "Json: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String idNoSpecialChar = Util.fetchUserChatClass(mContext).getEmail() + receiverEmail;
        String result = idNoSpecialChar.replaceAll("[\\-\\+\\.\\^:,@]", "");
        Log.e("Previous", "Previous: " + idNoSpecialChar);
        Log.e("After", "After: " + result);
        result = toUppercase(result);
        Log.e("After", "UPPERCASE: " + result);
        String keyMap = sortKeyMap(result);
        Log.e("KeyMap", "KeyMap: " + keyMap);
        firebaseAdapter = new ChatFirebaseAdapter(mFirebaseDatabaseReference.child(CHAT_REFERENCE), userModel.getName(), this, Util.fetchUserChatClass(mContext).getEmail(), receiverEmail, keyMap, true);

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }

               /* if (!(friendlyMessageCount > 0))
                    llWelcomeMsg.setVisibility(View.VISIBLE);

                else
                    llWelcomeMsg.setVisibility(View.GONE);*/
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
    }

    /**
     * Send simple text msg to chat
     */
    private void sendMessageFirebase() {
        String idNoSpecialChar = Util.fetchUserChatClass(mContext).getEmail() + receiverEmail;
        String result = idNoSpecialChar.replaceAll("[\\-\\+\\.\\^:,@]", "");
        Log.e("Previous", "Previous: " + idNoSpecialChar);
        Log.e("After", "After: " + result);
        result = toUppercase(result);
        Log.e("After", "UPPERCASE: " + result);
        String keyMap = sortKeyMap(result);
        Log.e("KeyMap", "KeyMap: " + keyMap);
        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
        //ChatModel model = new ChatModel(userModel, edMessage.getText().toString(), receiverFirebaseID, userChatClass.firebaseId, receiverInstanceID, userChatClass.getEmail(), receiverEmail, keyMap, Calendar.getInstance().getTime().getTime() + "", null);
      //  mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(model);
        //edMessage.setText(null);
    }

    ChatFirebaseAdapter firebaseAdapter;

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("nameUser", nameUser);
        intent.putExtra("urlPhotoUser", urlPhotoUser);
        intent.putExtra("urlPhotoClick", urlPhotoClick);
        startActivity(intent);
    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }

    @Override
    public void clickFileChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {
        new DownloadFileFromURL("" + urlPhotoClick).execute("" + urlPhotoClick);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String sortKeyMap(String str) {

        int MAX_CHAR = 99;
        int char_count[] = new int[MAX_CHAR];
        int sum = 0;

        // Traverse the string
        for (int i = 0; i < str.length(); i++) {
            // Count occurrence of uppercase alphabets
            if (Character.isUpperCase(str.charAt(i)))
                char_count[str.charAt(i) - 'A']++;

                //Store sum of integers
            else
                sum = sum + (str.charAt(i) - '0');

        }

        String res = "";

        // Traverse for all characters A to Z
        for (int i = 0; i < MAX_CHAR; i++) {
            char ch = (char) ('A' + i);

            // Append the current character
            // in the string no. of times it
            //  occurs in the given string
            while (char_count[i]-- != 0)
                res = res + ch;
        }

        // Append the sum of integers
        if (sum > 0)
            res = res + sum;

        // return resultant string
        return res;
    }

    private String toUppercase(String value) {
        StringBuilder sb = new StringBuilder(value);
        for (int index = 0; index < sb.length(); index++) {
            char c = sb.charAt(index);
            if (Character.isLowerCase(c)) {
                sb.setCharAt(index, Character.toUpperCase(c));
            } else {
                sb.setCharAt(index, Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }


    //====== Attachments =========

    private void showAttachOptions() {


        final Dialog customDialog = new Dialog(ChatActivity.this);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_select_dialog, null);
        Button btn_album = (Button) view.findViewById(R.id.btn_album);
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                photoGalleryIntent();
            }
        });

        Button btn_camera = (Button) view.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                verifyStoragePermissions();

            }
        });
        Button btn_otherFiles = (Button) view.findViewById(R.id.btn_otherFiles);
        btn_otherFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                isFileRequest = true;
                verifyStoragePermissions();

            }
        });

        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                customDialog.dismiss();
            }
        });

        customDialog.setCancelable(false);
        customDialog.setContentView(view);
        customDialog.setCanceledOnTouchOutside(false);
        // Start AlertDialog
        customDialog.show();
    }

    /**
     * Upload photo taken by camera
     */
    private void photoCameraIntent() {
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Send photo from gallery
     */
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ChatActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            // we already have permission, lets go ahead and call camera intent
            if (isFileRequest) {
                isFileRequest = false;
                otherFileIntent();
            } else {
                photoCameraIntent();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (isFileRequest) {
                        isFileRequest = false;
                        otherFileIntent();
                    } else {
                        photoCameraIntent();
                    }
                }
                break;
        }
    }

    private void otherFileIntent() {
        // Toast.makeText(mContext, "Other Files", Toast.LENGTH_SHORT).show();
        /*   String[] mimetypes = {"application*//*|text*//*"};
        Intent i = new Intent();*/
        // i.setType("*/*");
       /* i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Choose file"), OTHER_FILE_REQUEST);*/

        Intent galleryintent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            galleryintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            galleryintent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        galleryintent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        galleryintent.setType("application/*|text/*");
        startActivityForResult(galleryintent, OTHER_FILE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        StorageReference storageRef = storage.getReferenceFromUrl(StaticConstants.URL_STORAGE_REFERENCE).child(StaticConstants.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri, "image");
                } else {
                    //URI IS NULL
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {
                    //IS NULL
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
          /*      Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    MapModel mapModel = new MapModel(latLng.latitude+"",latLng.longitude+"");
                    ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime()+"",mapModel);
                    mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }else{
                    //PLACE IS NULL
                }*/
            }
        } else if (requestCode == OTHER_FILE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri, "file");
                } else {
                    //URI IS NULL
                }
            }
        }
        ;
    }

    private ProgressDialog fileUploadProgressBar;

    /**
     * Sends the file to the firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final File file) {


        if (storageReference != null) {
            if (Util.isInternetAvailable(mContext)) {
                fileUploadProgressBar = new ProgressDialog(mContext);
                fileUploadProgressBar.setMessage("Sending file. Please wait...");
                fileUploadProgressBar.setIndeterminate(false);
                fileUploadProgressBar.setMax(100);
                fileUploadProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                fileUploadProgressBar.setCancelable(false);
                if (!fileUploadProgressBar.isShowing())
                    fileUploadProgressBar.show();
            }
            Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            UploadTask uploadTask = storageReference.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    UserClass userClass = Util.fetchUserClass(mContext);
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    // Create KeyMap
                    String idNoSpecialChar = Util.fetchUserChatClass(mContext).getEmail() + receiverEmail;
                    String result = idNoSpecialChar.replaceAll("[\\-\\+\\.\\^:,@]", "");
                    Log.e("Previous", "Previous: " + idNoSpecialChar);
                    Log.e("After", "After: " + result);
                    result = toUppercase(result);
                    Log.e("After", "UPPERCASE: " + result);
                    String keyMap = sortKeyMap(result);
                    Log.e("KeyMap", "KeyMap: " + keyMap);
                    // Fetch Chat Class
                    UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                    // Create File Model
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getName(), file.length() + "");
                   /* ChatModel chatModel = new ChatModel(userModel, "", userClass.adminFirebaseId,
                            userClass.firebaseId, userClass.firebaseInstanceId, userClass.getEmail(),
                            Calendar.getInstance().getTime().getTime() + "", fileModel);*/

                    ChatModel chatModel = new ChatModel(userModel, "", receiverFirebaseID,
                            userChatClass.firebaseId, receiverInstanceID,
                            userChatClass.getEmail(), receiverEmail, keyMap,
                            Calendar.getInstance().getTime().getTime() + "", fileModel);

                    mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    int uploadProgress = (int) progress;

                    if (fileUploadProgressBar != null && fileUploadProgressBar.isShowing()) {
                        if (uploadProgress == 100)
                            fileUploadProgressBar.dismiss();
                        else
                            fileUploadProgressBar.setProgress(uploadProgress);
                    }
                }
            });
        } else {
            //IS NULL
        }

    }

    /**
     * Sends the file to the firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final Uri file, String type) {

        if (storageReference != null) {

            if (Util.isInternetAvailable(mContext)) {
                fileUploadProgressBar = new ProgressDialog(mContext);
                fileUploadProgressBar.setMessage("Sending file. Please wait...");
                fileUploadProgressBar.setIndeterminate(false);
                fileUploadProgressBar.setMax(100);
                fileUploadProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                fileUploadProgressBar.setCancelable(false);
                if (!fileUploadProgressBar.isShowing())
                    fileUploadProgressBar.show();
            }

            if (type.equalsIgnoreCase("image")) {
                final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
                StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
                UploadTask uploadTask = imageGalleryRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess sendFileFirebase");
                        UserClass userClass = Util.fetchUserClass(mContext);
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        // Create KeyMap
                        String idNoSpecialChar = Util.fetchUserChatClass(mContext).getEmail() + receiverEmail;
                        String result = idNoSpecialChar.replaceAll("[\\-\\+\\.\\^:,@]", "");
                        Log.e("Previous", "Previous: " + idNoSpecialChar);
                        Log.e("After", "After: " + result);
                        result = toUppercase(result);
                        Log.e("After", "UPPERCASE: " + result);
                        String keyMap = sortKeyMap(result);
                        Log.e("KeyMap", "KeyMap: " + keyMap);
                        // Fetch Chat Class
                        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                        FileModel fileModel = new FileModel("img", downloadUrl.toString(), name, "");
                       /* ChatModel chatModel = new ChatModel(userModel, "", userClass.adminFirebaseId,
                                userClass.firebaseId, userClass.firebaseInstanceId, userClass.getEmail(),
                                Calendar.getInstance().getTime().getTime() + "", fileModel);*/
                        ChatModel chatModel = new ChatModel(userModel, "", receiverFirebaseID,
                                userChatClass.firebaseId, receiverInstanceID,
                                userChatClass.getEmail(), receiverEmail, keyMap,
                                Calendar.getInstance().getTime().getTime() + "", fileModel);
                        mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        int uploadProgress = (int) progress;
                        if (fileUploadProgressBar != null && fileUploadProgressBar.isShowing()) {
                            if (uploadProgress == 100)
                                fileUploadProgressBar.dismiss();
                            else
                                fileUploadProgressBar.setProgress(uploadProgress);
                        }
                    }
                });
            } else if (type.equalsIgnoreCase("file")) {
                final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
                String fileName = getFileName(file);
                StorageReference imageGalleryRef = storageReference.child(name + "_mentorfile_" + fileName + "_gallery");
                UploadTask uploadTask = imageGalleryRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess sendFileFirebase");
                        UserClass userClass = Util.fetchUserClass(mContext);
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        // Create KeyMap
                        String idNoSpecialChar = Util.fetchUserChatClass(mContext).getEmail() + receiverEmail;
                        String result = idNoSpecialChar.replaceAll("[\\-\\+\\.\\^:,@]", "");
                        Log.e("Previous", "Previous: " + idNoSpecialChar);
                        Log.e("After", "After: " + result);
                        result = toUppercase(result);
                        Log.e("After", "UPPERCASE: " + result);
                        String keyMap = sortKeyMap(result);
                        Log.e("KeyMap", "KeyMap: " + keyMap);
                        // Fetch Chat Class
                        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                        FileModel fileModel = new FileModel("file", downloadUrl.toString(), name, "");
                       /* ChatModel chatModel = new ChatModel(userModel, "", userClass.adminFirebaseId,
                                userClass.firebaseId, userClass.firebaseInstanceId, userClass.getEmail(),
                                Calendar.getInstance().getTime().getTime() + "", fileModel);*/
                        ChatModel chatModel = new ChatModel(userModel, "", receiverFirebaseID,
                                userChatClass.firebaseId, receiverInstanceID,
                                userChatClass.getEmail(), receiverEmail, keyMap,
                                Calendar.getInstance().getTime().getTime() + "", fileModel);
                        mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        int uploadProgress = (int) progress;
                        if (fileUploadProgressBar != null && fileUploadProgressBar.isShowing()) {
                            if (uploadProgress == 100)
                                fileUploadProgressBar.dismiss();
                            else
                                fileUploadProgressBar.setProgress(uploadProgress);
                        }
                    }
                });
            }
        } else {
            //IS NULL
        }

    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    ProgressDialog pDialog;

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private String downloadFileUrlChat = "";


        public DownloadFileFromURL(String url) {

            downloadFileUrlChat = url;
        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            if (!pDialog.isShowing())
                pDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/" + "Documents" + "/" + ChatFirebaseAdapter.getBetweenStrings(downloadFileUrlChat, "_mentorfile_", "_gallery"));

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/Documents" + "/" + ChatFirebaseAdapter.getBetweenStrings(downloadFileUrlChat, "_mentorfile_", "_gallery");
            // setting downloaded into image view
            //my_image.setImageDrawable(Drawable.createFromPath(imagePath));
            Log.e("Stored Path", "StoredPath: " + imagePath);
            if (imagePath != null && imagePath.length() > 0) {
                File file = new File(imagePath);
                /* Uri path = Uri.fromFile(file);
                 *//* String mime = getContentResolver().getType(path);

                // Open file with user selected app
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(path, mime);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);*//*
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(fileExt(path.toString()).substring(1));
                newIntent.setDataAndType(Uri.fromFile(file),mimeType);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }*/

                try {
                    FileOpen.openFile(mContext, file);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}
