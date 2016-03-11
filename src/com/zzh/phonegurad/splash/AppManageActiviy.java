package com.zzh.phonegurad.splash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;
import com.zzh.phoneguard.dao.AppManageDao;
import com.zzh.phoneguard.domain.AppInfo;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class AppManageActiviy extends Activity implements View.OnClickListener {

	public static final int LOADING = 1;

	private RelativeLayout rl_loading;

	private RelativeLayout rl_show;

	private ListView lv_appList;

	private List<AppInfo> allApps = new ArrayList<AppInfo>();
	private List<AppInfo> userApps = new ArrayList<AppInfo>();
	private List<AppInfo> systemApps = new ArrayList<AppInfo>();

	private TextView tv_room;
	private TextView tv_sdcard;

	private TextView tv_tag;

	private PopupWindow pw;

	private AppInfo itemApp;

	private PackageManager pm;

	private RemoveAppRecevier recevier;

	private MyAppAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		showLoadUI();
		initData();

		initEvnet();
	}

	public void initPopupWindow(View parent) {
		View view = View.inflate(AppManageActiviy.this,
				R.layout.popup_appmanage, null);
		pw = new PopupWindow(view, -2, -2);

		LinearLayout ll_launcher = (LinearLayout) view
				.findViewById(R.id.ll_popup_launcher);
		LinearLayout ll_share = (LinearLayout) view
				.findViewById(R.id.ll_popup_share);
		LinearLayout ll_uninstall = (LinearLayout) view
				.findViewById(R.id.ll_popup_uninstall);
		LinearLayout ll_setting = (LinearLayout) view
				.findViewById(R.id.ll_popup_setting);

		ll_launcher.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		ll_uninstall.setOnClickListener(this);
		ll_setting.setOnClickListener(this);

		// 显示动画
		AlphaAnimation alpha = new AlphaAnimation(0, 1);// 从透明到不透明
		alpha.setDuration(500);
		alpha.setInterpolator(new Interpolator() {

			@Override
			public float getInterpolation(float input) {

				return 10 * input;
			}
		});
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0.5f, 1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration(500);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(scale);
		set.addAnimation(alpha);
		view.startAnimation(set);

		// popup显示的位置
		int[] location = new int[2];
		parent.getLocationInWindow(location);
		pw.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, location[0] + 50,
				location[1]);

	}

	public void closePopupWindow() {
		if (pw != null && pw.isShowing()) {
			pw.dismiss();
			pw = null;
		}
	}

	/**
	 * popup窗体的点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popup_launcher:// 启动
			launcher();
			break;
		case R.id.ll_popup_share:// 启动
			share();
			break;
		case R.id.ll_popup_uninstall:// 启动
			uninstall();
			break;
		case R.id.ll_popup_setting:// 启动
			setting();
			break;
		default:
			break;
		}
		closePopupWindow();
	}

	public void launcher() {
		Intent intent = pm.getLaunchIntentForPackage(itemApp.getPackageName());
		startActivity(intent);
	}

	public void uninstall() {
		// 判断是应用软件还是系统软件

		if (itemApp.isSystemApp()) {
			// 系统软件
			// 先判断手机是否有ROOT权限
			if (!RootTools.isRootAvailable()) {
				ShowToast.showToast(AppManageActiviy.this, "无root权限，无法删除", 0);
				return;
			}
			// 有root权限，给予这个软件root权限
			try {
				if (!RootTools.isAccessGiven()) {
					ShowToast.showToast(AppManageActiviy.this, "安全卫士无权限删除", 0);
					return;
				}
				RootTools.sendShell("mount -o remount rw /system", 30000);
				RootTools.sendShell("rm -r " + itemApp.getAppPath(), 30000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RootToolsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// 用户软件
			/**
			 * <intent-filter> <action android:name="android.intent.action.VIEW"
			 * /> <action android:name="android.intent.action.DELETE" />
			 * <category android:name="android.intent.category.DEFAULT" /> <data
			 * android:scheme="package" /> </intent-filter>
			 */
			Intent intent = new Intent();
			intent.setAction("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + itemApp.getPackageName()));
			startActivity(intent);
		}
	}

	private class RemoveAppRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// 接收到卸载软件的广播
			if (itemApp.isSystemApp()) {
				systemApps.remove(itemApp);
			} else {
				userApps.remove(itemApp);
			}
			initRomSDshow();// 重新显示rom和SD容量
			adapter.notifyDataSetChanged();
		}

	}

	public void share() {
		// 分享发送短信
		/**
		 * <intent-filter> <action android:name="android.intent.action.SEND" />
		 * <category android:name="android.intent.category.DEFAULT" /> <data
		 * android:mimeType="text/plain" /> </intent-filter>
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"Please go to http://www.baidu.com download.");
		startActivity(intent);

	}

	public void setting() {
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:" + itemApp.getPackageName()));
		startActivity(intent);
	}

	private void initEvnet() {

		lv_appList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || (position == userApps.size() + 1)) {
					return;
				}
				closePopupWindow();
				initPopupWindow(view);
				itemApp = (AppInfo) lv_appList.getItemAtPosition(position);
			}

		});
		/**
		 * listview滑动事件
		 */
		lv_appList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem >= userApps.size() + 1) {
					tv_tag.setText("系统软件:(" + systemApps.size() + ")");
				} else {
					tv_tag.setText("个人软件:(" + userApps.size() + ")");
				}
			}
		});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOADING:// 加载界面显示完毕，更新APP列表
				rl_loading.setVisibility(View.GONE);// 缓冲界面关闭
				rl_show.setVisibility(View.VISIBLE);// app界面显示
				// 思考？
				// 接下来就是对数据的处理操作了！如何获取软件的的信息呢？
				// 创建一个业务类，来获得相应的软件显示信息：
				// 如，软件名，图标，储存位置，软件大小
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 显示ROM和SD卡容量
	 */
	private void initRomSDshow() {
		// 获得所有的软件信息
		allApps = AppManageDao.getAllApp(AppManageActiviy.this);
		// 设置界面手机和sd卡的控件
		long freeROMSize = AppManageDao.getROMFreeSize(AppManageActiviy.this);
		long totalROMSize = AppManageDao.getROMTotalSize(AppManageActiviy.this);
		long freeSDSize = AppManageDao.getSDCardFreeSize(AppManageActiviy.this);
		long totalSDSize = AppManageDao
				.getSDCardTotalSize(AppManageActiviy.this);
		// long字节转换成相应的字节量
		String freeROMSize_string = Formatter.formatFileSize(
				AppManageActiviy.this, freeROMSize);
		String totalROMSize_string = Formatter.formatFileSize(
				AppManageActiviy.this, totalROMSize);
		String freeSDSize_string = Formatter.formatFileSize(
				AppManageActiviy.this, freeSDSize);
		String totalSDSize_string = Formatter.formatFileSize(
				AppManageActiviy.this, totalSDSize);
		// 显示容量比
		tv_room.setText("ROM:" + freeROMSize_string + "/" + totalROMSize_string
				+ "-");
		tv_sdcard.setText("SD:" + freeSDSize_string + "/" + totalSDSize_string);

	}

	private void initData() {
		initRomSDshow();

		// 将显示的程序进行分类
		for (AppInfo app : allApps) {
			if (app.isSystemApp()) {
				systemApps.add(app);
			} else {
				userApps.add(app);
			}
		}

		// 设置个人软件
		tv_tag.setText("个人软件:(" + userApps.size() + ")");

		adapter = new MyAppAdapter();
		lv_appList.setAdapter(adapter);
	}

	private class MyAppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return allApps.size();
		}

		@Override
		public AppInfo getItem(int position) {
			AppInfo app = null;
			if (position <= userApps.size()) {
				app = userApps.get(position - 1);
			} else {
				app = systemApps.get(position - 2 - userApps.size());
			}
			return app;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			if (position == 0) {// 如果位于第一个条目，就显示为用户软件
				v = new TextView(AppManageActiviy.this);
				((TextView) v).setText("个人软件:(" + userApps.size() + ")");
				((TextView) v).setBackgroundColor(Color.GRAY);
				((TextView) v).setTextColor(Color.WHITE);
				return v;
			} else if (position == (userApps.size() + 1)) {
				v = new TextView(AppManageActiviy.this);
				((TextView) v).setText("系统软件:(" + systemApps.size() + ")");
				((TextView) v).setBackgroundColor(Color.GRAY);
				((TextView) v).setTextColor(Color.WHITE);
				return v;
			} else {
				// 判断当前的postion
				AppInfo appInfo = getItem(position);// 获得当前的对象
				ViewHolder holder = null;
				if (convertView == null || convertView instanceof TextView) {
					// 当convertView为空或者为TextView时，重新赋初值
					convertView = View.inflate(AppManageActiviy.this,
							R.layout.item_appmanage_list, null);
					holder = new ViewHolder();
					holder.tv_appName = (TextView) convertView
							.findViewById(R.id.tv_item_appmanage_appName);
					holder.tv_appSize = (TextView) convertView
							.findViewById(R.id.tv_item_appmanage_appSize);
					holder.tv_store = (TextView) convertView
							.findViewById(R.id.tv_item_appmanage_store);
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_item_appmanage_icon);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.tv_appName.setText(appInfo.getAppName());
				holder.tv_appSize.setText(Formatter.formatFileSize(
						AppManageActiviy.this, appInfo.getAppSize()));
				holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
				if (appInfo.isInstallROM()) {
					holder.tv_store.setText("手机内存");
				} else {
					holder.tv_store.setText("SD卡");
				}

				return convertView;
			}

		}

	}

	class ViewHolder {
		ImageView iv_icon;
		TextView tv_appName;
		TextView tv_appSize;
		TextView tv_store;
	}

	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_appmanage);

		rl_loading = (RelativeLayout) findViewById(R.id.rl_appmanage_firstloading);
		rl_show = (RelativeLayout) findViewById(R.id.rl_appmanage_show);

		tv_room = (TextView) findViewById(R.id.tv_appmanage_rom);
		tv_sdcard = (TextView) findViewById(R.id.tv_appmanage_sdcard);

		lv_appList = (ListView) findViewById(R.id.lv_appmanage_showapp);

		tv_tag = (TextView) findViewById(R.id.tv_appmanage_tag);

		pm = getPackageManager();

		recevier = new RemoveAppRecevier();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(recevier, filter);
	}

	/**
	 * 加载缓冲界面
	 */
	private void showLoadUI() {
		rl_loading.setVisibility(View.VISIBLE);// 缓冲界面显示
		rl_show.setVisibility(View.GONE);// app界面显示关闭

		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				// 保持2秒钟的事件
				SystemClock.sleep(2000);
				// 跳转到showAppList软件显示列表
				msg.what = LOADING;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closePopupWindow();
	}

}
