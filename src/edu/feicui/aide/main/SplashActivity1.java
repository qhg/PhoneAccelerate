package edu.feicui.aide.main;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import edu.feicui.aide.R;

public class SplashActivity1 extends BaseActivity implements AnimationListener {
	/**
	 * msg的what，用来区分消息的发出者
	 */
	final int MESSAGE_SPLAH = 1;
	ImageView mImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash1);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		// 帧动画 drawable animation
		mImg = (ImageView) findViewById(R.id.img_anim);
		AnimationDrawable animation = (AnimationDrawable) mImg.getBackground();
		animation.start();
		// 补间动画 view animation
		Animation alphaAnimation = AnimationUtils.loadAnimation(this,
				R.anim.alpha);
		mImg.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this);
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mHandler.sendEmptyMessageDelayed(MESSAGE_SPLAH, 1500);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		gotoActivity(this, MainActivity.class);
		finish();
	}
}
