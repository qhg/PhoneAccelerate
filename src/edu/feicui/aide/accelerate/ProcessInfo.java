package edu.feicui.aide.accelerate;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	public boolean flag;
	public Drawable img;
	public String label;
	public String memory;
	public String textRight;
	public String packageName;

	public ProcessInfo() {

	}

	public ProcessInfo(boolean flag, Drawable img, String label, String memory,
			String textRight) {
		this.flag = flag;
		this.img = img;
		this.label = label;
		this.memory = memory;
		this.textRight = textRight;
	}

	@Override
	public String toString() {
		return "ProcessInfo [flag=" + flag + ", img=" + img + ", label="
				+ label + ", memory=" + memory + ", textRight=" + textRight
				+ "]";
	}

}
