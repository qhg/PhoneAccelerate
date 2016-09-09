package edu.feicui.aide.soft;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.main.BaseActivity;
import edu.feicui.aide.util.ProcessUtil;

/**
 * 该类是SoftActivity的跳转页
 * 
 */
public class SecondSoftActivity extends BaseActivity implements OnClickListener {
	/**
	 * 左上回退按钮
	 */
	ImageView mImgBack;
	/**
	 * 中间标题
	 */
	TextView mTxtTitle;
	/**
	 * 底部复选框
	 */
	CheckBox mChb;
	/**
	 * 底部清除按钮
	 */
	TextView mTxtClear;
	/**
	 * ListView
	 */
	ListView mLst;
	/**
	 * 适配器对象
	 */
	private SoftAdapter mAdapter;
	/**
	 * 布局填充器
	 */
	LayoutInflater mLayoutInflater;
	/**
	 * 进程工具类
	 */
	ProcessUtil mProcessUtil;
	/**
	 * 数据源
	 */
	private List<SoftInfo> mList;
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_secondsoft);
	}

	void init() {
		mLayoutInflater = getLayoutInflater();
		mAdapter = new SoftAdapter();
		mProcessUtil = ProcessUtil.getInstance(this);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImgBack = (ImageView) findViewById(R.id.img_second_back);
		mImgBack.setOnClickListener(this);
		mTxtTitle = (TextView) findViewById(R.id.txt_second_title);
		// 底部复选框
		mChb = (CheckBox) findViewById(R.id.chb_second_bottom);
		mChb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				List<SoftInfo> list = mAdapter.getList();
				for (int i = 0; i < list.size(); i++) {
					list.get(i).flag = isChecked;
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		mTxtClear = (TextView) findViewById(R.id.txt_second_clear);
		mTxtClear.setOnClickListener(this);
		// ListView
		mLst = (ListView) findViewById(R.id.lst_second);
		setListView();
		mLst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.chb.toggle();
				// 保留到数据源
				boolean flag = holder.chb.isChecked();
				List<SoftInfo> list = mAdapter.getList();
				list.get(position).flag = flag;
				// 判断当前子条目是否已全部选中,从而改变底部CheckBox相应状态
				boolean flagChb = true;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).flag == false) {
						flagChb = false;
					}
				}
				mChb.setChecked(flagChb);
			}
		});
	}

	/**
	 * 该方法用于加载不用数据源及其适配器
	 */
	public void setListView() {
		Intent intent = getIntent();
		mDialog = ProgressDialog
				.show(this,
						this.getResources().getString(
								R.string.dialog_progress_title),
						this.getResources().getString(
								R.string.dialog_progress_message));
		mLst.setVisibility(View.INVISIBLE);
		String title = intent.getStringExtra("title");
		mTxtTitle.setText(title);
		// 获取参数Value
		final int value = intent.getIntExtra("key", 0);
		new Thread() {
			public void run() {
				// 通过参数value判断获得不同数据源
				mList = mProcessUtil.getSoftInfo(value);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter.setList(mList);
						mLst.setAdapter(mAdapter);
						mDialog.cancel();
						mLst.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img_second_back:// 左上返回按钮
			finish();
			break;
		case R.id.txt_second_clear:// 底部清理按钮
			unloadAllSelected();
		default:
			break;
		}
	}

	/**
	 * 卸载已选中的软件
	 */
	public void unloadAllSelected() {
		List<SoftInfo> list = mAdapter.getList();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).flag == true) {
				Uri packageUri = Uri
						.parse("package:" + list.get(i).packageName);
				Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
				startActivity(intent);
				list.remove(i);
			}
		}

		mAdapter.notifyDataSetChanged();
		// 设置底部复选框的状态
		boolean flagChb = true;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).flag == false) {
				flagChb = false;
			}
		}
		mChb.setChecked(flagChb);
	}

	class SoftAdapter extends BaseAdapter {
		List<SoftInfo> list;

		public void setList(List<SoftInfo> list) {
			this.list = list;
		}

		public List<SoftInfo> getList() {
			return list;
		}

		@Override
		public int getCount() {
			return null != list ? list.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return null != list ? list.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.item_second_soft, parent, false);
				holder.chb = (CheckBox) convertView
						.findViewById(R.id.chb_soft_item);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.img_soft_item);
				holder.label = (TextView) convertView
						.findViewById(R.id.txt_soft_item_label);
				holder.packgeName = (TextView) convertView
						.findViewById(R.id.txt2_soft_item_packagename);
				holder.versionName = (TextView) convertView
						.findViewById(R.id.txt_soft_item_version);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.chb.setChecked(list.get(position).flag);
			holder.icon.setImageDrawable(list.get(position).icon);
			holder.label.setText(list.get(position).label);
			holder.packgeName.setText(list.get(position).packageName);
			holder.versionName.setText(list.get(position).versionName);
			return convertView;
		}
	}

	class ViewHolder {
		CheckBox chb;
		ImageView icon;
		TextView label;
		TextView packgeName;
		TextView versionName;
	}
}
