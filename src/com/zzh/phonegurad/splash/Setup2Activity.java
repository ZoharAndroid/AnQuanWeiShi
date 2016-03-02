package com.zzh.phonegurad.splash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class Setup2Activity extends SetupXBaseActivity {
	
	private ImageView iv_lockIcon;
	
	private SharedPreferences sp;
	
	private TelephonyManager telephone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	
	private void initData() {
		//判断sp中是否有值
		if(isSIMCardBind()){
			iv_lockIcon.setImageResource(R.drawable.lock);
		}else{
			iv_lockIcon.setImageResource(R.drawable.unlock);
		}
	}
	
	/**
	 * 按键：绑定/解绑SIM卡
	 * @param view
	 */
	public void bindSIMCard(View view){
		//判断当前是否绑定
		//如果没有绑定，然后就进行绑定并存储SIM卡
		//如果绑定了，就解绑，清空sp中的数据
		if(isSIMCardBind()){
			//是，绑定了，清空sp中的数据
			sp.edit().putString(MyContasts.BINDSIMCARD, "").commit();
			//设置解锁图片
			iv_lockIcon.setImageResource(R.drawable.unlock);
		}else{
			//否则，没有绑定，sp中BindSIMCard就为空，然后就进行绑定并存储SIM卡
			//获取SIM值
			String simNumber = telephone.getSimSerialNumber();
			//储存SIM值到sp中
			sp.edit().putString(MyContasts.BINDSIMCARD, simNumber).commit();
			//设置图标
			iv_lockIcon.setImageResource(R.drawable.lock);
		}
	}
	/**
	 * 判断绑定结果
	 * return true:绑定了SIM卡;false：没有绑定
	 */
	private boolean isSIMCardBind(){
		String result;
		//读取sp中的数据
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		result = sp.getString(MyContasts.BINDSIMCARD, "");
		if(TextUtils.isEmpty(result)){//是为空，说明没有绑定sim卡
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activiy_setup2);
		
		iv_lockIcon = (ImageView) findViewById(R.id.iv_setup2_lockIcon);
		
		telephone = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	}

	@Override
	public void previousEvent() {
		startActivityAndFinish(Setup1Activity.class);
	}

	@Override
	public void nextEvent() {
		if(!isSIMCardBind()){//没有绑定
			ShowToast.showToast(Setup2Activity.this, "请先点击绑定SIM卡", 0);
			return;
		}
		startActivityAndFinish(Setup3Activity.class);

	}

}
