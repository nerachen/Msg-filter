package com.yfvesh.tm.enterprisemsg.smsobserver;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.yfvesh.tm.enterprisemsg.R;
import com.yfvesh.tm.enterprisemsg.EPMessageItem;
import com.yfvesh.tm.enterprisemsg.EnterprisemsgActivity;
import com.yfvesh.tm.enterprisemsg.epmsgprovider.EPMsgProviderHelper;

public class SMSHandler extends Handler {

	public static final String TAG = "TMSMSHandler";

	public static final int MSG_ADD_EPMSG_ITEM = 101;
	public static final int MSG_ADD_EPMSGS_DONE = 102;
	private Context mContext;

	public SMSHandler(Context context) {
		super();
		this.mContext = context;
	}

	public void handleMessage(Message message) {
		switch (message.what) {
		case MSG_ADD_EPMSG_ITEM: {
			EPMessageItem item = (EPMessageItem) message.obj;
			Log.d(TAG, "recevie ep message:" + item);
			EPMsgProviderHelper.addEPMsgItem(item, mContext);
			break;
		}
		case MSG_ADD_EPMSGS_DONE: {
			int count = EPMsgProviderHelper.getUnreadCount(mContext);
			showNotification(count);
			braodcastUpdateIntent(count);
			break;
		}
		default:
			break;
		}
	}

	protected void showNotification(int count) {
		if (mContext == null) {
			return;
		}
		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		// The PendingIntent to launch enterprise message if the user selects
		// this notification
		Intent intent = new Intent(mContext, EnterprisemsgActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, 0);

		// The ticker text
		String tickerText = mContext.getResources().getString(
				R.string.txt_unread_message_1)
				+ count
				+ mContext.getResources().getString(
						R.string.txt_unread_message_2);
		Notification notif = new Notification(R.drawable.icon_new_message,
				tickerText, System.currentTimeMillis());
		notif.defaults |= Notification.DEFAULT_LIGHTS;
		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.status_expand_view);
		contentView.setImageViewResource(R.id.descripimg,
				R.drawable.icon_new_message);
		contentView.setTextViewText(R.id.labelfrom, tickerText);
		contentView.setTextViewText(R.id.labelbody, Calendar.getInstance().getTime().toString());
		notif.contentView = contentView;
		notif.contentIntent = contentIntent;
		notif.sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.qhc );
		// Set the info for the views that show in the notification panel.
		// notif.setLatestEventInfo(mContext, from, message, contentIntent);

		nm.notify(R.string.app_name, notif);
	}

	private void braodcastUpdateIntent(int count) {
		if(mContext == null) {
			return;
		}
		Intent itent = new Intent();
		itent.setAction(EnterprisemsgActivity.MSG_BROADCAST_NEW);
		itent.putExtra(EnterprisemsgActivity.MSG_BROADCAST_NEW_COUNT, count);
		mContext.sendBroadcast(itent);
	}
}
