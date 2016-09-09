package edu.feicui.aide.check;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.main.BaseActivity;
import edu.feicui.aide.util.MemoryUtil;

/**
 * 手机检测
 * 
 */
public class CheckActivity extends BaseActivity {
	/**
	 * 左上角回退按钮
	 */
	ImageView mImgBack;

	/**
	 * 显示充电动画控件
	 */
	ImageView mImgLevel;
	/**
	 * 显示充电比例文本
	 */
	TextView mTxtLevel;
	/**
	 * 显示充电状态
	 */
	TextView mTxtStatus;
	/**
	 * 设备名称
	 */
	TextView mTxtBrand;
	/**
	 * 系统版本
	 */
	TextView mTxtVersion;
	/**
	 * 全部运行内存
	 */
	TextView mTxtTotalRAM;
	/**
	 * 可用内存
	 */
	TextView mTxtAvailRAM;
	/**
	 * cpu名称
	 */
	TextView mTxtCpuname;
	/**
	 * cpu数量
	 */
	TextView mTxtCpuNum;
	/**
	 * 手机分辨率
	 */
	TextView mTxtPhoneMetrics;
	/**
	 * 相机分辨率
	 */
	TextView mTxtCameraMetrics;
	/**
	 * 基带版本
	 */
	TextView mTxtBaseband;
	/**
	 * 是否root
	 */
	TextView mTxtIsroot;
	/**
	 * CheckInfo类对象
	 */
	CheckUtil mInfo;
	/**
	 * 电池广播接收器
	 */
	BatteryReceiver mReceiver;
	/**
	 * 获取内存类对象
	 */
	MemoryUtil mMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_check);
	}

	void init() {
		mMem = MemoryUtil.getInstance(this);
		mInfo = CheckUtil.getInstance(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 调用电池广播注册方法
		registerBattery();
	}

	/**
	 * 该方法用来注册电池广播（动态注册）
	 */
	public void registerBattery() {
		mReceiver = new BatteryReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		// 左上角回退键
		mImgBack = (ImageView) findViewById(R.id.img_check_back);
		mImgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mImgLevel = (ImageView) findViewById(R.id.img_check_level);
		mTxtLevel = (TextView) findViewById(R.id.txt_check_level);

		mTxtStatus = (TextView) findViewById(R.id.txt_check_status);

		mTxtBrand = (TextView) findViewById(R.id.txt_check_brand);
		mTxtBrand
				.setText(String.format("设备名称：%1$S", Build.BRAND.toUpperCase()));

		mTxtVersion = (TextView) findViewById(R.id.txt_check_version);
		mTxtVersion.setText(String.format("系统版本：%1$S", Build.VERSION.RELEASE));

		mTxtTotalRAM = (TextView) findViewById(R.id.txt_check_total);
		String total = Formatter.formatFileSize(this, mMem.getTotalMem());
		String totalRAM = String.format("全部运行内存：%1$s", total);
		mTxtTotalRAM.setText(totalRAM);

		mTxtAvailRAM = (TextView) findViewById(R.id.txt_check_avail);
		String avail = Formatter.formatFileSize(this, mMem.getAvailMem());
		String availRAM = String.format("剩余运行内存：%1$s", avail);
		mTxtAvailRAM.setText(availRAM);

		mTxtCpuname = (TextView) findViewById(R.id.txt_check_cpuname);
		String cpuname = String.format("cpu名称：%1$s", mInfo.getCpuName());
		mTxtCpuname.setText(cpuname);

		mTxtCpuNum = (TextView) findViewById(R.id.txt_check_cpunum);
		String cpuNum = String.format("cpu数量：%1$d", mInfo.getCpuNum());
		mTxtCpuNum.setText(cpuNum);

		mTxtPhoneMetrics = (TextView) findViewById(R.id.txt_check_phone);
		mTxtPhoneMetrics.setText(mInfo.getPhoneMetrics());

		mTxtCameraMetrics = (TextView) findViewById(R.id.txt_check_camera);
		mTxtCameraMetrics.setText(mInfo.getCameraMetrics());

		mTxtBaseband = (TextView) findViewById(R.id.txt_check_baseband);
		String baseband = String
				.format("基带版本：%1$s", mInfo.getBasebandVersion());
		mTxtBaseband.setText(baseband);

		mTxtIsroot = (TextView) findViewById(R.id.txt_check_root);
		String isRoot = String.format("是否ROOT：%1$s", mInfo.isRoot());
		mTxtIsroot.setText(isRoot);
	}

	/**
	 * 该内部类用来接收电池广播
	 * 
	 */
	class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 充电状态
			String statusKey = BatteryManager.EXTRA_STATUS;
			int value = intent.getIntExtra(statusKey, 0);
			switch (value) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN:// 未知
				mTxtStatus.setText("未知状态");
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING:// 充电中
				mTxtStatus.setText("充电中");
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:// 放电中
				mTxtStatus.setText("放电中");
				break;
			case BatteryManager.BATTERY_STATUS_FULL:// 充满
				mTxtStatus.setText("充满");
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:// 未充电
				mTxtStatus.setText("未充电");
				break;
			}
			// 获取最大电量
			String maxLevel = BatteryManager.EXTRA_SCALE;
			int max = intent.getIntExtra(maxLevel, 0);
			// 获取当前电量
			String levelKey = BatteryManager.EXTRA_LEVEL;
			int level = intent.getIntExtra(levelKey, 0);
			int ratio = (int) ((float) level / max * 100);
			String str = String.format("%1$d %%", ratio);
			mTxtLevel.setText(str);
			mImgLevel.setImageLevel(ratio);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭电池广播
		unregisterReceiver(mReceiver);
	}
}
