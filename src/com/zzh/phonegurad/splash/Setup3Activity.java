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
		// 先读取sp中是否包含了安全号码
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		String number = sp.getString(MyContasts.SAFENUMBER, "");
		if (!TextUtils.isEmpty(number)) {// 如果不为空
			// 设置到界面中
			et_safeNumber.setText(number);
		} else {
			// 不进行操作
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
		if (TextUtils.isEmpty(safeNumber)) {
			ShowToast.showToast(Setup3Activity.this, "安全码不能为空", 0);
			return;
		}
		// 输入了安全号码，就把安全号码进行储存
		sp.edit().putString(MyContasts.SAFENUMBER, safeNumber).commit();

		startActivityAndFinish(Setup4Activity.class);
	}

	/**
	 * 按键选择安全号码：读取手机联系人信息，获取号码
	 * 
	 * @param view
	 */
	public void selectSafeNumber(View view) {
		// 打开一个界面：用于列出手机联系人的信息
		Intent intent = new Intent(Setup3Activity.this,
				ListContactsActvity.class);
		// 获取打开界面返回的值
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			et_safeNumber.setText(data.getStringExtra("phoneNumber"));// 获取上一个界面返回来的手机号码
		}
	}
}
