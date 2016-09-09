package edu.feicui.aide.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

/**
 * 有关文件的操作
 * 
 */
public class FileUtil {
	/**
	 * sdcard 目录
	 */
	public final static File FILE_SDCARD;
	/**
	 * 应用根目录
	 */
	public final static File FILE_APPROOT;
	/**
	 * 应用日志目录
	 */
	public final static File FILE_LOG;
	static {
		FILE_SDCARD = getSDPath();
		FILE_APPROOT = new File(FILE_SDCARD, "aide");
		FILE_LOG = new File(FILE_APPROOT, "Log");
		try {
			if (!FILE_LOG.exists()) {
				FILE_LOG.mkdirs();
			}
		} catch (Exception e) {
			// 不做处理
		}
	}

	/**
	 * 获取SDcard路径
	 * 
	 * @return
	 */
	public static File getSDPath() {
		File sdDir = null;
		// 判断sdcard是否存在
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir;
	}

	/**
	 * 读取指定路径的文件
	 * 
	 * @param filename
	 *            file路径
	 * @return
	 */
	public String readFile(String filename) {
		StringBuffer sb = new StringBuffer();
		String str = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			while ((str = reader.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
