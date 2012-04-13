package com.yfvesh.tm.enterprisemsg.ttsmgr;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public final class TtsMgr implements TextToSpeech.OnInitListener, OnUtteranceCompletedListener{
	private TextToSpeech mTts;
	private static TtsMgr mTTSInstance;
	
	private boolean mbTtsInited = false;
	private boolean mbTtsLocaled = false;
	
	private Map<String,String> mParamMap = new HashMap<String,String>();
	private Locale mLocale = Locale.US;
	private TtsStatusListener mListner;
	private TtsMgr() {
	}
	
	public static TtsMgr  getInstance( ) {
		if( mTTSInstance == null ) {
			mTTSInstance = new TtsMgr();
		}
		return mTTSInstance;
	}
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			//create tts done
			mbTtsInited = true;
			setLocale( mLocale );
		}
	}
	
	public void  init(Context ctx,Locale locale,TtsStatusListener listner,String id) {
		mLocale = locale;
		mListner = listner;
		mParamMap.clear();
		mParamMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);
		if( mTts == null ) {
			mTts = new TextToSpeech(ctx,this);
		}
	}
	
	public void deInit() {
		if( mTts != null ) {
			mTts.stop();
			mTts.shutdown();
			mbTtsInited = false;
			mbTtsLocaled = false;
			mTts = null;
		}
		mListner = null;
	}
	
	public void releaseTts() {
		if( mTts != null ) {
			mTts.shutdown();
		}
	}
	public boolean ttsSpeak(String str) {
		boolean bdone = false;
		if( mbTtsLocaled ) {
			mTts.setOnUtteranceCompletedListener(this);
			int result = mTts.speak(str,TextToSpeech.QUEUE_FLUSH, (HashMap<String,String>)mParamMap);
			if(TextToSpeech.SUCCESS == result ) {
				bdone = true;
			}
		}
		return bdone;
	}
	public void ttsStop() {
		if( mbTtsLocaled ) {
			mTts.stop();
		}
	}
	
	public boolean isInited() {
		return mbTtsInited;
	}
	
	public boolean isLocaled() {
		return mbTtsLocaled;
	}
	
	public boolean setLocale(Locale locale) {
		boolean bdone = false;
		if( mbTtsInited ) {
			int result = mTts.setLanguage(locale);
			if( TextToSpeech.LANG_MISSING_DATA  == result ||
					TextToSpeech.LANG_NOT_SUPPORTED == result) {
				bdone = false;
			} else {
				bdone = true;
			}
		}
		mbTtsLocaled = bdone;
		return bdone;
	}

	public void setTtsListener(TtsStatusListener listner) {
		mListner = listner;
	}
	@Override
	public void onUtteranceCompleted(String arg0) {
		if( mListner != null) {
			mListner.onTtsDone(arg0);
		}
	}
}
