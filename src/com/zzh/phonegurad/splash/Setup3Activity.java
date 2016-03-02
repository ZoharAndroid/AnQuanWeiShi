package com.zzh.phonegurad.splash;

import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class Setup3Activity extends SetupXBaseActivity {
	
	private EditText et_safeNumber;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}


	private void initData() {
		//先读取sp中是否包含了安全号码
		sp=getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		String number = sp.getString(MyContasts.SAFENUMBER, "");
		if(!TextUtils.isEmpty(number)){//如果不为空
			//设置到界面中
			et_safeNumber.setText(number);
		}else{
			//不进行操作
		}
	}


	private void initView() {
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activiy_setup3);
		
		et_safeNumber = (EditText) findViewById(R.id.et_setup3_safeNumber);
	}
	
	@Override
	public void previousEvent() {
		startActivityAndFinish(Setup2Activity.class);
	}

	@Override
	public void nextEvent() {
		String safeNumber = et_safeNumber.getText().toString().trim();
		if(TextUtils.isEmpty(safeNumber)){
			ShowToast.showToast(Setup3Activity.this, "安全码不能为空", 0);
			return;
		}
		//输入了安全号码，就把安全号码进行储存
		sp.edit().putString(MyContasts.SAFENUMBER, safeNumber).commit();
		
		startActivityAndFinish(Setup4Activity.class);
	}
}
