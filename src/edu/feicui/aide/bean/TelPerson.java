package edu.feicui.aide.bean;

/**
 * 封装的具体通讯录的信息
 * 
 */
public class TelPerson {
	public String _id;
	public String name;
	public int hasPhoneNumber;
	public String mobileNumber;
	public String homeNumber;
	public String workNumber;
	public String email;

	@Override
	public String toString() {
		return "Person [_id=" + _id + ", name=" + name + ", hasPhoneNumber="
				+ hasPhoneNumber + ", mobileNumber=" + mobileNumber
				+ ", homeNumber=" + homeNumber + ", workNumber=" + workNumber
				+ ", email=" + email + "]";
	}
}
