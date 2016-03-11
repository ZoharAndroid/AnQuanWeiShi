package com.zzh.phoneguard.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private String appName;// app名
	private Drawable appIcon;// app图标
	private String appPath;// app储存位置
	private long appSize;// app大小
	private boolean isSystemApp;// 是否是系统app
	private boolean isInstallROM;// 是否安装到ROM
	private String packageName;//软件的包名

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isSystemApp() {
		return isSystemApp;
	}

	public void setSystemApp(boolean isSystemApp) {
		this.isSystemApp = isSystemApp;
	}

	public boolean isInstallROM() {
		return isInstallROM;
	}

	public void setInstallROM(boolean isInstallROM) {
		this.isInstallROM = isInstallROM;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public long getAppSize() {
		return appSize;
	}

	public void setAppSize(long appSize) {
		this.appSize = appSize;
	}

}
