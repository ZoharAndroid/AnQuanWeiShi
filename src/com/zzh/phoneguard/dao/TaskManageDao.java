package com.zzh.phoneguard.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.zzh.phoneguard.domain.TaskInfo;

/**
 * 进程任务的工具类
 * 
 * @author Administrator
 * 
 */
public class TaskManageDao {
	/**
	 * 获取可用内存大小
	 * 
	 * @param context
	 *            上下文
	 * @return long 字节大小
	 */
	public static long getFreeMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	/**
	 * 获取总的内存大小
	 * 
	 * @param context
	 *            上下文
	 * 
	 * @return 以byte为单位的long的内存大小
	 */
	public static long getTotalMemory(Context context) {

		// ActivityManager am = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// MemoryInfo outInfo = new MemoryInfo();
		// am.getMemoryInfo(outInfo);
		// return outInfo.totalMem;
		// 由于上面的代码只有在API16的时候才能使用，所以为了能够兼容低版本的手机，才用读取"/proc/meminfo/"来获得总的内存大小
		long size = 0;
		
		try {
			// 从这个文件读取出来
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));// 从文件中读取字节流数据
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));// 字节流转换成字符流
			// 读取该文件的第一行，就是内存总的大小
			String memInfo = reader.readLine();// MemTotal: 513492 kB
			// 解析这个字符串
			// 首先将这个字符串转化为字节数组
			char c = 0;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < memInfo.length(); i++) {
				c = memInfo.charAt(i);
				if(c >= '0' && c <='9'){
					sb.append(c);
				}
			}
			reader.close();
			size = Long.parseLong(sb.toString()) * 1024;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 获取所有进程的数据 每个进程包含：图片、软件名、包名、路径名、软件大小信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getAllTaskInfo(Context context) {
		List<TaskInfo> tasks = new CopyOnWriteArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();// 获取包的管理器
		// 获取所有正在运行的进程
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		// 遍历该进程获取进程的信息
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//包名
			String packageName = info.processName;// 获取进程名，也就是包名
			TaskInfo task = new TaskInfo();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
				//软件名
				String name = (String) packageInfo.applicationInfo.loadLabel(pm);
				//图标
				Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
				//软件路径
				String path = packageInfo.applicationInfo.sourceDir;
				//获取运行的内存大小
				android.os.Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
				long size = memoryInfo[0].getTotalPrivateDirty() * 1024;
				//判断是用户进程还是系统进程
				int flag = packageInfo.applicationInfo.flags;
				if((flag & packageInfo.applicationInfo.FLAG_SYSTEM)!=0){
					//是系统的进程
					task.setSystem(true);
				}else{
					task.setSystem(false);
				}
				
				task.setAppPackageName(packageName);
				task.setAppPath(path);
				task.setTaskIcon(icon);
				task.setTaskName(name);
				task.setTaskSize(size);
				
				tasks.add(task);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tasks;
	}
}
