package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zzh.shoujiweishi.R;

public class AToolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_atools);

	}

	/**
	 * 电话归属地查询 按键
	 * @param view
	 */
	public void phoneLocationCheck(View view) {
		Intent phoneCheckIntent = new Intent(AToolsActivity.this, PhoneLocationCheckActivity.class);
		startActivity(phoneCheckIntent);
	}

	/**
	 * 短信备份按键
	 * @param view
	 */
	public void smsBackup(View view) {

	}

	/**
	 * 短信恢复按键
	 * @param view
	 */
	public void smsResume(View view) {

	}

	/**
	 * 程序锁按键
	 * @param view
	 */
	public void appLock(View view) {

	}
}
