package edu.feicui.aide.main;

import android.app.Application;
import edu.feicui.aide.util.CrashHandler;

public class AideApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init(this);
	}
}
