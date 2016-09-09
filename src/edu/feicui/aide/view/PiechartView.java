package edu.feicui.aide.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 软件管理圆形饼图
 *
 */
public class PiechartView extends View {
	/**
	 * 画笔
	 */
	Paint mPaint;
	/**
	 * 矩形(绘制圆所需的参数)
	 */
	RectF mRectF;
	/**
	 * 手机内置空间占比的角度大小
	 */
	float mPhoneAngle;
	/**
	 * 外部存储空间占比的角度大小
	 */
	float mSDAngle;

	public PiechartView(Context context) {
		this(context, null);
	}

	public PiechartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PiechartView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
		// 设置抗锯齿
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		mRectF = new RectF(0, 0, width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 绘制黄色饼形图
		mPaint.setColor(Color.YELLOW);
		canvas.drawArc(mRectF, -90f, 360f, true, mPaint);
		// 绘制蓝色手机内置空间
		mPaint.setColor(Color.BLUE);
		canvas.drawArc(mRectF, -90f, mPhoneAngle, true, mPaint);
		// 绘制绿色外置存储空间
		mPaint.setColor(Color.GREEN);
		canvas.drawArc(mRectF, -90f + mPhoneAngle, mSDAngle, true, mPaint);
	}

	/**
	 * 绘制动态饼形图
	 * 
	 * @param phoneRatio
	 *            手机内置空间占比
	 * @param SDRatio
	 *            外置存储空间占比
	 */
	public void drawPiechart(float phoneRatio, float SDRatio) {
		final float phoneAngle = phoneRatio * 360;
		final float SDAngle = SDRatio * 360;
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				mPhoneAngle += 10;
				mSDAngle += 10;
				// 通知刷新
				postInvalidate();
				if (mPhoneAngle >= phoneAngle) {
					mPhoneAngle = phoneAngle;
				}
				if (mSDAngle >= SDAngle) {
					mSDAngle = SDAngle;
				}
				// 退出条件
				if (mPhoneAngle >= phoneAngle && mSDAngle >= SDAngle) {
					timer.cancel();
				}
			}

		};
		// 50毫秒后开始绘制，每次绘制间隔50毫秒
		timer.schedule(task, 50, 50);
	}
}
