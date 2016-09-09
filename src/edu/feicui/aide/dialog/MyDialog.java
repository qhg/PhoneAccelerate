package edu.feicui.aide.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import edu.feicui.aide.R;

public class MyDialog extends Dialog implements
		android.view.View.OnClickListener {
	Button mBtnYes;
	Button mBtnNo;
	OnMyDialogListener mDialogListener;

	public MyDialog(Context context) {
		this(context, 0);
	}

	public MyDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_my);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		// 点击确认按钮
		mBtnYes = (Button) findViewById(R.id.btn_yes);
		mBtnYes.setOnClickListener(this);
		// 点击取消按钮
		mBtnNo = (Button) findViewById(R.id.btn_no);
		mBtnNo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_yes:// 点击对话框确认按钮时的操作
			mDialogListener.onClickYesListener();
			break;
		case R.id.btn_no:// 点击对话框取消按钮时的操作
			mDialogListener.onClickNoListener();
			break;
		}
	}

	/**
	 * 自定义对话框的回调接口
	 *
	 */
	public interface OnMyDialogListener {
		/**
		 * 该方法在用户点击确定时执行
		 */
		void onClickYesListener();

		/**
		 * 该方法在用户点击取消时执行
		 */
		void onClickNoListener();
	}

	/**
	 * 该方法用来实例化对话框对象
	 */
	public void setOnMyDiaLogListener(OnMyDialogListener mDialogListener) {
		this.mDialogListener = mDialogListener;
	}
}
