package com.connectapp.user.location;

import java.text.DateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class BackgroundLocationService extends Service implements LocationListener,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private final long INTERVAL = 1000 * 5; // 5 seconds
	private final long FASTEST_INTERVAL = 1000 * 1; // 1 second
	private LocationRequest locationRequest;
	private GoogleApiClient googleApiClient;
	private FusedLocationProviderApi fusedLocationProviderApi;
	private Location mCurrentLocation;
	public double latitude; // latitude
	public double longitude; // longitude
	private static final String TAG = "BackgroundLocationService";
	public final static String MY_ACTION = "MY_ACTION";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		checkConnection();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
	}

	private void checkConnection() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.e(TAG, "NO LOCATION FOUND!");
			onCreate();
		} else {
			Log.v("GPS Connection Found:", "true");
			if (mCurrentLocation == null) {
				getLocation();
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy Service");
		try {
			if (googleApiClient != null) {
				googleApiClient.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopSelf();
	}

	public void getLocation() {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		fusedLocationProviderApi = LocationServices.FusedLocationApi;
		googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
				.addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
		if (googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.v("onConnected", "Entering here.");
		try {
			fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
		} catch (Exception e) {
			Log.e(TAG, "----------------- Exception at onConnected -------------------");
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.v("onConnectionSuspended", "Entering here.");
	}

	@Override
	public void onLocationChanged(Location mLocation) {
		mCurrentLocation = mLocation;
		DateFormat.getTimeInstance().format(new Date());
		long atTime = mCurrentLocation.getTime();
		DateFormat.getTimeInstance().format(new Date(atTime));

		if (mCurrentLocation != null) {

			String latitude = String.valueOf(mCurrentLocation.getLatitude());
			String longitude = String.valueOf(mCurrentLocation.getLongitude());
			Log.v("Background Location Service: ", "Latitude: " + latitude + " Longitude: " + longitude
					+ " Accuracy: " + mCurrentLocation.getAccuracy());

			if (mCurrentLocation.getAccuracy() == 10.0) {
				Log.v(TAG, "accuracy is = 10");

				onDestroy();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e(TAG, "ON CONNECTION FAILED!");
		onCreate();
	}

}
