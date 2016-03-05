package com.zzh.phonegurad.splash;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzh.phoneguard.dao.BlacklistNameDB;
import com.zzh.phoneguard.domain.BlacklistMember;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;
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
	
	private AlertDialog dialog;
	
	private BlacklistNameDB blackDB;
	
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
		initEvent();
	}
	
	/**
	 * 滑动的效果
	 */
	private void initEvent() {
		lv_listShowData.setOnScrollListener(new OnScrollListener() {
			/**
			 * 活动状态的改变
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_TOUCH_SCROLL://按着滑动
					
					break;
				case SCROLL_STATE_IDLE://停止时刻
					int lastVisibleIndex = view.getLastVisiblePosition();//获取最后的位置
					//判断是否为最后一条数据
					blackDB = BlacklistNameDB.getInstance(SmsTelActivity.this);
					if(lastVisibleIndex == (blackDB.getTotal()-1)){
						ShowToast.showToast(SmsTelActivity.this, "无数据要显示", 0);
						return;
					}
					
					break;
				case SCROLL_STATE_FLING://惯性滑动
					break;
				default:
					break;
				}
			}
			/**
			 * 正在滑动
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
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
	
	/**
	 * 添加黑名单按键
	 * 
	 * @param view
	 */
	public void addBlacklist(View view){
		//一点击，就弹出对话框，进行添加
		AlertDialog.Builder blackDialog = new AlertDialog.Builder(SmsTelActivity.this);
		View dialogView = View.inflate(SmsTelActivity.this, R.layout.setblacklist_dialog_smstel, null);
		
		//找到该对话框中的控件
		final EditText et_setBlack = (EditText) dialogView.findViewById(R.id.et_dialog_smstel_blackNumber);
		final CheckBox cb_setSms = (CheckBox) dialogView.findViewById(R.id.cb_dialog_smstel_setSms);
		final CheckBox cb_setPhone = (CheckBox) dialogView.findViewById(R.id.cb_dialog_smstel_setPhone);
		
		Button et_sureSetBlack = (Button) dialogView.findViewById(R.id.bt_dialog_smstel_sureSetBlack);
		/**
		 * 按键：确定设置黑名单
		 */
		et_sureSetBlack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//先判断用户是否输入了黑名单号码
				String blacklistNumber = et_setBlack.getText().toString().trim();
				if(TextUtils.isEmpty(blacklistNumber)){
					//如果用户没有设置黑名单
					ShowToast.showToast(SmsTelActivity.this, "请输入要拦截的手机号", 0);
					return;
				}
				//判断复选框是否输入了
				int mode = 0;
				if(cb_setSms.isChecked()){
					mode |= BlacklistMember.BLACK_SMS;
				}else if(cb_setPhone.isChecked()){
					mode |= BlacklistMember.BLACK_PHONE;
				}
				if(mode==0){
					ShowToast.showToast(SmsTelActivity.this, "请选择一种或两种拦截方式", 0);
					return;
				}
				//保存数据
				BlacklistMember listMember = new BlacklistMember(blacklistNumber, mode);
				blackDB = BlacklistNameDB.getInstance(SmsTelActivity.this);
				blackDB.addBlacklist(listMember);//添加到数据库中
				//members.add(0, listMember);
				members.clear();//清空listview内容的容器
				//重新加载数据
				members = blackDB.queryBlacklist();
				//通知适配器重新加载数据
				lv_listShowData.setSelection(members.size());
				//adapter.notifyDataSetChanged(); //这个会让界面不会跳动到开始
				dialog.dismiss();
			}
		});
		
		/**
		 * 按键：取消黑名单设置
		 */
		Button et_cancelSetBlack = (Button) dialogView.findViewById(R.id.bt_dialog_smstel_cancelSetBlack);
		et_cancelSetBlack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		blackDialog.setView(dialogView);
		dialog = blackDialog.create();
		dialog.show();
	}
	
	/**
	 * 自定义适配器
	 * 
	 * @author Administrator
	 *
	 */
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return members.size();
		}
		
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
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
			//设置删除图标的点击事件
			holder.iv_deleteIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					blackDB = BlacklistNameDB.getInstance(SmsTelActivity.this);
					AlertDialog.Builder notineDialog = new AlertDialog.Builder(SmsTelActivity.this);
					notineDialog.setTitle("提醒：");
					notineDialog.setMessage("确认删除:"+ members.get(position).getBlacklistNumber()+"?");
					notineDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							blackDB.removeBlacklist(members.get(position));//数据库中清除该内容
							//清空容器
							members.clear();
							//重新加载容器
							members = blackDB.queryBlacklist();
							adapter.notifyDataSetChanged();
						}
					});
					notineDialog.setNegativeButton("取消", null);
					notineDialog.show();
				}
			});
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
