package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.zzh.phoneguard.service.BlacklistService;
import com.zzh.phoneguard.utils.LogUtil;
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
		// 判断服务开启了吗？
		if (ServiceUtils.isServiceRun(SettingActivity.this,
				BlacklistService.class)) {
			isl_black.setClickStatus(true);
		} else {
			isl_black.setClickStatus(false);
		}
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
				System.out.println(""+isl_lanjie.getClickStatus());
				// 将状态保存到sp文件中
				sp.edit().putBoolean(MyContasts.ISBOOTBLACKLIST,isl_lanjie.getClickStatus()).commit();
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
	}

}
