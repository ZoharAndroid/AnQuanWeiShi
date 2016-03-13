package com.zzh.phoneguard.service;

import java.util.List;

import com.zzh.phoneguard.dao.TaskManageDao;
import com.zzh.phoneguard.domain.TaskInfo;
import com.zzh.phoneguard.utils.LogUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockClearMemService extends Service {
	
	private LockScreenRecevier receiver;

	@Override
	public void onCreate() {
		super.onCreate();
		//创建一个接收广播接收器，用户接收锁屏的广播
		receiver = new LockScreenRecevier();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		LogUtil.d("LockClearMemService", "服务创建成功>>>>>>>>>>>>>>>>");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * 创建一个广播接收器
	 * 
	 * @author Administrator
	 *
	 */
	public class LockScreenRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//一接收到锁屏的广播，就清理内存
			ActivityManager am  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<TaskInfo> tasks  = TaskManageDao.getAllTaskInfo(context);
			List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
			for (RunningAppProcessInfo process : runningAppProcesses) {
				am.killBackgroundProcesses(process.processName);
			}
			LogUtil.d("LockClearMemService", "锁屏清理完成>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}

}
