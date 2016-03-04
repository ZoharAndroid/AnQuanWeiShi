package com.zzh.phonegurad.splash;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zzh.phoneguard.domain.ContactInfo;
import com.zzh.phoneguard.utils.ContactUtils;
import com.zzh.shoujiweishi.R;

public class ListContactsActvity extends ListActivity {

	private List<ContactInfo> contacts;// 所有手机联系人信息

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 获取ListView
		listView = getListView();
		// 获取手机联系人
		contacts = ContactUtils.getAllContactInfo(this);// 获取所有手机联系人

		MyListApdater adapter = new MyListApdater();
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取号码
				String number = contacts.get(position).getPhone();
				Intent intent = new Intent();
				intent.putExtra("phoneNumber", number);
				setResult(0, intent);
				// 销毁这个界面
				finish();
			}

		});

	}

	class MyListApdater extends BaseAdapter {

		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ListContactsActvity.this,
						R.layout.itme_contactslist_listactivity, null);
				holder = new ViewHolder();
				// 初始化ViewHolder中的控件
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_item_contacts_name);
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_item_contacts_number);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_name.setText(contacts.get(position).getName());
			holder.tv_number.setText(contacts.get(position).getPhone());

			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_name;// 手机联系人
		TextView tv_number; // 手机号
	}
}
