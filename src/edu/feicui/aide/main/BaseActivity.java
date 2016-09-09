package edu.feicui.aide.main;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import edu.feicui.aide.util.LogWrapper;

/**
 * 所有Activity的基类<br/>
 * 作用：提取Activity的公共操作
 * 
 */
public class BaseActivity extends Activity {
	/**
	 * 当前类名
	 */
	private final String TAG = this.getClass().getSimpleName();
	protected Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		LogWrapper.d(TAG, "onCreate");
	}

	private void init() {
		mHandler = new BaseActivity.BaseHandler(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		LogWrapper.d(TAG, "onRestart");
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogWrapper.d(TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogWrapper.d(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogWrapper.d(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogWrapper.d(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogWrapper.d(TAG, "onDestroy");
	}

	/**
	 * 实现Activity的跳转
	 * 
	 * @param packageContext
	 *            上下文
	 * @param cls
	 *            目标类
	 */
	public void gotoActivity(Context packageContext, Class<?> cls) {
		Intent intent = new Intent(packageContext, cls);
		startActivity(intent);
	}

	/**
	 * 用于Activity跳转时传递消息
	 */
	public void gotoActivity(Intent intent) {
		startActivity(intent);
	}

	/**
	 * Subclasses must implement this to receive messages.<br/>
	 * 子类必须实现该方法来处理消息
	 */
	public void handleMessage(Message msg) {

	}

	class BaseHandler extends Handler {
		WeakReference<BaseActivity> reference;// 弱引用

		public BaseHandler(BaseActivity activity) {
			reference = new WeakReference<BaseActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			BaseActivity activity = reference.get();
			activity.handleMessage(msg);
		}
	}
}
