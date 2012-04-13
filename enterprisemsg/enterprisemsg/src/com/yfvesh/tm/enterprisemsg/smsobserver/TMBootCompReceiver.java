package com.yfvesh.tm.enterprisemsg.smsobserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TMBootCompReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d("tag","TMBootCompReceiver onReceive");
		
		Intent i = new Intent("com.yfvesh.tm.smsobserver.TMBootService");
		context.startService(i); 
	}

}
