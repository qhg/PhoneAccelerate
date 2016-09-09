package edu.feicui.aide.garbage;

import java.io.File;

import android.graphics.drawable.Drawable;

public class ChildInfo {

	File file;
	boolean flag;
	Drawable icon;
	String name;
	long size;

	public ChildInfo(File file, boolean flag, Drawable icon, String name,
			long size) {
		super();
		this.file = file;
		this.flag = flag;
		this.icon = icon;
		this.name = name;
		this.size = size;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "CacheInfo [file=" + file + ", flag=" + flag + ", icon=" + icon
				+ ", name=" + name + ", size=" + size + "]";
	}

}
