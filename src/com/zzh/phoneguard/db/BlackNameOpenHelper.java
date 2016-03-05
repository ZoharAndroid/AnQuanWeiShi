package com.zzh.phoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 黑名单数据库
 * @author Administrator
 *
 */
public class BlackNameOpenHelper extends SQLiteOpenHelper {

	/**
	 * 创建的表格：
	 * 	Blacklist：
	 * 		number 手机号码
	 * 		mode	拦截模式
	 */
	public static final String CREATE_BLACKLIST = "create table Blacklist ("
			+"id integer primary key autoincrement,"
			+"number text,"
			+"mode integer)";
	
	public BlackNameOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(CREATE_BLACKLIST);//创建数据库
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
