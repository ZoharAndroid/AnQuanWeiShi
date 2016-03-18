package com.zzh.phoneguard.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

/**
 * 短信的备份和恢复的工具类
 * 
 * @author Administrator
 * 
 */
public class SMSUtils {
	
	public static void smsResume(Context context, ProgressCallBack pd) {
		Uri uri = Uri.parse("content://sms");
		// 短信dom4j
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(Environment
					.getExternalStorageDirectory(), "sms.xml"));
			Element root = doc.getRootElement();
			
			List elements = root.elements("sms");
			Iterator<Element> elementIterator = elements.iterator();
			pd.setMax(elements.size());
			int number = 0;
			while (elementIterator.hasNext()) {
				SystemClock.sleep(300);
				Element smsEle = elementIterator.next();
				// 恢复短信
				ContentValues values = new ContentValues();
				values.put("address", smsEle.elementText("address"));
				values.put("type", smsEle.elementText("type"));
				values.put("date", smsEle.elementText("date"));
				values.put("body", smsEle.elementText("body"));
				
				
				//保存信息到短信数据中
				context.getContentResolver().insert(uri, values);
				pd.setProgress(++number);
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 短信恢复
	 * 
	 * @param context
	 * @param pb
	 */
	public static void SMSResume(Context context, ProgressCallBack pb) {
		try {
			Uri uri = Uri.parse("content://sms");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File(Environment.getExternalStorageDirectory(),
					"smsbackup.xml"));
			Element root = doc.getRootElement();
			List<Element> elements =   root.elements("sms");
			Iterator<Element> iteratorElements = elements.iterator();
			pb.setMax(elements.size());
			int number = 1;
			while(iteratorElements.hasNext()){
				SystemClock.sleep(300);
				Element smsElement = iteratorElements.next();
				ContentValues values = new ContentValues();
				 values.put("address", smsElement.elementText("address"));
				 values.put("type", smsElement.elementText("type"));
				 values.put("body", smsElement.elementText("body"));
				 values.put("date", smsElement.elementText("date"));
				 context.getContentResolver().insert(uri, values);
				 pb.setProgress(number++);
			}
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取短信的条目
	 * 
	 * @param context
	 * @return
	 */
	public static int getSmsCount(Context context) {
		int number = 0;
		Uri uri = Uri.parse("content://sms");
		// 获取短信的内容的address：号码；type：发送还是接受;body：短信内容；date：日期
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "address", "type", "body", "date" }, null, null,
				null);
		number = cursor.getCount();
		cursor.close();
		return number;
	}
	
	
	public static void smsBake(Context context, ProgressCallBack pd) {
		Uri uri = Uri.parse("content://sms");
		XmlSerializer newSerializer = Xml.newSerializer();
		File file = new File(Environment.getExternalStorageDirectory(),
				"sms.xml");

		try {
			newSerializer.setOutput(new FileOutputStream(file), "utf-8");
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "address", "type", "date", "body" }, null,
					null, null);
			newSerializer.startDocument("utf-8", true);// 只有xml语法没问题
			newSerializer.startTag(null, "smses");// 设置跟标记
			int count = cursor.getCount();
			// 设置对话框最大值
			pd.setMax(count);

			newSerializer.attribute(null, "count", count + "");
			int number = 0;
			while (cursor.moveToNext()) {
				// 取每条短信
				newSerializer.startTag(null, "sms");// 一条短信的开始
				
				// address 无名语句块   方法中  类中
				{
					newSerializer.startTag(null, "address");
					newSerializer.text(cursor.getString(0));
					newSerializer.endTag(null, "address");
				}
				// type
				{
					newSerializer.startTag(null, "type");
					newSerializer.text(cursor.getString(1));
					newSerializer.endTag(null, "type");
				}
				// date
				{
					newSerializer.startTag(null, "date");
					newSerializer.text(cursor.getString(2));
					newSerializer.endTag(null, "date");
				}
				// body
				{
					newSerializer.startTag(null, "body");
					newSerializer.text(cursor.getString(3));
					newSerializer.endTag(null, "body");
				}

				SystemClock.sleep(500);
				newSerializer.endTag(null, "sms");// 一条短信的开始
				pd.setProgress(++number);
				// 保存短信
			}// end while
			cursor.close();

			newSerializer.endTag(null, "smses");// 结束根标记
			newSerializer.endDocument();// 结束文档
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param pb
	 *            ProgressDialog进度条
	 */
	public static void SMSBackup(Context context, ProgressCallBack pb) {
		// 通过内容提供者，获取短信内容
		try {
			Uri uri = Uri.parse("content://sms/");
			// 获取短信的内容的address：号码；type：发送还是接受;body：短信内容；date：日期
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "address", "type", "date" , "body"}, null,
					null, null);

			XmlSerializer serialize = Xml.newSerializer();
			// 设置序列化器的参数
			File file = new File(Environment.getExternalStorageDirectory(),
					"smsbackup.xml");
			serialize.setOutput(new FileOutputStream(file), "utf-8");
			serialize.startDocument("utf-8", true);
			serialize.startTag(null, "smses");
			serialize.attribute(null, "count", cursor.getCount() + "");// 获取短信的条目
			
			pb.setMax(cursor.getCount());// 设置最大值
			
			int count = 1;
			while (cursor.moveToNext()) {
				serialize.startTag(null, "sms");

				serialize.startTag(null, "address");
				serialize.text(cursor.getString(0));
				serialize.endTag(null, "address");

				serialize.startTag(null, "type");
				serialize.text(cursor.getString(1));
				serialize.endTag(null, "type");


				serialize.startTag(null, "date");
				serialize.text(cursor.getString(2));
				serialize.endTag(null, "date");
				
				serialize.startTag(null, "body");
				serialize.text(cursor.getString(3));
				serialize.endTag(null, "body");

				serialize.endTag(null, "sms");

				SystemClock.sleep(500);
				pb.setProgress(count++);
			}
			cursor.close();
			serialize.endTag(null, "smses");
			serialize.endDocument();
			//pb.dismiss();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public interface ProgressCallBack {
		void setProgress(int currentProgress);

		void setMax(int max);
	}

	/**
	 * 短信的备份
	 */
	public static void SMSBackup(Context context) {
		// 通过内容提供者，获取短信内容
		try {
			Uri uri = Uri.parse("content://sms");
			// 获取短信的内容的address：号码；type：发送还是接受;body：短信内容；date：日期
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "address", "body", "date", "type" }, null,
					null, null);

			XmlSerializer serialize = Xml.newSerializer();
			// 设置序列化器的参数
			File file = new File(Environment.getExternalStorageDirectory(),
					"smsbackup.xml");
			serialize.setOutput(new FileOutputStream(file), "utf-8");
			serialize.startDocument("utf-8", true);
			serialize.startTag(null, "smses");
			serialize.attribute(null, "count", cursor.getCount() + "");// 获取短信的条目
			while (cursor.moveToNext()) {
				serialize.startTag(null, "sms");

				serialize.startTag(null, "address");
				serialize.text(cursor.getString(0));
				serialize.endTag(null, "address");

				serialize.startTag(null, "body");
				serialize.text(cursor.getString(1));
				serialize.endTag(null, "body");
				
				serialize.startTag(null, "date");
				serialize.text(cursor.getString(2));
				serialize.endTag(null, "date");
				
				serialize.startTag(null, "type");
				serialize.text(cursor.getString(3));
				serialize.endTag(null, "type");

				serialize.endTag(null, "sms");
			}
			cursor.close();
			serialize.endTag(null, "smses");
			serialize.endDocument();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
