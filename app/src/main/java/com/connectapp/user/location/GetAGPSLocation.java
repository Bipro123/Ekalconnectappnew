/*package com.connectapp.user.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.connectapp.user.util.Util;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GetAGPSLocation extends AsyncTask<Void, Void, Location> implements LocationListener, ConnectionCallbacks{

	private Context mContext;
	private Activity mActivity;
	private ProgressDialog mProgressDialog;
	private AlertDialog systemAlertDialog;
	private Location mCurrentLocation;
	private String TAG;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private FusedLocationProviderApi fusedLocationProviderApi;

	public GetAGPSLocation(Context mContext, Activity mActivity) {

		this.mContext = mContext;
		this.mActivity = mActivity;
		this.TAG = getClass().getSimpleName();
		mProgressDialog = new ProgressDialog(this.mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("Fetching Location! Please wait...");
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Location doInBackground(Void... params) {
		return null;
	}

	@Override
	protected void onPostExecute(Location result) {
		super.onPostExecute(result);
	}

	private void checkingLocation() {
		if (isGooglePlayServicesAvailable()) {
			LocationManager locationManager = (LocationManager) mContext.getSystemService("location");
			if (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")) {
				Log.v("GPS Connection Found:", "true");
				if (this.mCurrentLocation == null) {
					this.mProgressDialog.setMessage("Fetching present location...");
					this.mProgressDialog.setCancelable(true);
					createLocationRequest();
					return;
				}
				return;
			}
			Log.e(this.TAG, "NO LOCATION FOUND!");
			if (this.systemAlertDialog == null) {
				this.systemAlertDialog = Util.showSettingsAlert(mContext.getApplicationContext(), this.systemAlertDialog);
				return;
			} else if (!this.systemAlertDialog.isShowing()) {
				this.systemAlertDialog = Util.showSettingsAlert(mContext.getApplicationContext(), this.systemAlertDialog);
				return;
			} else {
				return;
			}
		}
		GooglePlayServicesUtil.getErrorDialog(GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext), mActivity, 10)
				.show();
	}

	private boolean isGooglePlayServicesAvailable() {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == 0) {
			return true;
		}
		return false;
	}

	protected void createLocationRequest() {
		System.out.println("Fused Location called!");
		this.mProgressDialog.setMessage("Fetching present location...");
		this.mProgressDialog.setCancelable(true);
		showProgressDialog();
		this.mLocationRequest = LocationRequest.create();
		this.mLocationRequest.setPriority(100);
		this.mLocationRequest.setNumUpdates(1);
		this.mLocationRequest.setInterval(5000);
		this.mLocationRequest.setFastestInterval(1000);
		this.fusedLocationProviderApi = LocationServices.FusedLocationApi;
		if (this.mGoogleApiClient == null) {
			this.mGoogleApiClient = new Builder(mContext).addApi(LocationServices.API).addConnectionCallbacks(GetAGPSLocation.this)
					.addOnConnectionFailedListener(this).build();
			this.mGoogleApiClient.connect();
			return;
		}
		return;
	}
	private void showProgressDialog() {
		if (!this.mProgressDialog.isShowing()) {
			this.mProgressDialog.show();
		}
	}

	private void hideProgressDialog() {
		if (this.mProgressDialog.isShowing()) {
			this.mProgressDialog.dismiss();
		}
	}
}
*/