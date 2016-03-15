package com.zzh.phoneguard.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzh.phoneguard.utils.MD5Util;


/**
 * 病毒库中的操作类
 * @author Administrator
 *
 */
public class AntiVirusDao {
	/**
	 * 判断文件的MD5值是否存在于文件库中
	 * @param context 上下文
	 * @param file 需要查询的文件
	 * @return 返回文件的描述
	 */
	public static String existInAnrivirus(Context context,File file){
		String result = "";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir()+"/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{MD5Util.getMD5(file)});
		//Cursor cursor=db.query("datable", new String[]{"desc"}, "md5=?", new String[]{MD5Util.getMD5(file)}, null, null, null);
		while(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
}
