package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zzh.shoujiweishi.R;

public class PhoneLostFindActivity extends Activity {

	private TextView tv_showSafeNumber;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	private void initData() {
		// 读取sp文件中是否设置了手机防盗
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		boolean isSetFlag = sp.getBoolean(MyContasts.ISSETPHONELOSTFIND, false);
		if (isSetFlag) {// 开启了手机防盗功能
			// 进入手机防盗界面
			initView();
			// 然后从sp中读取安全号码
			String safeNumber = sp.getString(MyContasts.SAFENUMBER, "");
			tv_showSafeNumber.setText(safeNumber);
		} else {
			// 进入设置的第一个界面
			loadSetup1View();// 加载第一个界面
		}
	}

	/**
	 * 重新开始设置向导
	 * 
	 * @param view
	 */
	public void tv__restartSet(View view) {
		// 清除手机防盗标志
		sp.edit().putBoolean(MyContasts.ISSETPHONELOSTFIND, false).commit();
		// 重新加载setup1界面
		loadSetup1View();
	}

	/**
	 * 加载第一个界面
	 */
	private void loadSetup1View() {
		Intent intent = new Intent(PhoneLostFindActivity.this,
				Setup1Activity.class);
		startActivity(intent);
		finish();
	}

	private void initView() {
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_phonelostfind);

		tv_showSafeNumber = (TextView) findViewById(R.id.tv_phonelostfind_showSafeNumber);
	}
}
