package com.zzh.phonegurad.splash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zzh.phoneguard.utils.ShowToast;
import com.zzh.shoujiweishi.R;

/**
 * 流量统计
 * 
 * @author Administrator
 * 
 */
public class TranficActivity extends Activity {
	protected static final int FINISH = 1;
	protected static final int LOADING = 2;
	private ListView lv_data;
	private MyTraficAdapter adapter;
	private PackageManager pm;

	private List<AppData> datas = new ArrayList<TranficActivity.AppData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intiView();
		initData();
	}

	private class AppData {
		private Drawable icon;
		private String appName;
		private int uid;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FINISH:
				// 数据加载完成，更心列表显示
				pb_loading.setVisibility(View.GONE);
				lv_data.setVisibility(View.VISIBLE);
				adapter = new MyTraficAdapter();
				lv_data.setAdapter(adapter);
				break;
			case LOADING:
				// 数据加载完成，更心列表显示
				pb_loading.setVisibility(View.VISIBLE);
				lv_data.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};
	private ProgressBar pb_loading;

	private void initData() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = LOADING;
				handler.sendMessage(msg);
				long startTime = System.currentTimeMillis();
				// 获取所有安装的包
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(0);
				// 遍历包获取图标和软件名以及uid
				for (PackageInfo packageInfo : installedPackages) {
					AppData data = new AppData();

					data.icon = packageInfo.applicationInfo.loadIcon(pm);
					data.appName = packageInfo.applicationInfo.loadLabel(pm)
							+ "";
					data.uid = packageInfo.applicationInfo.uid;

					datas.add(data);
				}
				long endTime = System.currentTimeMillis();
				if (endTime - startTime < 2000) {
					SystemClock.sleep(2000 - (endTime - startTime));
					// 遍历完成发送信息列出数据
					msg = Message.obtain();
					msg.what = FINISH;
					handler.sendMessage(msg);
				} else {
					// 遍历完成发送信息列出数据
					msg = Message.obtain();
					msg.what = FINISH;
					handler.sendMessage(msg);
				}

			};
		}.start();

	}

	private void intiView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tranfic);

		// 想要这个列表显示应用程序的图标、软件名、获取应用程序的的uid
		// 获取包管理器
		pm = getPackageManager();
		
		pb_loading = (ProgressBar) findViewById(R.id.pb_tranfic_activity_loading);

		lv_data = (ListView) findViewById(R.id.lv_tranfic_activity_data);

	}

	private class MyTraficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(TranficActivity.this,
						R.layout.item_tranfic_list, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_item_transfic_icon);
				holder.tv_appname = (TextView) convertView
						.findViewById(R.id.tv_item_transfic_appName);
				holder.iv_lookicon = (ImageView) convertView
						.findViewById(R.id.iv_item_transfic_lockicon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_icon.setImageDrawable(datas.get(position).icon);
			holder.tv_appname.setText(datas.get(position).appName);

			holder.iv_lookicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 一点击，获取当前对象的uid，来获取当前消耗的流量
					String pathRcv = "/proc/uid_stat/"
							+ datas.get(position).uid + "/tcp_rcv";
					String pathSnd = "/proc/uid_stat/"
							+ datas.get(position).uid + "/tcp_snd";

					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(new FileInputStream(
										new File(pathRcv))));

						String sizeRcv = br.readLine();
						long rcv = Long.parseLong(sizeRcv);
						br = new BufferedReader(new InputStreamReader(
								new FileInputStream(new File(pathSnd))));
						String sizeSnd = br.readLine();
						long snd = Long.parseLong(sizeSnd);

						ShowToast.showToast(
								TranficActivity.this,
								"下载流量："
										+ Formatter.formatFileSize(
												TranficActivity.this, rcv)
										+ "\n"
										+ "上传流量："
										+ Formatter.formatFileSize(
												TranficActivity.this, snd), 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			return convertView;
		}

	}

	private class ViewHolder {
		private ImageView iv_icon;
		private TextView tv_appname;
		private ImageView iv_lookicon;
	}
}
