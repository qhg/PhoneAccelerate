package edu.feicui.aide.tel;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
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
import edu.feicui.aide.bean.ContactsInfo;
import edu.feicui.aide.main.BaseActivity;

/**
 * 通话记录具体信息
 * 
 */
public class TelContactsActivity extends BaseActivity {
	/**
	 * 中间标题
	 */
	TextView mTxtTitle;
	/**
	 * 左上返回按钮
	 */
	ImageView mImgBack;
	/**
	 * ListView
	 */
	ListView mLst;
	/**
	 * 布局填充器
	 */
	LayoutInflater mLayoutInflater;
	/**
	 * 通话记录数据源
	 */
	List<ContactsInfo> mListContacts;
	/**
	 * TelManager类对象
	 */
	TelManager mManager;
	/**
	 * 缓冲进度条
	 */
	ProgressDialog mDialog;
	/**
	 * 适配器
	 */
	TelContactsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_tel_contacts);
	}

	void init() {
		mLayoutInflater = getLayoutInflater();
		mManager = TelManager.getInstance(this);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImgBack = (ImageView) findViewById(R.id.img_telcontacts_back);
		mImgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTxtTitle = (TextView) findViewById(R.id.txt_telcontacts_title);
		mTxtTitle.setText(getResources().getString(R.string.txt_tel_contacts));
		// ListView
		mLst = (ListView) findViewById(R.id.lst_telcontacts);
		// 设置拨号功能
		mLst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String number = mListContacts.get(position).number;
				String uri = "tel:" + number;
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			}
		});
		// 加载数据源
		setData();
		// 适配器
		mAdapter = new TelContactsAdapter();
		// 加载适配器
		mLst.setAdapter(mAdapter);
	}

	/**
	 * 子线程中加载数据源,并在获得数据源前显示缓冲条
	 */
	public void setData() {
		// 开启缓冲进度条
		mDialog = ProgressDialog
				.show(this,
						this.getResources().getString(
								R.string.dialog_progress_title),
						this.getResources().getString(
								R.string.dialog_progress_message));
		// 隐藏ListView
		mLst.setVisibility(View.INVISIBLE);
		new Thread() {
			public void run() {
				mListContacts = mManager.getContactsInfo();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
						mDialog.cancel();
						mLst.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();
	}

	class TelContactsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return null != mListContacts ? mListContacts.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return null != mListContacts ? mListContacts.get(position) : null;
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
				convertView = mLayoutInflater.inflate(
						R.layout.item_telcontacts, parent, false);
				holder.img = (ImageView) convertView
						.findViewById(R.id.img_telcontacts_type);
				holder.txtName = (TextView) convertView
						.findViewById(R.id.txt_telcontacts_name);
				holder.txtNumber = (TextView) convertView
						.findViewById(R.id.txt_telcontacts_number);
				holder.txtTime = (TextView) convertView
						.findViewById(R.id.txt_telcontacts_time);
				holder.txtDuration = (TextView) convertView
						.findViewById(R.id.txt_telcontacts_duration);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置type
			int type = mListContacts.get(position).type;
			switch (type) {
			case CallLog.Calls.INCOMING_TYPE:// 呼入
				holder.img.setImageResource(R.drawable.call_incoming);
				break;
			case CallLog.Calls.OUTGOING_TYPE:// 呼出
				holder.img.setImageResource(R.drawable.call_outgoing);
				break;
			case CallLog.Calls.MISSED_TYPE:// 未接
				holder.img.setImageResource(R.drawable.call_missed);
				break;
			default:
				break;
			}
			// 设置姓名
			holder.txtName.setText(mListContacts.get(position).name);
			// 设置电话号码
			holder.txtNumber.setText(mListContacts.get(position).number);
			// 设置通话时长
			holder.txtDuration.setText(mListContacts.get(position).duration);
			// 设置通话时间
			holder.txtTime.setText(mListContacts.get(position).time);
			return convertView;
		}
	}

	class ViewHolder {
		ImageView img;
		TextView txtName;
		TextView txtNumber;
		TextView txtTime;
		TextView txtDuration;
	}
}
