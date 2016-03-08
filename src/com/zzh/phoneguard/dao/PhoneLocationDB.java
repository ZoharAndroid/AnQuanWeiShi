package com.zzh.phoneguard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询类
 * 
 * @author Administrator
 * 
 */
public class PhoneLocationDB {
	/**
	 * 手机号码从数据库中查询归属地
	 * 
	 * @param context
	 *            上下文
	 * @param phoneNumber
	 *            要输入的号码
	 * @return 归属地信息
	 */
	public static String phoneLocationCheck(Context context, String phoneNumber) {
		String checkNumber = phoneNumber.substring(0, 7);
		String address = "";
		// 获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir()
				+ "/address.db", null, SQLiteDatabase.OPEN_READONLY);
		// 查询数据库
		Cursor cursor = db
				.rawQuery(
						"select location from data2 where id = (select outkey from data1 where id = ?)",
						new String[] { checkNumber });
		if (cursor.moveToNext()) {
			address = cursor.getString(0);
		}
		cursor.close();
		// db.query(table, columns, selection, selectionArgs, groupBy, having,
		// orderBy, limit)
		return address;
	}

	/**
	 * 固定电话 查询
	 * @param context
	 * @param telNumber
	 * @return
	 */
	public static String telLocationCheck(Context context, String telNumber) {
		String address = "";
		Cursor cursor = null;
		// 获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir()
				+ "/address.db", null, SQLiteDatabase.OPEN_READONLY);
		// 查询数据库
		String checkNumber = telNumber.substring(1, 3);
		cursor = db.rawQuery("select location from data2 where area = ?",
				new String[] { checkNumber });
		if (cursor.moveToNext()) {
			address = cursor.getString(0);
		} else {
			checkNumber = telNumber.substring(1, 4);
			cursor = db.rawQuery("select location from data2 where area = ?",
					new String[] { checkNumber });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
		}
		cursor.close();
		// db.query(table, columns, selection, selectionArgs, groupBy, having,
		// orderBy, limit)
		return address;
	}

	/**
	 * 归属地查询
	 * @param context
	 * @param phoneNumber
	 * @return
	 */
	public static String locationChech(Context context, String phoneNumber) {
		String address = "";
		if (phoneNumber.length() == 3) {
			if (phoneNumber.equals("110")) {
				address = "匪警";
			} else if (phoneNumber.equals("119")) {
				address = "火警";
			} else if (phoneNumber.equals("120")) {
				address = "急救中心";
			} else if (phoneNumber.equals("122")) {
				address = "道路交通事故报警";
			} else {
				address = "请拨打114进行查询";
			}
		} else if (phoneNumber.length() == 5) {
			if (phoneNumber.equals("10086")) {
				address = "中国移动";
			} else if (phoneNumber.equals("10010")) {
				address = "中国联通";
			} else if (phoneNumber.equals("12306")) {
				address = "铁路中心";
			} else if (phoneNumber.equals("95566")) {
				address = "中国银行";
			} else {
				address = "请拨打114进行查询";
			}
		} else if (phoneNumber.length() > 7) {
			if (phoneNumber.length() <= 8) {
				address = "本地固话";
			} else {
				if (phoneNumber.length() == 11) {
					address = phoneLocationCheck(context, phoneNumber);
				} else {
					address = telLocationCheck(context, phoneNumber);
				}
			}
		} else {
			address = "号码有误";
		}
		return address;
	}
}
