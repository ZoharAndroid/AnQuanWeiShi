package com.zzh.phoneguard.domain;

/**
 * 黑名单成员封装类
 * 
 * @author Administrator
 * 
 */
public class BlacklistMember {

	public static final int BLACK_SMS = 1;
	public static final int BLACK_PHONE = 2;
	public static final int BLACK_ALL = 3;
	
	public static final String BLACKLISTNUMBER= "number";
	public static final String BLACKLISTMODE = "mode";
	
	private String blacklistNumber;// 黑名单号码
	/**
	 * mode = 0:不拦截 1：短信拦截 2:电话拦截 3：全部拦截
	 */
	private int mode;// 拦截类型

	public BlacklistMember(String blacklistNumber, int mode) {
		super();
		this.blacklistNumber = blacklistNumber;
		this.mode = mode;
	}

	public String getBlacklistNumber() {
		return blacklistNumber;
	}

	public void setBlacklistNumber(String blacklistNumber) {
		this.blacklistNumber = blacklistNumber;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlackListMember [blacklistNumber=" + blacklistNumber
				+ ", mode=" + mode + "]";
	}

}
