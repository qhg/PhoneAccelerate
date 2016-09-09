package edu.feicui.aide.garbage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * 该列提供获取缓存中垃圾文件的方法
 * 
 */
public class CacheUtil {
	/**
	 * 上下文
	 */
	Context mContext;
	private PackageManager mManager;
	/**
	 * 指定类型文件大小
	 */
	long mLen;
	/**
	 * 手机缓存垃圾大小
	 */
	long phoneCacheLen;
	/**
	 * 手机内存垃圾大小
	 */
	long phoneStorageLen;
	/**
	 * SD卡缓存来及大小
	 */
	long SDcardCacheLen;
	/**
	 * SD卡内存垃圾大小
	 */
	long SDcardStorageLen;
	/**
	 * 缓存文件垃圾大小
	 */
	long fileCacheLen;
	/**
	 * 所有垃圾文件大小
	 */
	long allLen;
	OnFlushListener mFlushListener;

	public CacheUtil(Context context) {
		mContext = context;
	}

	/**
	 * 获取对应类型的数据源
	 * 
	 * @param type
	 *            五种类型分别对应
	 * @return
	 */
	public List<ChildInfo> getChild(String type) {
		List<ChildInfo> list = new ArrayList<>();
		mLen = 0;
		mManager = mContext.getPackageManager();
		// 获取所有已安装的应用
		List<PackageInfo> packages = mManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : packages) {
			// 获取包名
			String packageName = packageInfo.packageName;

			// 获取每个应用的上下文
			Context context = getPackageContext(packageName);
			if (null != context) {
				File file = null;
				// 手机缓存垃圾
				switch (type) {
				case "phoneCache":// 手机缓存垃圾
					file = context.getCacheDir();
					break;
				case "phoneStorage":// 手机内存垃圾
					file = context.getFilesDir();
					break;
				case "SDcardCache":// SD卡缓存垃圾
					file = context.getExternalCacheDir();
					break;
				case "SDcardStorage":// SD卡内存垃圾
					file = context.getExternalFilesDir(null);
					break;
				case "fileCache":// 缓存文件垃圾
					file = Environment.getDownloadCacheDirectory();
					break;
				default:
					break;
				}
				listFile(file, packageInfo, true, list, type);
			}
		}
		return list;
	}

	/**
	 * 递归遍历给定的文件
	 * 
	 * @param file
	 * @param icon
	 * @param isFinish
	 * @param list
	 */
	private void listFile(File file, PackageInfo packageInfo, boolean isFinish,
			List<ChildInfo> list, String type) {
		if (null == file || !file.canRead() || file.length() == 0) {
			return;
		}
		if (file.isFile()) {
			// 获取该应用的icon
			Drawable icon = packageInfo.applicationInfo.loadIcon(mManager);
			// 获取该应用的label
			String name = packageInfo.applicationInfo.loadLabel(mManager)
					.toString();
			long size = file.length();
			mLen += size;
			switch (type) {
			case "phoneCache":// 手机缓存垃圾
				phoneCacheLen = mLen;
				break;
			case "phoneStorage":// 手机内存垃圾
				phoneStorageLen = mLen;
				break;
			case "SDcardCache":// SD卡缓存垃圾
				SDcardCacheLen = mLen;
				break;
			case "SDcardStorage":// SD卡内存垃圾
				SDcardStorageLen = mLen;
				break;
			case "fileCache":// 缓存文件垃圾
				fileCacheLen = mLen;
				break;
			}
			allLen += size;
			ChildInfo info = new ChildInfo(file, false, icon, name, size);
			list.add(info);
			// 回调用于刷新各大小值
			mFlushListener.flush();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				listFile(files[i], packageInfo, false, list, type);
			}
		}
		// 遍历完成
		if (isFinish) {
			return;
		}
	}

	/**
	 * 回调接口
	 * 
	 */
	public interface OnFlushListener {
		void flush();
	}

	/**
	 * 子类通过该方法实现回调
	 */
	public void setOnFlushListener(OnFlushListener flushListener) {
		mFlushListener = flushListener;
	}

	/**
	 * @param packageName
	 *            包名
	 * @return 对应包名的上下文
	 */
	private Context getPackageContext(String packageName) {
		Context packageContext = null;
		try {
			packageContext = mContext.createPackageContext(packageName,
					Context.CONTEXT_IGNORE_SECURITY
							| Context.CONTEXT_INCLUDE_CODE);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return packageContext;
	}
}
