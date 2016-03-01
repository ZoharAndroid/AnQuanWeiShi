package com.zzh.phoneguard.utils;

import android.app.Activity;
import android.widget.Toast;

public class ShowToast {
	public static void showToast(final Activity context, final String text, final int duration){
		if(Thread.currentThread().getName().equals("main")){
			//如果在主线程
			Toast.makeText(context, text, duration).show();
		}else{
			//如果在子线程
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(context, text, duration).show();
				}
			});
		}
	}
}
