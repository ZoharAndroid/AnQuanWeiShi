package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zzh.phoneguard.service.BlacklistService;
import com.zzh.phoneguard.service.PhoneLocationChechService;
import com.zzh.phoneguard.utils.ServiceUtils;
import com.zzh.phoneguard.view.ItemSettingLayout;
import com.zzh.shoujiweishi.R;

public class SettingActivity extends Activity {

	private SharedPreferences sp;

	private ItemSettingLayout isl_updata;// 自动更新
	private ItemSettingLayout isl_lanjie;// 开机启动拦截
	private ItemSettingLayout isl_black;// 黑名单拦截
	private ItemSettingLayout isl_location;// 电话归属地
	private ItemSettingLayout isl_watchdog;// 看门狗服务
	private TextView tv_style;//归属地样式

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		initView();
		initData();
		initEvent();
	}

	private void initData() {
		// 读取sp中的值
		isl_updata.setClickStatus(sp.getBoolean(MyContasts.ISAUTOUPDATA, true));

		/**
		 * 是否开启启动拦截
		 */
		isl_lanjie.setClickStatus(sp.getBoolean(MyContasts.ISBOOTBLACKLIST,false));
		/**
		 * 判断黑名单服务开启了吗？
		 */
		if (ServiceUtils.isServiceRun(SettingActivity.this,
				BlacklistService.class)) {
			isl_black.setClickStatus(true);
		} else {
			isl_black.setClickStatus(false);
		}
		/**
		 * 归属地开启
		 */
		if(ServiceUtils.isServiceRun(SettingActivity.this, PhoneLocationChechService.class)){
			isl_black.setClickStatus(true);
		}else{
			isl_black.setClickStatus(false);
		}
		/**
		 * 归属地样式风格
		 */
		tv_style.setText(items[sp.getInt(MyContasts.PHONELOCATIONSTYLE, 0)]);
		
	}

	private void initEvent() {
		/**
		 * 开机自动启动
		 */
		isl_updata.setClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断当前选中的状态
				isl_updata.setClickStatus(!isl_updata.getClickStatus());
				// 将状态保存到sp文件中
				sp.edit()
						.putBoolean(MyContasts.ISAUTOUPDATA,
								isl_updata.getClickStatus()).commit();

			}
		});
		/**
		 * 开机启动拦截
		 */
		isl_lanjie.setClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断当前选中的状态
				isl_lanjie.setClickStatus(!isl_lanjie.getClickStatus());
				// 将状态保存到sp文件中
				sp.edit().putBoolean(MyContasts.ISBOOTBLACKLIST,isl_lanjie.getClickStatus()).commit();
				//保存了当前状态后，就开始接收开启广播状态
			}
		});
		/**
		 * 黑名单拦截
		 */
		isl_black.setClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isl_black.setClickStatus(!isl_black.getClickStatus());
				Intent blackServiceIntent = new Intent(SettingActivity.this, BlacklistService.class);
				// 判断服务是否正在运行
				if(ServiceUtils.isServiceRun(SettingActivity.this,
						BlacklistService.class)){
					// 服务已经开启，就开启这个服务
					stopService(blackServiceIntent);
				} else {
					// 服务已经开启，就停止这个服务
					startService(blackServiceIntent);
				}
			}

		});

		isl_location.setClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断当前选中的状态
				isl_location.setClickStatus(!isl_location.getClickStatus());
				if(ServiceUtils.isServiceRun(SettingActivity.this, PhoneLocationChechService.class)){
					Intent locationService = new Intent(SettingActivity.this , PhoneLocationChechService.class);
					stopService(locationService);
				}else {
					Intent locationService = new Intent(SettingActivity.this , PhoneLocationChechService.class);
					startService(locationService);
				}
			
			}
		});
		
		isl_watchdog.setClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isl_watchdog.setClickStatus(!isl_watchdog.getClickStatus());
				// 判断当前选中的状态
			}
		});

	}
	
	/**
	 * 点击事件：来电归属地样式风格选择
	 * @param view
	 */
	private String[] items = new String[]{"金属灰","苹果绿","天空蓝","果粒橙","透明白"};
	
	public void phoneLocationStyle(View view){
		AlertDialog.Builder styleDialog = new AlertDialog.Builder(this);
		styleDialog.setSingleChoiceItems(items, sp.getInt(MyContasts.PHONELOCATIONSTYLE, 0),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//储存用户选择的颜色
				sp.edit().putInt(MyContasts.PHONELOCATIONSTYLE, which).commit();
				//改变setting界面归属地风格
				tv_style.setText(items[which]);
				dialog.dismiss();
			}
		});
		
		styleDialog.show();
	}

	private void initView() {
		// 全屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_setting);

		// 初始化自定义view的控件
		isl_updata = (ItemSettingLayout) findViewById(R.id.isl_setting_update);
		isl_lanjie = (ItemSettingLayout) findViewById(R.id.isl_setting_lanjie);
		isl_black = (ItemSettingLayout) findViewById(R.id.isl_setting_black);
		isl_location = (ItemSettingLayout) findViewById(R.id.isl_setting_location);
		isl_watchdog = (ItemSettingLayout) findViewById(R.id.isl_setting_watchdog);
		
		tv_style = (TextView) findViewById(R.id.tv_item_setting_style);
	}

}
