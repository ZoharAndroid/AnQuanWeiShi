package com.zzh.phoneguard.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.zzh.phoneguard.domain.AppInfo;

public class AppManageDao {
	/**
	 * 获得ROM空闲的大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getROMFreeSize(Context context){
		return Environment.getDataDirectory().getFreeSpace();//获取手机空闲总大小空间
	}
	
	/**
	 * 获取手机ROM总的大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getROMTotalSize(Context context){
		return Environment.getDataDirectory().getTotalSpace();//获取手机总大小空间
	}
	/**
	 * 获得SD空闲的大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getSDCardFreeSize(Context context){
		return Environment.getExternalStorageDirectory().getFreeSpace();
	}
	/**
	 * 获得SD总的大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getSDCardTotalSize(Context context){
		return Environment.getExternalStorageDirectory().getTotalSpace();
	}
	
	/**
	 * 获取所有的App信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAllApp(Context context){
		List<AppInfo> apps = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);//获取素有的安装包
		//遍历所有的包
		for(PackageInfo packageInfo : packageInfos){
			AppInfo appInfo = new AppInfo();
			//获取应用程序的名
			appInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm)+"");
			//获取应用程序的图标
			appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
			//程序路径
			appInfo.setAppPath(packageInfo.applicationInfo.sourceDir);
			//程序大小
			File file = new File(packageInfo.applicationInfo.sourceDir);
			appInfo.setAppSize(file.length());
			/**
			 * 判断程序的是否是系统软件
			 */
			int flag = packageInfo.applicationInfo.flags;
			if((flag & ApplicationInfo.FLAG_SYSTEM)!=0){
				appInfo.setSystemApp(true);
			}else{
				appInfo.setSystemApp(false);
			}
			/**
			 * 安装于sd卡还是rom
			 */
			if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
				appInfo.setInstallROM(false);
			}else{
				appInfo.setInstallROM(true);
			}
			/**
			 * 获取包名
			 * 
			 */
			appInfo.setPackageName(packageInfo.applicationInfo.packageName);
			apps.add(appInfo);
		}
		return apps;
	}
}
