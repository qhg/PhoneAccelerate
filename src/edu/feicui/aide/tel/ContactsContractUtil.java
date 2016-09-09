package edu.feicui.aide.tel;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import edu.feicui.aide.bean.TelPerson;

/**
 * 提供获取通讯录数据的方法
 * 
 */
public class ContactsContractUtil {
	Context mContext;
	ContentResolver mContentResolver;

	public ContactsContractUtil(Context context) {
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	/**
	 * 获取通讯录信息(Contacts总表)
	 * 
	 * @return
	 */
	public List<TelPerson> getContactsInfo() {
		List<TelPerson> list = new ArrayList<>();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		Cursor cursor = mContentResolver.query(uri, new String[] {
				ContactsContract.Contacts._ID,// 主键_ID
				ContactsContract.Contacts.DISPLAY_NAME,// 姓名
				ContactsContract.Contacts.HAS_PHONE_NUMBER // 是否有电话
				}, null, null, null);
		// 下标
		int _idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
		int displayNameIndex = cursor
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		int hasPhoneNumberIndex = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				long _id = cursor.getLong(_idIndex);
				String name = cursor.getString(displayNameIndex);
				int hasPhoneNumber = cursor.getInt(hasPhoneNumberIndex);
				TelPerson p = new TelPerson();
				p._id = String.valueOf(_id);
				p.name = name;
				p.hasPhoneNumber = hasPhoneNumber;
				// 获取指定联系人电话号码
				if (hasPhoneNumber > 0) {
					setPhoneNumber(p, p._id);
				}
				if (null == p.homeNumber) {
					p.homeNumber = "未知";
				}
				if (null == p.workNumber) {
					p.workNumber = "未知";
				}
				if (null == p.mobileNumber) {
					p.mobileNumber = "未知";
				}
				// 获取指定联系人电子邮件
				setEmailadr(p, p._id);
				if (null == p.email) {
					p.email = "未知";
				}
				list.add(p);
				// Log.e("tag", p.toString());
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 获取指定联系人的电话号码,并分类
	 * 
	 * @param p
	 *            指定目标联系人
	 * @param _id
	 *            目标联系人的ID
	 */
	public void setPhoneNumber(TelPerson p, String _id) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = mContentResolver.query(uri, new String[] {
				ContactsContract.CommonDataKinds.Phone.NUMBER,// 电话号码
				ContactsContract.CommonDataKinds.Phone.TYPE },// 电话类型
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?",// 筛选条件
				new String[] { _id },// 条件的参数
				null);
		// 下标
		int numberIndex = cursor
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		int typeIndex = cursor
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(numberIndex);
				int type = cursor.getInt(typeIndex);
				switch (type) {
				case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:// 家庭电话
					p.homeNumber = number;
					break;
				case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:// 工作电话
					p.workNumber = number;
					break;
				case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:// 工作电话
					p.mobileNumber = number;
					break;
				default:
					break;
				}
			}
			cursor.close();
		}
	}

	/**
	 * 获取指定联系人的email
	 * 
	 * @param p
	 * @param _id
	 */
	public void setEmailadr(TelPerson p, String _id) {
		Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		Cursor cursor = mContentResolver
				.query(uri,
						new String[] { ContactsContract.CommonDataKinds.Email.ADDRESS, },// 电子邮件地址
						ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " =?",// 筛选条件
						new String[] { _id },// 条件参数
						null);
		int emailadrIndex = cursor
				.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
		if (null != cursor) {
			while (cursor.moveToNext()) {
				String emailadr = cursor.getString(emailadrIndex);
				p.email = emailadr;
			}
			cursor.close();
		}
	}
}
