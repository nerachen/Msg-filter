package com.yfvesh.tm.enterprisemsg.epmsgprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.yfvesh.tm.enterprisemsg.EPMessageItem;

public final class EPMsgProviderHelper {

	private static final String[] EP_COLUMNS = new String[] { EPMsgDatas._ID,
			EPMsgDatas.EP_ID, EPMsgDatas.EP_TYPE, EPMsgDatas.EP_PROTOCOL,
			EPMsgDatas.EP_FROM, EPMsgDatas.EP_BODY, EPMsgDatas.EP_TIME,
			EPMsgDatas.EP_READ };

	private static final Uri EP_URL = EPMsgDatas.CONTENT_URI;

	private EPMsgProviderHelper() {
	}

	public static int getUnreadCount(Context ctx) {
		int count = 0;
		if (ctx == null) {
			return count;
		}
		Cursor cur = ctx.getContentResolver().query(EP_URL, EP_COLUMNS, null,
				null, null);
		if (cur != null && cur.moveToFirst()) {
			do {
				int epread = cur
						.getInt(cur.getColumnIndex(EPMsgDatas.EP_READ));
				if (epread == 0) {
					// 0 unread
					count++;
				}
			} while (cur.moveToNext());
			cur.close();
		}
		return count;
	}

	public static List<EPMessageItem> getAllEPMsgs(Activity activity) {
		List<EPMessageItem> epitemlist = new ArrayList<EPMessageItem>();
		Cursor cur = activity
				.managedQuery(EP_URL, EP_COLUMNS, null, null, null);
		if (cur != null && cur.moveToFirst()) {
			int id;
			int epid;
			int eptype;
			int epprotocol;
			String epfrom;
			String epbody;
			String eptime;
			int epread;
			do {
				id = cur.getInt(cur.getColumnIndex(EPMsgDatas._ID));
				/* ep_id not used now, id is enough */
				epid = cur.getInt(cur.getColumnIndex(EPMsgDatas.EP_ID));
				eptype = cur.getInt(cur.getColumnIndex(EPMsgDatas.EP_TYPE));
				epprotocol = cur.getInt(cur
						.getColumnIndex(EPMsgDatas.EP_PROTOCOL));
				epfrom = cur.getString(cur.getColumnIndex(EPMsgDatas.EP_FROM));
				epbody = cur.getString(cur.getColumnIndex(EPMsgDatas.EP_BODY));
				eptime = cur.getString(cur.getColumnIndex(EPMsgDatas.EP_TIME));
				epread = cur.getInt(cur.getColumnIndex(EPMsgDatas.EP_READ));
				Log.d("tag", "id=" + id + " ep_id = " + epid + " ep_type = "
						+ eptype + " ep_protocol = " + epprotocol
						+ " ep_from = " + epfrom + " ep_body = " + epbody
						+ " ep_time = " + eptime + " ep_read = " + epread);
				EPMessageItem epmsgitem = new EPMessageItem();
				epmsgitem.setId(id);
				epmsgitem.setType(eptype);
				epmsgitem.setProtocol(epprotocol);
				epmsgitem.setPhone(epfrom);
				epmsgitem.setBody(epbody);
				epmsgitem.setTime(EPMsgDatas.formatStr2Date(eptime));
				epmsgitem.setUsrRead(epread);

				epitemlist.add(epmsgitem);
			} while (cur.moveToNext());
			cur.close();
		}
		Collections.reverse(epitemlist);
		return epitemlist;
	}

	public static void addEPMsgItem(EPMessageItem epitem, Context ctx) {
		ContentValues contentvalues = new ContentValues();
		// EPMsgDatas._ID is auto generated
		contentvalues.put(EPMsgDatas.EP_ID, 0);// ep_id not used now
		contentvalues.put(EPMsgDatas.EP_TYPE, epitem.getType());
		contentvalues.put(EPMsgDatas.EP_PROTOCOL, epitem.getProtocol());
		contentvalues.put(EPMsgDatas.EP_FROM, epitem.getPhone());
		contentvalues.put(EPMsgDatas.EP_BODY, epitem.getBody());

		String datestr = EPMsgDatas.formatDateStr(epitem.getTime());
		contentvalues.put(EPMsgDatas.EP_TIME, datestr);
		contentvalues.put(EPMsgDatas.EP_READ, epitem.getUsrReadInt());

		ctx.getContentResolver().insert(EPMsgDatas.CONTENT_URI, contentvalues);
	}

	public static void updateEPMsgItem(EPMessageItem epitem, Context ctx) {
		ContentValues contentvalues = new ContentValues();

		contentvalues.put(EPMsgDatas.EP_ID, 0);// ep_id not used now
		contentvalues.put(EPMsgDatas.EP_TYPE, epitem.getType());
		contentvalues.put(EPMsgDatas.EP_PROTOCOL, epitem.getProtocol());
		contentvalues.put(EPMsgDatas.EP_FROM, epitem.getPhone());
		contentvalues.put(EPMsgDatas.EP_BODY, epitem.getBody());

		String datestr = EPMsgDatas.formatDateStr(epitem.getTime());
		contentvalues.put(EPMsgDatas.EP_TIME, datestr);
		contentvalues.put(EPMsgDatas.EP_READ, epitem.getUsrReadInt());

		String selection = EPMsgDatas._ID + "= " + epitem.getId();
		updateValue(contentvalues, selection, null, ctx);
	}

	public static void deleteEPMsgItem(EPMessageItem epitem, Context ctx) {
		String selection = EPMsgDatas._ID + "= " + epitem.getId();
		deleteValue(selection, null, ctx);
	}

	public static void deleteAllEPMsgItems(Context ctx) {
		String selection = EPMsgDatas._ID + " > -1";
		deleteValue(selection, null, ctx);
	}

	private static void updateValue(ContentValues contentvalues,
			String selection, String[] selectionArgs, Context ctx) {
		ctx.getContentResolver().update(EPMsgDatas.CONTENT_URI, contentvalues,
				selection, selectionArgs);
	}

	private static void deleteValue(String selection, String[] selectionArgs,
			Context ctx) {
		ctx.getContentResolver().delete(EPMsgDatas.CONTENT_URI, selection,
				selectionArgs);
	}
}
