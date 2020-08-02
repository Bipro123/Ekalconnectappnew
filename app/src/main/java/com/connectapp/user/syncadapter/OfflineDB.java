package com.connectapp.user.syncadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.connectapp.user.db.HistoryDB;

public class OfflineDB extends SQLiteOpenHelper implements DBConstants {

	private static final String TAG = "OfflineDB";
	private String CREATE_TABLE_STATEMENT;
	private Context mContext;


	public OfflineDB(Context context) {

		super(context, DB_NAME, null, DB_VERSION);
		this.mContext = context;
		Log.i(TAG, "Constuctor");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.i(TAG, "oncreate tables");
		db.execSQL(getCreatetableStatements());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		Log.i("Tag", "Old version" + oldVersion + " New version: " + newVersion + "Constant variable version name: " + DB_VERSION);
		db.execSQL("DROP TABLE IF EXISTS " + OFFLINE_TABLE);

		db.setVersion(DB_VERSION);

		// create new tables
		onCreate(db);

	}

	private String getCreatetableStatements() {

		CREATE_TABLE_STATEMENT = CREATE_TABLE_BASE + OFFLINE_TABLE + START_COLUMN + _ID + INTEGER + PRIMARY_KEY + AUTO_ICNREMENT
				+ COMMA + MU_ID + TEXT + COMMA + THREAD_ID + TEXT + COMMA + IMAGE + TEXT + COMMA + LATITUDE + TEXT + COMMA
				+ LONGITUDE + TEXT + COMMA + PICTURE_CATEGORY + TEXT + COMMA + KEYWORDS + TEXT + COMMA + ADDRESS + TEXT + COMMA
				+ DATE + TEXT + COMMA + TIME + TEXT + COMMA + SCHOOL_CODE + TEXT + COMMA + RATH_NUMBER + TEXT + COMMA
				+ VILLAGE_NAME + TEXT + COMMA + OTHER_DATA + TEXT + COMMA + UNIQUE + START_COLUMN + DATE + COMMA + TIME
				+ FINISH_COLUMN + ON_CONFLICT_REPLACE + FINISH_COLUMN;

		Log.d("DB Query", "" + CREATE_TABLE_STATEMENT);
		System.out.println("getCreatetableStatements");
		return CREATE_TABLE_STATEMENT;

	}

	/**
	 * Deletes a particular row per se
	 */
	public boolean deleteRow(String date, String time) {

		Log.d("DATABASE HELPER", "----------------\n\n  delete row  \n");
		Log.d("Date", "" + date);
		Log.d("Time", "" + time);
		if (saveHistory(date, time)) {
			return deleteOfflineRow(date, time);
		} else
			return false;

	}

	/**
	 * history of the submitted data is maintained in a separate DB.
	 * */
	private boolean saveHistory(String date, String time) {

		boolean isHistorySaved = false;
		SQLiteDatabase historyDB = this.getReadableDatabase();
		String[] columns = { _ID, MU_ID, THREAD_ID, IMAGE, LATITUDE, LONGITUDE, PICTURE_CATEGORY, KEYWORDS, ADDRESS, DATE, TIME,
				SCHOOL_CODE, RATH_NUMBER, VILLAGE_NAME, OTHER_DATA };

		Cursor cur = historyDB.query(OFFLINE_TABLE, columns, DATE + "= ? AND " + TIME + " = ?", new String[] { date, time },
				null, null, null);
		if (!isDatabaseEmpty(cur)) {
			try {
				if (cur.moveToFirst()) {
					do {

						ContentValues cv = new ContentValues();
						cv.put(DBConstants.MU_ID, cur.getString(cur.getColumnIndex(DBConstants.MU_ID)));
						cv.put(DBConstants.THREAD_ID, cur.getString(cur.getColumnIndex(DBConstants.THREAD_ID)));
						cv.put(DBConstants.IMAGE, cur.getString(cur.getColumnIndex(DBConstants.IMAGE)));
						cv.put(DBConstants.LATITUDE, cur.getString(cur.getColumnIndex(DBConstants.LATITUDE)));
						cv.put(DBConstants.LONGITUDE, cur.getString(cur.getColumnIndex(DBConstants.LONGITUDE)));
						cv.put(DBConstants.PICTURE_CATEGORY, cur.getString(cur.getColumnIndex(DBConstants.PICTURE_CATEGORY)));
						cv.put(DBConstants.KEYWORDS, cur.getString(cur.getColumnIndex(DBConstants.KEYWORDS)));
						cv.put(DBConstants.ADDRESS, cur.getString(cur.getColumnIndex(DBConstants.ADDRESS)));
						cv.put(DBConstants.DATE, cur.getString(cur.getColumnIndex(DBConstants.DATE)));
						cv.put(DBConstants.TIME, cur.getString(cur.getColumnIndex(DBConstants.TIME)));
						cv.put(DBConstants.SCHOOL_CODE, cur.getString(cur.getColumnIndex(DBConstants.SCHOOL_CODE)));
						cv.put(DBConstants.RATH_NUMBER, cur.getString(cur.getColumnIndex(DBConstants.RATH_NUMBER)));
						cv.put(DBConstants.VILLAGE_NAME, cur.getString(cur.getColumnIndex(DBConstants.VILLAGE_NAME)));
						cv.put(DBConstants.OTHER_DATA, cur.getString(cur.getColumnIndex(DBConstants.OTHER_DATA)));
						if(cur.getString(cur.getColumnIndex(DBConstants.MU_ID)).equals("9")) {
							cv.put(DBConstants.VILLAGE_NAME2, cur.getString(cur.getColumnIndex(DBConstants.VILLAGE_NAME2)));
							cv.put(DBConstants.ANCHAL_DATA, cur.getString(cur.getColumnIndex(DBConstants.ANCHAL_DATA)));
							cv.put(DBConstants.PATIENT_NAME, cur.getString(cur.getColumnIndex(DBConstants.PATIENT_NAME)));
							cv.put(DBConstants.HEAD_OF_FAMILY, cur.getString(cur.getColumnIndex(DBConstants.HEAD_OF_FAMILY)));
							cv.put(DBConstants.AGE, cur.getString(cur.getColumnIndex(DBConstants.AGE)));
//							cv.put(DBConstants.HISTORY_TAKING, cur.getString(cur.getColumnIndex(DBConstants.HISTORY_TAKING)));
							cv.put(DBConstants.VISION_VR, cur.getString(cur.getColumnIndex(DBConstants.VISION_VR)));
							cv.put(DBConstants.GENDER, cur.getString(cur.getColumnIndex(DBConstants.GENDER)));
							cv.put(DBConstants.GLASSES, cur.getString(cur.getColumnIndex(DBConstants.GLASSES)));
							cv.put(DBConstants.DATE_OF_EXAMINATION, cur.getString(cur.getColumnIndex(DBConstants.DATE_OF_EXAMINATION)));
							cv.put(DBConstants.VISION_VL, cur.getString(cur.getColumnIndex(DBConstants.VISION_VL)));
//							cv.put(DBConstants.HISTORY_COMPLAINTS, cur.getString(cur.getColumnIndex(DBConstants.HISTORY_COMPLAINTS)));
							cv.put(DBConstants.PAST_HISTORY, cur.getString(cur.getColumnIndex(DBConstants.PAST_HISTORY)));
							cv.put(DBConstants.PRESENT_HISTORY, cur.getString(cur.getColumnIndex(DBConstants.PRESENT_HISTORY)));
							cv.put(DBConstants.BP_SYSTOLIC, cur.getString(cur.getColumnIndex(DBConstants.BP_SYSTOLIC)));
							cv.put(DBConstants.BP_DIASTOLIC, cur.getString(cur.getColumnIndex(DBConstants.BP_DIASTOLIC)));
							cv.put(DBConstants.BMI_HEIGHT, cur.getString(cur.getColumnIndex(DBConstants.BMI_HEIGHT)));
							cv.put(DBConstants.BMI_WEIGHT, cur.getString(cur.getColumnIndex(DBConstants.BMI_WEIGHT)));
							cv.put(DBConstants.BMI_OBESITY, cur.getString(cur.getColumnIndex(DBConstants.BMI_OBESITY)));
							cv.put(DBConstants.SUGAR_FASTING, cur.getString(cur.getColumnIndex(DBConstants.SUGAR_FASTING)));
							cv.put(DBConstants.SUGAR_PP, cur.getString(cur.getColumnIndex(DBConstants.SUGAR_PP)));
							cv.put(DBConstants.SUGAR_RANDOM, cur.getString(cur.getColumnIndex(DBConstants.SUGAR_RANDOM)));
							cv.put(DBConstants.MEDICINE, cur.getString(cur.getColumnIndex(DBConstants.MEDICINE)));
							cv.put(DBConstants.AMOUNT, cur.getString(cur.getColumnIndex(DBConstants.AMOUNT)));
							cv.put(DBConstants.STATE, cur.getString(cur.getColumnIndex(DBConstants.STATE)));
						}
						new HistoryDB().saveHistoryData(mContext, cv);
						isHistorySaved = true;
					} while (cur.moveToNext());
				}
				cur.close();
			} catch (Exception e) {
				e.printStackTrace();
				isHistorySaved = false;
			}
		} else {
			isHistorySaved = false;
		}
		return isHistorySaved;
	}

	private Boolean isDatabaseEmpty(Cursor mCursor) {

		if (mCursor.moveToFirst()) {
			// NOT EMPTY
			return false;

		} else {
			// IS EMPTY
			return true;
		}

	}

	private boolean deleteOfflineRow(String date, String time) {
		SQLiteDatabase mdb = this.getWritableDatabase();
		return mdb.delete(OFFLINE_TABLE, DATE + "= ? AND " + TIME + " = ?", new String[] { date, time }) > 0;
	}
}
