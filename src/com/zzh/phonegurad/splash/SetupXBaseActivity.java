package com.zzh.phonegurad.splash;

import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * setupX界面跳转活动抽象类
 * @author Administrator
 *
 */
public abstract class SetupXBaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	/**
	 * 点击上一步要实现的事件
	 */
	public abstract void previousEvent();
	/**
	 * 点击下一步要实现的事件
	 */
	public abstract void nextEvent();
	
	/**
	 * 按键 点击:上一步
	 * @param view
	 */
	public void previousSetup(View view){
		//点击上一步要实现的事件
		previousEvent();
		//实现点击界面效果，一定要放在事件的后面，否则动画效果出不来
		overridePendingTransition(R.drawable.previous_in_setup, R.drawable.previous_out_setup);
	}
	
	/**
	 * 按键 点击:下一步
	 * @param view
	 */
	public void nextSetup(View view){
		//点击下一步要实现的事件
		nextEvent();
		//实现点击界面效果
		overridePendingTransition(R.drawable.next_in_setup, R.drawable.next_out_setup);
	}
	
	public void startActivityAndFinish(Class type){
		Intent intent = new Intent(SetupXBaseActivity.this, type);
		startActivity(intent);
		finish();
	}
	
}
