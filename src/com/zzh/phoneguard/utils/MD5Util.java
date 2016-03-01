package com.zzh.phoneguard.utils;

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
}
