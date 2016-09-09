package edu.feicui.aide.bean;

/**
 * 通讯大全JavaBean
 *
 */
public class TelClass {
	public String name;
	public int idx;

	public TelClass(String name, int idx) {
		super();
		this.name = name;
		this.idx = idx;
	}

	@Override
	public String toString() {
		return "TelClass [name=" + name + ", idx=" + idx + "]";
	}

}
