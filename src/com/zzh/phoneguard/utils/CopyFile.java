package com.zzh.phoneguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class CopyFile {
	/**
	 * 复制asset目录下的文件 到files/目录下
	 * @param context
	 * @param fileName
	 */
	public static void copyAssetFile(Context context, String fileName) {
		AssetManager assetManager = context.getAssets();
		
		try {
			InputStream is = assetManager.open(fileName);
			FileOutputStream fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
			// 开始复制到指定的目录下
			copy(is, fos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 流复制
	 * @param is
	 * @param os
	 */
	public static void copy(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				len = is.read(buffer);
			}
			is.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 文件复制
	 * @param file1
	 * @param file2
	 */
	public static void copy(File file1, File file2){
		try {
			copy(new FileInputStream(file1), new FileOutputStream(file2));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 通过路径复制
	 * @param path1
	 * @param path2
	 */
	public static void copy(String path1, String path2){
		copy(new File(path1), new File(path2));
	}
}
