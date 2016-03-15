package com.zzh.phonegurad.splash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zzh.phoneguard.domain.VersionJson;
import com.zzh.phoneguard.utils.CopyFile;
import com.zzh.phoneguard.utils.LogUtil;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class SplashActivity extends Activity {

	private static final int UPDATAVERSION = 1;// 更新版本标志

	private static final int EQUALVERSION = 2;// 本地版本和服务器版本一样

	/**
	 * 本包的所有信息
	 */
	private PackageInfo packageInfo;

	private RelativeLayout rl_root;
	private TextView tv_versionNumber;
	private ProgressBar pb_download;
	
	private SharedPreferences sp;

	/**
	 * 消息处理机制
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATAVERSION:
				// ShowToast.showToast(SplashActivity.this, "发现新版本", 0);
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						SplashActivity.this);
				dialog.setTitle("版本更新提示");// 设置对话框标题
				/**
				 * 取消按键的处理
				 */
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						//直接加载主界面
						loadMain();
					}
				});
				final VersionJson version = (VersionJson) msg.obj;

				dialog.setMessage(version.getVersionDesc());// 设置对话框内容
				// 取消按钮
				dialog.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 就直接加载到主界面
								loadMain();
							}
						});
				// 确定按钮:开始更新
				dialog.setPositiveButton("更新",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 下载并且更新软件
								downloadAndUpdata(version.getDownloadUrl());
							}
						});
				dialog.show();
				// loadMain();
				break;
			case EQUALVERSION:
				loadMain();
				break;
			default:
				break;
			}
		};
	};

	private void downloadAndUpdata(String downloadUrl) {
		HttpUtils httpUtils = new HttpUtils();
		pb_download.setVisibility(View.VISIBLE);//进度条显示
		final String target = "/sdcard/phoneguard.apk";
		httpUtils.download(downloadUrl, target,
				new RequestCallBack<File>() {
			/**
			 * 下载进度显示
			 */
			@Override
			public void onLoading(long total, long current,
					boolean isUploading) {
				super.onLoading(total, current, isUploading);
				pb_download.setMax((int)total);
				pb_download.setProgress((int)current);
			}
					/**
					 * 下载成功
					 */
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// 就开始自动安装新的版本，如何让手机自动安装新的版本软件呢？就需要参考上层应用的代码了。如下：
						/**
						 * Android系统上层所有应用的源代码\packages\apps\PackageInstaller -
						 * <intent-filter> <action
						 * android:name="android.intent.action.VIEW" />
						 * <category
						 * android:name="android.intent.category.DEFAULT" />
						 * <data android:scheme="content" /> <data
						 * android:scheme="file" /> <data android:mimeType=
						 * "application/vnd.android.package-archive" />
						 * </intent-filter>
						 */
						//下载成功，进度条消失
						pb_download.setVisibility(View.GONE);
						
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"phoneguard.apk")),
								"application/vnd.android.package-archive");
						/**
						 * 自动安装的时候弹出的对话框的取消按键的处理
						 */
						startActivityForResult(intent, 0);// 开启意图
					}

					/**
					 * 下载失败
					 */
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ShowToast.showToast(SplashActivity.this, "下载失败", 0);
						// 加载到主界面
						loadMain();
					}
				});
	}
	
	/**
	 * 自动安装的时候弹出的对话框的取消按键的处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//直接加载主界面
		loadMain();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);

		initView(); // 初始化layout中的控件
		initVersionNumber();// 设置界面中的版本号，从包的类容中读取版本信息
		startSplashAnimation();// 开始splash界面的动画效果
		initData();
		
		//复制assets目录下的文件
		//判断文件是否存在
		if(!isFileExsit("address.db")){
			new Thread(){
				public void run() {
					CopyFile.copyAssetFile(SplashActivity.this,"address.db");
					LogUtil.v("复制文件", "文件复制完成");
				};
			}.start();
		}
		//复制病毒库
		if(!isFileExsit("antivirus.db")){
			new Thread(){
				public void run() {
					CopyFile.copyAssetFile(SplashActivity.this,"antivirus.db");
					LogUtil.v("复制文件", "文件复制完成");
				};
			}.start();
		}
		
		//读取sp中的自动加载的标志
		if(sp.getBoolean(MyContasts.ISAUTOUPDATA, true)){
			checkVersion();// 连接网络，检查版本信息
		}else{
			loadMain();
		}
	}
	

	
	/**
	 * 判断文件是/files/目录下是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean isFileExsit(String fileName) {
		boolean result = false;
		File file = new File("data/data/"+getPackageName()+"/files/"+fileName);
		result = file.exists();
		return result;
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
	}

	/**
	 * 连接网络，检查版本信息
	 */
	private void checkVersion() {
		new Thread() {
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
				String response = res.toString();
				br.close();
				is.close();// 关闭流操作
				// 解析JSon数据
				VersionJson versionData = new VersionJson();
				versionData = parseJson(response);

				// 判断服务器版本是否和包的版本信息一样
				if (versionData.getVersionCode() != packageInfo.versionCode) {
					// 如果不一样，则弹出对话框进行提示更新
					// 这是在子线程中进行的操作，要修改ui界面，所以就要利用消息机制
					message.what = UPDATAVERSION;// 发送更新版本的消息
					message.obj = versionData; 
				} else {
					// 如果版本和服务器提供的版本一样，就直接跳转到主界面

				}
			} else {
				// 2000 服务器连接异常
				// 直接跳转到主界面
				ShowToast.showToast(SplashActivity.this, "2000  服务器连接异常", 0);
			}
		} catch (MalformedURLException e) {
			// 2001 URL网络地址异常
			e.printStackTrace();
			ShowToast.showToast(SplashActivity.this, "2001 URL网络地址异常", 0);
			// 直接跳转到主界面
		} catch (IOException e) {
			// 2002 IO异常
			e.printStackTrace();
			ShowToast.showToast(SplashActivity.this, "2002 IO异常", 0);
			// 直接跳转到主界面
		} catch (JSONException e) {
			// 2003 JSon数据异常
			e.printStackTrace();
			ShowToast.showToast(SplashActivity.this, "2003 JSon数据异常", 0);
			// 直接跳转到主界面
		} finally {
			// 不管如何，都会跳转到主界面
			if (message.what == UPDATAVERSION) {
				// 什么都不执行

			} else {
				message.what = EQUALVERSION;
			}

			long endTime = System.currentTimeMillis();
			if ((endTime - startTime) < 2000) {
				SystemClock.sleep(2000 - (endTime - startTime));// splash页面加载小于2秒钟的时候，就睡眠到2秒钟
			} else {
				// 超过2秒钟，就不进行延时操作了
			}

			handler.sendMessage(message);
		}
	}

	/**
	 * 由splash界面加载home主界面
	 */
	private void loadMain() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();// 销毁splash界面
	}

	/**
	 * 解析Json数据
	 * 
	 * @throws JSONException
	 */
	private VersionJson parseJson(String response) throws JSONException {
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
			packageInfo = packageManger.getPackageInfo(getPackageName(), 0);// 获取本包的所有信息
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
		pb_download=(ProgressBar) findViewById(R.id.pb_splash_download);
	}
}
