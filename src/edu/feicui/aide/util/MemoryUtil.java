package edu.feicui.aide.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

/**
 * 该类用来获取RAM信息
 * 
 */
public class MemoryUtil {
	/**
	 * 获取内存大小类对象
	 */
	static MemoryUtil memUtil;
	/**
	 * 上下文
	 */
	Context mContext;
	/**
	 * 活动APP管理
	 */
	ActivityManager mActivityManager;

	private MemoryUtil(Context context) {
		this.mContext = context;
		mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public static MemoryUtil getInstance(Context context) {
		if (memUtil == null) {
			synchronized (MemoryUtil.class) {
				if (memUtil == null) {
					context = context.getApplicationContext();
					memUtil = new MemoryUtil(context);
				}
			}
		}
		return memUtil;
	}

	/**
	 * @return 获取可用内存大小(long)<br/>
	 *         单位为B
	 */
	public long getAvailMem() {
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		mActivityManager.getMemoryInfo(memoryInfo);
		// 获得数据单位默认为byte;
		long availMem = memoryInfo.availMem;
		// Log.e("可用内存=", availMem + "");
		return availMem;
	}

	/**
	 * @return 获取总内存大小(long)<br/>
	 *         单位为B
	 */
	public long getTotalMem() {
		BufferedReader bufferedReader = null;
		long totalMem = 0;
		try {
			bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"));
			// 获取第一行的内容
			String str1 = bufferedReader.readLine();
			// 获取第一次出现空格的下标
			int start = str1.indexOf(' ');
			// 获取第一次出现字符k的下标
			int end = str1.indexOf('k');
			// 截取制定下标之间的内容
			String str2 = str1.substring(start, end);
			// 去除前后空格
			String str3 = str2.trim();
			// 该number单位为KB;
			long number1 = Long.parseLong(str3);
			// 转换单位为B
			totalMem = number1 * 1024;
			// Log.e("总内存=", totalMem + " ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return totalMem;
	}

	/**
	 * 获取已用内存大小（long）<br/>
	 * 单位为B
	 * 
	 * @return
	 */
	public long getConsumeMem() {
		return getTotalMem() - getAvailMem();
	}

	/**
	 * @return 返回在内存文本中显示的字符串
	 */
	public String getTextMem() {
		long consumeLong = getTotalMem() - getAvailMem();
		String total = Formatter.formatFileSize(mContext, getTotalMem());
		String consume = Formatter.formatFileSize(mContext, consumeLong);
		return String.format("已用内存： %1$S/%2$S", consume, total);
	}

	/**
	 * 
	 * @return 获取用于在进度条中显示的值
	 */
	public int getProgress() {
		int progress = (int) ((double) (getTotalMem() - getAvailMem())
				/ getTotalMem() * 100);
		return progress;
	}
}
