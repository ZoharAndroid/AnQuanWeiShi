package com.zzh.phoneguard.utils;

import android.app.Activity;
import android.widget.Toast;

public class ShowToast {
	public static void showToast(final Activity context, final String text, final int duration){
		if(Thread.currentThread().getName().equals("main")){
			//如果在主线程main中，直接toast显示
			Toast.makeText(context, text, duration).show();
		}else{
			//在子线程中，则运行在
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(context, text, duration).show();
				}
			});
		}
	}
}
