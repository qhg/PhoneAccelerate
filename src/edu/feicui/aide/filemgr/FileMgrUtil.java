package edu.feicui.aide.filemgr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import edu.feicui.aide.bean.FileInfo;
import edu.feicui.aide.bean.SecondFileInfo;

/**
 * 该类提供获取文件管理的相关数据源的方法
 * 
 */
public class FileMgrUtil {

	/**
	 * 第二界面数据源(全部，包括无后缀文件)
	 */
	List<SecondFileInfo> secAllFile;// 所有文件（包括无后缀的文件）
	/**
	 * 第二界面数据源（文档）
	 */
	List<SecondFileInfo> secTxtFile;// 文档数据源
	/**
	 * 第二界面数据源（音频）
	 */
	List<SecondFileInfo> secAudioFile;// 音频
	/**
	 * 第二界面数据源（视频）
	 */
	List<SecondFileInfo> secVideoFile;// 视频
	/**
	 * 第二界面数据源（图片）
	 */
	List<SecondFileInfo> secImgFile;// 图片
	/**
	 * 第二界面数据源（压缩包）
	 */
	List<SecondFileInfo> secZipFile;// 压缩包
	/**
	 * 第二界面数据源（安装包）
	 */
	List<SecondFileInfo> secApkFile;// 安装包
	/**
	 * 第二界面数据源（无后缀文件）
	 */
	List<SecondFileInfo> secSpacilFile;// 无后缀所有文件
	/**
	 * 第一界面数据源(封装各类型文件信息)
	 */
	List<FileInfo> mList;
	/**
	 * FileInfo的表示全部的对象
	 */
	FileInfo allFile;// 所有文件
	/**
	 * FileInfo类表示文档的对象
	 */
	FileInfo txtFile;// 文档
	/**
	 * FileInfo类表示文档的对象
	 */
	FileInfo audioFile;// 音频
	/**
	 * FileInfo类表示视频的对象
	 */
	FileInfo videoFile;// 视频
	/**
	 * FileInfo类表示图片的对象
	 */
	FileInfo imageFile;// 图片
	/**
	 * FileInfo类表示压缩包的对象
	 */
	FileInfo zipFile;// 压缩包
	/**
	 * FileInfo类表示安装包的对象
	 */
	FileInfo apkFile;// 安装包
	/**
	 * 所有文件大小
	 */
	long allLen;
	/**
	 * 文档大小
	 */
	long txtLen;
	/**
	 * 音频大小
	 */
	long audioLen;
	/**
	 * 视频大小
	 */
	long videoLen;
	/**
	 * 图片大小
	 */
	long imageLen;
	/**
	 * 压缩包大小
	 */
	long zipLen;
	/**
	 * 安装包大小
	 */
	long apkLen;
	/**
	 * 回调接口对象
	 */
	onFlushListener mFlushListener;
	private static FileMgrUtil mFileMgrUtil;

	private FileMgrUtil() {
		init();
	}

	/**
	 * @return 获取FileMgrUtil的一个单例
	 */
	public static FileMgrUtil getInstance() {
		if (null == mFileMgrUtil) {
			synchronized (FileMgrUtil.class) {
				if (null == mFileMgrUtil) {
					mFileMgrUtil = new FileMgrUtil();
				}
			}
		}
		return mFileMgrUtil;
	}

	/**
	 * 初始化数据源，并设置各数据源类型
	 */
	void init() {
		mList = new ArrayList<>();
		allFile = new FileInfo();
		allFile.setFileType("全部");
		mList.add(allFile);

		txtFile = new FileInfo();
		txtFile.setFileType("文档");
		mList.add(txtFile);

		audioFile = new FileInfo();
		audioFile.setFileType("音频");
		mList.add(audioFile);

		videoFile = new FileInfo();
		videoFile.setFileType("视频");
		mList.add(videoFile);

		imageFile = new FileInfo();
		imageFile.setFileType("图片");
		mList.add(imageFile);

		zipFile = new FileInfo();
		zipFile.setFileType("压缩包");
		mList.add(zipFile);

		apkFile = new FileInfo();
		apkFile.setFileType("安装包");
		mList.add(apkFile);

		// 跳转后的数据源初始化
		secAllFile = new ArrayList<>();
		secTxtFile = new ArrayList<>();
		secAudioFile = new ArrayList<>();
		secVideoFile = new ArrayList<>();
		secImgFile = new ArrayList<>();
		secZipFile = new ArrayList<>();
		secApkFile = new ArrayList<>();
		secSpacilFile = new ArrayList<>();
	}

	/**
	 * 设置FileInfo数据源信息
	 * 
	 * @return
	 */
	public void SetFileInfo() {
		File file = getSDPath();
		listFile(file, true);
	}

	/**
	 * 获取SDcard路径
	 * 
	 * @return
	 */
	public File getSDPath() {
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
	 * 遍历指定文件目录<br/>
	 * 相应信息封装至javabean中<br/>
	 * 递归一次调用一次刷新界面方法
	 * 
	 * @param file
	 * @return
	 */
	public void listFile(File file, boolean isFinish) {
		// 判断file是否有效
		if (!file.exists() || !file.canRead() || file.length() == 0) {
			return;
		}
		if (file.isFile()) {// 如果是文件
			allLen += file.length();
			allFile.setFileLen(allLen);
			// 文件名
			String fileName = file.getName();
			// 没有后缀的文件另外保存
			if (fileName.indexOf(".") == -1) {
				long time = file.lastModified();
				long size = file.length();
				SecondFileInfo info1 = new SecondFileInfo(false, "未知",
						fileName, time, size, file);
				secSpacilFile.add(info1);
				return;
			}
			// 封装各子条目信息(有后缀)
			setFile(file);
			// 刷新界面(使用回调)
			mFlushListener.onFlush();
			return;
		} else {// 如果是文件夹
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				listFile(files[i], false);
			}
		}
		// 读取完毕后退出递归
		if (isFinish) {
			// 在所有文件中加入无后缀的文件
			secAllFile.addAll(secSpacilFile);
			// 设置FileInfo中状态属性的值为true
			setState();
			return;
		}
	}

	/**
	 * 判断参数文件的类型并获取其大小
	 * 
	 * @param file
	 *            当前文件
	 */
	public void setFile(File file) {
		// 文件名
		String fileName = file.getName();
		String[] strs = fileName.split("\\.");
		// 获取文件后缀(作为icon)
		String suffix = strs[1];
		// 文件最后修改时间
		long time = file.lastModified();
		// 文件大小
		long size = file.length();
		SecondFileInfo info = new SecondFileInfo(false, suffix, fileName, time,
				size, file);
		secAllFile.add(info);
		// 每判断一次后设置其大小，并刷新以获得动态效果
		if (isTextFile(suffix)) {// 文档
			txtLen += file.length();
			txtFile.setFileLen(txtLen);
			secTxtFile.add(info);
		} else if (isAudioFile(suffix)) {// 音频
			audioLen += file.length();
			audioFile.setFileLen(audioLen);
			secAudioFile.add(info);
		} else if (isImageFile(suffix)) {// 图片
			imageLen += file.length();
			imageFile.setFileLen(imageLen);
			secImgFile.add(info);
		} else if (isProgramFile(suffix)) {// 安装包
			apkLen += file.length();
			apkFile.setFileLen(apkLen);
			secApkFile.add(info);
		} else if (isVideoFile(suffix)) {// 音频
			videoLen += file.length();
			videoFile.setFileLen(videoLen);
			secVideoFile.add(info);
		} else if (isZipFile(suffix)) {// 压缩包
			zipLen += file.length();
			zipFile.setFileLen(zipLen);
			secZipFile.add(info);
		}
	}

	/**
	 * 文件全部读取完毕后设置数据源中状态为true;
	 */
	public void setState() {
		allFile.setFinish(true);
		txtFile.setFinish(true);
		apkFile.setFinish(true);
		videoFile.setFinish(true);
		zipFile.setFinish(true);
		imageFile.setFinish(true);
		audioFile.setFinish(true);
	}

	/**
	 * 根据文件名后缀判断该文件是否为文档类文件
	 * 
	 * @param suffix
	 *            文件名的后缀
	 * 
	 * @return 如果是文档类文件返回true，否则返回false
	 */
	public boolean isTextFile(String suffix) {
		String[] str = { "txt", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
				"pdf", "c", "h", "cpp", "hpp", "java", "js", "html", "xml",
				"xhtml", "css" };
		for (String string : str) {
			if (suffix.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件名后缀判断该文件是否为视频类文件
	 * 
	 * @param suffix
	 *            文件名的后缀
	 * 
	 * @return 如果是视频类文件返回true，否则返回false
	 */
	public boolean isVideoFile(String suffix) {
		String[] str = { "avi", "mp4", "rm", "rmvb", "3gp", "flash" };
		for (String string : str) {
			if (suffix.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件名后缀判断该文件是否为音频类文件
	 * 
	 * @param suffix
	 *            文件名的后缀
	 * 
	 * @return 如果是音频类文件返回true，否则返回false
	 */
	public boolean isAudioFile(String suffix) {
		String[] str = { "mp3", "wav", "wma", };
		for (String string : str) {
			if (suffix.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件名后缀判断该文件是否为图像类文件
	 * 
	 * @param suffix
	 *            文件名的后缀
	 * 
	 * @return 如果是图像类文件返回true，否则返回false
	 */
	public boolean isImageFile(String suffix) {
		String[] str = { "bmp", "jpg", "gif", "png", "jpeg", "ico", "tiff",
				"xcf" };
		for (String string : str) {
			if (suffix.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件名后缀判断该文件是否为压缩文件
	 * 
	 * @param suffix
	 *            文件名的后缀
	 * 
	 * @return 如果是压缩文件返回true，否则返回false
	 */
	public boolean isZipFile(String suffix) {
		String[] str = { "zip", "rar", "gz", "tar" };
		for (String string : str) {
			if (suffix.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据文件名后缀判断该文件是否为程序包文件
	 * 
	 * @param suffix
	 *            文件名的后缀
	 * 
	 * @return 如果是程序包文件返回true，否则返回false
	 */
	public boolean isProgramFile(String suffix) {
		String[] str = { "apk" };
		for (String string : str) {
			if (suffix.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 子类必须实现该方法以实现事件的监听
	 */
	public void setOnFlushListener(onFlushListener flushListener) {
		mFlushListener = flushListener;
	}

	/**
	 * 回调接口<br/>
	 * onFlush() 方法用于实现具体操作
	 * 
	 */
	public interface onFlushListener {
		void onFlush();
	}
}
