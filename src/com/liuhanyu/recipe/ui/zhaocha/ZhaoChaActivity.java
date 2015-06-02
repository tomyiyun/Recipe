package com.liuhanyu.recipe.ui.zhaocha;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhanyu.recipe.R;
import com.liuhanyu.recipe.ui.AcitivityWithHelpMenu;

public class ZhaoChaActivity extends AcitivityWithHelpMenu {
	private static final int TIME_MSG = 0x001;
	private RelativeLayout main_layout;
	private TextView timeTxt;
	private ImageView main_img;
	private Button restart;
	private DisplayMetrics metric;
	private float windowWidth, windowHegiht, imageWidth, imageHegit;
	private int radius = 40;
	private int k = 2;
	private long countTime;
	private imageOnTouchListener listener;
	float point[][] = { { 512, 630 }, { 320, 520 } };
	boolean flag[] = { true, true };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_zhaocha);
		init();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

	private void init() {
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		windowWidth = metric.widthPixels;
		windowHegiht = metric.heightPixels;
		initData();
		findViews();
		setListener();
		Toast.makeText(getApplicationContext(), "请找出两幅图中两处不同点",
				Toast.LENGTH_LONG).show();
		handler.sendEmptyMessage(TIME_MSG);
	}

	public void initData() {
		for (int i = 0; i < flag.length; i++) {
			flag[i] = true;
		}
		point[0][0] = 512;
		point[0][1] = 630;
		point[1][0] = 320;
		point[1][1] = 520;
		countTime = 0;
		k=2;
	}

	public void findViews() {
		main_layout = (RelativeLayout) findViewById(R.id.activity_game_zhaocha_main_layout);
		main_img = (ImageView) findViewById(R.id.activity_game_zhaocha_main_img);
		timeTxt = (TextView) findViewById(R.id.activity_game_zhaocha_time);
		restart=(Button)findViewById(R.id.activity_game_zhaocha_restart);
	}

	public void setListener() {
		listener = new imageOnTouchListener();
		main_img.setOnTouchListener(listener);
		restart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ZhaoChaActivity.this
				.setContentView(R.layout.activity_game_zhaocha);
				handler.removeMessages(TIME_MSG);
				init();
			}
		});
	}

	public float getImgX(ImageView view, float x) {
		float widthNow = view.getWidth();
		float widthRaw = 640;
		float trueX = 0;
		trueX = x;
		trueX = (trueX / widthNow * widthRaw);
		return trueX;
	}

	public float getImgY(ImageView view, float y) {
		float heightNow = view.getHeight();
		float heightRaw = 684;
		float trueY = 0;
		trueY = y;
		trueY = (trueY / heightNow * heightRaw);
		return trueY;
	}

	class imageOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				float rawX = event.getX();
				float rawY = event.getY();
				float X = getImgX(main_img, rawX);
				float Y = getImgY(main_img, rawY);
				// System.out.println(X);
				// System.out.println(Y);
				// System.out.println(getImgX(main_img, X));
				// System.out.println(getImgY(main_img, Y));
				float dis1 = (X - point[0][0]) * (X - point[0][0])
						+ (Y - point[0][1]) * (Y - point[0][1]);
				float dis2 = (X - point[1][0]) * (X - point[1][0])
						+ (Y - point[1][1]) * (Y - point[1][1]);
				float rad = 2500;
				int radiusNow = (int) (radius);
				if (dis1 < rad && dis2 > rad) {
					if (flag[0]) {
						main_layout
								.addView(new CircleView(
										getApplicationContext(), rawX, rawY,
										radiusNow));
						main_layout.addView(new CircleView(
								getApplicationContext(), rawX, rawY
										- main_layout.getHeight() / 2,
								radiusNow));
						flag[0] = false;
						k--;
						Toast.makeText(getApplicationContext(),
								"真棒！！还有" + k + "处不同", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"此处已找到，不能重复。。。。", Toast.LENGTH_SHORT).show();
					}

				}
				if (dis1 > rad && dis2 < rad) {
					if (flag[1]) {
						main_layout
								.addView(new CircleView(
										getApplicationContext(), rawX, rawY,
										radiusNow));
						main_layout.addView(new CircleView(
								getApplicationContext(), rawX, rawY
										- main_layout.getHeight() / 2,
								radiusNow));
						flag[1] = false;
						k--;
						Toast.makeText(getApplicationContext(),
								"真棒！！还有" + k + "处不同", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"此处已找到，不能重复。。。。", Toast.LENGTH_SHORT).show();
					}

				}
				if (k == 0) {
					handler.removeMessages(TIME_MSG);
					Builder dialog = new AlertDialog.Builder(
							ZhaoChaActivity.this);
					dialog.setCancelable(false);
					dialog.setNegativeButton("否", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ZhaoChaActivity.this.finish();
							dialog.dismiss();
						}
					});
					dialog.setMessage("恭喜您通关了，是否进行下一关？").setTitle("提示")
							.setPositiveButton("是", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ZhaoChaActivity.this
											.setContentView(R.layout.activity_game_zhaocha);
									init();
									dialog.dismiss();
								}
							});
					dialog.show();
				}

			}
			return false;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TIME_MSG:
				countTime += 1000;
				Date date = new Date(countTime);
				SimpleDateFormat dateformat2 = new SimpleDateFormat("mm分ss秒 ");
				String time = dateformat2.format(date);
				timeTxt.setText(time);
				handler.sendEmptyMessageDelayed(TIME_MSG, 1000);
			default:
				break;
			}
		}

	};

	@Override
	public int getHelpStingId() {
		// TODO Auto-generated method stub
		return R.string.zhaocha_help;
	}

}
