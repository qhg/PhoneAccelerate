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

public class CircleView extends View {
	/**
	 * 弧形所在的矩形
	 */
	RectF mRectF;
	/**
	 * 弧形的画笔
	 */
	Paint mPaint;
	/**
	 * 弧形扫过的角度
	 */
	private float mSweepAngle;
	/**
	 * 绘制的开关
	 */
	private boolean mIsRunning;
	/**
	 * 绘制弧形的状态
	 */
	private int mState;

	public CircleView(Context context) {
		this(context, null);
	}

	public CircleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
		mPaint.setColor(Color.GREEN);
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		mRectF = new RectF(0, 0, width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawArc(mRectF, -90f, mSweepAngle, true, mPaint);
	}

	/**
	 * 绘制动态变化的弧形
	 * 
	 * @param sweepAngle
	 *            传入参数为起始的角度
	 */
	public void drawCircle(final float sweepAngle) {
		if (mIsRunning) {
			return;
		}
		mSweepAngle = sweepAngle;
		// 绘制中，改变开关mIsRunning
		mIsRunning = true;
		mState = 0;
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				switch (mState) {
				case 0:// 回退状态
					mSweepAngle -= 10;
					postInvalidate();// 通知刷新
					if (mSweepAngle <= 0) {
						mSweepAngle = 0;
						// 改状态为前进状态
						mState = 1;
					}
					break;
				case 1:// 前进状态
					mSweepAngle += 10;
					postInvalidate();// 通知刷新
					if (mSweepAngle >= sweepAngle) {
						// 退出绘制
						mSweepAngle = sweepAngle;
						timer.cancel();
						mIsRunning = false;
					}
					break;
				}
			}

		};
		// 绘制完后
		timer.schedule(task, 30, 30);
	}
}
