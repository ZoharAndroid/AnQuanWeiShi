package com.zzh.phoneguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.zzh.phoneguard.dao.PhoneLocationDB;

public class PhoneLocationChechService extends Service {

	private TelephonyManager manager;
	private MyPhoneStateListener listener;
	
	private OutCallReceiver outCallreceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//如果用户拨打电话，发送一个拨打电话的广播
		outCallreceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(outCallreceiver, filter);

	}
	
	/**
	 * 创建一个打电话出去的广播接收者
	 * @author Administrator
	 *
	 */
	public  class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获得外拨电话的号码
			String number = getResultData();
			//查询号码
			String address = PhoneLocationDB.locationChech(context, number);
			Toast.makeText(context, address	, 1).show();
		}
		
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 正在响铃
				String address = PhoneLocationDB.locationChech(
						getApplicationContext(), incomingNumber);
				Toast.makeText(getApplicationContext(), address, 1).show();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 挂断电话

				break;

			case TelephonyManager.CALL_STATE_IDLE:// 闲置状态
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
