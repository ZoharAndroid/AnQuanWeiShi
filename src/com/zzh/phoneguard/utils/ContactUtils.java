package com.zzh.phoneguard.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zzh.phoneguard.domain.ContactInfo;

/**
 * 读取联系人的工具类
 * 
 * @author Administrator
 * 
 */
public class ContactUtils {
	/**
	 * 获取所有联系人的信息
	 * 
	 * @param context
	 *            上下文
	 * @return 返回包含有联系人信息对象的集合
	 */
	public static List<ContactInfo> getAllContactInfo(Context context) {
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		// 获取手机中联系人的数据库
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			System.out.println("联系人的条目id：" + id);
			ContactInfo contactInfo = new ContactInfo();// 一个联系人条目对应一个联系人对象
			contactInfo.setId(id); // 把id值放入该联系人对象的成员属性中
			if (id != null) {
				Cursor dataCursor = resolver.query(dataUri, new String[] {
						"mimetype", "data1" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String data1 = dataCursor.getString(1);
					String mimetype = dataCursor.getString(0);
					if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {// 邮箱
						contactInfo.setEmail(data1);
					} else if ("vnd.android.cursor.item/im".equals(data1)) {// 即时通讯
						contactInfo.setQq(data1);
					} else if ("vnd.android.cursor.item/phone_v2"
							.equals(mimetype)) {// 手机号码
						contactInfo.setPhone(data1);
					} else if ("vnd.android.cursor.item/postal-address_v2"
							.equals(mimetype)) {// 联系人地址
						contactInfo.setAddress(data1);
					} else if ("vnd.android.cursor.item/name".equals(mimetype)) {// 联系人姓名
						contactInfo.setName(data1);
					}
				}

				// 一个联系人信息都添加完毕之后，就应该添加到集合中
				infos.add(contactInfo);
				dataCursor.close();
			}
		}
		cursor.close();
		// 返回包含所有联系人的集合
		return infos;
	}
}
