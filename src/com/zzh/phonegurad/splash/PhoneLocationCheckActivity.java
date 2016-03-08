package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.zzh.phoneguard.dao.PhoneLocationDB;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class PhoneLocationCheckActivity extends Activity {

	private EditText et_inputPhone;
	private TextView tv_showPhoneLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
	}

	private void initEvent() {
		et_inputPhone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				queryPhoneLocation();
				
			}
		});
	}

	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.phonecheck_atools_activity);

		et_inputPhone = (EditText) findViewById(R.id.et_inputPhoneChech_phoneloactioncheck);
		tv_showPhoneLocation = (TextView) findViewById(R.id.tv_showPhoneLocation_phoneloactioncheck);

	}

	/**
	 * 按键：查询
	 * 
	 * @param view
	 */
	public void queryPhoneLocation(View view) {
		queryPhoneLocation();
	}
	
	public void queryPhoneLocation() {
		String number = et_inputPhone.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			/**
			 * 输入框抖动效果
			 */
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_inputPhone.startAnimation(shake);

			ShowToast
					.showToast(PhoneLocationCheckActivity.this, "查询的号码不能为空", 0);
			return;
		}
		// 从数据库中查询该号码的归属地
		String address = PhoneLocationDB.locationChech(
				PhoneLocationCheckActivity.this, number);
		tv_showPhoneLocation.setText(address);

	}
}
