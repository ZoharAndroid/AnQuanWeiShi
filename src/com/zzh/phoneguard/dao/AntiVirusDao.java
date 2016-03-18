package com.zzh.phoneguard.dao;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzh.phoneguard.domain.VirusInfo;
import com.zzh.phoneguard.utils.MD5Util;


/**
 * 病毒库中的操作类
 * 
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
		//Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{MD5Util.getMD5(file)});
		Cursor cursor=db.query("datable", new String[]{"desc"}, "md5=?", new String[]{MD5Util.getMD5(file)}, null, null, null);
		while(cursor.moveToNext()){
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 返回当前应用程序的病毒库版本
	 * 
	 * @param context 上下文
	 * @return	int数据，当前病毒库的版本
	 */
	public static int getCurrentVersion(Context context){
		int number = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir()+"/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("version", new String[]{"subcnt"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			number = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return number;
	}
	
	/**
	 * 往病毒库中添加病毒种类
	 * 
	 * @param context
	 * @param info
	 */
	public static void addNewVirus(Context context,VirusInfo info){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getFilesDir()+"/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("md5", info.getMd5());
		values.put("type", info.getType());
		values.put("name", info.getName());
		values.put("desc", info.getDesc());
		db.insert("datable", null, values);
	}
}
