package com.zzh.phoneguard.utils;

import android.app.Activity;
import android.widget.Toast;

public class ShowToast {
	public static void showToast(final Activity context, final String text, final int duration){
		if(Thread.currentThread().getName().equals("main")){
			//��������߳�main�У�ֱ��toast��ʾ
			Toast.makeText(context, text, duration).show();
		}else{
			//�����߳��У���������
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(context, text, duration).show();
				}
			});
		}
	}
}
