package com.zzh.phonguard.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 获取手机短信和通讯记录
 * @author Administrator
 *
 */
public class SmsTelphoneGetContent {
	/**
	 * 从短信内容提供者读取号码
	 * @param context
	 * @return 短信号码List集合
	 */
	public static List<String> getSmsContent(Context context){
		List<String> listSms = new ArrayList<String>();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, null);
		while(cursor.moveToNext()){
			//获取数据
			String number = cursor.getString(0);
			if(!listSms.contains(number)){
				listSms.add(number);
			}
		}
		return listSms;
	}
	/**
	 * 从手机号码内容提供者读取号码
	 * @param context
	 * @return 手机号码List集合
	 */
	public static List<String> getTelphoneContent(Context context){
		List<String> listTelphone = new ArrayList<String>();
		Uri uri = Uri.parse("content://cal_log/calls");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"number"}, null, null, null);
		while(cursor.moveToNext()){
			String number = cursor.getString(0);
			if(!listTelphone.contains(number)){
				listTelphone.add(number);
			}
		}
		return listTelphone;
	}
}
