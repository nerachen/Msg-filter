package com.yfvesh.tm.enterprisemsg;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yfvesh.tm.enterprisemsg.R;

public class PopUpDialog extends Activity implements OnClickListener {

	/* request code for launch the popup */
	public static final int SHOWDLG_REASON_CLEAR_QUERY = 101;
	public static final int SHOWDLG_REASON_DEL_CUR_QUERY = 102;
	
	/* string tag in bundle to launch the popup */
	public static final String DIALOG_REQ_REASON = "dlgreason";
	
	/* button to cancel the login progress */
	private Button mBtnCancel;

	/* button to cancel the login progress */
	private Button mBtnConfirm;

	/* the indicator image view */
	private ImageView mImgView;
	/* the text info for status */
	private TextView mTxtView;

	/* handler to help handle status */
	private MsgHandler mMsgHandler;

	/* message indicate the network result */
	public static final int MSG_FINSIH_CANCELED = 102;
	public static final int MSG_FINSIH_DONE = 104;

	private int mDlgReqCode = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		mMsgHandler = new MsgHandler();

		mBtnCancel = (Button) findViewById(R.id.btncancel);
		mBtnCancel.setOnClickListener(this);
		mBtnConfirm = (Button) findViewById(R.id.btnconfirm);
		mBtnConfirm.setOnClickListener(this);
		mImgView = (ImageView) findViewById(R.id.imgquery);
		mTxtView = (TextView) findViewById(R.id.textqueryinfo);

		getBundleInfo();
		initUiBaseonReq();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btncancel: {
			mMsgHandler.sendEmptyMessage(MSG_FINSIH_CANCELED);
			break;
		}
		case R.id.btnconfirm: {
			mMsgHandler.sendEmptyMessage(MSG_FINSIH_DONE);
			break;
		}
		default:
			break;
		}
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//	}

	/* get the reason for launch dlg */
	private void getBundleInfo() {
		final Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey(DIALOG_REQ_REASON)) {
			mDlgReqCode = bundle
					.getInt(DIALOG_REQ_REASON);
		}
	}

	/* update UI based on the request type */
	private void initUiBaseonReq() {
		if (mDlgReqCode == SHOWDLG_REASON_CLEAR_QUERY) {
			// set UI for clear request
			mImgView.setImageResource(R.drawable.icon_sure);
			mTxtView.setText(R.string.txt_delete_all_confirm);
			mBtnConfirm.setVisibility(View.VISIBLE);
			mBtnCancel.setVisibility(View.VISIBLE);
		} else if (mDlgReqCode == SHOWDLG_REASON_DEL_CUR_QUERY) {
			// set UI for clear request
			mImgView.setImageResource(R.drawable.icon_sure);
			mTxtView.setText(R.string.txt_delete_one_confirm);
			mBtnConfirm.setVisibility(View.VISIBLE);
			mBtnCancel.setVisibility(View.VISIBLE);
		}
	}
	
	private class MsgHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FINSIH_CANCELED: {
				setResult(RESULT_CANCELED);
				finish();
				break;
			}
			case MSG_FINSIH_DONE: {
				setResult(RESULT_OK);
				finish();
				break;
			}
			default:
				break;
			}
		}
	}
}
