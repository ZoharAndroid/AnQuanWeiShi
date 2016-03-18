package com.zzh.phonegurad.splash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zzh.phoneguard.utils.SMSUtils;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

public class AToolsActivity extends Activity {
	protected static final int FINISH = 1;

	protected static final int LOADING = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_atools);

	}

	/**
	 * 电话归属地查询 按键
	 * 
	 * @param view
	 */
	public void phoneLocationCheck(View view) {
		Intent phoneCheckIntent = new Intent(AToolsActivity.this,
				PhoneLocationCheckActivity.class);
		startActivity(phoneCheckIntent);
	}

	/**
	 * 短信备份按键
	 * 
	 * @param view
	 */
	public void smsBackup(View view) {
		// 开始备份短信
		// 怎么备份短信?1.读取短信。2、写入本地(xml格式)

		if (SMSUtils.getSmsCount(AToolsActivity.this) == 0) {// 获取短信的条目为0
			ShowToast.showToast(AToolsActivity.this, "需要备份的短信为空", 0);
			return;
		}

		new Thread() {
			public void run() {
				try {
					// 从内容提供者那里获取短息等私有数据
					ContentResolver resolver = getContentResolver();
					Uri uri = Uri.parse("content://sms/");
					Cursor cursor = resolver.query(uri, new String[] {
							"address", "date", "type", "body" }, null, null,
							null);
					// 1.然后把得到的短信的内容保存为xml文件
					XmlSerializer serializer = Xml.newSerializer();

					// 2.设置序列化器的参数
					File file = new File(
							Environment.getExternalStorageDirectory(),
							"backup.xml");
					FileOutputStream fos = new FileOutputStream(file);
					serializer.setOutput(fos, "utf-8");

					// 写数据
					serializer.startDocument("utf-8", true);
					// 写整个xml文件的头
					serializer.startTag(null, "root");
					while (cursor.moveToNext()) {
						// 这是一条短信息的头
						serializer.startTag(null, "message");
						// 分别设置每条短信的内容
						// 1.发送地址
						serializer.startTag(null, "address");
						// 获取短信的地址
						String address = cursor.getString(0);
						// 设置给这个xml文件
						serializer.text(address);
						serializer.endTag(null, "address");

						// 2.发送时间
						serializer.startTag(null, "date");
						String date = cursor.getString(1);
						serializer.text(date);
						serializer.endTag(null, "date");

						// 3.设置短信的类型：是接收短信还是发送短信
						serializer.startTag(null, "type");
						String type = cursor.getString(2);
						serializer.text(type);
						serializer.endTag(null, "type");

						// 4.短信的内容
						serializer.startTag(null, "body");
						String body = cursor.getString(3);
						serializer.text(body);
						serializer.endTag(null, "body");

						// 至此，一条短信已将完成生成了xml文件了
						serializer.endTag(null, "message");
					}
					cursor.close();
					serializer.endTag(null, "root");
					serializer.endDocument();
					fos.close();
					ShowToast.showToast(AToolsActivity.this, "短信备份成功", 1);

				} catch (Exception e) {
					e.printStackTrace();
					ShowToast.showToast(AToolsActivity.this, "短信备份成功", 1);
				}

			};
		}.start();
	}

	/**
	 * 短信恢复按键
	 * 
	 * @param view
	 */
	public void smsResume(View view) {
		// 读取本地的xml文件,也就是解析xml文件
		// 存放到手机短信中
		// 要恢复短信，就是解析xml文件，读取备份中的文件
		// 先判断是否清楚以前旧的短信内容
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提醒：");
		builder.setMessage("是否清除手机中还保留的短信？");
		builder.setPositiveButton("清除", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Uri uri = Uri.parse("content://sms/");
				getContentResolver().delete(uri, null, null);
				// 恢复短信
				restore();
			}
		});

		builder.setNegativeButton("不清除", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 直接恢复短信
				restore();
			}
		});

		builder.show();
	}

	/**
	 * 还原所有备份的短信
	 */
	protected void restore() {
		try {
			// 也就是从sd卡中解析backup.xml文件，
			// 1、初始化一个xml解析器
			XmlPullParser parser = Xml.newPullParser();

			// 2.设置解析的参数
			File file = new File(Environment.getExternalStorageDirectory(),
					"backup.xml");
			FileInputStream inputStream = new FileInputStream(file);
			parser.setInput(inputStream, "utf-8");

			// 3.读取数据
			String address = null;
			String date = null;
			String type = null;
			String body = null;

			int parserType = parser.getEventType();
			while (parserType != XmlPullParser.END_DOCUMENT) {// 当文件没有在xml文件的末尾
				switch (parserType) {
				case XmlPullParser.START_TAG:
					if ("address".equals(parser.getName())) {
						address = parser.nextText();
					} else if ("date".equals(parser.getName())) {
						date = parser.nextText();
					} else if ("type".equals(parser.getName())) {
						type = parser.nextText();
					} else if ("body".equals(parser.getName())) {
						body = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("message".equals(parser.getName())) {
						// 到这里获取了一条完整的信息
						// 然后把信息数据加入到系统的短信应用数据库中
						ContentResolver resoler = getContentResolver();
						ContentValues values = new ContentValues();
						values.put("address", address);
						values.put("date", date);
						values.put("type", type);
						values.put("body", body);
						resoler.insert(Uri.parse("content://sms/"), values);
					}
					break;
				}
				parserType = parser.next();

			}
			Toast.makeText(this, "短信还原成功", 0).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "短信还原失败", 0).show();
		}
	}

	/**
	 * 程序锁按键
	 * 
	 * @param view
	 */
	public void appLock(View view) {

	}

	/**
	 * 抽屉菜单
	 * 
	 * @param view
	 */
	public void chouTiMenu(View view) {
		Intent intent = new Intent(AToolsActivity.this, ChouTiActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
