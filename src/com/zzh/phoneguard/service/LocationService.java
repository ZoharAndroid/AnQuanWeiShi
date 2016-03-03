package com.zzh.phoneguard.service;

import java.util.List;

import com.zzh.phonegurad.splash.MyContasts;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	private String provider;//地理位置提供器
	
	private SharedPreferences sp;
	
	private SmsManager smsManager;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/**
	 * 每次创建服务的时候调用
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		smsManager = SmsManager.getDefault();
		//获取地理位置信息
		LocationManager location = (LocationManager) getSystemService(LOCATION_SERVICE);
		//获取位置提供器
		List<String> providerList = location.getProviders(true);
		if(providerList.contains(LocationManager.GPS_PROVIDER)){
			provider=LocationManager.GPS_PROVIDER;
		}else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
			provider=LocationManager.NETWORK_PROVIDER;
		}else{
			//发送短信给安全号码，提示没有开启gps和网络
			String safeNumber = sp.getString(MyContasts.SAFENUMBER, "");
			smsManager.sendTextMessage(safeNumber, null, "Don't open GPS or Network service", null, null);
			//然后关闭该服务，节约电量
			stopSelf();
		}
		
		location.requestLocationUpdates(provider, 0	, 0, locationListenr);
	}
	
	LocationListener locationListenr = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			//一旦发生位置改变，就发送地理位置的短信给安全号码
			//获取地理位置信息
			StringBuffer locationInfo = new StringBuffer();
			locationInfo.append("Accuracy:"+location.getAccuracy()+"\n")//精度
			.append("Latitude:"+location.getLatitude()+"\n")//纬度
			.append("Longitude:"+location.getLongitude()+"\n")//经度
			.append("Altitude:"+location.getAltitude());//海拔
			
			//获取安全号码
			String safeNumber = sp.getString(MyContasts.SAFENUMBER, "");
			//获得一个短信管理器
			smsManager.sendTextMessage(safeNumber, null, locationInfo.toString(), null, null);
			//然后停止这个服务
			stopSelf();
		}
	};

	/**
	 * 每次启动服务的时候调用
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}
