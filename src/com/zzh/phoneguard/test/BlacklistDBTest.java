package com.zzh.phoneguard.test;

import java.util.List;

import com.zzh.phoneguard.dao.BlacklistNameDB;
import com.zzh.phoneguard.domain.BlacklistMember;

import android.test.AndroidTestCase;

public class BlacklistDBTest extends AndroidTestCase {
	
	
	public void testAdd(){
		for (int i = 0; i < 200	; i++) {
			BlacklistNameDB blacklist = BlacklistNameDB.getInstance(getContext());
			BlacklistMember member = new BlacklistMember("110"+i, BlacklistMember.BLACK_ALL);
			blacklist.addBlacklist(member);
		}
	}
	
	public void testQueryBlacklist(){
		BlacklistNameDB blacklist = BlacklistNameDB.getInstance(getContext());
		
		System.out.println(blacklist.queryBlacklist());
	}
	
	public void testRemovBlicklist(){
		BlacklistNameDB blacklist = BlacklistNameDB.getInstance(getContext());
		List<BlacklistMember> members = blacklist.queryBlacklist();
		for(BlacklistMember member : members){
			blacklist.removeBlacklist(member);
			System.out.println("删除成功");
		}
		
	}
}
