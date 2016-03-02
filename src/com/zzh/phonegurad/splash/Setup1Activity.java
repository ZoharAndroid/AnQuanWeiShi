package com.zzh.phonegurad.splash;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.zzh.shoujiweishi.R;

public class Setup1Activity extends SetupXBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activiy_setup1);
	}
	
	

	@Override
	public void previousEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextEvent() {
		startActivityAndFinish(Setup2Activity.class);
	}
}
