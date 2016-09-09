package edu.feicui.aide.util;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

/**
 * 崩溃处理
 *
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
	/**
	 * 上下文
	 */
	private Context mContext;
	/**
	 * CrashHandler对象
	 */
	private static CrashHandler mInstance;
	/**
	 * 系统对象
	 */
	private Thread.UncaughtExceptionHandler mExceptionHandler;
	/**
	 * 时间格式
	 */
	private SimpleDateFormat mFormat;

	private CrashHandler() {
		mExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		mFormat = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
	}

	/**
	 * 获得一个CrashHandler的单例对象
	 */
	public static CrashHandler getInstance() {
		if (null == mInstance) {
			synchronized (CrashHandler.class) {
				if (null == mInstance) {
					mInstance = new CrashHandler();
				}
			}
		}
		return mInstance;
	}

	public void init(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当给定线程因给定的未捕获的异常而终止时，调用该方法
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			// 将异常信息写入SDcard中
			String name = getCurrentTime() + ".txt";
			File file = new File(FileUtil.FILE_LOG, name);
			// 创建目标文件

			PrintWriter err = new PrintWriter(file);
			do {
				ex.printStackTrace(err);
			} while (ex.getCause() != null);
			err.flush();
			err.close();
			// 保留系统原有处理方式
			if (null != mExceptionHandler) {
				mExceptionHandler.uncaughtException(thread, ex);
			}
		} catch (Exception e) {
			// 不作处理
		}
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public String getCurrentTime() {
		Date date = new Date();
		String time = mFormat.format(date);
		return time;

	}
}
