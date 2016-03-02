package com.zzh.phonegurad.splash;

import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PhoneLostFindActivity extends Activity {
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//读取sp文件中是否设置了手机防盗
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		boolean isSetFlag = sp.getBoolean(MyContasts.ISSETPHONELOSTFIND, false);
		if(isSetFlag){
			initView();
			//进入设置的第一个界面
		}else{
			//进入手机防盗界面
			loadSetup1View();//加载第一个界面
		}
	}

	/**
	 * 加载第一个界面
	 */
	private void loadSetup1View() {
		Intent intent =  new Intent(PhoneLostFindActivity.this, Setup1Activity.class);
		startActivity(intent);
	}

	private void initView() {
		setContentView(R.layout.activity_phonelostfind);
		
	}
}
