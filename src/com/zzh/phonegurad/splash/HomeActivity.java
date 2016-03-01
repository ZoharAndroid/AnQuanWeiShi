package com.zzh.phonegurad.splash;

import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private GridView gv_jiugongge;// 九宫格显示
	String[] iconName = new String[] { "手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计",
			"手机杀毒", "缓存清理", "高级工具", "设置中心" };
	int[] icon = new int[] { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.icon_selector, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.safe, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();// 初始化界面
		initData();// 初始化界面数据
	}

	private void initData() {
		GridViewAdapter adapter = new GridViewAdapter();
		gv_jiugongge.setAdapter(adapter);
	}

	private void initView() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_home);

		gv_jiugongge = (GridView) findViewById(R.id.gv_home_jiugongge);
	}

	/**
	 * 创建一个GridView配置器
	 * 
	 * @author Administrator
	 * 
	 */
	public class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return iconName.length;
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
			ViewHolder viewHolder=null;
			if(convertView == null){
				convertView = View.inflate(HomeActivity.this, R.layout.item_home_gridview, null);
				viewHolder = new ViewHolder();
				viewHolder.iv=(ImageView) convertView.findViewById(R.id.iv_griadview_item_icon);
				viewHolder.tv=(TextView) convertView.findViewById(R.id.tv_griadview_item_iconName);
				convertView.setTag(viewHolder);//将viewHolder储存在view中
			}else{
				viewHolder=(ViewHolder) convertView.getTag();//从view中获取到ViewHolder
			}
			
			viewHolder.iv.setBackgroundResource(icon[position]);
			viewHolder.tv.setText(iconName[position]);
			
			return convertView;
		}

	}
	
	private class ViewHolder{
		ImageView iv;
		TextView tv;
	}

}
