package com.zzh.phoneguard.domain;

import android.graphics.drawable.Drawable;

/**
 * 进程的封装类
 * 
 * @author Administrator
 * 
 */
public class TaskInfo {
	private Drawable taskIcon;// 图标
	private String taskName;// 进程名
	private long taskSize;// 大小
	private String appPath;// 进程的路径名
	private String appPackageName;// 进程的包名
	private boolean isSystem;// 是否是系统进程	
	private boolean isChecked;//是否勾选了
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public Drawable getTaskIcon() {
		return taskIcon;
	}

	public void setTaskIcon(Drawable taskIcon) {
		this.taskIcon = taskIcon;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public long getTaskSize() {
		return taskSize;
	}

	public void setTaskSize(long taskSize) {
		this.taskSize = taskSize;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getAppPackageName() {
		return appPackageName;
	}

	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

}
