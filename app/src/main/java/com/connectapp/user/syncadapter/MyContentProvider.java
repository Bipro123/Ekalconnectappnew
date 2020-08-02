package com.connectapp.user.syncadapter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Content Provider class for sync adapter.
 */
public class MyContentProvider extends ContentProvider {

	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	// Uri Matcher for the content provider
	@SuppressWarnings("unused")
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = Constant.PROVIDER;
		return matcher;
	}

	OfflineDB dbHelper;

	@Override
	public String getType(Uri uri) {

		return new String();
	}

	@Override
	public boolean onCreate() {
		System.out.println("Content Provider");
		Context ctx = getContext();
		dbHelper = new OfflineDB(ctx);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		//FETCH DATA
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cur = db.query(OfflineDB.OFFLINE_TABLE, null, null, null, null, null, null);

		return cur;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase mdb = dbHelper.getWritableDatabase();
		mdb.beginTransaction();
		try {
			Log.v("ContentProvider", "Insert Called.");
			long id = mdb.insert(OfflineDB.OFFLINE_TABLE, null, values);
			mdb.setTransactionSuccessful();
			if (id != -1)
				getContext().getContentResolver().notifyChange(uri, null);
			return Constant.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnsupportedOperationException("URI: " + uri + " not supported.");

		} finally {
			mdb.endTransaction();
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		return 0;
	}
}
