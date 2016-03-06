package com.zzh.phonegurad.splash;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zzh.phonguard.provider.SmsTelphoneGetContent;
import com.zzh.shoujiweishi.R;

public class SmsListAddBlackActivity extends ListActivity {
	
	private List<String> smsListNumbers = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ListView smsList = getListView();//获取ListView
		//获取数据
		smsListNumbers = SmsTelphoneGetContent.getSmsContent(SmsListAddBlackActivity.this);
		MySmsAdpater adapter = new MySmsAdpater();
		smsList.setAdapter(adapter);
		
		//条目点击事件
		smsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//一点击条目，就获取条目的信息
				String number = smsListNumbers.get(position);
				Intent intent = new Intent();
				intent.putExtra(SmsTelActivity.INTENTINTERFACE, number);
				setResult(0, intent);
				finish();
			}
		});
	}
	
	private  class MySmsAdpater extends BaseAdapter{

		@Override
		public int getCount() {
			return smsListNumbers.size();
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
				convertView = View.inflate(SmsListAddBlackActivity.this, R.layout.itme_smslist_smslistaddblack, null);
				holder = new ViewHolder();
				
				holder.tv_number = (TextView) convertView.findViewById(R.id.tv_item_smsblack_number);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
				
			}
			
			holder.tv_number.setText(smsListNumbers.get(position));
			
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		private TextView tv_number;
	}
}
