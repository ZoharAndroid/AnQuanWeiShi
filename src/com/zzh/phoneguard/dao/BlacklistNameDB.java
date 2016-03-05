package com.zzh.phoneguard.dao;

import java.util.ArrayList;
import java.util.List;

import com.zzh.phoneguard.db.BlackNameOpenHelper;
import com.zzh.phoneguard.domain.BlacklistMember;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 黑名单实现类
 * @author Administrator
 *
 */
public class BlacklistNameDB {
	/**
	 * 数据库名
	 */
	public static final String DBNAME ="blacklistdb";
	
	public static final String TABLENAME = "Blacklist";

	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	
	private static BlacklistNameDB blackListNameDB;
	
	private SQLiteDatabase db;
	/**
	 * 私有化数据库
	 * @param context
	 */
	private BlacklistNameDB(Context context){
		BlackNameOpenHelper dbHelper = new BlackNameOpenHelper(context, DBNAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	public synchronized static BlacklistNameDB getInstance(Context context){
		if(blackListNameDB==null){
			blackListNameDB = new BlacklistNameDB(context);
		}
		return blackListNameDB;
	}
	
	/**
	 * 添加黑名单
	 * @param member
	 * @return -1:添加数据不成功
	 */
	public int addBlacklist(BlacklistMember member){
		int result = 0;
		ContentValues values = new ContentValues();
		values.put(BlacklistMember.BLACKLISTNUMBER, member.getBlacklistNumber());
		values.put(BlacklistMember.BLACKLISTMODE, member.getMode());
		result = (int) db.insert(TABLENAME, null, values);
		return result;
	}
	/**
	 * 按黑名单号码删除
	 * @param blackNumber 黑名单号码
	 * @return 0：删除失败；
	 */
	public boolean removeBlacklist(String blackNumber){
		int result=0;
		result =db.delete(TABLENAME, BlacklistMember.BLACKLISTNUMBER+"= ?", new String[]{blackNumber});
		if(result==0){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 按黑名单成员删除
	 * @param member 黑名单成员
	 * @return 0：删除失败；
	 */
	public boolean removeBlacklist(BlacklistMember member){
		return removeBlacklist(member.getBlacklistNumber());
	}
	
	/**
	 * 按黑名单号码和拦截模式添加数据
	 * @param blackNumber 黑名单号码
	 * @param mode 拦截模式
	 * @return 0:更新失败
	 */
	public boolean updataBlacklist(String blackNumber,int mode){
		int result=0;
		ContentValues values = new ContentValues();
		values.put(BlacklistMember.BLACKLISTNUMBER, blackNumber);
		values.put(BlacklistMember.BLACKLISTMODE, mode);
		result=db.update(BlacklistNameDB.TABLENAME, values, BlacklistMember.BLACKLISTNUMBER+"=?", new String[]{blackNumber});
		if(result!=0){
			return true;
		}
		return false;
	}
	
	/**
	 * 添加数据
	 * @param member 黑名单成员
	 * @return 0：更新失败
	 */
	public boolean updataBlacklist(BlacklistMember member){
		return updataBlacklist(member.getBlacklistNumber(), member.getMode());
	}
	
	/**
	 * 查询所有数据
	 * @return	查询到的数据内容
	 */
	public List<BlacklistMember> queryBlacklist(){
		List<BlacklistMember> members = new ArrayList<BlacklistMember>();
		Cursor cursor=db.query(TABLENAME, null, null, null, null, null, null);
		while(cursor.moveToNext()){
			String number=cursor.getString(cursor.getColumnIndex(BlacklistMember.BLACKLISTNUMBER));
			int mode = cursor.getInt(cursor.getColumnIndex(BlacklistMember.BLACKLISTMODE));
			BlacklistMember member = new BlacklistMember(number, mode);
			members.add(member);
		}
		return members;
	}
	
	public List<BlacklistMember> getMember(String startIndex, String number){
		List<BlacklistMember> listMember = new ArrayList<BlacklistMember>();
		//db.query(TABLENAME, new String[]{BlacklistMember.BLACKLISTNUMBER,BlacklistMember.BLACKLISTMODE}, BlacklistMember.BLACKLISTNUMBER+"=? ", selectionArgs, groupBy, having, orderBy, limit)
		Cursor cursor = db.rawQuery("select"+BlacklistMember.BLACKLISTNUMBER+","+BlacklistMember.BLACKLISTMODE+"from"+TABLENAME+" limit ? ,?", new String[]{startIndex,number});
		while(cursor.moveToNext()){
			int mode = cursor.getInt(1);
			String blackNumber = cursor.getString(0);
			BlacklistMember member = new BlacklistMember(blackNumber, mode);
			listMember.add(member);
		}
		
		return listMember;
	}
	
	public int getTotal(){
		Cursor cursor = db.rawQuery("select count(1) from "+TABLENAME, null);
		cursor.moveToNext();
		int rows = cursor.getInt(0);
		return rows;
	}
}
