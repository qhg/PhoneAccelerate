package edu.feicui.aide.accelerate;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.feicui.aide.R;
import edu.feicui.aide.dialog.MyDialog;
import edu.feicui.aide.dialog.MyDialog.OnMyDialogListener;
import edu.feicui.aide.main.BaseActivity;
import edu.feicui.aide.util.MemoryUtil;
import edu.feicui.aide.util.ProcessUtil;

/**
 * 该类为实现手机加速功能
 * 
 */
public class AccelerateActivity extends BaseActivity implements
		OnItemClickListener, View.OnClickListener {

	/**
	 * 左上角回退按钮
	 */
	ImageView mImgBack;
	/**
	 * 手机品牌
	 */
	TextView mTxtBrand;
	/**
	 * 手机产品名称 及 版本信息
	 */
	TextView mTxtProduct;
	/**
	 * Item ListView
	 */
	ListView mListView;
	/**
	 * 底部全选图标
	 */
	ImageView mImgAll;
	/**
	 * 布局填充器
	 */
	LayoutInflater mInflater;
	/**
	 * 系统进程数据源
	 */
	List<ProcessInfo> mListSystem;
	/**
	 * 用户进程数据源
	 */
	List<ProcessInfo> mListUser;

	/**
	 * 底部切换用户进程和系统进程按钮
	 */
	ToggleButton mTgl;
	/**
	 * 自定义适配器
	 */
	MyAdapter mAdapter;
	/**
	 * 上部进度条
	 */
	ProgressBar mPgb;
	/**
	 * 显示内存文本
	 */
	TextView mTxtMem;
	/**
	 * 上部一键清理按钮
	 */
	Button mBtn;
	/**
	 * 管理APP进程
	 */
	ProcessUtil mProcessUtil;
	/**
	 * 自定义对话框对象
	 */
	private MyDialog mMyDialog;
	/**
	 * 获取内存信息对象
	 */
	private MemoryUtil mMemUtil;
	boolean mFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_accelerate);
	}

	/**
	 * 初始化自定义适配器、布局填充器、管理APP进程对象
	 */
	void init() {
		mAdapter = new MyAdapter();
		mInflater = getLayoutInflater();
		mProcessUtil = ProcessUtil.getInstance(AccelerateActivity.this);
		mMyDialog = new MyDialog(this, R.style.Dialog_FS);
		mMemUtil = MemoryUtil.getInstance(this);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		// 设置手机品牌
		mTxtBrand = (TextView) findViewById(R.id.txt_accelerate_brand);
		mTxtBrand.setText(Build.BRAND);
		// 设置手机产品名和系统版本号
		mTxtProduct = (TextView) findViewById(R.id.txt_accelerate_product);
		String product = String.format("Huawei %1$S Android%2$S",
				Build.PRODUCT, Build.VERSION.RELEASE);
		mTxtProduct.setText(product);
		// 进度条
		mPgb = (ProgressBar) findViewById(R.id.pgb_accelerate_up);
		// 显示中内存数据文本
		mTxtMem = (TextView) findViewById(R.id.txt_accelerate_mem);

		mImgBack = (ImageView) findViewById(R.id.img_accelerate_back);
		mImgBack.setOnClickListener(this);
		mBtn = (Button) findViewById(R.id.btn_accelerate_clear);
		mBtn.setOnClickListener(this);

		// 实例化ListView
		mListView = (ListView) findViewById(R.id.lst_accelerate_mid);
		// 加载数据源，并显示初始系统进程
		initData();
		// 设置子条目的点击事件
		mListView.setOnItemClickListener(this);

		mImgAll = (ImageView) findViewById(R.id.img_accelerate_bottom);
		// 设置底部全选图标点击事件
		mImgAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mFlag = !mFlag;
				List<ProcessInfo> list = mAdapter.getData();
				for (int i = 0; i < list.size(); i++) {
					list.get(i).flag = mFlag;
				}
				// 改变底部全选图标图片
				setImgAll(mFlag);
				mAdapter.notifyDataSetChanged();
			}
		});

		mTgl = (ToggleButton) findViewById(R.id.tgl_acceletate_bottom);
		mTgl.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 判断ToogleButton按钮的状态，从而加载不同数据源
				boolean flag = true;
				if (isChecked) {
					mAdapter.setData(mListSystem);
					for (int i = 0; i < mListSystem.size(); i++) {
						if (mListSystem.get(i).flag == false) {
							flag = false;
						}
					}
				} else {
					mAdapter.setData(mListUser);
					for (int i = 0; i < mListUser.size(); i++) {
						if (mListUser.get(i).flag == false) {
							flag = false;
						}
					}
				}
				// 根据flag结果设置不同图片
				setImgAll(flag);
				// 刷新适配器
				mAdapter.notifyDataSetChanged();
			}
		});

	}

	/**
	 * 在加载数据源时显示进度条对话框<br/>
	 * 
	 * 加载完成后显示ListView
	 */
	public void initData() {
		// 加载数据之前显示进度条，隐藏ListView
		final ProgressDialog dialog = ProgressDialog.show(this, this
				.getResources().getString(R.string.dialog_progress_title), this
				.getResources().getString(R.string.dialog_progress_message));
		mListView.setVisibility(View.INVISIBLE);
		new Thread() {

			@Override
			public void run() {// 子线程中加载数据源防止ANR异常

				// 系统进程数据源
				mListSystem = mProcessUtil.getSystemRunningAppInfo();
				// 用户进程数据源
				mListUser = mProcessUtil.getUserRunningAppInfo();

				// 该方法可以在子线程中直接操作UI
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						// 传递系统数据给适配器中的list
						mAdapter.setData(mListUser);
						// 加载适配器
						mListView.setAdapter(mAdapter);
						dialog.cancel();
						// 在文本框中设置已用内存数据
						mTxtMem.setText(mMemUtil.getTextMem());
						// 刷新进度条显示
						mPgb.setProgress(mMemUtil.getProgress());
						// 显示listview
						mListView.setVisibility(View.VISIBLE);

					}
				});
			}
		}.start();

	}

	/**
	 * 自定义适配器
	 * 
	 */
	class MyAdapter extends BaseAdapter {
		List<ProcessInfo> list;

		public void setData(List<ProcessInfo> list) {
			this.list = list;
		}

		public List<ProcessInfo> getData() {
			return list;
		}

		@Override
		public int getCount() {
			return null != list ? list.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_accelerate,
						parent, false);
				holder = new ViewHolder();
				holder.chb = (CheckBox) convertView
						.findViewById(R.id.chb_accelerate_item);
				holder.img = (ImageView) convertView
						.findViewById(R.id.img_accelerate_item);
				holder.txtUp = (TextView) convertView
						.findViewById(R.id.txt1_accelerate_item);
				holder.txtDown = (TextView) convertView
						.findViewById(R.id.txt2_accelerate_item);
				holder.txtRight = (TextView) convertView
						.findViewById(R.id.txt_accelerate_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 给不同控件设置相应数据
			holder.chb.setChecked(list.get(position).flag);
			holder.img.setImageDrawable(list.get(position).img);
			holder.txtUp.setText(list.get(position).label);
			holder.txtDown.setText(list.get(position).memory);
			holder.txtRight.setText(list.get(position).textRight);
			return convertView;
		}
	}

	static class ViewHolder {
		CheckBox chb;
		ImageView img;
		TextView txtUp;
		TextView txtDown;
		TextView txtRight;
	}

	/*
	 * 子条目的点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.chb.toggle();

		// 保留到相应数据源
		boolean flag = holder.chb.isChecked();
		List<ProcessInfo> list = mAdapter.getData();
		list.get(position).flag = flag;
		// 遍历当前数据源，根据flagAll状态设置不同图片
		boolean flagAll = true;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).flag == false) {
				flagAll = false;
			}
		}
		setImgAll(flagAll);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_accelerate_clear:// 一键清理按钮
			// 如果该数据是系统的则需要进行二次判断
			if (mTgl.isChecked()) {
				showDialog();
				mMyDialog.show();
			} else {
				clearAllSelected();
			}
			break;
		case R.id.img_accelerate_back:// 左上角回退按钮
			finish();
		default:
			break;
		}
	}

	/**
	 * 显示对话框,并对用户选择结果进行相关操作<br/>
	 * MyDialog中回调方法的具体实现
	 */
	public void showDialog() {
		mMyDialog.setOnMyDiaLogListener(new OnMyDialogListener() {

			@Override
			public void onClickYesListener() {
				clearAllSelected();
				mMyDialog.cancel();
			}

			@Override
			public void onClickNoListener() {

				mMyDialog.cancel();

			}
		});
	}

	/**
	 * 删除选中的子条目
	 */
	public void clearAllSelected() {
		List<ProcessInfo> list = mAdapter.getData();
		// Log.e("tag", "删除前的进程数量="
		// + mProcessUtil.mActivityManager.getRunningAppProcesses().size());
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).flag == true) {
				// Log.e("tag", list.get(i).packageName);
				mProcessUtil.mActivityManager.killBackgroundProcesses(list
						.get(i).packageName);
				// 从数据源中移除已删除子条目
				list.remove(i);
			}
		}

		// Log.e("tag", "删除后的进程数量="
		// + mProcessUtil.mActivityManager.getRunningAppProcesses().size());
		// 刷新适配器
		mAdapter.notifyDataSetChanged();
		// 刷新进度条和相应文本框中内容
		mTxtMem.setText(mMemUtil.getTextMem());
		mPgb.setProgress(mMemUtil.getProgress());

		// 删除完成后改变底部复选框的状态
		if (list.size() == 0) {
			mFlag = false;
		} else {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).flag == false) {
					mFlag = false;
				}
			}
		}
		setImgAll(mFlag);
	}

	public void setImgAll(boolean flag) {
		if (flag) {
			mImgAll.setImageDrawable(this.getResources().getDrawable(
					R.drawable.check_rect_correct_checked));
		} else {
			mImgAll.setImageDrawable(this.getResources().getDrawable(
					R.drawable.check_rect_correct_default));
		}
	}
}
