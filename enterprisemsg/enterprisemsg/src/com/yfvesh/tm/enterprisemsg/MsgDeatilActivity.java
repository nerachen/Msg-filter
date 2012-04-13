/**
 * 
 */
package com.yfvesh.tm.enterprisemsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yfvesh.tm.enterprisemsg.R;
import com.yfvesh.tm.enterprisemsg.ttsmgr.TtsMgr;
import com.yfvesh.tm.enterprisemsg.ttsmgr.TtsStatusListener;

/**
 * @author HCHEN8
 * 
 */
public class MsgDeatilActivity extends Activity implements OnClickListener,
		TtsStatusListener {

	public static final String EPMSG_ITEM = "enterprisemsgitem";
	public static final String EPMSG_ITEM_LIST = "enterprisemsgitems";
	public static final String EPMSG_INDEX = "enterprisemsgindex";
	public static final String EPMSG_PLAY_FLAG = "enterprisemsgplaying";

	/* buttons on the screen */
	private Button mBtnTtsPlay;
	private Button mBtnTtsStop;

	/* the message item passed from message list */
	private EPMessageItem mEPMsgItem;

	/* the list of EPMessageItem items */
	private List<EPMessageItem> mEPMsgItemList;

	/* the index for message item in the list */
	private int mEPMsgIndex = -1;

	/*
	 * the flag indicate whether the message is play in tts before enter this
	 * view
	 */
	private boolean mbMsgPlayInTts = false;

	/* the TTS engine manager,helps to play the text */
	private TtsMgr mTtsMgr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msgdetails);
		super.onCreate(savedInstanceState);

		Button btnback = (Button) this.findViewById(R.id.btnback);
		mBtnTtsPlay = (Button) this.findViewById(R.id.btnttsplay);
		mBtnTtsStop = (Button) this.findViewById(R.id.btnttsstop);
		ImageButton btndelete = (ImageButton) this.findViewById(R.id.btndel);
		btnback.setOnClickListener(this);
		mBtnTtsPlay.setOnClickListener(this);
		mBtnTtsStop.setOnClickListener(this);
		btndelete.setOnClickListener(this);

		getBundleInfo();
		initUiBaseOnMsg();
		mTtsMgr = TtsMgr.getInstance();
		if (!mTtsMgr.isInited()) {
			// only when not initialized, most time the ListView shall have been
			// initialized TTS
			mTtsMgr.init(this, Locale.US, this, EnterprisemsgActivity.TTS_ID);
		} else {
			mTtsMgr.setTtsListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnback: {
			goBack();
			break;
		}
		case R.id.btnttsplay: {
			boolean bresult = ttsSpeak();
			if (bresult) {
				mBtnTtsPlay.setVisibility(View.GONE);
				mBtnTtsStop.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.btnttsstop: {
			ttsStop();
			mBtnTtsPlay.setVisibility(View.VISIBLE);
			mBtnTtsStop.setVisibility(View.GONE);
			break;
		}
		case R.id.btndel: {
			showCustomDialog(PopUpDialog.SHOWDLG_REASON_DEL_CUR_QUERY);
			break;
		}
		default:
			break;
		}
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//	}

	@Override
	public void onPause() {
		mTtsMgr.ttsStop();
		super.onPause();
	}

//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}

	private void showCustomDialog(int reason) {
		if (PopUpDialog.SHOWDLG_REASON_DEL_CUR_QUERY == reason) {
			// show query
			Intent intent = new Intent();
			intent.setClass(this, PopUpDialog.class);
			intent.putExtra(PopUpDialog.DIALOG_REQ_REASON,
					PopUpDialog.SHOWDLG_REASON_DEL_CUR_QUERY);
			startActivityForResult(intent,
					PopUpDialog.SHOWDLG_REASON_DEL_CUR_QUERY);
		} 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (PopUpDialog.SHOWDLG_REASON_DEL_CUR_QUERY == requestCode) {
			if (RESULT_OK == resultCode) {
				ttsStop();
				// deleteMsgItem();
				setResult(EnterprisemsgActivity.CUSTOM_DLG_RESULT_DEL);
				this.finish();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public boolean ttsSpeak() {
		if (mEPMsgItem != null) {
			return mTtsMgr.ttsSpeak(mEPMsgItem.getBody());
		} else {
			return false;
		}
	}

	public void ttsStop() {
		mTtsMgr.ttsStop();
	}

	private void goBack() {
		this.finish();
	}

	@SuppressWarnings("unchecked")
	private void getBundleInfo() {
		final Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey(EPMSG_ITEM)) {
			mEPMsgItem = (EPMessageItem) bundle.getSerializable(EPMSG_ITEM);
		}
		if (bundle.containsKey(EPMSG_ITEM_LIST)) {
			mEPMsgItemList = (ArrayList<EPMessageItem>) bundle
					.getSerializable(EPMSG_ITEM_LIST);
		}
		if (bundle.containsKey(EPMSG_INDEX)) {
			mEPMsgIndex = bundle.getInt(EPMSG_INDEX);
		}
		if (bundle.containsKey(EPMSG_PLAY_FLAG)) {
			mbMsgPlayInTts = bundle.getBoolean(EPMSG_PLAY_FLAG);
		}
	}

	private void initUiBaseOnMsg() {
		if (mEPMsgItemList != null && mEPMsgIndex >= 0
				&& mEPMsgItemList.size() > mEPMsgIndex) {
			mEPMsgItem = mEPMsgItemList.get(mEPMsgIndex);
			TextView txtfrom = (TextView) this.findViewById(R.id.labelfrom);
			txtfrom.setText(mEPMsgItem.getPhone());
			TextView txttime = (TextView) this.findViewById(R.id.labeltime);
			txttime.setText(mEPMsgItem.getTime().toLocaleString());
			TextView txtbody = (TextView) this.findViewById(R.id.labelbody);
			txtbody.setText(mEPMsgItem.getBody());
		} else if (mEPMsgItem != null) {
			TextView txtfrom = (TextView) this.findViewById(R.id.labelfrom);
			txtfrom.setText(mEPMsgItem.getPhone());
			TextView txttime = (TextView) this.findViewById(R.id.labeltime);
			txttime.setText(mEPMsgItem.getTime().toLocaleString());
			TextView txtbody = (TextView) this.findViewById(R.id.labelbody);
			txtbody.setText(mEPMsgItem.getBody());
		}
		if (mbMsgPlayInTts) {
			mBtnTtsPlay.setVisibility(View.GONE);
			mBtnTtsStop.setVisibility(View.VISIBLE);
		} else {
			mBtnTtsPlay.setVisibility(View.VISIBLE);
			mBtnTtsStop.setVisibility(View.GONE);
		}
	}

	@Override
	public void onTtsDone(String id) {
		Log.d("tag", "onTtsDone:" + id);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBtnTtsPlay.setVisibility(View.VISIBLE);
				mBtnTtsStop.setVisibility(View.GONE);
			}
		});
	}
}
