package com.zzh.phonegurad.splash;

import java.util.ArrayList;
import java.util.List;


import com.zzh.phonguard.provider.SmsTelphoneGetContent;
import com.zzh.shoujiweishi.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TelphoneListAddBlackActivity extends ListActivity {
	
		private List<String> telphoneListNumbers = new ArrayList<String>();
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			ListView telList = getListView();//获取ListView
			//获取数据
			telphoneListNumbers = SmsTelphoneGetContent.getSmsContent(TelphoneListAddBlackActivity.this);
			MyTelAdpater adapter = new MyTelAdpater();
			telList.setAdapter(adapter);
			
			//条目点击事件
			telList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//一点击条目，就获取条目的信息
					String number = telphoneListNumbers.get(position);
					Intent intent = new Intent();
					intent.putExtra(SmsTelActivity.INTENTINTERFACE, number);
					setResult(0, intent);
					finish();
				}
			});
		}
		
		private  class MyTelAdpater extends BaseAdapter{

			@Override
			public int getCount() {
				return telphoneListNumbers.size();
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
				if(convertView == null){
					convertView = View.inflate(TelphoneListAddBlackActivity.this, R.layout.itme_telphonelist_telphonelistaddblack, null);
					holder = new ViewHolder();
					
					holder.tv_number = (TextView) convertView.findViewById(R.id.tv_item_telphoneblack_number);
					
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
					
				}
				
				holder.tv_number.setText(telphoneListNumbers.get(position));
				
				return convertView;
			}
			
		}
		
		private class ViewHolder {
			private TextView tv_number;
		}
}
