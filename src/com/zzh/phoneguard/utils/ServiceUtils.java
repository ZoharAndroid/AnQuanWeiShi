package com.zzh.phoneguard.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 服务工具类
 * @author Administrator
 *
 */
public class ServiceUtils {
	/**
	 * 判断当前服务是否正在运行
	 * @param <T>
	 * 
	 * @param context
	 * @param serviceClassName 需要判断的服务类
	 * @return true:包含当前的服务；false：不包含当前的服务
	 */
	public static  boolean isServiceRun(Context context, Class serviceClassName){
		boolean result = false;
		//获得所有的服务
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceInfos = activityManager.getRunningServices(50);
		for(RunningServiceInfo serviceInfo : serviceInfos){
			if(serviceInfo.service.getClass() ==  serviceClassName){
				result =  true;
				break;
			}
		}
		return result;
	}
}
