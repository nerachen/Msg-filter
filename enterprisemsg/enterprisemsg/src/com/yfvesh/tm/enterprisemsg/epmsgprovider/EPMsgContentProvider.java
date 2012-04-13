package com.yfvesh.tm.enterprisemsg.epmsgprovider;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EPMsgContentProvider extends ContentProvider {

	private static final String TAG = "EPMsgContentProvider";

	private static final String DATABASE_NAME = "yftmepmsg2.db";

	private static final int DATABASE_VERSION = 1;

	private static final String DATA_TABLE_NAME = "epmsgdatas";

	public static final String AUTHORITY = "com.yfvesh.tm.epmsgprovider";

	private static final int USRDATA_ITEMS = 1;
	private static final int USRDATA_ITEM = 2;
	private static Map<String, String> mDataProjectionMap = new HashMap<String, String>();

	private static UriMatcher mUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		mUriMatcher.addURI(AUTHORITY, "epmsgdatas", USRDATA_ITEMS);
		mUriMatcher.addURI(AUTHORITY, "epmsgdatas/#", USRDATA_ITEM);

		mDataProjectionMap.put(EPMsgDatas._ID, EPMsgDatas._ID);
		mDataProjectionMap.put(EPMsgDatas.EP_ID, EPMsgDatas.EP_ID);
		mDataProjectionMap.put(EPMsgDatas.EP_TYPE, EPMsgDatas.EP_TYPE);
		mDataProjectionMap.put(EPMsgDatas.EP_PROTOCOL, EPMsgDatas.EP_PROTOCOL);
		mDataProjectionMap.put(EPMsgDatas.EP_FROM, EPMsgDatas.EP_FROM);
		mDataProjectionMap.put(EPMsgDatas.EP_BODY, EPMsgDatas.EP_BODY);
		mDataProjectionMap.put(EPMsgDatas.EP_TIME, EPMsgDatas.EP_TIME);
		mDataProjectionMap.put(EPMsgDatas.EP_READ, EPMsgDatas.EP_READ);
	}

	private DatabaseHelper mDBHelper;

	@Override
	public boolean onCreate() {

		mDBHelper = new DatabaseHelper(this.getContext());
		return true;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE_NAME);
			db.execSQL("CREATE TABLE " + DATA_TABLE_NAME + " ("
					+ EPMsgDatas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ EPMsgDatas.EP_ID + " INTEGER," + EPMsgDatas.EP_TYPE
					+ " INTEGER," + EPMsgDatas.EP_PROTOCOL + " INTEGER,"
					+ EPMsgDatas.EP_FROM + " TEXT," + EPMsgDatas.EP_BODY
					+ " TEXT," + EPMsgDatas.EP_TIME + " TEXT,"
					+ EPMsgDatas.EP_READ + " INTEGER" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "onUpgrade from oldVersion:" + oldVersion
					+ " to newVersion:" + newVersion);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count;
		switch (mUriMatcher.match(uri)) {
		case USRDATA_ITEMS: {
			count = db.delete(DATA_TABLE_NAME, where, whereArgs);
			break;
		}
		case USRDATA_ITEM: {
			String itemid = uri.getPathSegments().get(1);
			count = db.delete(DATA_TABLE_NAME, EPMsgDatas._ID
					+ "="
					+ itemid
					+ (!TextUtils.isEmpty(where) ? (" AND (" + where + ")")
							: ""), whereArgs);
			break;
		}
		default: {
			throw new IllegalArgumentException("Unknown Uri +" + uri);
		}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		String str = null;
		switch (mUriMatcher.match(uri)) {
		case USRDATA_ITEMS: {
			str = EPMsgDatas.CONTENT_TYPE_ITEMS;
			break;
		}
		case USRDATA_ITEM: {
			str = EPMsgDatas.CONTENT_TYPE_ITEM;
			break;
		}
		default: {
			break;
		}
		}
		return str;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (mUriMatcher.match(uri) != USRDATA_ITEMS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values = (initialValues != null ? new ContentValues(
				initialValues) : new ContentValues());
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		EPMsgDatas.checkEPMsgValues(values);
		long rowId = db.insert(DATA_TABLE_NAME, EPMsgDatas.EP_ID, values);
		if (rowId > 0) {
			Uri datauri = ContentUris.withAppendedId(EPMsgDatas.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(datauri, null);
			return datauri;
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(uri)) {
		case USRDATA_ITEMS: {
			qb.setTables(DATA_TABLE_NAME);
			qb.setProjectionMap(mDataProjectionMap);
			break;
		}
		case USRDATA_ITEM: {
			qb.setTables(DATA_TABLE_NAME);
			qb.setProjectionMap(mDataProjectionMap);
			qb.appendWhere(EPMsgDatas._ID + "=" + uri.getPathSegments().get(1));
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count;
		switch (mUriMatcher.match(uri)) {
		case USRDATA_ITEMS: {
			count = db
					.update(DATA_TABLE_NAME, values, selection, selectionArgs);
			break;
		}
		case USRDATA_ITEM: {
			String itemid = uri.getPathSegments().get(1);
			count = db.update(
					DATA_TABLE_NAME,
					values,
					EPMsgDatas._ID
							+ "="
							+ itemid
							+ (!TextUtils.isEmpty(selection) ? (" AND ("
									+ selection + ")") : ""), selectionArgs);
			break;
		}
		default: {
			throw new IllegalArgumentException("Unknown Uri +" + uri);
		}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
