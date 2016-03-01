package com.zzh.phoneguard.domain;

/**
 * 服务器版本信息的封装类：
 * 
 * versionCode: 版本号; versionDesc：版本信息; downloadUrl：下载地址
 * 
 * @author Administrator
 * 
 */
public class VersionJson {
	private int versionCode;
	private String versionDesc;
	private String downloadUrl;

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Override
	public String toString() {
		return "VersionJson [versionCode=" + versionCode + ", versionDesc="
				+ versionDesc + ", downloadUrl=" + downloadUrl + "]";
	}

}
