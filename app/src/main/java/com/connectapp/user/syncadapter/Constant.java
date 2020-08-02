package com.connectapp.user.syncadapter;

import android.database.Cursor;
import android.net.Uri;

import com.connectapp.user.data.OfflineSubmission;

/**
 * Sync Adapter Constants.
 * **/
public class Constant {

	public static final String ACCOUNT_NAME = "EkalConnectApp";

	public static final String ACCOUNT_TYPE = "com.connectapp.user.sync";

	public static final String PROVIDER = "com.connectapp.provider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + Constant.PROVIDER + "/Offline");

	public static OfflineSubmission getOfflineSubmissionFromCursor(Cursor cur) {

		OfflineSubmission offlineSubmission = new OfflineSubmission();
		offlineSubmission.setMuId(cur.getString(cur.getColumnIndex(DBConstants.MU_ID)));
		offlineSubmission.setThreadID(cur.getString(cur.getColumnIndex(DBConstants.THREAD_ID)));
		offlineSubmission.setBase64Image(cur.getString(cur.getColumnIndex(DBConstants.IMAGE)));
		offlineSubmission.setLatitude(cur.getString(cur.getColumnIndex(DBConstants.LATITUDE)));
		offlineSubmission.setLongitude(cur.getString(cur.getColumnIndex(DBConstants.LONGITUDE)));
		offlineSubmission.setComments(cur.getString(cur.getColumnIndex(DBConstants.PICTURE_CATEGORY)));
		offlineSubmission.setKeywords(cur.getString(cur.getColumnIndex(DBConstants.KEYWORDS)));
		offlineSubmission.setAddress(cur.getString(cur.getColumnIndex(DBConstants.ADDRESS)));
		offlineSubmission.setDate(cur.getString(cur.getColumnIndex(DBConstants.DATE)));
		offlineSubmission.setTime(cur.getString(cur.getColumnIndex(DBConstants.TIME)));
		offlineSubmission.setSchoolCode(cur.getString(cur.getColumnIndex(DBConstants.SCHOOL_CODE)));
		offlineSubmission.setRathNumber(cur.getString(cur.getColumnIndex(DBConstants.RATH_NUMBER)));
		offlineSubmission.setVillageName(cur.getString(cur.getColumnIndex(DBConstants.VILLAGE_NAME)));
		offlineSubmission.setOtherData(cur.getString(cur.getColumnIndex(DBConstants.OTHER_DATA)));

		return offlineSubmission;

	}
}
