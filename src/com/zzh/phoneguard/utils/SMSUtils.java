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
	public static void SMSResume(Context context, ProgressDialog pb) {
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
				SystemClock.sleep(200);
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

	// public static void SMSResume(Context context,ProgressDialog pb) {
	// // 通过内容提供者，获取短信内容
	// try {
	// //新建一个xml解析器
	// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	// XmlPullParser parser = factory.newPullParser();
	// //XmlPullParser parser = Xml.newPullParser();
	// //设置xml解析参数
	// File file = new File(Environment.getExternalStorageDirectory(),
	// "smsbackup.xml");
	// parser.setInput(new FileInputStream(file), "utf-8");
	// //读取数据
	// int eventType = parser.getEventType();
	// int number = getSmsCount(context);
	// int process = 0;
	// pb.setMax(number);
	// String address = "";
	// String type="";
	// String body="";
	// String date="";
	// while(eventType != parser.END_DOCUMENT){
	// String nodeName = parser.getName();
	// switch (eventType) {
	// //开始解析某个节点
	// case XmlPullParser.START_TAG:
	// if("address".equals(nodeName)){
	// address = parser.nextText();
	// }else if ("type".equals(nodeName)){
	// type = parser.nextText();
	// }else if("body".equals(nodeName)){
	// body = parser.nextText();
	// }else if("date".equals(nodeName)){
	// date = parser.nextText();
	// }
	// break;
	// //完成解析某个节点
	// case XmlPullParser.END_TAG:
	// if("sms".equals(nodeName)){
	// Uri uri = Uri.parse("content://sms");
	// ContentValues values = new ContentValues();
	// values.put("address", address);
	// values.put("type", type);
	// values.put("body", body);
	// values.put("date", date);
	// System.out.println(address + type + body + date +">>>>>>>>>>>");
	// context.getContentResolver().insert(uri, values);
	// SystemClock.sleep(200);
	// pb.setProgress(++process);
	// }
	// break;
	// default:
	// break;
	// }
	// parser.next();
	// }
	// pb.dismiss();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (XmlPullParserException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	public static int getSmsCount(Context context) {
		int number = 0;
		Uri uri = Uri.parse("content://sms");
		// 获取短信的内容的address：号码；type：发送还是接受;body：短信内容；date：日期
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "address", "type", "body", "date" }, null, null,
				null);
		number = cursor.getCount();
		return number;
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param pb
	 *            ProgressDialog进度条
	 */
	public static void SMSBackup(Context context, ProgressDialog pb) {
		// 通过内容提供者，获取短信内容
		try {
			Uri uri = Uri.parse("content://sms");
			// 获取短信的内容的address：号码；type：发送还是接受;body：短信内容；date：日期
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "address", "type", "body", "date" }, null,
					null, null);

			XmlSerializer serialize = Xml.newSerializer();
			// 设置序列化器的参数
			File file = new File(Environment.getExternalStorageDirectory(),
					"smsbackup.xml");
			serialize.setOutput(new FileOutputStream(file), "utf-8");
			serialize.startDocument("utf-8", true);
			serialize.startTag("", "smses");
			serialize.attribute("", "count", cursor.getCount() + "");// 获取短信的条目
			pb.setMax(cursor.getCount());// 设置最大值

			int count = 1;
			while (cursor.moveToNext()) {
				serialize.startTag("", "sms");

				serialize.startTag("", "address");
				serialize.text(cursor.getString(0));
				serialize.endTag("", "address");

				serialize.startTag("", "type");
				serialize.text(cursor.getString(1));
				serialize.endTag("", "type");

				serialize.startTag("", "boady");
				serialize.text(cursor.getString(2));
				serialize.endTag("", "boady");

				serialize.startTag("", "date");
				serialize.text(cursor.getString(3));
				serialize.endTag("", "date");

				serialize.endTag("", "sms");

				SystemClock.sleep(200);
				pb.setProgress(count++);
			}
			cursor.close();
			serialize.endTag("", "smses");
			serialize.endDocument();
			pb.dismiss();

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

	/**
	 * 短信的备份
	 */
	public static void SMSBackup(Context context) {
		// 通过内容提供者，获取短信内容
		try {
			Uri uri = Uri.parse("content://sms");
			// 获取短信的内容的address：号码；type：发送还是接受;body：短信内容；date：日期
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "address", "type", "body", "date" }, null,
					null, null);

			XmlSerializer serialize = Xml.newSerializer();
			// 设置序列化器的参数
			File file = new File(Environment.getExternalStorageDirectory(),
					"smsbackup.xml");
			serialize.setOutput(new FileOutputStream(file), "utf-8");
			serialize.startDocument("utf-8", true);
			serialize.startTag("", "smses");
			serialize.attribute("", "count", cursor.getCount() + "");// 获取短信的条目
			while (cursor.moveToNext()) {
				serialize.startTag("", "sms");

				serialize.startTag("", "address");
				serialize.text(cursor.getString(0));
				serialize.endTag("", "address");

				serialize.startTag("", "type");
				serialize.text(cursor.getString(1));
				serialize.endTag("", "type");

				serialize.startTag("", "boady");
				serialize.text(cursor.getString(2));
				serialize.endTag("", "boady");

				serialize.startTag("", "date");
				serialize.text(cursor.getString(3));
				serialize.endTag("", "date");

				serialize.endTag("", "sms");
			}
			cursor.close();
			serialize.endTag("", "smses");
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
