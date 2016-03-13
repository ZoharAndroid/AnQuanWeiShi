package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.zzh.phoneguard.service.LockClearMemService;
import com.zzh.phoneguard.utils.ServiceUtils;
import com.zzh.shoujiweishi.R;

public class TaskSettingActivity extends Activity {

	private RelativeLayout rl_showtask;
	private RelativeLayout rl_cleartask;
	private CheckBox cb_hide;
	private CheckBox cb_clear;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		cb_hide.setChecked(sp.getBoolean(MyContasts.ISHIDESYSTEMTASK, false));
		if(ServiceUtils.isServiceRun(TaskSettingActivity.this, LockClearMemService.class)){
			//如果服务正在运行
			cb_clear.setChecked(true);
		}else{
			cb_clear.setChecked(false);
		}
	}
	
	/**
	 * 初始化事件
	 */
	private void initEvent() {

		/**
		 * 隐藏系统进程
		 */
		rl_showtask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cb_hide.setChecked(!cb_hide.isChecked());
				
				sp.edit().putBoolean(MyContasts.ISHIDESYSTEMTASK, cb_hide.isChecked())
				.commit();
			}
		});
		/**
		 * 锁屏时清理进程
		 */
		rl_cleartask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cb_clear.setChecked(!cb_clear.isChecked());
System.out.println(cb_clear.isChecked());
				//判断是否开启该服务，注册广播，开启服务，如果用户选择了，就开启
				Intent intent = new Intent(TaskSettingActivity.this,LockClearMemService.class);
				if(cb_clear.isChecked()){
					//如果开启了
					startService(intent);
				}else{
					stopService(intent);
				}
			}
		});

		// cb_hide.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// cb_hide.setChecked(isChecked);
		// }
		// });
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.taskmanage_setting);

		rl_showtask = (RelativeLayout) findViewById(R.id.rl_taskmanage_showtask);
		rl_cleartask = (RelativeLayout) findViewById(R.id.rl_taskmanage_cleartask);

		cb_hide = (CheckBox) findViewById(R.id.cb_tasksetting_hide);
		cb_clear = (CheckBox) findViewById(R.id.cb_tasksetting_clear);

		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

}
