package com.zzh.phonegurad.splash;

import java.lang.reflect.Modifier;

import com.zzh.phoneguard.utils.LogUtil;
import com.zzh.phoneguard.utils.MD5Util;
import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	public static final String TAG = "HomeActivity";

	private Dialog dialog;

	private AlertDialog.Builder newDialog;

	private SharedPreferences sp;// 保存设置的密码

	private GridView gv_jiugongge;// 九宫格显示
	
	private GridViewAdapter adapter;//适配器

	
	String[] iconName = new String[] { "手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计",
			"手机杀毒", "缓存清理", "高级工具", "设置中心" };
	int[] icon = new int[] { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.icon_selector, R.drawable.taskmanager,
			R.drawable.netmanager, R.drawable.safe, R.drawable.sysoptimize,
			R.drawable.atools, R.drawable.settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();// 初始化界面
		initData();// 初始化界面数据
		processClickEvent();
	}
	

	@Override
	protected void onResume() {
		//通知适配器，重新检索数据
		super.onResume();
		System.out.println("onResume");
		adapter.notifyDataSetChanged();
	}


	private void initData() {
		adapter = new GridViewAdapter();
		gv_jiugongge.setAdapter(adapter);
	}

	private void initView() {
		// 全屏设置
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_home);
		// 找到控件
		gv_jiugongge = (GridView) findViewById(R.id.gv_home_jiugongge);
	}

	/**
	 * 处理主界面中的条目的各个点击事件
	 */
	private void processClickEvent() {
		// 设置点击事件
		gv_jiugongge.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:// 点击的第0个条目,进入“手机防盗”
						// 判断是否设置了密码
					if (isSetPassword()) {
						// 如果设置了密码，就弹出输入密码对话框
						showInputPasswordDialog();
					} else {
						// 如果没有设置密码，就弹出设置新密码的对话框
						showSetPasswordDialog();
					}
					break;
				case 1://通讯卫士
					Intent smstelIntent = new Intent(HomeActivity.this, SmsTelActivity.class);
					startActivity(smstelIntent);
					break;
				case 8:// 进入设置中心
					Intent settingIntent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(settingIntent);
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * 断是否设置了密码
	 * 
	 * @return
	 */
	protected boolean isSetPassword() {
		// 没有密码，就进入设置新密码的对话框
		boolean result = false;
		sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
		// 从sp文件中读取密码信息
		String password = sp.getString(MyContasts.PASSWORD, "");
		if (!TextUtils.isEmpty(password)) {
			// 有密码，就进入输入密码对话框
			result = true;
		}
		return result;
	}

	/**
	 * 如果没有设置密码，就弹出设置新密码的对话框
	 */
	protected void showSetPasswordDialog() {
		// 新建对话框
		newDialog = new AlertDialog.Builder(HomeActivity.this);

		View view = View.inflate(HomeActivity.this,
				R.layout.setpassword_dialog_home, null);
		// 找到布局的控件
		final EditText et_password1 = (EditText) view
				.findViewById(R.id.et_dialog_home_password1);
		final EditText et_password2 = (EditText) view
				.findViewById(R.id.et_dialog_home_password2);
		Button bt_setPassword = (Button) view
				.findViewById(R.id.bt_dialog_home_setPassword);
		Button bt_cancelSet = (Button) view
				.findViewById(R.id.bt_dialog_home_cancelPassword);
		/**
		 * 设置密码
		 */
		bt_setPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String passwordOne = et_password1.getText().toString().trim();
				String passwordTwo = et_password2.getText().toString().trim();
				if (TextUtils.isEmpty(passwordTwo)
						|| TextUtils.isEmpty(passwordOne)) {
					ShowToast.showToast(HomeActivity.this, "密码不能为空", 0);
					return;
				}

				if (passwordTwo.equals(passwordOne)) {// 两次密码一直
					// 保存密码
					sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putString(MyContasts.PASSWORD,
							MD5Util.MD5Lock(passwordOne));
					editor.commit();
					ShowToast.showToast(HomeActivity.this, "密码保存成功", 0);
					// 然后销毁对话框
					dialog.dismiss();
					LogUtil.d(TAG, "sp数据添加成功");
				} else {// 两次密码不一致
						// 弹出对对话框进行提示
					ShowToast.showToast(HomeActivity.this, "输入密码不一致", 0);
					// 然后清空输入的密码框
					et_password1.setText("");
					et_password2.setText("");
				}
			}
		});

		/**
		 * 取消设置密码
		 */
		bt_cancelSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// 设置自定义对话框
		newDialog.setView(view);
		// 这样做为了能够销毁自定义对话框
		dialog = newDialog.create();
		// 显示对话框
		dialog.show();
	}

	/**
	 * 如果设置了密码，就弹出输入密码对话框
	 */
	protected void showInputPasswordDialog() {
		// 初始化对话框
		newDialog = new AlertDialog.Builder(HomeActivity.this);

		View view = View.inflate(HomeActivity.this,
				R.layout.inputpassword_dialog_home, null);
		// 找到该布局中的控件
		final EditText et_input = (EditText) view
				.findViewById(R.id.et_dialog_home_input);
		Button bt_ensureInput = (Button) view
				.findViewById(R.id.bt_dialog_home_ensureInput);
		Button bt_cancelInput = (Button) view
				.findViewById(R.id.bt_dialog_home_cancelInput);
		/**
		 * 确认密码
		 */
		bt_ensureInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取输入框中的密码
				String inputPassword = et_input.getText().toString().trim();
				// 判断是否输入了密码
				if (TextUtils.isEmpty(inputPassword)) {
					ShowToast.showToast(HomeActivity.this, "密码不能为空", 0);
					return;
				}

				// 获取sp中存储的密码值
				String password = sp.getString(MyContasts.PASSWORD, "null");
				if (MD5Util.MD5Lock(inputPassword).equals(password)) {// 输入密码相等
					// 跳转到手机防盗界面
					loadPhoneLostFind();
					dialog.dismiss();
				} else {
					// 密码不正确
					ShowToast.showToast(HomeActivity.this, "输入的密码有误，请重新输入", 0);
					et_input.setText("");// 清空密码框
				}
			}
		});
		/**
		 * 取消输入
		 */
		bt_cancelInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 撤销对话框
				dialog.dismiss();
			}
		});

		newDialog.setView(view);

		dialog = newDialog.create();
		dialog.show();
	}

	/**
	 * 跳转到手机防盗界面
	 */
	protected void loadPhoneLostFind() {
		Intent intent = new Intent(HomeActivity.this,
				PhoneLostFindActivity.class);
		startActivity(intent);

	}

	/*
	 * 创建一个GridView配置器
	 * 
	 * @author Administrator
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
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(HomeActivity.this,R.layout.item_home_gridview, null);
				viewHolder = new ViewHolder();
				viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv_griadview_item_icon);
				viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_griadview_item_iconName);
				convertView.setTag(viewHolder);// 将viewHolder储存在view中
			} else {
				viewHolder = (ViewHolder) convertView.getTag();// 从view中获取到ViewHolder
			}

			viewHolder.iv.setBackgroundResource(icon[position]);
			viewHolder.tv.setText(iconName[position]);
			/**
			 * 判断用户是否修改了图标名字
			 */
			if(position==0){//如果位于第一个图标
				//然后判断sp中储存的修改名字是否为空，如果不为空，就加载新修改的名字
				sp = getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
				String modifNewName = sp.getString(MyContasts.MODIFFINDNAME, "");
				if(TextUtils.isEmpty(modifNewName)){
					//如果为空，不进行操作
				}else{
					//如果不为空，加载新的名字
					viewHolder.tv.setText(modifNewName);
				}
				
			}

			return convertView;
		}

	}

	private class ViewHolder {
		ImageView iv;
		TextView tv;
	}

	/**
	 * 显示菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	/**
	 * 菜单项选择点击事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_modife_lostfind:// 修改手机防盗名字
			System.out.println("进入了菜单选项第一项");
			// 弹出自定义对话框进行修改
			showModifFindNameDialog();
			break;

		default:
			break;
		}
		return true;
	}
	

	/**
	 * 弹出修改手机防盗名对话框
	 */
	private void showModifFindNameDialog() {
		AlertDialog.Builder modifDialog = new AlertDialog.Builder(HomeActivity.this);
		View modifView = View.inflate(HomeActivity.this, R.layout.modiffindname_dialog_home, null);
		//找到对话框布局中的控件
		final EditText et_modfiName = (EditText) modifView.findViewById(R.id.et_dialog_home_modifname);
		Button bt_sureModif = (Button) modifView.findViewById(R.id.bt_dialog_home_suremodif);
		Button bt_canselModif = (Button) modifView.findViewById(R.id.bt_dialog_home_cancelmodif);
		
		/**
		 * 按键:用户确认修改手机防盗名字
		*/
		bt_sureModif.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//判断用户是否修改了当前的值
				String modifName = et_modfiName.getText().toString().trim();
				if(TextUtils.isEmpty(modifName)){
					//用户没有修改
					ShowToast.showToast(HomeActivity.this, "请输入要修改的名字", 0);
					return;
				}
				//修改了，就保存当前的用户提交的名字
				//保存到sp文件中
				sp=getSharedPreferences(MyContasts.SPNAME, MODE_PRIVATE);
				sp.edit().putString(MyContasts.MODIFFINDNAME, modifName).commit();
				//然后撤销该对话框
				dialog.dismiss();
				onResume();//恢复活动
			}
		});
		
		/**
		 * 按键：用户取消修改手机防盗名字
		 */
		bt_canselModif.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//直接撤销该会话框
				dialog.dismiss();
			}
		});
		
		//将界面自定义对话加到到标准对话框中
		modifDialog.setView(modifView);
		dialog = modifDialog.create();
		dialog.show();
		
	}

}
