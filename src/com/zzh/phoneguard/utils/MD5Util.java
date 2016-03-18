package com.zzh.phoneguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	/**
	 * MD5加密
	 * @param resoure
	 * @return 
	 */
	public static String MD5Lock(String resoure) {
		String res="";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] bys=md.digest(resoure.getBytes());
			
			for(byte b : bys){
				int data = b & 0xff;
				String hex = Integer.toHexString(data);
				if(hex.length()==1){
					hex="0"+hex;
				}
				res+=hex;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 获取文件的MD5值
	 * @param file 文件
	 * @return	返回文件的MD5值
	 */
	public static String getMD5(File file){
		StringBuffer result = new StringBuffer();
		//首先获取MD5的处理方法
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			FileInputStream fis = new FileInputStream(file);
			byte[] bts  = new byte[1024];
			int len = 0;
			while((len = fis.read(bts))!= -1 ){
				instance.update(bts,0,len);
			}
			//将实例消化吸收
			byte[] digest = instance.digest();
			//进行分解
			for(byte b : digest){
				String hex = 	Integer.toHexString(b & 0xff);
				//判断是否为首位为零
				if(hex.length() == 1){
					hex = "0"+hex;
				}
				result .append(hex);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}
}
