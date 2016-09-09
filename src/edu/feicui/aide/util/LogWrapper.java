package edu.feicui.aide.util;

import android.util.Log;
import edu.feicui.aide.BuildConfig;

public class LogWrapper {
	/**
	 * 自动和手动开关
	 */
	private final static boolean DEBUG = BuildConfig.DEBUG && true;

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}
}
