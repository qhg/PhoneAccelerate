package edu.feicui.aide.soft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.main.BaseActivity;
import edu.feicui.aide.util.SpaceSizeUtil;
import edu.feicui.aide.view.PiechartView;

/**
 * SoftManager实现第二个按钮功能
 * 
 */
public class SoftActivity extends BaseActivity implements OnClickListener {
	/**
	 * 进度条（内部空间）
	 */
	ProgressBar mPgbInternal;
	/**
	 * 内部存储文本
	 */
	TextView mTxtInternal;
	/**
	 * 进度条（外部空间）
	 */
	ProgressBar mPgbStorage;
	/**
	 * 外部存储文本
	 */
	TextView mTxtStorage;
	/**
	 * 所有软件
	 */
	TextView mTxtAllSoft;
	/**
	 * 系统软件
	 */
	TextView mTxtSystemSoft;
	/**
	 * 用户软件
	 */
	TextView mTxtUser;

	/**
	 * 意图Intent
	 */
	private Intent mIntent;
	/**
	 * 获取ROM和SDcard大小的对象
	 */
	SpaceSizeUtil mSpaceSizeUtil;
	/**
	 * 自定义View 圆形饼图
	 */
	PiechartView mPiechar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_soft);
	}

	private void init() {
		mIntent = new Intent(this, SecondSoftActivity.class);
		mSpaceSizeUtil = SpaceSizeUtil.getInstace(this);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mPiechar = (PiechartView) findViewById(R.id.soft_circle_view);
		// 手机ROM和SDcard总大小
		long total = mSpaceSizeUtil.getRomTotalSize()
				+ mSpaceSizeUtil.getSDTotalSize();
		// ROM中已用大小
		long romConsume = mSpaceSizeUtil.getRomTotalSize()
				- mSpaceSizeUtil.getRomAvailableSize();
		// SDcard中已用大小
		long sdConsume = mSpaceSizeUtil.getSDTotalSize()
				- mSpaceSizeUtil.getSDAvailableSize();
		float phoneRatio = (float) romConsume / total;
		float SDRatio = (float) sdConsume / total;
		mPiechar.drawPiechart(phoneRatio, SDRatio);

		// 手机可用内存进度条
		mPgbInternal = (ProgressBar) findViewById(R.id.pgb_soft_internal);
		// 设置已用内存进度条初始值
		mPgbInternal.setProgress(mSpaceSizeUtil.getInternalInt());
		// 手机SDcard进度条
		mPgbStorage = (ProgressBar) findViewById(R.id.pgb_soft_storage);
		// 设置已用SDcard进度条初始值
		mPgbStorage.setProgress(mSpaceSizeUtil.getStorageInt());
		// 设置ROM文本内容
		mTxtInternal = (TextView) findViewById(R.id.txt_internal_mem);
		// 将不同内存值转化为String类型

		String strInternal = String.format("%1$S/%2$S",
				mSpaceSizeUtil.getRomConsumeSizeString(),
				mSpaceSizeUtil.getRomTotalSizeString());
		mTxtInternal.setText(strInternal);
		// 设置SDcrd文本内容
		mTxtStorage = (TextView) findViewById(R.id.txt_storage_mem);

		String strStorage = String.format("%1$S/%2$S",
				mSpaceSizeUtil.getSDConsumeSizeString(),
				mSpaceSizeUtil.getSDTotalSizeString());
		mTxtStorage.setText(strStorage);
		// 所有软件点击事件
		mTxtAllSoft = (TextView) findViewById(R.id.txt_soft_all);
		mTxtAllSoft.setOnClickListener(this);
		// 系统软件点击事件
		mTxtSystemSoft = (TextView) findViewById(R.id.txt_soft_system);
		mTxtSystemSoft.setOnClickListener(this);
		mTxtUser = (TextView) findViewById(R.id.txt_soft_user);
		mTxtUser.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		String value = null;
		switch (id) {
		case R.id.txt_soft_all:// 所有软件按钮
			value = this.getResources().getString(R.string.txt_soft_all);
			mIntent.putExtra("title", value);
			mIntent.putExtra("key", 0);
			break;
		case R.id.txt_soft_system:// 系统软件按钮
			value = this.getResources().getString(R.string.txt_soft_system);
			mIntent.putExtra("title", value);
			mIntent.putExtra("key", 1);
			break;
		case R.id.txt_soft_user:// 用户软件
			value = this.getResources().getString(R.string.txt_soft_user);
			mIntent.putExtra("title", value);
			mIntent.putExtra("key", 2);
			break;
		}
		startActivity(mIntent);
	}
}
