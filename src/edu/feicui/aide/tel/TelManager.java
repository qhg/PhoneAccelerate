package edu.feicui.aide.tel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import edu.feicui.aide.bean.ContactsInfo;
import edu.feicui.aide.bean.TelClass;
import edu.feicui.aide.bean.TelTableClass;

/**
 * 提供对数据库数据的获取方法
 * 
 */
public class TelManager {
	/**
	 * 上下文
	 */
	Context mContext;
	/**
	 * 当前类实例
	 */
	static TelManager sInstance;
	/**
	 * 对Assets资源管理的类
	 */
	AssetManager mAssetManager;
	/**
	 * 内部存储类
	 */
	File dbFile;
	/**
	 * 内容解析器,可对内容提供者进行操作
	 */
	ContentResolver mContentResolver;

	private TelManager(Context context) {
		mContext = context;
		mAssetManager = context.getAssets();
		mContentResolver = context.getContentResolver();
		// 该应用对应的内部存储File
		dbFile = new File(context.getFilesDir(), "commonnum.db");
		copyDBFile();
	}

	public static TelManager getInstance(Context context) {
		if (null == sInstance) {
			synchronized (TelManager.class) {
				if (null == sInstance) {
					context = context.getApplicationContext();
					sInstance = new TelManager(context);
				}
			}
		}
		return sInstance;
	}

	/**
	 * 将Assets/db/commonnum.db文件中的内容复制到dbfile中<br/>
	 * dbfile为该应用对应的内部存储file
	 * 
	 */
	public void copyDBFile() {
		InputStream in = null;
		OutputStream out = null;
		try {
			// 输入流，用于读取DB内容
			in = mAssetManager.open("db/commonnum.db");
			// 输出流，用于写数据
			out = new FileOutputStream(dbFile);
			byte[] b = new byte[1024];
			int n = 0;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取classlist表中的数据,用于TelMgrActivity
	 * 
	 * @return
	 */
	public List<TelClass> getClassList() {
		List<TelClass> list = new ArrayList<>();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		String sql = "select * from classlist";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex("name"));
			int idx = c.getInt(c.getColumnIndex("idx"));
			TelClass t = new TelClass(name, idx);
			list.add(t);
		}
		c.close();
		db.close();
		return list;
	}

	/**
	 * 获取各表单中的数据（对应各子条目）<br/>
	 * 需提供相应表名为参数
	 * 
	 * @return
	 */
	public List<TelTableClass> getTableList(String tableName) {
		List<TelTableClass> list = new ArrayList<>();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		String sql = "select * from " + tableName;
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			int _id = c.getInt(c.getColumnIndex("_id"));
			String number = c.getString(c.getColumnIndex("number"));
			String name = c.getString(c.getColumnIndex("name"));
			TelTableClass t = new TelTableClass(_id, number, name);
			list.add(t);
		}
		c.close();
		db.close();
		return list;
	}

	/**
	 * 获取通讯记录信息
	 * 
	 * @return
	 */
	public List<ContactsInfo> getContactsInfo() {
		List<ContactsInfo> list = new ArrayList<>();
		Uri uri = CallLog.Calls.CONTENT_URI;
		Cursor cursor = mContentResolver.query(uri, null, null, null, null);

		int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
		int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		if (null != cursor) {
			while (cursor.moveToNext()) {
				// 通讯时间点
				long time = cursor.getLong(dateIndex);
				Date date = new Date(time);
				String timeStr = dateFormat.format(date);
				// 通讯类型
				int type = cursor.getInt(typeIndex);
				// 电话号码
				String number = cursor.getString(numberIndex);
				// 通讯时长
				long duration = cursor.getLong(durationIndex);
				long sec = duration % 60;// 秒
				long min = duration / 60 % 60;// 分
				long hour = duration / 60 / 60 % 24;// 时
				String durationStr = String.format("时长：%1$02d时%2$02d分%3$02d秒",
						hour, min, sec);
				// 通讯名
				String name = "陌生人";
				Cursor cur = mContentResolver.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.NUMBER
								+ " = ?", new String[] { number }, null);
				if (cur.moveToFirst()) {
					name = cur
							.getString(cur
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				}

				// 将信息封装入ContactsInfo类中
				ContactsInfo contact = new ContactsInfo(number, name,
						durationStr, timeStr, type);
				list.add(contact);
				// Log.e("tag", contact.toString());
				cur.close();
			}
		}
		cursor.close();
		return list;
	}
}
