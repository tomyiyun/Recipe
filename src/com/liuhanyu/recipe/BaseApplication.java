package com.liuhanyu.recipe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.liuhanyu.recipe.support.Constants;

public final class BaseApplication extends Application {

	private static final String TAG = BaseApplication.class.getSimpleName();
	private static BaseApplication apolloContext = null;

	private static int displayWidth;
	private static int displayHeight;
	private static int densityDpi;
	private static float density;
	private Activity activity;
	private int nowTermWeek;

	public static BaseApplication getInstance() {
		return apolloContext;
	}

	public int getNowTermWeek() {
		return nowTermWeek;
	}

	public void setNowTermWeek(int nowTermWeek) {
		this.nowTermWeek = nowTermWeek;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public int getDensityDpi() {
		return densityDpi;
	}

	public float getDensity() {
		return density;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		apolloContext = this;
		initAvatarSize();
	}

	private void initAvatarSize() {
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		// 要获取屏幕的宽和高等参数，首先需要声明一个DisplayMetrics对象，屏幕的宽高等属性存放在这个对象�?
		DisplayMetrics DM = new DisplayMetrics();
		// 获取窗口管理�?获取当前的窗�?调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象�?�?��通过getMetrics(DM)获取
		windowManager.getDefaultDisplay().getMetrics(DM);
		displayWidth = DM.widthPixels;
		displayHeight = DM.heightPixels;
		// 使用display.getOrientation() 判断横竖屏不准确
		if (displayWidth > displayHeight) {
			displayWidth = DM.heightPixels;
			displayHeight = DM.widthPixels;
		}
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		density = metrics.density;
		densityDpi = metrics.densityDpi;
		if (densityDpi <= DisplayMetrics.DENSITY_LOW) {
		} else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
		} else if (densityDpi <= DisplayMetrics.DENSITY_HIGH
				&& displayWidth <= Constants.DISPLAY_HDPI_WIDTH) {
		} else {
		}
		if (BuildConfig.DEBUG) {
			Log.v("Display Width: ", " " + displayWidth);
			Log.v("Display Height: ", " " + displayHeight);
			Log.v("Display Density: ", " " + densityDpi);
			Log.d(TAG, "initAvatarSize Finish : " + System.currentTimeMillis()
					/ 1000);
		}
	}
}
