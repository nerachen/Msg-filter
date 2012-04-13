package com.yfvesh.tm.enterprisemsg.smsobserver;

import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.yfvesh.tm.enterprisemsg.R;
import com.yfvesh.tm.enterprisemsg.EPMessageItem;

public class SMSObserver extends ContentObserver {

	public static final String TAG = "LocalTestSMSObserver";

	private static final String[] PROJECTION = new String[] { SMS._ID,// 0
			SMS.TYPE,// 1
			SMS.ADDRESS,// 2
			SMS.BODY,// 3
			SMS.DATE,// 4
			SMS.THREAD_ID,// 5
			SMS.READ,// 6 //0 un-read, 1 read
			SMS.PROTOCOL // 7
	};

	//private static final String TO_BE_REP_EP_CENTER_NUM = "replacedbyactuallcenternum";
	// private static final String SELECTION = SMS.ADDRESS + " = "
	// + TO_BE_REP_EP_CENTER_NUM + " and " + SMS.PROTOCOL + " = 0"
	// + " and (" + SMS.TYPE + " = " + SMS.MESSAGE_TYPE_INBOX + ")";

	private static final String SELECTION = SMS.PROTOCOL + " = 0" + " and ("
			+ SMS.TYPE + " = " + SMS.MESSAGE_TYPE_INBOX + ")" + " and ("
			+ SMS.READ + " =0)";

	private static final int COLUMN_INDEX_ID = 0;
	private static final int COLUMN_INDEX_TYPE = 1;
	private static final int COLUMN_INDEX_PHONE = 2;
	private static final int COLUMN_INDEX_BODY = 3;
	private static final int COLUMN_INDEX_DATE = 4;
	private static final int COLUMN_INDEX_READ = 6;
	private static final int COLUMN_INDEX_PROTOCOL = 7;

	private ContentResolver mResolver;
	private Handler mHandler;
	private Context mContext;

	public SMSObserver(ContentResolver contentResolver, Handler handler,
			Context ctx) {
		super(handler);
		this.mResolver = contentResolver;
		this.mHandler = handler;
		this.mContext = ctx;
	}

	@Override
	public void onChange(boolean selfChange) {
		// Log.i(TAG, "onChange : " + selfChange + "; " + SELECTION);
		super.onChange(selfChange);
		String selection = SELECTION;
//		if (mContext != null) {
//			selection = SELECTION.replace(TO_BE_REP_EP_CENTER_NUM, mContext
//					.getResources().getString(R.string.config_epcenter_num));
//		}

		Cursor cursor = mResolver.query(SMS.CONTENT_URI, PROJECTION,
				String.format(selection), null, null);
		int id, protocol;
		String body;
		long ltime;
		boolean bread;

		EPMessageItem item;
		int count = 0;
		while (cursor.moveToNext()) {
			id = cursor.getInt(COLUMN_INDEX_ID);
			body = cursor.getString(COLUMN_INDEX_BODY);
			protocol = cursor.getInt(COLUMN_INDEX_PROTOCOL);
			ltime = cursor.getLong(COLUMN_INDEX_DATE);
			bread = (cursor.getInt(COLUMN_INDEX_READ) == 0) ? false : true;
			if (protocol == SMS.PROTOCOL_SMS && body != null) {
				/* filter the content */
				if (!filterEPSMS(cursor.getString(COLUMN_INDEX_PHONE))) {
					continue;
				} else {
					Uri uri = ContentUris.withAppendedId(SMS.CONTENT_URI, id);
					// int deleterow = mResolver.delete(uri, null, null);
					mResolver.delete(uri, null, null);
					item = new EPMessageItem();
					item.setId(cursor.getInt(COLUMN_INDEX_ID));
					item.setType(cursor.getInt(COLUMN_INDEX_TYPE));
					item.setProtocol(cursor.getInt(COLUMN_INDEX_PROTOCOL));
					item.setPhone(cursor.getString(COLUMN_INDEX_PHONE));
					item.setBody(cursor.getString(COLUMN_INDEX_BODY));
					item.setUsrRead(bread);

					Date date = new Date(ltime);
					item.setTime(date);

					count++;
					Message message = new Message();
					message.what = SMSHandler.MSG_ADD_EPMSG_ITEM;
					message.obj = item;
					mHandler.sendMessage(message);
				}
			}
		}
		cursor.close();
		if (count > 0) {
			Message message = new Message();
			message.what = SMSHandler.MSG_ADD_EPMSGS_DONE;
			message.arg1 = count;
			mHandler.sendMessage(message);
		}
	}

	private boolean filterEPSMS(String phone) {
		boolean result = false;
		if(mContext != null && phone != null) {
			String filterfrom = mContext.getResources().getString(R.string.config_epcenter_num);
			if(phone.contains(filterfrom)) {
				result = true;
			}
		}
		return result;
	}
}
