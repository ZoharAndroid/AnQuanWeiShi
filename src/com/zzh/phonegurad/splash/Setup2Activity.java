package com.zzh.phonegurad.splash;

import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Setup2Activity extends SetupXBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activiy_setup2);
	}

	@Override
	public void previousEvent() {
		startActivityAndFinish(Setup1Activity.class);
	}

	@Override
	public void nextEvent() {
		startActivityAndFinish(Setup3Activity.class);
		
	}
	
	
}
