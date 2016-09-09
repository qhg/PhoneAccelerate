package edu.feicui.aide.tel;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.bean.TelClass;
import edu.feicui.aide.main.BaseActivity;

/**
 * 通讯大全(实现第四个功能)
 * 
 */
public class TelMgrActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	/**
	 * 左上角回退
	 */
	ImageView mImgBack;
	/**
	 * GridView
	 */
	GridView mGrv;
	/**
	 * 布局填充器
	 */
	LayoutInflater mInflater;
	/**
	 * 数据源
	 */
	List<TelClass> mList;
	/**
	 * 当前GridView适配器
	 */
	GridViewAdapter mAdapter;
	/**
	 * 数据库操作类
	 */
	TelManager mTelManager;
	/**
	 * 通话记录按钮
	 */
	TextView mTxtContacts;
	/**
	 * 通讯录
	 */
	TextView mTxtAddrlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_telmgr);
	}

	void init() {
		mInflater = getLayoutInflater();
		mAdapter = new GridViewAdapter();
		mTelManager = TelManager.getInstance(this);

	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImgBack = (ImageView) findViewById(R.id.img_telmgr_back);
		mImgBack.setOnClickListener(this);

		mTxtContacts = (TextView) findViewById(R.id.txt_tel_contacts);
		mTxtContacts.setOnClickListener(this);

		mTxtAddrlist = (TextView) findViewById(R.id.txt_tel_addrlist);
		mTxtAddrlist.setOnClickListener(this);
		mGrv = (GridView) findViewById(R.id.grv_tel);
		setdata();
		mGrv.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.img_telmgr_back:// 左上角回退
			finish();
			break;
		case R.id.txt_tel_contacts:// 通话记录
			gotoActivity(this, TelContactsActivity.class);
			break;
		case R.id.txt_tel_addrlist:// 通讯录
			gotoActivity(this, ContactsContractActivity.class);
			break;
		default:
			break;
		}
	}

	public void setdata() {
		new Thread() {
			public void run() {
				mList = mTelManager.getClassList();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter.setList(mList);
						mGrv.setAdapter(mAdapter);
					}
				});
			};

		}.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, TelDetailActivity.class);
		// 用于跳转后的标题
		String name = mAdapter.getList().get(position).name;
		// 用于跳转后作为数据表名的判断
		int idx = mAdapter.getList().get(position).idx;
		intent.putExtra("name", name);
		intent.putExtra("idx", idx);
		startActivity(intent);
	}

	class GridViewAdapter extends BaseAdapter {
		List<TelClass> list;

		public List<TelClass> getList() {
			return list;
		}

		public void setList(List<TelClass> list) {
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
				convertView = mInflater.inflate(R.layout.item_telmgr, parent,
						false);
				holder.txt = (TextView) convertView
						.findViewById(R.id.txt_tel_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txt.setText(list.get(position).name);
			// 给子条目循环设置背景
			switch (position % 3) {
			case 0:
				convertView.setBackgroundResource(R.drawable.bcg_red_selector);
				break;
			case 1:
				convertView
						.setBackgroundResource(R.drawable.bcg_green_selector);
				break;
			case 2:
				convertView
						.setBackgroundResource(R.drawable.bcg_yellow_selector);
				break;
			default:
				break;
			}
			return convertView;
		}

	}

	class ViewHolder {
		TextView txt;
	}

}
