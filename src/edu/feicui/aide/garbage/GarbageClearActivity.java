package edu.feicui.aide.garbage;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.garbage.CacheUtil.OnFlushListener;
import edu.feicui.aide.main.BaseActivity;

public class GarbageClearActivity extends BaseActivity implements
		OnClickListener {
	/**
	 * 布局填充器
	 */
	LayoutInflater mInflater;
	/**
	 * 左上回退
	 */
	ImageView mImgBack;
	/**
	 * 中上部显示总文件大小的控件
	 */
	TextView mTxtNum;
	/**
	 * 可拓展的ListView
	 */
	ExpandableListView mElst;
	/**
	 * 底部全选复选框
	 */
	CheckBox mChbAll;
	/**
	 * 底部清理按钮
	 */
	TextView mTxtClear;
	/**
	 * 缓存工具类
	 */
	CacheUtil mCacheUtil;
	/**
	 * 数据源
	 */
	List<List<ChildInfo>> mGroup;
	/**
	 * 手机缓存垃圾大小
	 */
	long phoneCacheSize;
	/**
	 * 手机内存垃圾大小
	 */
	long phoneStorageSize;
	/**
	 * SD卡缓存来及大小
	 */
	long SDcardCacheSize;
	/**
	 * SD卡内存垃圾大小
	 */
	long SDcardStorageSize;
	/**
	 * 缓存文件垃圾大小
	 */
	long fileCacheSize;
	/**
	 * 所有垃圾文件大小
	 */
	long allSize;
	/**
	 * 类别，group数据源
	 */
	String[] groupType = { "手机缓存垃圾", "手机内存垃圾", "SD卡缓存垃圾", "SD卡内存垃圾", "缓存文件垃圾" };
	/**
	 * 适配器
	 */
	MyAdapter mAdapter;
	/**
	 * 正在加载数据
	 */
	boolean isLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_garbage);
	}

	void init() {
		mInflater = getLayoutInflater();
		mCacheUtil = new CacheUtil(this);
		mGroup = new ArrayList<>();
		mAdapter = new MyAdapter();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImgBack = (ImageView) findViewById(R.id.img_garbage_back);
		mImgBack.setOnClickListener(this);
		mTxtNum = (TextView) findViewById(R.id.txt_garbage_num);

		mChbAll = (CheckBox) findViewById(R.id.chb_all_garbage);
		// 底部全选框改变事件
		mChbAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 遍历所有childs
				for (int i = 0; i < mGroup.size(); i++) {
					for (int j = 0; j < mGroup.get(i).size(); j++) {
						// 改变数据源
						mGroup.get(i).get(j).flag = isChecked;
					}
				}
				mAdapter.notifyDataSetChanged();
			}
		});

		mTxtClear = (TextView) findViewById(R.id.txt_clear_garbage);
		mTxtClear.setOnClickListener(this);// 底部清理按钮点击事件

		mElst = (ExpandableListView) findViewById(R.id.elst_garbage);
		mElst.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				ChildViewHolder childHolder = (ChildViewHolder) v.getTag();
				childHolder.flag.toggle();
				// 保留到数据源
				mGroup.get(groupPosition).get(childPosition).flag = childHolder.flag
						.isChecked();
				// 判断底部全选框状态
				boolean b = true;
				for (int i = 0; i < mGroup.size(); i++) {
					for (int j = 0; j < mGroup.get(i).size(); j++) {
						if (mGroup.get(i).get(j).flag == false) {
							b = false;
						}
					}
				}
				mChbAll.setChecked(b);
				return true;
			}
		});
		setData();
		mElst.setAdapter(mAdapter);

		mCacheUtil.setOnFlushListener(new OnFlushListener() {

			@Override
			public void flush() {
				mHandler.sendEmptyMessageDelayed(1, 200);
			}
		});
	}

	private void setData() {
		new Thread() {
			public void run() {
				isLoading = true;// 正在加载数据
				mGroup.add(mCacheUtil.getChild("phoneCache"));// 获取手机缓存垃圾数据
				mGroup.add(mCacheUtil.getChild("phoneStorage"));// 获取手机内存垃圾数据
				mGroup.add(mCacheUtil.getChild("SDcardCache"));// 获取SD卡缓存垃圾数据
				mGroup.add(mCacheUtil.getChild("SDcardStorage"));// 获取SD卡内存垃圾数据
				mGroup.add(mCacheUtil.getChild("fileCache"));// 缓存文件垃圾数据
				isLoading = false;// 加载完成
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						String allSize = Formatter.formatFileSize(
								GarbageClearActivity.this, mCacheUtil.allLen);
						// 设置总大小
						mTxtNum.setText(allSize);
						mAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img_garbage_back:// 左上回退
			finish();
			break;
		case R.id.txt_clear_garbage:// 底部清理按钮
			clearAllSelected();
			break;
		default:
			break;
		}
	}

	private void clearAllSelected() {
		// 遍历所有childs
		for (int i = 0; i < mGroup.size(); i++) {
			for (int j = 0; j < mGroup.get(i).size(); j++) {
				// 如果child中的CheckBox状态为true，则删除
				if (mGroup.get(i).get(j).flag == true) {
					mGroup.get(i).get(j).file.delete();
					mGroup.get(i).remove(j);
				}
			}
		}
		// 清零
		clearToZero();
		// 删除文件后刷新各分类大小显示值
		setTypeSize();
		mAdapter.notifyDataSetChanged();
		// 设置总大小
		String text = Formatter.formatFileSize(GarbageClearActivity.this,
				allSize);
		mTxtNum.setText(text);
		// 删除完成后判断底部全选框状态
		boolean b = true;
		if (allSize == 0) {
			b = false;
		} else {
			for (int i = 0; i < mGroup.size(); i++) {
				for (int j = 0; j < mGroup.get(i).size(); j++) {
					if (mGroup.get(i).get(j).flag == false) {
						b = false;
					}
				}
			}
		}
		mChbAll.setChecked(b);

	}

	/**
	 * 设置删除后类型文件的大小 及总大小
	 */
	private void setTypeSize() {
		for (int i = 0; i < mGroup.get(0).size(); i++) {
			phoneCacheSize += mGroup.get(0).get(i).size;
		}
		for (int i = 0; i < mGroup.get(1).size(); i++) {
			phoneStorageSize += mGroup.get(1).get(i).size;
		}
		for (int i = 0; i < mGroup.get(2).size(); i++) {
			SDcardCacheSize += mGroup.get(2).get(i).size;
		}
		for (int i = 0; i < mGroup.get(3).size(); i++) {
			SDcardStorageSize += mGroup.get(3).get(i).size;
		}
		for (int i = 0; i < mGroup.get(4).size(); i++) {
			fileCacheSize += mGroup.get(4).get(i).size;
		}
		allSize = phoneCacheSize + phoneStorageSize + SDcardCacheSize
				+ SDcardStorageSize + fileCacheSize;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		String allSize = Formatter.formatFileSize(this, mCacheUtil.allLen);
		// 设置总大小
		mTxtNum.setText(allSize);
		phoneCacheSize = mCacheUtil.phoneCacheLen;
		phoneStorageSize = mCacheUtil.phoneStorageLen;
		SDcardCacheSize = mCacheUtil.SDcardCacheLen;
		SDcardStorageSize = mCacheUtil.SDcardStorageLen;
		fileCacheSize = mCacheUtil.fileCacheLen;
		// 刷新适配器
		mAdapter.notifyDataSetChanged();
	}

	class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return null != mGroup ? mGroup.size() : 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupViewHolder groupHolder = null;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.item_group_garbage,
						parent, false);
				groupHolder = new GroupViewHolder();
				groupHolder.type = (TextView) convertView
						.findViewById(R.id.txt_type_group_garbage);
				groupHolder.groupSize = (TextView) convertView
						.findViewById(R.id.txt_size_group_garbage);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupViewHolder) convertView.getTag();
			}
			groupHolder.type.setText(groupType[groupPosition]);
			String text = null;
			switch (groupPosition) {
			case 0:
				text = Formatter.formatFileSize(GarbageClearActivity.this,
						phoneCacheSize);
				break;
			case 1:
				text = Formatter.formatFileSize(GarbageClearActivity.this,
						phoneStorageSize);
				break;
			case 2:
				text = Formatter.formatFileSize(GarbageClearActivity.this,
						SDcardCacheSize);
				break;
			case 3:
				text = Formatter.formatFileSize(GarbageClearActivity.this,
						SDcardStorageSize);
				break;
			case 4:
				text = Formatter.formatFileSize(GarbageClearActivity.this,
						fileCacheSize);
				break;
			}
			groupHolder.groupSize.setText(text);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder childHolder = null;
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.item_childs_garbage,
						parent, false);
				childHolder = new ChildViewHolder();
				childHolder.flag = (CheckBox) convertView
						.findViewById(R.id.chb_child_garbage);
				childHolder.icon = (ImageView) convertView
						.findViewById(R.id.img_child_garbage);
				childHolder.name = (TextView) convertView
						.findViewById(R.id.txt_name_child_garbage);
				childHolder.size = (TextView) convertView
						.findViewById(R.id.txt_size_child_garbage);
				convertView.setTag(childHolder);
			} else {
				childHolder = (ChildViewHolder) convertView.getTag();
			}
			childHolder.flag.setChecked(mGroup.get(groupPosition).get(
					childPosition).flag);
			childHolder.icon.setImageDrawable(mGroup.get(groupPosition).get(
					childPosition).icon);
			childHolder.name.setText(mGroup.get(groupPosition).get(
					childPosition).name);
			String size = Formatter.formatFileSize(GarbageClearActivity.this,
					mGroup.get(groupPosition).get(childPosition).size);
			childHolder.size.setText(size);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			if (isLoading) {
				return false;
			}
			return true;
		}

	}

	class GroupViewHolder {
		TextView type;
		TextView groupSize;
		ProgressBar Pgb;
	}

	class ChildViewHolder {
		CheckBox flag;
		ImageView icon;
		TextView name;
		TextView size;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGroup.clear();
		clearToZero();
	}

	void clearToZero() {
		phoneCacheSize = 0;
		phoneStorageSize = 0;
		SDcardCacheSize = 0;
		SDcardStorageSize = 0;
		fileCacheSize = 0;
		allSize = 0;
	}
}
