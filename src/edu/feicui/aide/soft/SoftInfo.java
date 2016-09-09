package edu.feicui.aide.soft;

import android.graphics.drawable.Drawable;

public class SoftInfo {
	public boolean flag;
	public Drawable icon;
	public String label;
	String packageName;
	String versionName;

	public SoftInfo(boolean flag, Drawable icon, String label,
			String packageName, String versionName) {
		super();
		this.flag = flag;
		this.icon = icon;
		this.label = label;
		this.packageName = packageName;
		this.versionName = versionName;
	}

	@Override
	public String toString() {
		return "SoftInfo [icon=" + icon + ", label=" + label + ", packageName="
				+ packageName + ", versionName=" + versionName + "]";
	}

}
