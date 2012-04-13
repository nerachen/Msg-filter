package com.yfvesh.tm.enterprisemsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yfvesh.tm.enterprisemsg.R;
import com.yfvesh.tm.enterprisemsg.epmsgprovider.EPMsgProviderHelper;
import com.yfvesh.tm.enterprisemsg.ttsmgr.TtsMgr;
import com.yfvesh.tm.enterprisemsg.ttsmgr.TtsStatusListener;

public class EnterprisemsgActivity extends Activity implements OnClickListener {
	/* request code for show message details */
	public static final int SHOWDLG_REASON_MSG_DETAIL = 201;
	/* request code for show delete message query */
	public static final int CUSTOM_DLG_RESULT_DEL = 301;
	
	/* the tag for tts play */
	public static final String TTS_ID = "enterprisemsgactivity";

	/* message send when new message arrive */
	public static final int MSG_NEW_EPMSG_UPDATED = 401;

	/* the filter for new enterprise message */
	public static final String MSG_BROADCAST_NEW = "com.yfvesh.tm.epmsg.new";
	/* the number indicates how many new message arrive */
	public static final String MSG_BROADCAST_NEW_COUNT = "newmsgnumber";

	/* the items and the adapter for message list view */
	private List<EPMessageItem> mEPMsgItemList = new ArrayList<EPMessageItem>();
	private MyMsgListAdapter mEPMsgListAdapter;

	/* the TTS engine manager,helps to play the text */
	private TtsMgr mTtsMgr;

	/* the index for shown message */
	private int mPosIndex = -1;

	/* the index for shown message */
	private int mPlayIndex = -1;

	/* the receiver to detect new message arrived */
	private EPMsgUpdateReceiver mEPMsgUpdateRcvr;

	/* the message handler help to handle events */
	private MsgHandler mMsgHandle = new MsgHandler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);

		startSmsDetectService();

		ListView epmsglistview = (ListView) this.findViewById(R.id.msglist);
		Button btnback = (Button) this.findViewById(R.id.btnback);
		Button btnclr = (Button) this.findViewById(R.id.btnclearall);
		btnback.setOnClickListener(this);
		btnclr.setOnClickListener(this);
		
		/* bring the title image foreground for the shadow effect */
		View linearLayout1 = findViewById(R.id.linearLayout1);
		linearLayout1.bringToFront();

		mEPMsgListAdapter = new MyMsgListAdapter(this, mEPMsgItemList);
		epmsglistview.setAdapter(mEPMsgListAdapter);

		mTtsMgr = TtsMgr.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
		initEPMsgListView();
		if (!mTtsMgr.isInited()) {
			mTtsMgr.init(this, Locale.US, null, TTS_ID);
		}
		registerMsgUpdateRcvr();
	}

	@Override
	public void onPause() {
		unregisterMsgUpdateRcvr();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mTtsMgr.isInited()) {
			mTtsMgr.deInit();
		}
		mTtsMgr.releaseTts();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnback:
			goBack();
			break;
		case R.id.btnclearall:
			if (mEPMsgListAdapter.getCount() > 0) {
				showCustomDialog(PopUpDialog.SHOWDLG_REASON_CLEAR_QUERY);
			}
			break;
		default:
			break;
		}
	}

	/* codes marked off for local test of enterprise messages */
//	private void initEPMsgForTest() {
//		mEPMsgItemList.add(new EPMessageItem("100861", "hello I am Jack !",
//				Calendar.getInstance().getTime(), false));
//		mEPMsgItemList.add(new EPMessageItem("100862",
//				"hi I am calling you this afternoon!", Calendar.getInstance()
//						.getTime(), true));
//		mEPMsgItemList
//				.add(new EPMessageItem(
//						"100863",
//						"hello performing com.yfvesh.tm.enterprisemsg.EnterprisemsgActivity activity launch \n performing com.yfvesh.tm.enterprisemsg.EnterprisemsgActivity activity launch",
//						Calendar.getInstance().getTime(), true));
//		mEPMsgItemList.add(new EPMessageItem("100864",
//				"wow I am calling you this afternoon! \n"
//						+ "adb is running normally \n"
//						+ "Application already deployed. No need to reinstall."
//						+ "Starting activity" + "Android Launch!"
//						+ "Installing enterprisemsg.apk..." + "Success!"
//						+ "Uploading enterprisemsg.apk onto device"
//						+ "Starting activity" + "Android Launch!"
//						+ "Installing enterprisemsg.apk..." + "Success!"
//						+ "Uploading enterprisemsg.apk onto device"
//						+ "Starting activity" + "Android Launch!"
//						+ "Installing enterprisemsg.apk..." + "Success!"
//						+ "Uploading enterprisemsg.apk onto device"
//						+ "Application already deployed. No need to reinstall."
//						+ "Starting activity" + "Android Launch!"
//						+ "Installing enterprisemsg.apk..." + "Success!"
//						+ "Uploading enterprisemsg.apk onto device"
//						+ "Starting activity" + "Android Launch!"
//						+ "Installing enterprisemsg.apk..." + "Success!"
//						+ "Uploading enterprisemsg.apk onto device"
//						+ "Starting activity" + "Android Launch!"
//						+ "Installing enterprisemsg.apk..." + "Success!"
//						+ "Uploading enterprisemsg.apk onto device"
//						+ "Uploading enterprisemsg.apk onto device", Calendar
//						.getInstance().getTime(), true));
//		mEPMsgItemList.add(new EPMessageItem("100865", "hello I am Jack !",
//				Calendar.getInstance().getTime(), false));
//		mEPMsgItemList.add(new EPMessageItem("100866", "hello I am Jack !",
//				Calendar.getInstance().getTime(), false));
//		mEPMsgItemList.add(new EPMessageItem("100867", "hello I am Jack !",
//				Calendar.getInstance().getTime(), false));
//	}

	private void initEPMsgFromProvider() {
		mEPMsgItemList.clear();
		mEPMsgItemList.addAll(EPMsgProviderHelper.getAllEPMsgs(this));
	}

	private void initEPMsgListView() {
		// initEPMsgForTest();
		initEPMsgFromProvider();
		mEPMsgListAdapter.notifyDataSetChanged();
	}

	private void goBack() {
		this.finish();
	}

	private boolean ttsSpeak(EPMessageItem item) {
		return mTtsMgr.ttsSpeak(item.getBody());
	}

	private void ttsStop() {
		mTtsMgr.ttsStop();
		mPlayIndex = -1;
	}

	private void showEPMsgDeatil(EPMessageItem item, int pos) {
		Intent intent = new Intent();
		intent.setClass(this, MsgDeatilActivity.class);
		// intent.putExtra(MsgDeatilActivity.EPMSG_ITEM, item);
		intent.putExtra(MsgDeatilActivity.EPMSG_ITEM_LIST, (ArrayList<EPMessageItem>)mEPMsgItemList);
		mPosIndex = pos;
		if (mPlayIndex != -1 && mPlayIndex == mPosIndex) {
			// currently is playing in tts
			intent.putExtra(MsgDeatilActivity.EPMSG_PLAY_FLAG, true);
		} else {
			ttsStop();
		}
		intent.putExtra(MsgDeatilActivity.EPMSG_INDEX, pos);
		startActivityForResult(intent, SHOWDLG_REASON_MSG_DETAIL);
	}

	private void showCustomDialog(int reason) {
		if (PopUpDialog.SHOWDLG_REASON_CLEAR_QUERY == reason) {
			// show query
			Intent intent = new Intent();
			intent.setClass(this, PopUpDialog.class);
			intent.putExtra(PopUpDialog.DIALOG_REQ_REASON,
					PopUpDialog.SHOWDLG_REASON_CLEAR_QUERY);
			startActivityForResult(intent,
					PopUpDialog.SHOWDLG_REASON_CLEAR_QUERY);
		} 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (PopUpDialog.SHOWDLG_REASON_CLEAR_QUERY == requestCode) {
			if (RESULT_OK == resultCode) {
				deleteAllMsg();
				mEPMsgListAdapter.notifyDataSetChanged();
			}
		} else if (SHOWDLG_REASON_MSG_DETAIL == requestCode) {
			/* after back from msg details, play shall be false */
			mPlayIndex = -1;
			if (RESULT_CANCELED == resultCode) {
				updateReadFlag(mPosIndex);
				mEPMsgListAdapter.notifyDataSetChanged();
			} else if (RESULT_OK == resultCode) {
				updateReadFlag(mPosIndex);
				mEPMsgListAdapter.notifyDataSetChanged();
			} else if (CUSTOM_DLG_RESULT_DEL == resultCode) {
				deleteOneMsg(mPosIndex);
				mEPMsgListAdapter.notifyDataSetChanged();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void deleteAllMsg() {
		mEPMsgItemList.clear();
		// delete the items from database
		EPMsgProviderHelper.deleteAllEPMsgItems(this);
	}

	private void updateReadFlag(int pos) {
		if (mEPMsgItemList.size() > pos && pos >= 0) {
			mEPMsgItemList.get(pos).setUsrRead(true);
			// mEPMsgListAdapter.notifyDataSetChanged();
			EPMsgProviderHelper.updateEPMsgItem(mEPMsgItemList.get(pos), this);
		}
	}

	private void deleteOneMsg(int pos) {
		if (mEPMsgItemList.size() > pos && pos >= 0) {
			EPMessageItem epitem = mEPMsgItemList.remove(pos);
			// delete the item from database
			EPMsgProviderHelper.deleteEPMsgItem(epitem, this);
		}
	}

	private class MyMsgListAdapter extends ArrayAdapter<EPMessageItem> {
		private List<EPMessageItem> mItems;

		public MyMsgListAdapter(Context context, List<EPMessageItem> items) {
			super(context, 0, items);
			mItems = items;
		}

		@Override
		public View getView(int position, View convertview, ViewGroup parent) {
			final EPMessageItem info = mItems.get(position);
			final int index = position;
			View cvrtview = convertview;
			if (cvrtview == null) {
				final LayoutInflater inflater = getLayoutInflater();
				cvrtview = inflater.inflate(R.layout.enterprise_msg_item,
						parent, false);
			}
			final View msgview = cvrtview.findViewById(R.id.msgitemview);
			msgview.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showEPMsgDeatil(info, index);
				}
			});
			final ImageView imgdescrip = (ImageView) cvrtview
					.findViewById(R.id.descripimg);
			if (info.getUsrRead()) {
				imgdescrip.setImageResource(R.drawable.icon_old_message);
				msgview.setBackgroundResource(R.drawable.msg_item_selector_old);
			} else {
				imgdescrip.setImageResource(R.drawable.icon_new_message);
				msgview.setBackgroundResource(R.drawable.msg_item_selector_new);
			}
			final TextView textfromview = (TextView) cvrtview
					.findViewById(R.id.labelfrom);
			textfromview.setText(info.getPhone());
			final TextView textbodyview = (TextView) cvrtview
					.findViewById(R.id.labelbody);
			textbodyview.setText(info.getBody());
			if (!info.getUsrRead()) {
				textfromview.setTypeface(Typeface
						.defaultFromStyle(Typeface.BOLD));
				textbodyview.setTypeface(Typeface
						.defaultFromStyle(Typeface.BOLD));
			} else {
				textfromview.setTypeface(Typeface
						.defaultFromStyle(Typeface.NORMAL));
				textbodyview.setTypeface(Typeface
						.defaultFromStyle(Typeface.NORMAL));
			}
			final Button btnttsplay = (Button) cvrtview
					.findViewById(R.id.btnttsplay);
			final Button btnttsstop = (Button) cvrtview
					.findViewById(R.id.btnttsstop);
			final TtsStatusListener ttsstatuslsnr = new TtsStatusListener() {
				public void onTtsDone(String id) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							btnttsplay.setVisibility(View.VISIBLE);
							btnttsstop.setVisibility(View.GONE);
						}
					});
					if (mPlayIndex == index) {
						mPlayIndex = -1;
					}
				}
			};
			final View.OnClickListener onclickplaylsnr = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateReadFlag(index);
					ttsStop();
					if(ttsSpeak(info)) {
						btnttsstop.setVisibility(View.VISIBLE);
						btnttsplay.setVisibility(View.GONE);
						/* set tts status listener */
						mTtsMgr.setTtsListener(ttsstatuslsnr);
						mPlayIndex = index;
					}
				}
			};
			final View.OnClickListener onclickstoplsnr = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ttsStop();
					btnttsplay.setVisibility(View.VISIBLE);
					btnttsstop.setVisibility(View.GONE);
				}
			};
			if (mPlayIndex == index) {
				btnttsplay.setVisibility(View.GONE);
				btnttsstop.setVisibility(View.VISIBLE);
				mTtsMgr.setTtsListener(ttsstatuslsnr);
			} else {
				btnttsplay.setVisibility(View.VISIBLE);
				btnttsstop.setVisibility(View.GONE);
			}
			btnttsplay.setOnClickListener(onclickplaylsnr);
			btnttsstop.setOnClickListener(onclickstoplsnr);
			return cvrtview;
		}
	}

	private void startSmsDetectService() {
		Intent i = new Intent("com.yfvesh.tm.smsobserver.TMBootService");
		this.startService(i);
	}

	private void registerMsgUpdateRcvr() {
		if (mEPMsgUpdateRcvr == null) {
			mEPMsgUpdateRcvr = new EPMsgUpdateReceiver();
		}
		IntentFilter intentFilter = new IntentFilter(MSG_BROADCAST_NEW);
		registerReceiver(mEPMsgUpdateRcvr, intentFilter);
	}

	private void unregisterMsgUpdateRcvr() {
		if (mEPMsgUpdateRcvr != null) {
			unregisterReceiver(mEPMsgUpdateRcvr);
		}
	}

	private void updateMessageList(int count) {
		initEPMsgFromProvider();
		if (count > 0 && mPlayIndex >= 0) {
			mPlayIndex += count;
		}
		mEPMsgListAdapter.notifyDataSetChanged();
	}

	private class EPMsgUpdateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MSG_BROADCAST_NEW)) {
				Log.d("tag", "EPMsgUpdateReceiver ");
				Message message = new Message();
				message.what = MSG_NEW_EPMSG_UPDATED;
				if (intent.getExtras().containsKey(MSG_BROADCAST_NEW_COUNT)) {
					message.arg1 = intent.getExtras().getInt(
							MSG_BROADCAST_NEW_COUNT);
				} else {
					message.arg1 = -1;
				}
				mMsgHandle.sendMessage(message);
			}
		}
	}

	private class MsgHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_NEW_EPMSG_UPDATED: {
				// do the update here
				int count = msg.arg1;
				updateMessageList(count);
				break;
			}
			default:
				break;
			}
		}
	}
}
