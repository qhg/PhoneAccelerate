package edu.feicui.aide.bean;

/**
 * 通讯大全JavaBean
 *
 */
public class TelTableClass {
	public int _id;
	public String number;
	public String name;

	public TelTableClass(int _id, String number, String name) {
		super();
		this._id = _id;
		this.number = number;
		this.name = name;
	}

	@Override
	public String toString() {
		return "TelTableClass [_id=" + _id + ", number=" + number + ", name="
				+ name + "]";
	}

}
