package edu.feicui.aide.main;

import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.feicui.aide.R;
import edu.feicui.aide.accelerate.AccelerateActivity;
import edu.feicui.aide.accelerate.ProcessInfo;
import edu.feicui.aide.check.CheckActivity;
import edu.feicui.aide.filemgr.FileMgrActivity;
import edu.feicui.aide.garbage.GarbageClearActivity;
import edu.feicui.aide.soft.SoftActivity;
import edu.feicui.aide.tel.TelMgrActivity;
import edu.feicui.aide.util.MemoryUtil;
import edu.feicui.aide.util.ProcessUtil;
import edu.feicui.aide.view.CircleView;

public class MainActivity extends BaseActivity implements OnClickListener {
	/**
	 * 数字变小时的标识what
	 */
	private final int MESSAGE_DOWN = 1;
	/**
	 * 数字变大时的标识what
	 */
	private final int MESSAGE_UP = 2;
	/**
	 * 清理前已用内存比例
	 */
	private float mPrevious;
	/**
	 * 清理后已用内存比例
	 */
	private float mLater;
	/**
	 * 圆形中间显示已用内存大小的变化值
	 */
	private int mConsumeNum;
	/**
	 * 自定义CircleView<br/>
	 * 控制绿色部分
	 */
	CircleView mCircleView;
	/**
	 * 可点击的圆形按钮
	 */
	ImageButton mIgbCircle;
	/**
	 * 显示内存比例的文本框
	 */
	TextView mTxtPercent;
	/**
	 * 手机加速文字显示
	 */
	TextView mTxtState;
	/**
	 * 手机加速
	 */
	TextView mTxtAccelerate;
	/**
	 * 软件管理
	 */
	TextView mTxtSoftManage;
	/**
	 * 手机监测
	 */
	TextView mTxtCheck;
	/**
	 * 通讯大全
	 */
	TextView mTxtTel;
	/**
	 * 文件管理
	 */
	TextView mTxtFileManage;
	/**
	 * 垃圾清理
	 */
	TextView mTxtClear;
	/**
	 * 右上角菜单
	 */
	ImageButton mIgbMenu;
	/**
	 * 获取内存类对象
	 */
	MemoryUtil mMemUtil;
	private ProcessUtil mProcessUtil;
	/**
	 * 文本框内容更改开关，防止连续点击影响
	 */
	private boolean mFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		float ratio = (float) mMemUtil.getConsumeMem() / mMemUtil.getTotalMem();
		mCircleView.drawCircle(ratio * 360);
		mTxtPercent.setText((int) (ratio * 100) + "");
	}

	private void init() {
		mMemUtil = MemoryUtil.getInstance(this);
		mProcessUtil = ProcessUtil.getInstance(this);
		mPrevious = (float) mMemUtil.getConsumeMem() / mMemUtil.getTotalMem();
		mConsumeNum = (int) (mPrevious * 100);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mCircleView = (CircleView) findViewById(R.id.circle_mid);
		// 将初始内存消耗设置为初始值
		mCircleView.drawCircle(mPrevious * 360);

		mTxtPercent = (TextView) findViewById(R.id.txt_mid_percent);
		// 设置初始显示值
		mTxtPercent.setText("" + (int) (mPrevious * 100));

		mTxtState = (TextView) findViewById(R.id.txt_mid_accelerate);

		mIgbCircle = (ImageButton) findViewById(R.id.igb_mid);
		mIgbCircle.setOnClickListener(this);
		// 手机加速
		mTxtAccelerate = (TextView) findViewById(R.id.txt_bottom_accelerate);
		mTxtAccelerate.setOnClickListener(this);
		// 软件管理
		mTxtSoftManage = (TextView) findViewById(R.id.txt_bottom_softmanage);
		mTxtSoftManage.setOnClickListener(this);

		mTxtCheck = (TextView) findViewById(R.id.txt_bottom_check);
		mTxtCheck.setOnClickListener(this);

		mTxtTel = (TextView) findViewById(R.id.txt_bottom_tel);
		mTxtTel.setOnClickListener(this);

		mTxtFileManage = (TextView) findViewById(R.id.txt_bottom_filemanage);
		mTxtFileManage.setOnClickListener(this);

		mTxtClear = (TextView) findViewById(R.id.txt_bottom_clear);
		mTxtClear.setOnClickListener(this);

		mIgbMenu = (ImageButton) findViewById(R.id.imb_top_right);
		mIgbMenu.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.igb_mid:// 点击绘制动态圆弧并改变文本框的值
			clearOneKey();
			// 清理完成后所用内存比例
			mLater = (float) mMemUtil.getConsumeMem() / mMemUtil.getTotalMem();
			mCircleView.drawCircle(mLater * 360);
			// 设置变化的文本框内容
			mHandler.sendEmptyMessage(MESSAGE_DOWN);
			break;
		case R.id.txt_bottom_accelerate:// 手机加速
			gotoActivity(this, AccelerateActivity.class);
			break;
		case R.id.txt_bottom_softmanage:// 软件管理
			gotoActivity(this, SoftActivity.class);
			break;
		case R.id.txt_bottom_check:// 手机检测
			gotoActivity(this, CheckActivity.class);
			break;
		case R.id.txt_bottom_tel:// 通讯大全
			gotoActivity(this, TelMgrActivity.class);
			break;
		case R.id.txt_bottom_filemanage:// 文件管理
			gotoActivity(this, FileMgrActivity.class);
			break;
		case R.id.txt_bottom_clear:// 垃圾清理
			gotoActivity(this, GarbageClearActivity.class);
			break;
		}
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		int what = msg.what;
		switch (what) {
		case MESSAGE_DOWN:// 数值变小时执行
			mTxtState.setText(R.string.buttom_accelerating);
			if (mConsumeNum >= 0) {
				mTxtPercent.setText("" + mConsumeNum);
				mConsumeNum -= 2;
				mHandler.sendEmptyMessageDelayed(MESSAGE_DOWN, 15);
			} else {
				mConsumeNum = 0;
				mTxtPercent.setText("" + mConsumeNum);
				mHandler.sendEmptyMessageDelayed(MESSAGE_UP, 15);
			}

			break;
		case MESSAGE_UP:// 数值变大时执行
			if (mConsumeNum < (int) (mLater * 100)) {
				mTxtPercent.setText("" + mConsumeNum);
				mConsumeNum += 2;
				mHandler.sendEmptyMessageDelayed(MESSAGE_UP, 15);
			} else {
				mTxtPercent.setText("" + (int) (mLater * 100));
				mTxtState.setText(R.string.buttom_accelerate);
			}
			break;

		}

	}

	/**
	 * 清理所有正在运行的用户进程（自身除外）
	 */
	public void clearOneKey() {
		List<ProcessInfo> listUser = mProcessUtil.getUserRunningAppInfo();
		for (int i = 0; i < listUser.size(); i++) {
			// 排除自身
			if (listUser.get(i).packageName == this.getPackageName()) {
				continue;
			}
			mProcessUtil.mActivityManager.killBackgroundProcesses(listUser
					.get(i).packageName);
			listUser.remove(i);
		}
	}
}
