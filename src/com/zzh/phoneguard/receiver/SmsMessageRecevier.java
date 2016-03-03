package com.zzh.phoneguard.receiver;

import com.zzh.phoneguard.service.LocationService;
import com.zzh.shoujiweishi.R;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsMessageRecevier extends BroadcastReceiver {

	DevicePolicyManager mDPM;

	@Override
	public void onReceive(Context context, Intent intent) {
		mDPM = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		// 从Intent参数中取出一个Bundle对象
		Bundle bundle = intent.getExtras();
		// 然后使用pdus密钥来提取一个pdus 数据，其中每一个pdu都表示一条短信消息
		Object[] pdus = (Object[]) bundle.get("pdus");
		SmsMessage[] messages = new SmsMessage[pdus.length];
		for (int i = 0; i < messages.length; i++) {
			// 接着使用SmsMessage的createFromPdu()方法将每一个pdu字节数组转换成
			// SmsMessage对象
			messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
		}

		String smsBody = "";
		for (SmsMessage message : messages) {
			smsBody += message.getDisplayMessageBody();
			if (smsBody.equals("#*alarm*#")) {// 接收发送警告的声音
				System.out.println("#*alarm*#");
				MediaPlayer player = MediaPlayer.create(context, R.raw.music);
				player.setVolume(1, 1);
				player.start();
				abortBroadcast();
			} else if (smsBody.equals("#*location*#")) {// 接收发送地址的信息
				System.out.println("#*location*#");
				//开启获取gps服务
				Intent intentService = new Intent(context, LocationService.class);
				context.startService(intentService);
				
				abortBroadcast();
			} else if (smsBody.equals("#*wipedata*#")) {// 接收清除数据
				mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				abortBroadcast();
			} else if (smsBody.equals("#*lockscreen*#")) {// 接收锁屏的信息
				//mDPM.resetPassword("123", 0);//重置密码
				mDPM.lockNow();
				abortBroadcast();
			}
		}
		/*
		 * // 获取接收到的内容 // 从Intent参数中取出一个Bundle对象 Bundle bundle =
		 * intent.getExtras(); // 然后使用pdus密钥来提取一个pdus 数据，其中每一个pdu都表示一条短信消息
		 * Object[] pdus = (Object[]) bundle.get("pdus"); for (Object pdu :
		 * pdus) { SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
		 * String smsBody = message.getDisplayMessageBody(); if
		 * (smsBody.equals("#*alarm*#")) {// 接收发送警告的声音
		 * System.out.println("#*alarm*#"); abortBroadcast(); } else if
		 * (smsBody.equals("#*location*#")) {// 接收发送地址的信息
		 * System.out.println("#*location*#"); abortBroadcast(); } else if
		 * (smsBody.equals("#*wipedata*#")) {// 接收清除数据
		 * System.out.println("#*wipedata*#"); abortBroadcast(); } else if
		 * (smsBody.equals("#*lockscreen*#")) {// 接收锁屏的信息
		 * System.out.println("#*lockscreen*#"); abortBroadcast(); } }
		 */

	}
}
