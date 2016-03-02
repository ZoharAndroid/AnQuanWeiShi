package com.zzh.phonegurad.splash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class Setup4Activity extends SetupXBaseActivity {
	
	private CheckBox cb_openProtect;
	
	private TextView tv_openProtect;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		//获取checkbox的判断状态
		cb_openProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//如果勾选了，则储存当前勾选状态
					sp.edit().putBoolean(MyContasts.ISCHECKBOXOPEN, isChecked).commit();
					tv_openProtect.setText("防盗保护已开启");
				}else{
					sp.edit().putBoolean(MyContasts.ISCHECKBOXOPEN, isChecked).commit();
					tv_openProtect.setText("防盗保护未开启");
				}
			}
		});
	}

	private void initData() {
		//读取sp中的checkbox勾选状态
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		boolean isChecked = sp.getBoolean(MyContasts.ISCHECKBOXOPEN, false);
		if(isChecked){
			cb_openProtect.setChecked(true);
			tv_openProtect.setText("防盗保护已开启");
		}else{
			cb_openProtect.setChecked(false);
			tv_openProtect.setText("防盗保护未开启");
		}
		
	}

	private void initView() {
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activiy_setup4);
		
		cb_openProtect = (CheckBox) findViewById(R.id.cb_setup4_openProtect);
		tv_openProtect = (TextView) findViewById(R.id.tv_setup4_openProtect);
		
	}

	@Override
	public void previousEvent() {
		startActivityAndFinish(Setup3Activity.class);
	}

	@Override
	public void nextEvent() {
		if(!sp.getBoolean(MyContasts.ISCHECKBOXOPEN, false)){
			//没有勾选
			ShowToast.showToast(Setup4Activity.this, "请勾选开启手机防盗功能", 0);
			return;
		}
		//保存防盗开启值
		sp.edit().putBoolean(MyContasts.ISSETPHONELOSTFIND, true).commit();
		startActivityAndFinish(PhoneLostFindActivity.class);
	}
	
	
}
