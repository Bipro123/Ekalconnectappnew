package com.connectapp.user.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.data.Thread;
import com.connectapp.user.data.User;
import com.connectapp.user.util.Util;
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

import java.util.HashMap;

/**
 * Created by ritwik on 29-05-2018.
 */

public class GoogleSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private Context mContext;

    //Firebase and GoogleApiClient
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private static final int RC_SIGN_IN = 9001;
    private String TAG = getClass().getSimpleName();

    private ProgressDialog mProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //private Thread mThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = GoogleSignInActivity.this;
        setContentView(R.layout.activity_google_login);
        //mThread = (Thread) getIntent().getSerializableExtra("thread");
        //Google signin manager options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("SignIn", "onConnectionFailed:" + connectionResult);
        Util.initToast(this, "Google Play Services error.");
        hideProgressDialog();
    }

    public void onGoogleClick(View v) {

        mFirebaseAuth.signOut();
        //startActivity(new Intent(mContext, WalkthroughActivity.class));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
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

    private GoogleSignInAccount acct;

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        this.acct = acct;
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.e(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(mContext, "Authentication failed", Toast.LENGTH_SHORT).show();
                            // Util.initToast(mContext, "Authentication failed");
                        } else {

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            addUserToDatabase(FirebaseAuth.getInstance().getCurrentUser());
                            String instanceId = FirebaseInstanceId.getInstance().getToken();

                            Intent intent = new Intent(mContext, ChatActivity.class);
                            //intent.putExtra("thread", threadList.get(position));
                            startActivity(intent);
                           /* HashMap<String, String> requestMap = new HashMap<>();
                            requestMap.put("emailID", "" + acct.getEmail());
                            requestMap.put("mobile", "" + Util.fetchUserClass(mContext).getPhone());
                            requestMap.put("name", "" + acct.getDisplayName());
                            requestMap.put("fid", mFirebaseAuth.getCurrentUser().getUid());
                            requestMap.put("img", "" + mFirebaseAuth.getCurrentUser().getPhotoUrl());
                            requestMap.put("deviceToken",""+instanceId);
                            Log.e("firebaseId", "U Id: " + mFirebaseAuth.getCurrentUser().getUid());
                            volleyTaskManager.doRegistration(requestMap, true);*/
                            //Toast.makeText(mContext,"Logging in! Please wait...",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void showProgressDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private DatabaseReference mDatabase;

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
}
