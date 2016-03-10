package com.zzh.phoneguard.service;

import android.app.Service;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzh.phoneguard.dao.PhoneLocationDB;
import com.zzh.phonegurad.splash.MyContasts;
import com.zzh.shoujiweishi.R;

public class PhoneLocationChechService extends Service {

	private TelephonyManager manager;

	private MyPhoneStateListener listener;

	private OutCallReceiver outCallreceiver;

	// 获取窗体管理
	private WindowManager wm;

	private TextView tv_title;

	private View view;

	private WindowManager.LayoutParams params;

	private boolean isFirstStatus = true;// 屏幕空闲状态

	private SharedPreferences sp;

	private LinearLayout ll_toast_style;// 土司显示样式

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		view = View.inflate(getApplicationContext(), R.layout.sys_toast, null);
		initToash();
		initEvent();
		// 手机状态监听器
		listener = new MyPhoneStateListener();
		// 监听手机的电话状态
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 如果用户拨打电话，发送一个拨打电话的广播
		outCallreceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallreceiver, filter);
	}

	/**
	 * Toast窗体的点击事件
	 */
	public void initEvent() {

		view.setOnTouchListener(new OnTouchListener() {

			int downX = 0;
			int downY = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下
					downX = (int) event.getRawX();
					downY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:// 移动
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					int dx = moveX - downX;
					int dy = moveY - downY;

					// 设置参数
					params.x += dx;
					params.y += dy;
					// 跟新Toast的位置
					wm.updateViewLayout(view, params);
					// 给开始起重新复制
					downX = moveX;
					downY = moveY;
					break;
				case MotionEvent.ACTION_UP:// 离开
					if (params.x <= 0) {
						params.x = 0;
					} else if (params.x > wm.getDefaultDisplay().getWidth()
							- view.getWidth()) {
						params.x = wm.getDefaultDisplay().getWidth()
								- view.getWidth();
					}
					if (params.y <= 0) {
						params.y = 0;
					} else if (params.y > wm.getDefaultDisplay().getHeight()
							- view.getHeight()) {
						params.y = wm.getDefaultDisplay().getHeight()
								- view.getHeight();
					}
					// 保存当前的位置值
					sp.edit().putInt("X", params.x).commit();
					sp.edit().putInt("Y", params.y).commit();

					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	/**
	 * 移除Toast
	 */
	public void dismissToast() {
		if (view != null) {
			wm.removeView(view);
		}
	}

	/**
	 * Toast显示的状态
	 * 
	 * @param address
	 */
	public void show(String address) {
		ll_toast_style.setBackgroundResource(resourceId[sp.getInt(
				MyContasts.PHONELOCATIONSTYLE, 0)]);
		tv_title.setText(address);
		wm.addView(view, params);
	}

	private int[] resourceId = { R.drawable.call_locate_gray,
			R.drawable.call_locate_green, R.drawable.call_locate_blue,
			R.drawable.call_locate_orange, R.drawable.call_locate_white };

	/**
	 * 初始化Toast的设置内容
	 */
	public void initToash() {
		tv_title = (TextView) view.findViewById(R.id.tv_toast_title);
		ll_toast_style = (LinearLayout) view
				.findViewById(R.id.ll_toast_root_style);
		/**
		 * 设置该界面的样式,有个问题是只能显示一次？接下来就是要在显示的时候去改变
		 */
		ll_toast_style.setBackgroundResource(resourceId[sp.getInt(
				MyContasts.PHONELOCATIONSTYLE, 0)]);
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE // 取消不能触摸
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// 设置窗体的初始显示位置
		params.gravity = Gravity.TOP | Gravity.LEFT;// 左上角
		params.x = sp.getInt("X", 0);
		params.y = sp.getInt("Y", 0);

		params.format = PixelFormat.TRANSLUCENT;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.setTitle("Toast");
	}

	/**
	 * 创建一个打电话出去的广播接收者
	 * 
	 * @author Administrator
	 * 
	 */
	public class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获得外拨电话的号码
			String number = getResultData();
			// 查询号码
			String address = PhoneLocationDB.locationChech(context, number);
			show(address);
		}

	}

	/**
	 * 拨打电话的状态
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 正在响铃
				String address = PhoneLocationDB.locationChech(
						getApplicationContext(), incomingNumber);
				// Toast.makeText(getApplicationContext(), address, 1).show();
				show(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 挂断电话

				break;

			case TelephonyManager.CALL_STATE_IDLE:// 闲置状态
				if (!isFirstStatus) {
					dismissToast();
				} else {
					isFirstStatus = false;
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);

		unregisterReceiver(outCallreceiver);
	}

}
