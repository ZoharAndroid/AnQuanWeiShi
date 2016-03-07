package com.zzh.phoneguard.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.zzh.phoneguard.dao.BlacklistNameDB;
import com.zzh.phoneguard.domain.BlacklistMember;
import com.zzh.phoneguard.utils.LogUtil;
import com.zzh.phoneguard.utils.ServiceUtils;
import com.zzh.phoneguard.utils.ShowToast;

public class BlacklistService extends Service {

	private SmsBlacklistRecevier recevier = new SmsBlacklistRecevier();
	
	private MyPhoneStateListener listener;
	
	private TelephonyManager manager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 一创建服务，就从广播接收器中接收短信数据
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(Integer.MAX_VALUE);
		// 祖册一个广播接收器
		registerReceiver(recevier, intentFilter);
		LogUtil.v("INFORMATION", "黑名单拦截服务开启>>>>>>>>>>");

		// 电话监听
		// 获得电话管理器
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 设置电话监听状态
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	


	/**
	 * 手机状态
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 正在响铃
				BlacklistNameDB blacklistDB = BlacklistNameDB.getInstance(BlacklistService.this);
				if((blacklistDB.getMode(incomingNumber) & BlacklistMember.BLACK_PHONE)!=0){
					Log.v("电话广播", "电话已经挂断");
					//是拦截电话
					endCall();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 正在闲置

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://挂断电话
				
			default:
				break;
			}
		}
	}

	/**
	 * 短信广播接收器
	 * 
	 * @author Administrator
	 * 
	 */
	private class SmsBlacklistRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] datas = (Object[]) intent.getExtras().get("pdus");
			for (Object data : datas) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) data);
				// 获取消息内容
				String messageBody = message.getDisplayMessageBody();
				String number = message.getDisplayOriginatingAddress();// 获取号码
				// 判断当前号码时候和黑名单数据库中的内容一致
				BlacklistNameDB blacklistDB = BlacklistNameDB
						.getInstance(BlacklistService.this);
				if ((blacklistDB.getMode(number) & BlacklistMember.BLACK_SMS) != 0) {
					abortBroadcast();// 终止广播
				} else {
					// 进行短信内容拦截
					if (messageBody.contains("广告")) {
						abortBroadcast();
					}
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(recevier);
		recevier = null;
		
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		
		stopSelf();
		LogUtil.v("INFOTMATION", "<<<<<服务已经关闭");
	}

	/**
	 * 挂断电话
	 */
	public void endCall() {
		try {
			Class type = Class.forName("android.os.ServiceManager");
			Method getServiceMethod = type.getDeclaredMethod("getService", new Class[]{String.class});
			IBinder binder = (IBinder) getServiceMethod.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
			ITelephony tel = ITelephony.Stub.asInterface(binder);
			tel.endCall();//挂断电话
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
