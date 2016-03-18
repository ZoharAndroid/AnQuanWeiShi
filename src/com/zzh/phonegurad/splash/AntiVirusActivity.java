package com.zzh.phonegurad.splash;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zzh.phoneguard.dao.AntiVirusDao;
import com.zzh.phoneguard.dao.AppManageDao;
import com.zzh.phoneguard.domain.AppInfo;
import com.zzh.phoneguard.utils.LogUtil;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class AntiVirusActivity extends Activity {
	private ImageView iv_scanner;
	private TextView tv_desc;
	private ProgressBar pb_running;
	private LinearLayout ll_contain;
	private boolean isback=false;//是否按下返回按钮
	
	public static final  int SCANNER = 1 ;
	public static final int FINISH = 2;
	public static final  int FAILURE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		//然后联网检查是否要更新病毒数据库
		checkVirusVersion();
		//scan();
	}

	private void checkVirusVersion() {
		new Thread(){
			public void run() {
				//1、联网检查是否有要更新的版本
				HttpUtils http = new HttpUtils();
				//设置请求时间
				http.configTimeout(5000);
				//发送请求
				http.send(HttpMethod.GET, getResources().getString(R.string.ipVirus), new RequestCallBack<String>() {

					/**
					 * 失败了
					 */
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ShowToast.showToast(AntiVirusActivity.this, "更新失败", 0);
						Message msg = new Message();
						msg.what = FAILURE;
						handler.sendMessage(msg);
						//继续扫描
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						
					}
					
				});
				//2、有：提示用户是否更新病毒库，否：直接扫描
				//3、用户需要更新版本库，
				//4、获取新的版本库，更新后，再扫描
			};
		}.start();
	}

	/**
	 * 一个查询的列表
	 * 
	 * @author Administrator
	 *
	 */
	private class AppData{
		private String name;//软件名字
		private String desc;//扫描信息
		private Drawable icon;//软件的图标
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANNER://正在扫描
				AppData data = (AppData) msg.obj;
				tv_desc.setText("正在扫描:"+data.name);
				
				View view = View.inflate(AntiVirusActivity.this, R.layout.item_antivirus_list, null	);
				
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_antivirus_icon);
				TextView tv_appname = (TextView) view.findViewById(R.id.tv_item_antivirus_appName);
				ImageView iv_security = (ImageView) view.findViewById(R.id.iv_item_antivirus_securityicon);
				
				iv_icon.setImageDrawable(data.icon);
				tv_appname.setText(data.name);
				
				if(data.desc == ""){
					tv_appname.setTextColor(Color.BLACK);
					iv_security.setImageResource(R.drawable.list_icon_security);
				}else{
					LogUtil.d("AntiVirusActivity", "<<<<<<<<<<<<<"+data.desc);
					tv_appname.setTextColor(Color.RED);
					iv_security.setImageResource(R.drawable.list_icon_risk);
				}
				ll_contain.addView(view, 0 );
				
				break;
			case FINISH:// 完成扫描
				iv_scanner.clearAnimation();
				Toast.makeText(AntiVirusActivity.this, "扫描完成", 1).show();
				break;
			case FAILURE:
				scan();//继续扫描
				break;
			default:
				break;
			}
		};
	};
	

	/**
	 * 开始扫描
	 */
	private void scan() {
		new Thread(){
			public void run() {
				//首先，获取手机中的所有app信息
				List<AppInfo> allApps = AppManageDao.getAllApp(AntiVirusActivity.this);
				pb_running.setMax(allApps.size());
				int number = 0;
				//循环对比获取得app和数据库中的MD5值进行比较
				for(AppInfo app : allApps){
					
					if(isback){
						return;
					}
					AppData data = new AppData();
					data.name = app.getAppName();
					data.icon = app.getAppIcon();
					data.desc = AntiVirusDao.existInAnrivirus(AntiVirusActivity.this, new File(app.getAppPath()));
					Message msg = new Message();
					msg.what = SCANNER;
					msg.obj = data;
					
					handler.sendMessage(msg);
					pb_running.setProgress(++number);
				}
				Message msg = new Message();
				msg.what=FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_antivirus);
		iv_scanner = (ImageView) findViewById(R.id.iv_antivirus_scanner);
		tv_desc = (TextView) findViewById(R.id.tv_antivirus_checking);
		pb_running = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
		ll_contain = (LinearLayout)findViewById(R.id.ll_antivirus_contain);
		// 设置扫描动画
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setInterpolator(new Interpolator() {

			@Override
			public float getInterpolation(float arg0) {
				return 1 * arg0;
			}
		});
		ra.setDuration(800);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scanner.startAnimation(ra);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isback = true;
	}
}
