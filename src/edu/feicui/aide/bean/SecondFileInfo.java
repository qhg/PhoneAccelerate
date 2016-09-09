package edu.feicui.aide.bean;

import java.io.File;

public class SecondFileInfo {
	/**
	 * Chb的状态
	 */
	public boolean state;
	/**
	 * 后缀
	 */
	public String suffix;
	/**
	 * 文件名
	 */
	public String name;
	/**
	 * 最后修改时间
	 */
	public long time;
	/**
	 * 文件大小
	 */
	public long size;
	/**
	 * 该文件对象
	 */
	public File file;

	public SecondFileInfo() {

	}

	public SecondFileInfo(boolean state, String suffix, String name, long time,
			long size, File file) {
		super();
		this.state = state;
		this.suffix = suffix;
		this.name = name;
		this.time = time;
		this.size = size;
		this.file = file;
	}

	@Override
	public String toString() {
		return "SecondFileInfo [state=" + state + ", suffix=" + suffix
				+ ", name=" + name + ", time=" + time + ", size=" + size
				+ ", file=" + file + "]";
	}

}
