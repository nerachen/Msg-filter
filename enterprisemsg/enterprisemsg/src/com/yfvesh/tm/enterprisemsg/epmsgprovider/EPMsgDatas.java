package com.yfvesh.tm.enterprisemsg.epmsgprovider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public final class EPMsgDatas implements BaseColumns {

	public static final Uri CONTENT_URI = 
		Uri.parse("content://"+EPMsgContentProvider.AUTHORITY+"/epmsgdatas");
	public static final String CONTENT_TYPE_ITEMS = 
		"vnd.android.cursor.dir/vnd.yfveshtm.epmsgdatas";
	public static final String CONTENT_TYPE_ITEM = 
		"vnd.android.cursor.item/vnd.yfveshtm.epmsgdata";
	
	public static final String EP_ID = "ep_id";
	public static final String EP_TYPE = "ep_type";
	public static final String EP_PROTOCOL = "ep_protocol";
	public static final String EP_FROM = "ep_from";
	public static final String EP_BODY = "ep_body";
	public static final String EP_TIME = "ep_time";
	public static final String EP_READ = "ep_read";
	
	private EPMsgDatas() {
	}
	
	public static String formatDateStr(Date date) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return dateformat.format(date);
	}
	
	public static Date formatStr2Date(String str) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = Calendar.getInstance().getTime();
		try {
			date = dateformat.parse(str);
		} catch (ParseException e) {
		}
		return date;
	}
	
	public static void checkEPMsgValues(ContentValues values) {
		if(!values.containsKey(EPMsgDatas.EP_ID)) {
			values.put(EPMsgDatas.EP_ID, -1);
		}
		
		if(!values.containsKey(EPMsgDatas.EP_TYPE)) {
			values.put(EPMsgDatas.EP_TYPE, 0);
		}
		
		if(!values.containsKey(EPMsgDatas.EP_PROTOCOL)) {
			values.put(EPMsgDatas.EP_PROTOCOL, -1);
		}
		
		if(!values.containsKey(EPMsgDatas.EP_FROM)) {
			values.put(EPMsgDatas.EP_FROM, "");
		}
		
		if(!values.containsKey(EPMsgDatas.EP_BODY)) {
			values.put(EPMsgDatas.EP_BODY, "");
		}
		
		if(!values.containsKey(EPMsgDatas.EP_TIME)) {
			values.put(EPMsgDatas.EP_TIME, 0);
		}
		
		if(!values.containsKey(EPMsgDatas.EP_READ)) {
			values.put(EPMsgDatas.EP_READ, 0);
		}
	}
}
