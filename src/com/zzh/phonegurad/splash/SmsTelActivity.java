package com.zzh.phonegurad.splash;

import java.util.ArrayList;
import java.util.List;

import com.zzh.phoneguard.dao.BlacklistNameDB;
import com.zzh.phoneguard.domain.BlacklistMember;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 电话和短信拦截
 * @author Administrator
 *
 */
public class SmsTelActivity extends Activity {
	
	protected static final int NODATA =1;
	protected static final int SHOWDATA = 2;
	
	private RelativeLayout rl_loadingData;
	private RelativeLayout rl_noData;
	private RelativeLayout rl_showData;
	private ListView lv_listShowData;
	
	private List<BlacklistMember> members = new ArrayList<BlacklistMember>();
	
	private MyAdapter adapter;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NODATA://没有数据
				//就隐藏加载的界面
				rl_loadingData.setVisibility(View.GONE);
				//显示无数据界面
				rl_noData.setVisibility(View.VISIBLE);
				break;
			case SHOWDATA://有数据
				adapter.notifyDataSetChanged();
				rl_loadingData.setVisibility(View.GONE);
				rl_showData.setVisibility(View.VISIBLE);
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {
		//首先显示加载数据的界面
		rl_loadingData.setVisibility(View.VISIBLE);
		//开启线程读取数据库内容
		new Thread(){
			public void run() {
				long startTime =System.currentTimeMillis();
				//获取数据库
				BlacklistNameDB blacklistDB = BlacklistNameDB.getInstance(SmsTelActivity.this);
				//然后读取数据库中的内容
				members = blacklistDB.queryBlacklist();
				Message msg = new Message();
				//判断数据库是否包含了数据
				if (members.size()==0) {//如果为空
					//显示没有数据的界面
					msg.what=NODATA;
					long endTime =System.currentTimeMillis();
					if( ( endTime - startTime )< 2000){
						SystemClock.sleep(2000-(endTime - startTime));
					}
					handler.sendMessage(msg);
				}else{
					//显示数据界面
					msg.what=SHOWDATA;
					long endTime =System.currentTimeMillis();
					if( ( endTime - startTime )< 2000){
						SystemClock.sleep(2000-(endTime - startTime));
					}
					handler.sendMessage(msg);
				}
				
			};
		}.start();
	}

	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smstel);
		
		//加载控件
		rl_loadingData = (RelativeLayout) findViewById(R.id.rl_smstel_loadingData);
		rl_showData = (RelativeLayout) findViewById(R.id.rl_smstel_showData);
		rl_noData = (RelativeLayout) findViewById(R.id.rl_smstel_noData);
		
		lv_listShowData = (ListView) findViewById(R.id.lv_smstel_listShowData);
		
		adapter = new MyAdapter();
		lv_listShowData.setAdapter(adapter);
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return members.size();
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null ){
				convertView = View.inflate(SmsTelActivity.this, R.layout.item_smstel, null);
				holder = new ViewHolder();
				//然后获得该界面的布局控件
				holder.tv_blacknumber = (TextView) convertView.findViewById(R.id.tv_item_smstel_number);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_item_smstel_mode);
				holder.iv_deleteIcon = (ImageView) convertView.findViewById(R.id.iv_item_smstel_deleteicon);
				
				convertView.setTag(holder);//holder保存到convertView中
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			//设置数据
			BlacklistMember member = members.get(position);
			holder.tv_blacknumber.setText(member.getBlacklistNumber());
			switch (member.getMode()) {
			case BlacklistMember.BLACK_SMS:
				holder.tv_mode.setText("短信拦截");
				break;
			case BlacklistMember.BLACK_PHONE:
				holder.tv_mode.setText("电话拦截");
				break;
			case BlacklistMember.BLACK_ALL:
				holder.tv_mode.setText("电话和短信拦截");
			default:
				break;
			}
			return convertView;
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
	}
	
	private class ViewHolder{
		 TextView tv_blacknumber; //黑名单号码
		  TextView tv_mode;	//拦截模式
		 ImageView iv_deleteIcon;//删除图标
	}
}
