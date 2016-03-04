package com.zzh.phoneguard.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzh.shoujiweishi.R;

public class ItemSettingLayout extends LinearLayout {

	private View view;

	private TextView tv_item_title;
	private TextView tv_item_desc;
	private CheckBox cb_item_open;

	private String[] desc;
	private String title;
	private String content;

	public ItemSettingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.zzh.shoujiweishi",
				"title");
		content = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.zzh.shoujiweishi",
				"content");
		initData();
	}

	private void initData() {
		// 初始化数据
		// 分离数据
		desc = content.split("@");
		tv_item_title.setText(title);
		tv_item_desc.setText(desc[0]);
		tv_item_desc.setTextColor(Color.RED);
	}

	/**
	 * 创建自己的点击事件
	 */
	public void setClickListener(OnClickListener listener) {
		view.setOnClickListener(listener);
	}

	/**
	 * 设定自定义View的状态
	 * 
	 * @param boolean clickStatus 自定义view点击效果状态
	 * 
	 */
	public void setClickStatus(boolean clickStatus) {
		if (clickStatus) {// 点击选中
			tv_item_desc.setTextColor(Color.GREEN);// 被选中，设置字体为绿色
			tv_item_desc.setText(desc[1]);
		} else {
			// 没有被选中
			tv_item_desc.setTextColor(Color.RED);// 没有被选中，设置字体为红色色
			tv_item_desc.setText(desc[0]);
		}
		cb_item_open.setChecked(clickStatus);
	}

	/**
	 * 获取当前view的状态
	 * 
	 * @return true：当前被点击选中；false：当前没有被点击、没有选中
	 */
	public boolean getClickStatus() {
		return cb_item_open.isChecked();
	}

	private void initView() {
		// 添加到容器中
		view = View.inflate(getContext(), R.layout.item_setting, null);
		addView(view);

		tv_item_title = (TextView) view
				.findViewById(R.id.tv_item_setting_title);
		tv_item_desc = (TextView) view.findViewById(R.id.tv_item_setting_desc);
		cb_item_open = (CheckBox) view.findViewById(R.id.cb_item_setteing_open);

	}

	public ItemSettingLayout(Context context) {
		super(context);
	}

}
