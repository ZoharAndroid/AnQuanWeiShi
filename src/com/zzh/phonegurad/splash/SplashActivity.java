package com.zzh.phonegurad.splash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzh.phoneguard.domain.VersionJson;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class SplashActivity extends Activity {

	private static final int UPDATAVERSION = 1;//更新版本标志

	private static final int EQUALVERSION = 2;//本地版本和服务器版本一样

	/**
	 * 本包的所有信息
	 */
	private PackageInfo packageInfo;
	
	private RelativeLayout rl_root;
	private TextView tv_versionNumber;
	
	/**
	 * 消息处理机制
	 */
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATAVERSION:
				ShowToast.showToast(SplashActivity.this, "发现新版本", 0);
				loadMain();
				break;
			case EQUALVERSION:
				loadMain();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);

		initView();	//初始化layout中的控件
		initVersionNumber();//设置界面中的版本号，从包的类容中读取版本信息
		startSplashAnimation();// 开始splash界面的动画效果
		checkVersion();// 连接网络，检查版本信息
	}
	
	/**
	 * 连接网络，检查版本信息
	 */
	private void checkVersion(){
		new Thread(){
			public void run() {
				networkCheckVersion();
			};
		}.start();
	}
	
	/**
	 * 连接服务器，获取数据
	 */
	private void networkCheckVersion() {
		Message message = new Message();
		long startTime = System.currentTimeMillis();
		try {
			URL url = new URL(getResources().getString(R.string.ipAddress));// 读取ip地址
			// 开始连接
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			// 设置网络请求参数
			connection.setRequestMethod("GET");
			connection.setReadTimeout(3000);// 设置读取超时操作
			connection.setConnectTimeout(3000);// 设置连接超时操作
			int status = connection.getResponseCode();// 获取服务器返回的状态码,200:连接正常，404：无数据响应
			if (status == 200) {// 连接长长
				// 服务器读取的流转成字符串
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuffer res = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					res.append(line); // 流数据成功转换成字符串数据
				}
				String response=res.toString();
				br.close();
				is.close();// 关闭流操作
				//解析JSon数据
				VersionJson versionData = new VersionJson();
				versionData=parseJson(response);
				
				//判断服务器版本是否和包的版本信息一样
				if(versionData.getVersionCode() != packageInfo.versionCode){
					//如果不一样，则弹出对话框进行提示更新
					//这是在子线程中进行的操作，要修改ui界面，所以就要利用消息机制
					message.what=UPDATAVERSION;//发送更新版本的消息
				}else{
					//如果版本和服务器提供的版本一样，就直接跳转到主界面
					
				}
			} else {
				//2000  服务器连接异常
				//直接跳转到主界面
				ShowToast.showToast(SplashActivity.this, "2000  服务器连接异常", 0);
			}
		} catch (MalformedURLException e) {
			//2001 URL网络地址异常
			e.printStackTrace();
			ShowToast.showToast(SplashActivity.this, "2001 URL网络地址异常", 0);
			//直接跳转到主界面
		} catch (IOException e){
			//2002 IO异常
			e.printStackTrace();
			ShowToast.showToast(SplashActivity.this, "2002 IO异常", 0);
			//直接跳转到主界面
		}catch (JSONException e) {
			//2003 JSon数据异常
			e.printStackTrace();
			ShowToast.showToast(SplashActivity.this, "2003 JSon数据异常", 0);
			//直接跳转到主界面
		}finally{
			//不管如何，都会跳转到主界面
			if(message.what==UPDATAVERSION){
				//什么都不执行
				
			}else{
				message.what = EQUALVERSION;
			}
			
			long endTime = System.currentTimeMillis();
			if((endTime - startTime) < 2000){
				SystemClock.sleep(2000-(endTime - startTime));//splash页面加载小于2秒钟的时候，就睡眠到2秒钟
			}else{
				//超过2秒钟，就不进行延时操作了
			}
			
			handler.sendMessage(message);
		}
	}
	
	/**
	 * 由splash界面加载home主界面
	 */
	private void loadMain(){
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();//销毁splash界面
	}
	
	/**
	 * 解析Json数据
	 * @throws JSONException 
	 */
	private VersionJson parseJson(String response) throws JSONException{
		VersionJson jsonData = new VersionJson();
		JSONObject jsonObject = new JSONObject(response);
		jsonData.setVersionCode(jsonObject.getInt("versionCode"));
		jsonData.setVersionDesc(jsonObject.getString("versionDesc"));
		jsonData.setDownloadUrl(jsonObject.getString("downloadUrl"));
		return jsonData; 
		
	}
	/**
	 * 开始splash界面的动画效果
	 */
	private void startSplashAnimation() {
		// 开始splash界面的动画效果
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(2000);// 设置动画显示的时间
		animation.setFillAfter(true);// 设置动画效果：渐变
		rl_root.startAnimation(animation);// splash界面卡开启动画效果
	}

	/**
	 * 设置界面中的版本号，从包的类容中读取版本信息
	 */
	private void initVersionNumber() {
		// 设置界面中的版本号，从包的类容中读取版本信息
		PackageManager packageManger = getPackageManager();// 获取包的管理器
		try {
			packageInfo = packageManger.getPackageInfo(
					getPackageName(), 0);// 获取本包的所有信息
			tv_versionNumber.setText(packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化layout中的控件
	 */
	private void initView() {
		// 初始化layout中的控件
		rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
		tv_versionNumber = (TextView) findViewById(R.id.tv_splash_versionNumber);
	}
}
