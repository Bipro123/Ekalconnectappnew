package com.connectapp.user.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OfflineService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static LastMileSyncAdapter sSyncAdapter = null;

	@Override
	public void onCreate() {
		Log.v("OfflineService", "Offline Service");
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null)
				sSyncAdapter = new LastMileSyncAdapter(getApplicationContext(), true);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub
		super.onTaskRemoved(rootIntent);
		/*
		 * Rescheduling sync due to running one is killed on removing from
		 * recent applications.
		 */
		//requestSyncNow();
	}

	/**
	 * Initilaizes the sync adapter with proper Authentication through
	 * authentication service.
	 * **/
	@SuppressWarnings("unused")
	private void requestSyncNow() {

		Account appAccount = new Account(Constant.ACCOUNT_NAME, Constant.ACCOUNT_TYPE);
		AccountManager accountManager = AccountManager.get(this);
		accountManager.addAccountExplicitly(appAccount, null, null);
		ContentResolver.setIsSyncable(appAccount, Constant.PROVIDER, 1);
		ContentResolver.setMasterSyncAutomatically(true);
		ContentResolver.setSyncAutomatically(appAccount, Constant.PROVIDER, true);
	}
}
