package edu.feicui.aide.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;
import edu.feicui.aide.R;
import edu.feicui.aide.accelerate.ProcessInfo;
import edu.feicui.aide.soft.SoftInfo;

/**
 * 管理软件进程类
 * 
 */
public class ProcessUtil {
	/**
	 * 包管理
	 */
	private PackageManager mManager;
	/**
	 * 上下文
	 */
	Context mContext;
	private static ProcessUtil mProgressUtil;

	/**
	 * 活动APP管理
	 */
	public ActivityManager mActivityManager;

	private ProcessUtil(Context context) {
		this.mContext = context;
		mManager = context.getPackageManager();
		mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public static ProcessUtil getInstance(Context context) {
		if (mProgressUtil == null) {
			synchronized (ProcessUtil.class) {
				if (mProgressUtil == null) {
					context = context.getApplicationContext();
					mProgressUtil = new ProcessUtil(context);
				}
			}
		}
		return mProgressUtil;
	}

	/**
	 * 获取与参数信息相关的数据源
	 * 
	 * @param value
	 *            0 对应所有软件信息<br/>
	 *            1对应系统软件信息<br/>
	 *            2对应用户软件信息
	 * @return
	 */
	public List<SoftInfo> getSoftInfo(int value) {
		List<SoftInfo> list = new ArrayList<>();
		List<PackageInfo> data = new ArrayList<PackageInfo>();
		switch (value) {
		case 0:// 所有已安装数据源
			data = mManager.getInstalledPackages(0);
			break;
		case 1:// 系统软件数据源
			data = getSystemAPP();
			break;
		case 2:// 用户软件数据源
			data = getUserAPP();
			break;
		}
		for (int i = 0; i < data.size(); i++) {
			Drawable icon = data.get(i).applicationInfo.loadIcon(mManager);
			String label = data.get(i).applicationInfo.loadLabel(mManager)
					.toString();
			String packageName = data.get(i).packageName;
			String versionName = data.get(i).versionName;
			SoftInfo softInfo = new SoftInfo(false, icon, label, packageName,
					versionName);
			list.add(softInfo);
		}
		// Log.e("all=", list.size() + "");
		return list;
	}

	/**
	 * @return 返回所有用户已安装APP(PackageInfo)
	 */
	public List<PackageInfo> getUserAPP() {
		List<PackageInfo> all = mManager.getInstalledPackages(0);
		// 该集合用于存储用户APP
		List<PackageInfo> list = new ArrayList<>();
		for (int i = 0; i < all.size(); i++) {
			PackageInfo info = all.get(i);
			ApplicationInfo applicationInfo = info.applicationInfo;
			if (!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
				list.add(info);
			}
		}
		// Log.e("all=", list.size() + "");
		return list;
	}

	/**
	 * @return 返回所有系统的APP(PackageInfo)
	 */
	public List<PackageInfo> getSystemAPP() {
		List<PackageInfo> all = mManager.getInstalledPackages(0);
		// 该集合用于存储用户APP
		List<PackageInfo> list = new ArrayList<>();
		for (int i = 0; i < all.size(); i++) {
			PackageInfo info = all.get(i);
			ApplicationInfo applicationInfo = info.applicationInfo;
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				list.add(info);
			}
		}
		return list;
	}

	/**
	 * 运行中的系统数据源 <br/>
	 * 
	 * @return运行中的系统APP
	 */
	public List<ProcessInfo> getSystemRunningAppInfo() {
		List<ProcessInfo> list = new ArrayList<>();
		List<PackageInfo> allSystem = getSystemAPP();
		List<RunningAppProcessInfo> appPrecessInfos = mActivityManager
				.getRunningAppProcesses();
		for (int i = 0; i < appPrecessInfos.size(); i++) {
			// 正在运行的应用进程名
			RunningAppProcessInfo appProcessinfo = appPrecessInfos.get(i);
			String processName1 = appProcessinfo.processName;
			for (int j = 0; j < allSystem.size(); j++) {
				// 已安装的系统程序进程名
				PackageInfo packageInfo = allSystem.get(j);
				String processName2 = packageInfo.applicationInfo.processName;
				if (processName1.equals(processName2)) {
					// 图片
					Drawable img = packageInfo.applicationInfo
							.loadIcon(mManager);
					// 应用名
					String label = packageInfo.applicationInfo.loadLabel(
							mManager).toString();
					// 获取进程占用的内存
					int[] pids = new int[] { appProcessinfo.pid };
					Debug.MemoryInfo[] memoryInfo = mActivityManager
							.getProcessMemoryInfo(pids);
					int memory1 = memoryInfo[0].dalvikPrivateDirty;
					// 改变单位 kb 转换为Mb
					String memory2 = Formatter
							.formatFileSize(mContext, memory1);
					String memory = String.format("内存：%1$S", memory2);
					ProcessInfo info = new ProcessInfo(false, img, label,
							memory, "系统进程");
					// Log.e("tag", info.toString());
					// 获得APP包名并写入数据源
					info.packageName = packageInfo.packageName;
					list.add(info);
				}
			}
		}
		return list;

	}

	/**
	 * 运行中的APP用户数据源 <br/>
	 * 
	 * @return 获取正在运行的用户APP
	 */
	public List<ProcessInfo> getUserRunningAppInfo() {
		List<ProcessInfo> list = new ArrayList<>();
		List<PackageInfo> allUser = getUserAPP();
		List<RunningAppProcessInfo> appPrecessInfos = mActivityManager
				.getRunningAppProcesses();

		for (int i = 0; i < appPrecessInfos.size(); i++) {
			// 正在运行的应用进程名
			RunningAppProcessInfo appProcessinfo = appPrecessInfos.get(i);
			String processName1 = appProcessinfo.processName;
			for (int j = 0; j < allUser.size(); j++) {
				// 已安装的用户程序进程名
				PackageInfo packageInfo = allUser.get(j);
				String processName2 = packageInfo.applicationInfo.processName;
				if (processName1.equals(processName2)) {
					// 图片
					Drawable img = packageInfo.applicationInfo
							.loadIcon(mManager);
					// 应用名
					String label = packageInfo.applicationInfo.loadLabel(
							mManager).toString();
					// 获取该进程占用的内存
					int[] pids = new int[] { appProcessinfo.pid };
					Debug.MemoryInfo[] memoryInfo = mActivityManager
							.getProcessMemoryInfo(pids);
					int memory1 = memoryInfo[0].dalvikPrivateDirty;
					// 改变单位 kb 转换为Mb
					String memory2 = Formatter
							.formatFileSize(mContext, memory1);
					// 使用资源resources去除硬编码
					String memory = String.format(mContext.getResources()
							.getString(R.string.processinfo_memory), memory2);
					ProcessInfo info = new ProcessInfo(false, img, label,
							memory, "用户进程");
					// Log.e("tag", info.toString());

					// 获得APP包名并写入数据源
					info.packageName = packageInfo.packageName;
					list.add(info);
				}
			}
		}
		return list;
	}
}
