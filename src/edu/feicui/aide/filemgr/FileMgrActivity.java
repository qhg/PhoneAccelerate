package edu.feicui.aide.filemgr;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.bean.FileInfo;
import edu.feicui.aide.filemgr.FileMgrUtil.onFlushListener;
import edu.feicui.aide.main.BaseActivity;

/**
 * 文件管理第一界面
 * 
 */
public class FileMgrActivity extends BaseActivity {
	/**
	 * 左上回退
	 */
	ImageView mImgBack;
	/**
	 * 发现文件大小的具体数据
	 */
	TextView mTxtNum;
	/**
	 * ListView
	 */
	ListView mLst;
	/**
	 * 第一界面数据源
	 */
	List<FileInfo> mList;
	/**
	 * 布局填充器
	 */
	LayoutInflater mInflater;
	/**
	 * 文件工具类对象
	 */
	FileMgrUtil mFileMgrUtil;
	/**
	 * 适配器对象
	 */
	FileMgrAdapter mAdapter;
	/**
	 * 判断加载数据是否完成<br/>
	 * true表示正在加载<br/>
	 * false表示加载完成
	 */
	boolean isLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_fielmgr);
	}

	/**
	 * 初始化所需变量
	 */
	void init() {
		mFileMgrUtil = FileMgrUtil.getInstance();
		mInflater = getLayoutInflater();
		mAdapter = new FileMgrAdapter();
	}

	/**
	 * 加载数据
	 */
	public void load() {
		new Thread() {
			public void run() {
				isLoad = true;
				// 获取数据源
				mList = mFileMgrUtil.mList;
				// 加载数据源
				mFileMgrUtil.SetFileInfo();
				isLoad = false;
			};
		}.start();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImgBack = (ImageView) findViewById(R.id.img_file_back);
		mImgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTxtNum = (TextView) findViewById(R.id.txt_file_num);
		// ListView
		mLst = (ListView) findViewById(R.id.lst_file_mgr);
		// 加载数据
		load();
		// 加载适配器
		mLst.setAdapter(mAdapter);
		// ListView 子条目点击事件
		mLst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 数据未读取完成，不能跳转
				if (isLoad) {
					return;
				}
				// 使用带返回值的Intent
				Intent intent = new Intent(FileMgrActivity.this,
						SecondFileMgrActivity.class);
				String fileType = mList.get(position).fileType;
				// 将文件类型传递过去,用于分辨当前点击的子条目
				intent.putExtra("type", fileType);
				startActivityForResult(intent, position);
			}
		});
		mFileMgrUtil.setOnFlushListener(new onFlushListener() {
			// 回调具体实现
			@Override
			public void onFlush() {
				mHandler.sendEmptyMessageDelayed(1, 300);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 第二界面中已删除文件的大小
		long lenDeleted = data.getLongExtra("lenDeleted", 0);
		// 改变总文件中文件大小
		mFileMgrUtil.allLen -= lenDeleted;
		// 改变各相应文件的大小（在FileMgrUtil中的成员变量）
		switch (requestCode) {
		case 1:
			mFileMgrUtil.txtLen -= lenDeleted;
			break;
		case 2:
			mFileMgrUtil.audioLen -= lenDeleted;
			break;
		case 3:
			mFileMgrUtil.videoLen -= lenDeleted;
			break;
		case 4:
			mFileMgrUtil.imageLen -= lenDeleted;
			break;
		case 5:
			mFileMgrUtil.zipLen -= lenDeleted;
			break;
		case 6:
			mFileMgrUtil.apkLen -= lenDeleted;
			break;
		default:
			break;
		}
		// 改变相应子条目中文件大小
		mList.get(requestCode).fileLen -= lenDeleted;
		// Log.e("子条目中剩余文件大小:", mList.get(requestCode).fileLen + "");
		// Log.e("删除文件的大小:", lenDeleted + "");
		// Log.e("所有总文件中剩余文件大小：", mFileMgrUtil.allLen + "");
		// 改变"全部"子条目中大小值
		mList.get(0).setFileLen(mFileMgrUtil.allLen);
		// 刷新适配器
		mAdapter.notifyDataSetChanged();
		// 刷新上部显示中文件大小数据
		String lenStr = Formatter.formatFileSize(FileMgrActivity.this,
				mFileMgrUtil.allLen);
		mTxtNum.setText(lenStr);
	}

	class FileMgrAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return null != mList ? mList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return null != mList ? mList.get(position) : null;
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
				convertView = mInflater.inflate(R.layout.item_fielmgr, parent,
						false);
				holder.type = (TextView) convertView
						.findViewById(R.id.txt_fileitem_type);
				holder.size = (TextView) convertView
						.findViewById(R.id.txt_fileitem_size);
				holder.pgb = (ProgressBar) convertView
						.findViewById(R.id.pgb_file_item);
				holder.img = (ImageView) convertView
						.findViewById(R.id.img_file_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.type.setText(mList.get(position).fileType);
			// 显示子条目中文件大小数值
			long fileLen = mList.get(position).fileLen;
			String fileLenStr = Formatter.formatFileSize(FileMgrActivity.this,
					fileLen);
			holder.size.setText(fileLenStr);

			// flag为true时显示img,隐藏pgb;
			if (mList.get(position).isFinish) {
				holder.pgb.setVisibility(View.INVISIBLE);
				holder.img.setVisibility(View.VISIBLE);
			} else {
				holder.pgb.setVisibility(View.VISIBLE);
				holder.img.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	class ViewHolder {
		TextView type;
		TextView size;
		ProgressBar pgb;
		ImageView img;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		// 获取当前全部文件的大小
		long fileAllLen = mFileMgrUtil.allLen;
		String fileAllLenStr = Formatter.formatFileSize(FileMgrActivity.this,
				fileAllLen);
		// 设置全部文件大小
		mTxtNum.setText(fileAllLenStr);
		// 刷新适配器
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearToZero();
	}

	/**
	 * 该方法用来清0
	 */
	public void clearToZero() {
		// 大小清0(第一界面)
		mFileMgrUtil.allLen = 0;
		mFileMgrUtil.txtLen = 0;
		mFileMgrUtil.audioLen = 0;
		mFileMgrUtil.videoLen = 0;
		mFileMgrUtil.apkLen = 0;
		mFileMgrUtil.zipLen = 0;
		mFileMgrUtil.imageLen = 0;
		// 移除对象（第二界面）
		mFileMgrUtil.secAllFile.clear();
		mFileMgrUtil.secApkFile.clear();
		mFileMgrUtil.secAudioFile.clear();
		mFileMgrUtil.secImgFile.clear();
		mFileMgrUtil.secSpacilFile.clear();
		mFileMgrUtil.secTxtFile.clear();
		mFileMgrUtil.secVideoFile.clear();
		mFileMgrUtil.secZipFile.clear();
	}
}
