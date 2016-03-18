package com.zzh.phonegurad.splash;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzh.shoujiweishi.R;

public class CacheActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private LinearLayout ll_contain;
	private TextView tv_nodata;
	private RelativeLayout rl_loading;
	private TextView tv_number;
	private LinearLayout ll_show;
	private PackageManager pm;
	private List<CacheData> cacheDatas = new ArrayList<CacheData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		showCache();
	}

	/**
	 * 按键：清除手机缓存
	 * 
	 * @param view
	 */
	public void clearCache(View view) {
		Class type = pm.getClass();
		try {
			Method method = type.getDeclaredMethod("freeStorageAndNotify",
					new Class[] { long.class, IPackageDataObserver.class });

			method.invoke(pm, new Object[] { Integer.MAX_VALUE,
					new MyIPackageDataObserver() });

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 清除缓存的回调类
	 * 
	 * @author Administrator
	 *
	 */
	private class MyIPackageDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ll_contain.removeAllViews();//清空所有的组件
					tv_number.setText("0款软件有缓存，共0B");
					Toast.makeText(getApplicationContext(), "清空所有缓存", 1).show();
				}
			});
		}
		
	}

	/**
	 * 处理缓存
	 */
	private void showCache() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = LOADING;
				handler.sendMessage(msg);// 发送记载界面
				long startTime = System.currentTimeMillis();

				// 处理数据
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(0);
				for (PackageInfo packageInfo : installedPackages) {
					getCache(packageInfo.packageName);
				}

				long endTime = System.currentTimeMillis();
				if ((endTime - startTime) < 3000) {// 小于3秒，直接睡眠到3秒
					SystemClock.sleep(3000 - (endTime - startTime));
					msg = new Message();
					msg.what = FINISH;
					handler.sendMessage(msg);// 发送记载界面
				} else {// 负责直接发送
					msg = new Message();
					msg.what = FINISH;
					handler.sendMessage(msg);// 发送记载界面
				}

			};
		}.start();
	}

	/**
	 * 获取缓存回调类
	 * 
	 * @author Administrator
	 *
	 */
	class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
		private String packName;

		public MyIPackageStatsObserver(String packName) {
			this.packName = packName;
		}

		/**
		 * 回调结果
		 */
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			System.out.println("缓存大小：" + pStats.cacheSize);
			if (pStats.cacheSize != 0) {// 有缓存
				CacheData data = new CacheData();
				data.size = pStats.cacheSize;
				try {
					data.name = pm.getPackageInfo(packName, 0).applicationInfo
							.loadLabel(pm) + "";
					data.icon = pm.getPackageInfo(packName, 0).applicationInfo
							.loadIcon(pm);
					cacheDatas.add(data);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * 获取手机缓存
	 * 
	 * @param packName 包名
	 */
	private void getCache(String packName) {
		Class type = pm.getClass();
		try {
			Method method = type.getDeclaredMethod("getPackageSizeInfo",
					new Class[] { String.class, IPackageStatsObserver.class });

			method.invoke(pm, new Object[] { packName,
					new MyIPackageStatsObserver(packName) });

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private class CacheData {
		Drawable icon;
		String name;
		long size;
	}

	/**
	 * 消息机制
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				rl_loading.setVisibility(View.VISIBLE);
				ll_show.setVisibility(View.GONE);
				tv_nodata.setVisibility(View.GONE);
				break;
			case FINISH:
				if (cacheDatas.size() == 0) {// 没有数据
					rl_loading.setVisibility(View.GONE);
					ll_show.setVisibility(View.GONE);
					tv_nodata.setVisibility(View.VISIBLE);
				} else {// 有数据
					rl_loading.setVisibility(View.GONE);
					ll_show.setVisibility(View.VISIBLE);
					tv_nodata.setVisibility(View.GONE);
					long countSize = 0;// 总共缓存大小
					// 处理显示的数据
					for (CacheData data : cacheDatas) {
						View v = View.inflate(CacheActivity.this,
								R.layout.item_cache_view, null);
						ImageView iv_icon = (ImageView) v
								.findViewById(R.id.iv_item_cache_icon);
						TextView tv_name = (TextView) v
								.findViewById(R.id.tv_item_cache_appName);
						TextView tv_size = (TextView) v
								.findViewById(R.id.tv_item_cache_size);

						iv_icon.setImageDrawable(data.icon);
						tv_name.setText(data.name);
						tv_size.setText("缓存："
								+ Formatter.formatFileSize(CacheActivity.this,
										data.size));
						countSize += data.size;
						ll_contain.addView(v, 0);
					}
					tv_number.setText(cacheDatas.size()
							+ "款软件有缓存，共"
							+ Formatter.formatFileSize(CacheActivity.this,
									countSize));
				}

				break;
			default:
				break;
			}
		};
	};

	/**
	 * 初始化界面
	 */
	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cache);

		pm = getPackageManager();
		// 找到控件
		ll_contain = (LinearLayout) findViewById(R.id.ll_activity_cache_contain);
		tv_nodata = (TextView) findViewById(R.id.tv_cache_activity_nodata);
		rl_loading = (RelativeLayout) findViewById(R.id.rl_cache_activity_loading);
		tv_number = (TextView) findViewById(R.id.tv_cache_activity_number);
		ll_show = (LinearLayout) findViewById(R.id.ll_cache_activity_show);

	}
}
