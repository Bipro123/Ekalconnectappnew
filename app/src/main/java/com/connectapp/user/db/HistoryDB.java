package com.connectapp.user.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.connectapp.user.data.OfflineSubmission;

public class HistoryDB implements DBConstants {

	private static HistoryDB obj = null;


	public synchronized static HistoryDB obj() {

		if (obj == null)
			obj = new HistoryDB();
		return obj;

	}

	public Boolean saveHistoryData(Context context, ContentValues cv) {

		System.out.println(" ----------  ADD ROWS INTO HISTORY TABLE --------- ");
		SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.insert(HISTORY_TABLE, null, cv);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			mdb.endTransaction();
			return true;
		}

	}

	public ArrayList<OfflineSubmission> getHistory(Context context) {

		ArrayList<OfflineSubmission> historyArray = new ArrayList<OfflineSubmission>();
		//		String[] columns = { _ID, MU_ID, THREAD_ID_HISTORY, IMAGE, LATITUDE, LONGITUDE, COMMENTS, KEYWORDS,
		//				ADDRESS, DATE, TIME, SCHOOL_CODE, VILLAGE_NAME, OTHER_DATA };

		SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getReadableDatabase();
		/*Cursor cur = mdb.query(HISTORY_TABLE, columns, BLOCK_DISTRICT_ID + "=?" + "AND " + BLOCK_PROJ_TYPE + "=?", new String[] { districtId,
				projectType }, null, null, null);*/
		Cursor cur = mdb.query(HISTORY_TABLE, null, null, null, null, null, null);

		if (!isDatabaseEmpty(cur)) {
			try {
				if (cur.moveToFirst()) {
					do {
						OfflineSubmission offlineSubmission = new OfflineSubmission();
						offlineSubmission.setMuId(cur.getString(cur.getColumnIndex(DBConstants.MU_ID)));
						offlineSubmission.setThreadID(cur.getString(cur.getColumnIndex(DBConstants.THREAD_ID_HISTORY)));
						offlineSubmission.setBase64Image(cur.getString(cur.getColumnIndex(DBConstants.IMAGE)));
						offlineSubmission.setLatitude(cur.getString(cur.getColumnIndex(DBConstants.LATITUDE)));
						offlineSubmission.setLongitude(cur.getString(cur.getColumnIndex(DBConstants.LONGITUDE)));
						offlineSubmission.setComments(cur.getString(cur.getColumnIndex(DBConstants.COMMENTS)));
						offlineSubmission.setKeywords(cur.getString(cur.getColumnIndex(DBConstants.KEYWORDS)));
						offlineSubmission.setAddress(cur.getString(cur.getColumnIndex(DBConstants.ADDRESS)));
						offlineSubmission.setDate(cur.getString(cur.getColumnIndex(DBConstants.DATE)));
						offlineSubmission.setTime(cur.getString(cur.getColumnIndex(DBConstants.TIME)));
						offlineSubmission.setSchoolCode(cur.getString(cur.getColumnIndex(DBConstants.SCHOOL_CODE)));
						offlineSubmission.setRathNumber(cur.getString(cur.getColumnIndex(DBConstants.RATH_NUMBER)));
						offlineSubmission.setVillageName(cur.getString(cur.getColumnIndex(DBConstants.VILLAGE_NAME)));
						offlineSubmission.setOtherData(cur.getString(cur.getColumnIndex(DBConstants.OTHER_DATA)));
						if(cur.getString(cur.getColumnIndex(DBConstants.THREAD_ID_HISTORY)).equals("9"))
						{
							offlineSubmission.setVillageName2(cur.getString(cur.getColumnIndex(DBConstants.VILLAGE_NAME2)));
							offlineSubmission.setAnchal(cur.getString(cur.getColumnIndex(DBConstants.ANCHAL_DATA)));
							offlineSubmission.setPatientName(cur.getString(cur.getColumnIndex(DBConstants.PATIENT_NAME)));
							offlineSubmission.setHEAD_OF_FAMILY(cur.getString(cur.getColumnIndex(DBConstants.HEAD_OF_FAMILY)));
							offlineSubmission.setAGE(cur.getString(cur.getColumnIndex(DBConstants.AGE)));
//							offlineSubmission.setHISTORY_TAKING(cur.getString(cur.getColumnIndex(DBConstants.HISTORY_TAKING)));
							offlineSubmission.setVISION_VR(cur.getString(cur.getColumnIndex(DBConstants.VISION_VR)));
							offlineSubmission.setGENDER(cur.getString(cur.getColumnIndex(DBConstants.GENDER)));
							offlineSubmission.setDATE_OF_EXAMINATION(cur.getString(cur.getColumnIndex(DBConstants.DATE_OF_EXAMINATION)));
							offlineSubmission.setVISION_VL(cur.getString(cur.getColumnIndex(DBConstants.VISION_VL)));
							offlineSubmission.setHISTORY_COMPLAINTS(cur.getString(cur.getColumnIndex(DBConstants.HISTORY_COMPLAINTS)));
							offlineSubmission.setPAST_HISTORY(cur.getString(cur.getColumnIndex(DBConstants.PAST_HISTORY)));
							offlineSubmission.setPRESENT_HISTORY(cur.getString(cur.getColumnIndex(DBConstants.PRESENT_HISTORY)));
							offlineSubmission.setBP_SYSTOLIC(cur.getString(cur.getColumnIndex(DBConstants.BP_SYSTOLIC)));
							offlineSubmission.setBP_DIASTOLIC(cur.getString(cur.getColumnIndex(DBConstants.BP_DIASTOLIC)));
							offlineSubmission.setBMI_HEIGHT(cur.getString(cur.getColumnIndex(DBConstants.BMI_HEIGHT)));
							offlineSubmission.setBMI_WEIGHT(cur.getString(cur.getColumnIndex(DBConstants.BMI_WEIGHT)));
							offlineSubmission.setBMI_OBESITY(cur.getString(cur.getColumnIndex(DBConstants.BMI_OBESITY)));
							offlineSubmission.setSUGAR_FASTING(cur.getString(cur.getColumnIndex(DBConstants.SUGAR_FASTING)));
							offlineSubmission.setSUGAR_PP(cur.getString(cur.getColumnIndex(DBConstants.SUGAR_PP)));
							offlineSubmission.setSUGAR_RANDOM(cur.getString(cur.getColumnIndex(DBConstants.SUGAR_RANDOM)));
//							offlineSubmission.setHISTORY_TAKING(cur.getString(cur.getColumnIndex(DBConstants.HISTORY_TAKING)));
							offlineSubmission.setMEDICINE(cur.getString(cur.getColumnIndex(DBConstants.MEDICINE)));
							offlineSubmission.setAMOUNT(cur.getString(cur.getColumnIndex(DBConstants.AMOUNT)));
							offlineSubmission.setSTATE(cur.getString(cur.getColumnIndex(DBConstants.STATE)));

						}

						historyArray.add(offlineSubmission);
					} while (cur.moveToNext());
				}
				cur.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return historyArray;
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

	/*public void clearDBTables(Context mcContext) {

		System.out.println(" ----------  CLEAR BLOCK TABLES  --------- ");
		SQLiteDatabase mdb = DisaterManagementDatabase.getInstance(mcContext).getWritableDatabase();
		mdb.beginTransaction();
		try {
			mdb.delete(BLOCK_TABLE, null, null);
			mdb.delete(MPCS_PROJECT_TABLE, null, null);
			mdb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mdb.endTransaction();
		}
	}*/
}
