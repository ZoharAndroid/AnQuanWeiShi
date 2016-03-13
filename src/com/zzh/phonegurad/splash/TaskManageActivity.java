package com.zzh.phonegurad.splash;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzh.phoneguard.dao.TaskManageDao;
import com.zzh.phoneguard.domain.TaskInfo;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

/**
 * 进程管理
 * 
 * @author Administrator
 * 
 */
public class TaskManageActivity extends Activity {

	protected static final int LOADING = 1 << 0;

	protected static final int FINISH = 1 << 1;

	private RelativeLayout rl_loading;
	private LinearLayout ll_showing;
	private TextView tv_runTaskNum;
	private TextView tv_systemMemory;
	private ListView lv_tastShow;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOADING:// 显示加载界面
				rl_loading.setVisibility(View.VISIBLE);
				ll_showing.setVisibility(View.GONE);
				break;
			case FINISH:// 显示数据界面
				rl_loading.setVisibility(View.GONE);
				ll_showing.setVisibility(View.VISIBLE);
			default:
				break;
			}
		}
	};

	private List<TaskInfo> allTask;

	private List<TaskInfo> userTask;

	private List<TaskInfo> systemTask;

	private MyTaskAdapter adapter;

	private TextView tv_tag;

	private TaskInfo clickItemTask;

	private ActivityManager am;

	private long freeSize;

	private long totalSize;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	/**
	 * ListView的事件
	 */
	private void initEvent() {
		/**
		 * 条目点击事件
		 */
		// lv_tastShow.setOnItemClickListener(new OnItemClickListener() {
		//
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// //点击到TextView时，不进行相应
		// if(position == 0 || position == (userTask.size()+1)){
		// return;
		// }
		// //获得点击的条目
		// clickItemTask = (TaskInfo) lv_tastShow.getItemAtPosition(position);
		// boolean clickResult = clickItemTask.isChecked();
		// clickItemTask.setChecked(!clickResult);
		// if(clickItemTask.getAppPackageName().equals(getPackageName())){
		// clickItemTask.setChecked(false);
		// }
		// }
		// });
		/**
		 * ListView的滑动事件
		 */
		lv_tastShow.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem >= (userTask.size() + 1)) {
					tv_tag.setText("系统进程:(" + systemTask.size() + ")");
				} else {
					tv_tag.setText("用户进程:(" + userTask.size() + ")");
				}
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		allTask = new CopyOnWriteArrayList<TaskInfo>();
		userTask = new CopyOnWriteArrayList<TaskInfo>();
		systemTask = new CopyOnWriteArrayList<TaskInfo>();

		// 获取所有进程
		allTask = TaskManageDao.getAllTaskInfo(TaskManageActivity.this);
		// 分类为系统进程还是用户进程
		for (TaskInfo task : allTask) {
			if (task.isSystem()) {
				systemTask.add(task);
			} else {
				userTask.add(task);
			}
		}
		// 显示正在运行的软件和可用内存的大小
		tv_runTaskNum.setText("正在运行的进程:" + allTask.size() + "个");
		// 显示格式化后的内存大小
		showFormatMemory();

		adapter = new MyTaskAdapter();

		// ListView加载显示数据
		lv_tastShow.setAdapter(adapter);
	}

	/**
	 * 格式化内存大小并显示
	 */
	private void showFormatMemory() {
		freeSize = TaskManageDao.getFreeMemory(TaskManageActivity.this);
		totalSize = TaskManageDao.getTotalMemory(TaskManageActivity.this);

		String freeSizeStr = Formatter.formatFileSize(TaskManageActivity.this,
				freeSize);
		String totalSizeStr = Formatter.formatFileSize(TaskManageActivity.this,
				totalSize);
		// 格式化内存大小
		tv_systemMemory.setText("系统内存:" + freeSizeStr + "/" + totalSizeStr);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	/**
	 * ListView适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyTaskAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (sp.getBoolean(MyContasts.ISHIDESYSTEMTASK, false)) {// 如果为true
				return userTask.size() + 1;
			}
			return allTask.size() + 2;
		}

		@Override
		public TaskInfo getItem(int position) {
			TaskInfo itemTask = null;
			if (position <= userTask.size()) {
				itemTask = userTask.get(position - 1);
			} else {
				itemTask = systemTask.get(position - userTask.size() - 2);
			}
			return itemTask;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(TaskManageActivity.this);
				tv.setText("用户进程:(" + userTask.size() + ")");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				return tv;
			} else if (position == userTask.size() + 1) {
				TextView tv = new TextView(TaskManageActivity.this);
				tv.setText("系统进程:(" + systemTask.size() + ")");
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				return tv;
			} else {
				ViewHolder holder = null;
				if (convertView == null || convertView instanceof TextView) {
					convertView = View.inflate(TaskManageActivity.this,
							R.layout.item_taskmanage_list, null);
					holder = new ViewHolder();
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_item_taskmanage_appName);
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_item_taskmanage_icon);
					holder.tv_size = (TextView) convertView
							.findViewById(R.id.tv_item_taskmanage_size);
					holder.cb_select = (CheckBox) convertView
							.findViewById(R.id.cb_item_taskmanage_select);
					holder.rl_tasklist = (RelativeLayout) convertView
							.findViewById(R.id.rl_item_task_list);

					convertView.setTag(holder);

				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				final TaskInfo info = getItem(position);

				holder.tv_name.setText(info.getTaskName());
				holder.tv_size.setText(Formatter.formatFileSize(
						TaskManageActivity.this, info.getTaskSize()));
				holder.iv_icon.setImageDrawable(info.getTaskIcon());

				final ViewHolder hold = holder;

				// 条目点击事件
				holder.rl_tasklist.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (info.getAppPackageName().equals(getPackageName())) {
							return;
						}
						hold.cb_select.setChecked(!info.isChecked());// 点击了，取反
					}
				});

				// 获取当前的复选框状态
				holder.cb_select
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								info.setChecked(isChecked);
							}
						});

				holder.cb_select.setChecked(info.isChecked());

				if (info.getAppPackageName().equals(getPackageName())) {
					holder.cb_select.setVisibility(View.INVISIBLE);
				} else {
					holder.cb_select.setVisibility(View.VISIBLE);
				}

				return convertView;
			}
		}

	}

	public class ViewHolder {
		TextView tv_name;
		TextView tv_size;
		ImageView iv_icon;
		CheckBox cb_select;
		RelativeLayout rl_tasklist;
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_taskmanage);
		/**
		 * 加载界面和显示界面
		 */
		rl_loading = (RelativeLayout) findViewById(R.id.rl_taskmanamge_loading);
		ll_showing = (LinearLayout) findViewById(R.id.ll_tastmanage_showing);

		// 开启线程显示界面
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				msg.what = LOADING;// 加载界面
				handler.sendMessage(msg);
				// 让加载界面显示2秒钟
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};
		}.start();
		//
		/**
		 * 通过缓冲，找到其他界面
		 */
		// 显示正在运行的进程
		tv_runTaskNum = (TextView) findViewById(R.id.tv_taskmanage_runningtasknumber);
		// 显示内存大小
		tv_systemMemory = (TextView) findViewById(R.id.tv_taskmanage_systemmemory);
		// ListView显示软件列表
		lv_tastShow = (ListView) findViewById(R.id.lv_taskmanage_tastshow);
		// 标签
		tv_tag = (TextView) findViewById(R.id.tv_taskmanage_tag);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
	}

	/**
	 * 按键：全选
	 * 
	 * @param view
	 */
	public void allSelect(View view) {
		for (TaskInfo task : allTask) {
			if (task.getAppPackageName().equals(getPackageName())) {// 如果为自己，不勾选
				task.setChecked(false);
			} else {
				task.setChecked(true);// 否则全部勾选
			}
		}
		// 修改完毕后，通知适配器
		adapter.notifyDataSetChanged();
	}

	/**
	 * 按键：反选
	 * 
	 * @param view
	 */
	public void invertSelect(View view) {
		for (TaskInfo task : allTask) {
			if (task.getAppPackageName().equals(getPackageName())) {// 如果为自己，不勾选
				task.setChecked(false);
			} else {
				task.setChecked(!task.isChecked());// 否则全部勾选
			}
		}
		// 修改完毕后，通知适配器
		adapter.notifyDataSetChanged();
	}
	
	
	public void  clearTask(View view){
		clearTask();
	}

	/**
	 * 按键：清除进程
	 * 
	 * @param view
	 */
	public void clearTask() {
		int count = 0;// 清理的进程数
		long clearSize = 0; // 清理的大小
		for (TaskInfo info : userTask) {
			if (info.isChecked()) {
				// 如果勾选了,则可以清理
				am.killBackgroundProcesses(info.getAppPackageName());
				// 移除该进程
				userTask.remove(info);
				count++;
				clearSize += info.getTaskSize();
			}
		}
		for (TaskInfo info : systemTask) {
			if (info.isChecked()) {
				// 如果勾选了,则可以清理
				am.killBackgroundProcesses(info.getAppPackageName());
				// 移除该进程
				userTask.remove(info);
				count++;
				clearSize += info.getTaskSize();
			}
		}
		freeSize += clearSize;
		initData();// 重新加载数据
		ShowToast.showToast(
				TaskManageActivity.this,
				"安全卫士为您清理了"
						+ count
						+ "个进程,为您节约了"
						+ Formatter.formatFileSize(TaskManageActivity.this,
								clearSize) + "的内存", 1);
	}

	/**
	 * 按键：进程设置
	 * 
	 * @param view
	 */
	public void tastSetting(View view) {
		// 设置界面用途：1、是否显示系统进程；2、是否锁屏自动清理进程
		Intent intent = new Intent(TaskManageActivity.this,
				TaskSettingActivity.class);
		startActivity(intent);
	}
}
