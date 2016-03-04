package com.zzh.phoneguard.receiver;

import com.zzh.phonegurad.splash.MyContasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
/**
 * 系统完成启动的广播接收器
 * 
 * 作用：重新启动系统，检测SIM信息，发现SIM卡信息与之前储存的SIM
 * 		信息不同，说明手机用户被更换
 * 	
 * @author Administrator
 *
 */
public class BootCompleteRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//获取当前SIM卡信息
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		String simNumber = phoneManager.getSimSerialNumber();
		
		//判断当前SIM卡的信息和sp中间中储存的SIM卡信息进行比较
		SharedPreferences sp = context.getSharedPreferences(MyContasts.SPNAME, context.MODE_PRIVATE);
		String oldSimNumber = sp.getString(MyContasts.BINDSIMCARD, "");
		if(oldSimNumber.equals(simNumber)){
			//如果相等不进行操作
		}else{
			//发送短信给安全号码
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(sp.getString(MyContasts.SAFENUMBER, ""), null, "SIM Crad different your information", null, null);
		}
	}

}
