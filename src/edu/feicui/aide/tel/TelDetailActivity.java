package edu.feicui.aide.tel;

import java.util.List;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.bean.TelTableClass;
import edu.feicui.aide.main.BaseActivity;

/**
 * 用于展示跳转后的具体信息(通话大全)
 * 
 */
public class TelDetailActivity extends BaseActivity {
	/**
	 * 左上角回退键
	 */
	ImageView mImgBack;
	/**
	 * 上部标题
	 */
	TextView mTxtTitle;
	/**
	 * ListView
	 */
	ListView mLst;
	/**
	 * 表名
	 */
	String mTableName;
	/**
	 * 布局填充器
	 */
	LayoutInflater mInflater;
	/**
	 * 当前ListView的适配器
	 */
	TelDetailAdapter mAdapter;
	/**
	 * 数据源
	 */
	List<TelTableClass> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_teldetail);
	}

	void init() {
		mInflater = getLayoutInflater();
		mAdapter = new TelDetailAdapter();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImgBack = (ImageView) findViewById(R.id.img_teldetail_back);
		mImgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTxtTitle = (TextView) findViewById(R.id.txt_teldetail_title);
		getData();
		mLst = (ListView) findViewById(R.id.lst_teldetail);
		setData();
		mLst.setAdapter(mAdapter);
		mLst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String number = mAdapter.getList().get(position).number;
				String uri = "tel:" + number;
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			}
		});
	}

	/**
	 * 该方法接收Intent传来的信息
	 */
	public void getData() {
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		// 设置标题
		mTxtTitle.setText(name);
		// 获取数据表名
		int idx = intent.getIntExtra("idx", 1);
		mTableName = "table" + idx;
	}

	/**
	 * 加载数据源
	 */
	public void setData() {
		new Thread() {
			public void run() {
				mList = TelManager.getInstance(TelDetailActivity.this)
						.getTableList(mTableName);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter.setList(mList);
						mAdapter.notifyDataSetChanged();
					}
				});
			};

		}.start();
	}

	class TelDetailAdapter extends BaseAdapter {
		List<TelTableClass> list;

		public List<TelTableClass> getList() {
			return list;
		}

		public void setList(List<TelTableClass> list) {
			this.list = list;
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
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_teldetail,
						parent, false);
				holder.txtName = (TextView) convertView
						.findViewById(R.id.txt_name_teldetail_item);
				holder.txtNumber = (TextView) convertView
						.findViewById(R.id.txt_number_teldetail_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtName.setText(list.get(position).name);
			holder.txtNumber.setText(list.get(position).number);
			return convertView;
		}

	}

	class ViewHolder {
		TextView txtName;
		TextView txtNumber;
	}

}
