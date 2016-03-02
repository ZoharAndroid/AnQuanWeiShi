package com.zzh.phonegurad.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;
/**
 * setupX界面跳转活动抽象类
 * @author Administrator
 *
 */
public abstract class SetupXBaseActivity extends Activity {
	
	private GestureDetector guesture;//手势
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		guesture = new GestureDetector(new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
		/**
		e1  The first down motion event that started the fling. 
		e2  The move motion event that triggered the current onFling. 
		velocityX  The velocity of this fling measured in pixels per second along the x axis. 
		velocityY  The velocity of this fling measured in pixels per second along the y axis. 
		 */
				if(Math.abs(velocityX)>50){
					if(e1.getX()-e2.getX()>50){//从右向左滑动
						nextSetup(null);
					}else if(e2.getX() - e1.getX()>50){//从左往右滑动
						previousSetup(null);
					}
				}else{
					ShowToast.showToast(SetupXBaseActivity.this, "滑动速度太慢", 0);
				}
				return true;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		guesture.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	/**
	 * 点击上一步要实现的事件
	 */
	public abstract void previousEvent();
	/**
	 * 点击下一步要实现的事件
	 */
	public abstract void nextEvent();
	
	/**
	 * 按键 点击:上一步
	 * @param view
	 */
	public void previousSetup(View view){
		//点击上一步要实现的事件
		previousEvent();
		//实现点击界面效果，一定要放在事件的后面，否则动画效果出不来
		overridePendingTransition(R.drawable.previous_in_setup, R.drawable.previous_out_setup);
	}
	
	/**
	 * 按键 点击:下一步
	 * @param view
	 */
	public void nextSetup(View view){
		//点击下一步要实现的事件
		nextEvent();
		//实现点击界面效果
		overridePendingTransition(R.drawable.next_in_setup, R.drawable.next_out_setup);
	}
	
	public void startActivityAndFinish(Class type){
		Intent intent = new Intent(SetupXBaseActivity.this, type);
		startActivity(intent);
		finish();
	}
	
}
