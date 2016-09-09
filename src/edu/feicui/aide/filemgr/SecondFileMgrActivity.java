package edu.feicui.aide.filemgr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import edu.feicui.aide.R;
import edu.feicui.aide.bean.SecondFileInfo;
import edu.feicui.aide.main.BaseActivity;

public class SecondFileMgrActivity extends BaseActivity implements
		OnClickListener {
	/**
	 * ListView
	 */
	ListView mLst;
	/**
	 * 布局填充器
	 */
	LayoutInflater mInflater;
	SimpleDateFormat mDateFormat;
	/**
	 * 适配器
	 */
	SecondFileAdapter mAdapter;
	/**
	 * 中上标题
	 */
	TextView mTxtTitle;
	/**
	 * 左上回退
	 */
	ImageView mImgBack;
	/**
	 * 清除按钮
	 */
	TextView mTxtClear;
	/**
	 * 文件数量
	 */
	TextView mTxtNum;
	/**
	 * 占用空间
	 */
	TextView mTxtSize;
	/**
	 * 文件管理工具类对象
	 */
	FileMgrUtil mFileMgrUtil;
	/**
	 * 意图
	 */
	Intent mIntent;
	/**
	 * 删除文件的总大小
	 */
	long mLenDeleted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_secondfilemgr);
	}

	void init() {
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mAdapter = new SecondFileAdapter();
		mInflater = getLayoutInflater();
		mFileMgrUtil = FileMgrUtil.getInstance();
		mIntent = getIntent();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mTxtTitle = (TextView) findViewById(R.id.txt_second_file_title);
		mImgBack = (ImageView) findViewById(R.id.img_second_file_back);
		mImgBack.setOnClickListener(this);
		mTxtNum = (TextView) findViewById(R.id.txt_second_file_num);
		mTxtSize = (TextView) findViewById(R.id.txt_second_file_size);
		mTxtClear = (TextView) findViewById(R.id.txt_second_file_clear);
		mTxtClear.setOnClickListener(this);
		mLst = (ListView) findViewById(R.id.lst_second_file);
		setData();
		mLst.setAdapter(mAdapter);
		mLst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 当前子条目数据源
				List<SecondFileInfo> list = mAdapter.getList();
				String suffix = list.get(position).suffix;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				// 文件路径Uri
				Uri uri = Uri.fromFile(list.get(position).file);
				// 通过后缀判断文件类型，使用不同隐式意图打开
				if (mFileMgrUtil.isTextFile(suffix)) {// 文件
					intent.setDataAndType(uri, "text/*");
				} else if (mFileMgrUtil.isImageFile(suffix)) {// 图片
					intent.setDataAndType(uri, "image/*");
				} else if (mFileMgrUtil.isAudioFile(suffix)) {// 音频
					intent.setDataAndType(uri, "audio/*");
				} else if (mFileMgrUtil.isVideoFile(suffix)) {// 视频
					intent.setDataAndType(uri, "video/*");
				} else if (mFileMgrUtil.isZipFile(suffix)
						|| mFileMgrUtil.isProgramFile(suffix)) {// 压缩包和安装包
					intent.setDataAndType(uri, "application/*");
				} else {// 其他
					Toast.makeText(SecondFileMgrActivity.this, "壮士，兵器不称手！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				startActivity(intent);
			}
		});
	}

	/**
	 * 根据Intent传递来的消息，加载不同数据源和标题
	 */
	public void setData() {
		String type = mIntent.getStringExtra("type");

		switch (type) {
		case "全部":// 获取全部数据源（SecondFileInfo）
			mAdapter.setList(mFileMgrUtil.secAllFile);
			mTxtTitle.setText("全部");
			setTextContent(mFileMgrUtil.secAllFile, mFileMgrUtil.allLen);
			break;
		case "文档":
			mAdapter.setList(mFileMgrUtil.secTxtFile);
			mTxtTitle.setText("文档");
			setTextContent(mFileMgrUtil.secTxtFile, mFileMgrUtil.txtLen);
			break;
		case "音频":
			mAdapter.setList(mFileMgrUtil.secAudioFile);
			mTxtTitle.setText("音频");
			setTextContent(mFileMgrUtil.secAudioFile, mFileMgrUtil.audioLen);
			break;
		case "视频":
			mAdapter.setList(mFileMgrUtil.secVideoFile);
			mTxtTitle.setText("视频");
			setTextContent(mFileMgrUtil.secVideoFile, mFileMgrUtil.videoLen);
			break;
		case "图片":
			mAdapter.setList(mFileMgrUtil.secImgFile);
			mTxtTitle.setText("图片");
			setTextContent(mFileMgrUtil.secImgFile, mFileMgrUtil.imageLen);
			break;
		case "压缩包":
			mAdapter.setList(mFileMgrUtil.secZipFile);
			mTxtTitle.setText("压缩包");
			setTextContent(mFileMgrUtil.secZipFile, mFileMgrUtil.zipLen);
			break;
		case "安装包":
			mAdapter.setList(mFileMgrUtil.secApkFile);
			mTxtTitle.setText("安装包");
			setTextContent(mFileMgrUtil.secApkFile, mFileMgrUtil.apkLen);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置页面中文件个数和文件占用空间大小
	 * 
	 * @param list
	 *            目标集合
	 * @param length
	 *            某种类型文件大小
	 */
	public void setTextContent(List<SecondFileInfo> list, long length) {
		String numStr = String.format("%1$d个", list.size());
		mTxtNum.setText(numStr);
		String lengthStr = Formatter.formatFileSize(SecondFileMgrActivity.this,
				length);
		mTxtSize.setText(lengthStr);
	}

	class SecondFileAdapter extends BaseAdapter {
		List<SecondFileInfo> list;

		public List<SecondFileInfo> getList() {
			return list;
		}

		public void setList(List<SecondFileInfo> list) {
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
			// 保留当前下标
			final int id = position;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_second_file,
						parent, false);
				holder.chb = (CheckBox) convertView
						.findViewById(R.id.chb_second_file_item);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.img_second_file_item);
				holder.name = (TextView) convertView
						.findViewById(R.id.txt_second_file_item_name);
				holder.time = (TextView) convertView
						.findViewById(R.id.txt_second_file_item_time);
				holder.size = (TextView) convertView
						.findViewById(R.id.txt_second_file_item_size);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置子条目中CheckBox点击事件
			holder.chb
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// 将其状态保存到对应数据源
							list.get(id).state = isChecked;
						}
					});
			// 设置当前Checbox的状态
			holder.chb.setChecked(list.get(position).state);
			// 在已知后缀的情况下，对不同后缀设置不同icon
			String suffix = list.get(position).suffix;
			String iconName = "icon_" + suffix;
			// 获取已有图片资源的ID
			int ImgID = SecondFileMgrActivity.this.getResources()
					.getIdentifier(iconName, "drawable",
							SecondFileMgrActivity.this.getPackageName());
			// 设置icon
			if (mFileMgrUtil.isAudioFile(suffix)) {// 音频
				holder.icon.setImageResource(R.drawable.icon_audio);
			} else if (mFileMgrUtil.isVideoFile(suffix)) {// 视频
				holder.icon.setImageResource(R.drawable.icon_video);
			} else if (mFileMgrUtil.isZipFile(suffix)) {// 压缩包
				holder.icon.setImageResource(R.drawable.icon_rar);
			} else if (mFileMgrUtil.isProgramFile(suffix)) {// 安装包
				holder.icon.setImageResource(R.drawable.icon_app);
			} else if (0 != ImgID) {// 用后缀匹配的icon覆盖
				holder.icon.setImageResource(ImgID);
			} else if (mFileMgrUtil.isImageFile(suffix)) {// 文件类型为图片时特殊处理
				Glide.with(SecondFileMgrActivity.this)
						.load(list.get(position).file).into(holder.icon);
			} else {// 其他
				holder.icon.setImageResource(R.drawable.icon_file);
			}

			holder.name.setText(list.get(position).name);

			Date date = new Date(list.get(position).time);
			String time = mDateFormat.format(date);
			holder.time.setText(time);

			String size = Formatter.formatFileSize(SecondFileMgrActivity.this,
					list.get(position).size);
			holder.size.setText(size);
			return convertView;
		}
	}

	class ViewHolder {
		CheckBox chb;
		ImageView icon;
		TextView name;
		TextView time;
		TextView size;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img_second_file_back:// 左上回退按钮
			onBackPressed();
			break;
		case R.id.txt_second_file_clear:// 底部卸载按钮
			clearAllSelected();
			break;
		default:
			break;
		}
	}

	/**
	 * 删除所选的文件
	 */
	public void clearAllSelected() {
		// 当前数据源中文件总大小
		long length = 0;
		// 存储差值的变量每次进来都归0
		mLenDeleted = 0;
		// 当前ListView数据源
		List<SecondFileInfo> list = mAdapter.getList();
		// 删除所选文件
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).state == true) {
				mLenDeleted += list.get(i).size;
				list.get(i).file.delete();
				list.remove(i);
			}
		}
		// 刷新适配器
		mAdapter.notifyDataSetChanged();
		// 设置上部文件数目和占用空间
		for (int i = 0; i < list.size(); i++) {
			length += list.get(i).size;
		}
		setTextContent(list, length);
	}

	/*
	 * 拦截返回键
	 */
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		mIntent.putExtra("lenDeleted", mLenDeleted);
		setResult(RESULT_OK, mIntent);
		finish();
	}

}