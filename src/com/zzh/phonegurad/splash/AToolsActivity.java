package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zzh.phoneguard.utils.SMSUtils;
import com.zzh.phoneguard.utils.ShowToast;
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
		//开始备份短信
		//怎么备份短信?1.读取短信。2、写入本地(xml格式)	
		final ProgressDialog pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置样式
		pb.show();
		new Thread(){
			public void run() {
				SMSUtils.SMSBackup(AToolsActivity.this, pb);
			};
		}.start();
	}

	/**
	 * 短信恢复按键
	 * @param view
	 */
	public void smsResume(View view) {
		//读取本地的xml文件,也就是解析xml文件
		//存放到手机短信中
		final ProgressDialog pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置样式
		pb.show();
		new Thread(){
			public void run() {
				SMSUtils.SMSResume(AToolsActivity.this, pb);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						pb.dismiss();
					}
				});
			};
		}.start();
	}

	/**
	 * 程序锁按键
	 * @param view
	 */
	public void appLock(View view) {

	}
}
