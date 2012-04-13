package com.yfvesh.tm.enterprisemsg.smsobserver;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;

public class TMBootService extends Service {
	public static final String TAG = "TMBootService";
	/* the SMS observer */
	private ContentObserver mObserver;

	@Override
	public void onCreate() {
		super.onCreate();
		addSmsObserver();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		removeSmsObserver();
		super.onDestroy();
	}

	private void addSmsObserver() {
		ContentResolver resolver = getContentResolver();
		Handler handler = new SMSHandler(this);
		mObserver = new SMSObserver(resolver, handler,this);
		resolver.registerContentObserver(SMS.CONTENT_URI, true, mObserver);
	}

	private void removeSmsObserver() {
		getContentResolver().unregisterContentObserver(mObserver);
	}
}
