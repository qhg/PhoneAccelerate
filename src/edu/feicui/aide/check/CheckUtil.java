package edu.feicui.aide.check;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.DisplayMetrics;
import edu.feicui.aide.util.FileUtil;

public class CheckUtil {
	static CheckUtil mCheckInfo;
	/**
	 * 上下文
	 */
	Context mContext;

	private CheckUtil(Context context) {
		mContext = context;
	}

	public static CheckUtil getInstance(Context context) {
		if (null == mCheckInfo) {
			synchronized (CheckUtil.class) {
				if (null == mCheckInfo) {
					context = context.getApplicationContext();
					mCheckInfo = new CheckUtil(context);
				}
			}
		}
		return mCheckInfo;
	}

	/**
	 * 获取CPU信息
	 * 
	 * @return
	 */
	public String getCpuInfo() {
		// 获取CPU信息的路径为"/proc/cpuinfo"
		String path = "/proc/cpuinfo";
		FileUtil fileUtil = new FileUtil();
		// 读取所有CPU信息
		String cpuinfo = fileUtil.readFile(path);
		// Log.e("tag", cpuinfo);
		return cpuinfo;
	}

	/**
	 * 获取CPU名称
	 * 
	 * @return
	 */
	public String getCpuName() {
		String str = getCpuInfo();
		int start = str.indexOf(" ");
		int end = str.indexOf("\n");
		String cpuName = str.substring(start, end).trim();

		return cpuName;
	}

	/**
	 * 该方法获取cpu数量
	 * 
	 * @return
	 */
	public int getCpuNum() {
		String pathname = "/sys/devices/system/cpu";
		File file = new File(pathname);
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				boolean b = Pattern.matches("cpu[0-9]", filename);
				return b;
			}
		};
		File[] arrs = file.listFiles(filter);
		return arrs.length;
	}

	/**
	 * 获取手机屏幕分辨率
	 * 
	 * @return
	 */
	public String getPhoneMetrics() {
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		String str = String
				.format("手机分辨率:%1$d*%2$d", widthPixels, heightPixels);
		return str;
	}

	/**
	 * 获取相机最大分辨率
	 * 
	 * @return
	 */
	public String getCameraMetrics() {
		Camera camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		camera.release();
		List<Size> list = parameters.getSupportedPreviewSizes();
		// 假设分辨率最大下标为0;
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			int height = list.get(i).height;
			int width = list.get(i).width;
			// Log.e("tag", width + "*" + height);
			if (list.get(index).width * list.get(index).height < width * height) {
				index = i;
			}
		}
		// 获取最大分辨率
		String str = String.format("相机分辨率：%1$d*%2$d", list.get(index).width,
				list.get(index).height);
		return str;
	}

	/**
	 * 获取基带版本
	 * 
	 * @return
	 */
	public String getBasebandVersion() {
		String version = Build.getRadioVersion();
		return null != version ? version : "unknow";
	}

	/**
	 * 判断是否Root
	 * 
	 * @return “是” 表示已Root; “否” 表示未Root
	 */
	public String isRoot() {
		String[] sus = { "/system/bin/su", "/system/sbin/su", "/system/xbin/su" };
		boolean isRoot = false;
		for (String str : sus) {
			File file = new File(str);
			if (file.exists()) {
				isRoot = true;
				break;
			}
		}
		return isRoot ? "是" : "否";
	}
}
