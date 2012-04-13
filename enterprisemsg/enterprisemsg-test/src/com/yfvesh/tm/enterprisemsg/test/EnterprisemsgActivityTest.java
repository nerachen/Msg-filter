package com.yfvesh.tm.enterprisemsg.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.yfvesh.tm.enterprisemsg.EnterprisemsgActivity;
import com.yfvesh.tm.enterprisemsg.R;

public class EnterprisemsgActivityTest extends
		ActivityInstrumentationTestCase2<EnterprisemsgActivity> {

	private EnterprisemsgActivity mActivity;
	private Button mBtnClrAll;
	private Button mBtnBack;
	public EnterprisemsgActivityTest() {
		super("com.yfvesh.tm.enterprisemsg", EnterprisemsgActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mBtnClrAll = (Button)mActivity.findViewById(R.id.btnclearall);
		mBtnBack = (Button)mActivity.findViewById(R.id.btnback);
	}
	
	@Override
	protected void tearDown() throws Exception {
		mActivity = null;
		mBtnClrAll = null;
		mBtnBack = null;
		super.tearDown();
	}
	
	public void testPreCoditions() {
		assertNotNull( mBtnClrAll );
		assertNotNull( mBtnBack );
		assertTrue( mBtnClrAll.isEnabled() );
		assertTrue( mBtnBack.isEnabled() );
	}
}
