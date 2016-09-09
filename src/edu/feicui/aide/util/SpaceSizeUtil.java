package edu.feicui.aide.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * 获取ROM 和 SDcard 信息
 * 
 */
public class SpaceSizeUtil {
	Context mContext;
	static SpaceSizeUtil mSpaceSizeUtil;

	private SpaceSizeUtil(Context context) {
		mContext = context;
	}

	public static SpaceSizeUtil getInstace(Context context) {
		if (null == mSpaceSizeUtil) {
			synchronized (SpaceSizeUtil.class) {
				if (null == mSpaceSizeUtil) {
					context = context.getApplicationContext();
					mSpaceSizeUtil = new SpaceSizeUtil(context);
				}
			}
		}
		return mSpaceSizeUtil;
	}

	/**
	 * 获得SD卡总大小(long)
	 * 
	 * @return
	 */
	public long getSDTotalSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSizeLong();
		long totalBlocks = stat.getBlockCountLong();
		long sdTotal = blockSize * totalBlocks;
		return sdTotal;
	}

	/**
	 * 获得SD卡总大小（String）
	 * 
	 * @return
	 */
	public String getSDTotalSizeString() {
		long sdTotal = getSDTotalSize();
		String total = Formatter.formatFileSize(mContext, sdTotal);
		return total;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小(long)
	 * 
	 * @return
	 */
	public long getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSizeLong();
		long availableBlocks = stat.getAvailableBlocksLong();
		long sdAvail = blockSize * availableBlocks;
		return sdAvail;

	}

	/**
	 * 获取SDcard已用内存值(String)
	 * 
	 * @return
	 */
	public String getSDConsumeSizeString() {
		long avail = getSDAvailableSize();
		long total = getSDTotalSize();
		long consume = total - avail;
		String str = Formatter.formatFileSize(mContext, consume);
		return str;
	}

	/**
	 * 获得机身内存总大小(long)
	 * 
	 * @return
	 */
	public long getRomTotalSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSizeLong();
		long totalBlocks = stat.getBlockCountLong();
		long romTotal = blockSize * totalBlocks;
		return romTotal;
	}

	/**
	 * 获得ROM总大小（String）
	 * 
	 * @return
	 */
	public String getRomTotalSizeString() {
		long romTotal = getRomTotalSize();
		String total = Formatter.formatFileSize(mContext, romTotal);
		return total;
	}

	/**
	 * 获得机身可用内存(long)
	 * 
	 * @return
	 */
	public long getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSizeLong();
		long availableBlocks = stat.getAvailableBlocksLong();
		long romAvail = blockSize * availableBlocks;
		return romAvail;
	}

	/**
	 * 获取手机ROM已用内存(String)
	 * 
	 * @return
	 */
	public String getRomConsumeSizeString() {
		long avail = getRomAvailableSize();
		long total = getRomTotalSize();
		long consume = total - avail;
		String str = Formatter.formatFileSize(mContext, consume);
		return str;
	}

	/**
	 * 获得手机ROM在进度条中的值
	 * 
	 * @return
	 */
	public int getInternalInt() {
		long avail = getRomAvailableSize();
		long total = getRomTotalSize();
		long consume = total - avail;
		int num = (int) ((double) consume / total * 100);
		return num;
	}

	/**
	 * 获得SDcard在进度条中显示的值
	 * 
	 * @return
	 */
	public int getStorageInt() {
		long avail = getSDAvailableSize();
		long total = getSDTotalSize();
		long consume = total - avail;
		int num = (int) ((double) consume / total * 100);
		return num;
	}
}
