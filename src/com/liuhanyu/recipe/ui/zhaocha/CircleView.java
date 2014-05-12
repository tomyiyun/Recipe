package com.liuhanyu.recipe.ui.zhaocha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class CircleView extends View {
	Paint paint;
	float x, y;
	int radius;

	public CircleView(Context context, float X, float Y, int radius) {
		super(context);
		init();
		this.x = X;
		this.y = Y;
		this.radius = radius;
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		paint = new Paint();
		// 设置画笔颜色
		paint.setColor(Color.RED);
		// 设置画笔粗细
		paint.setStrokeWidth(2);
		// 设置样式(实心、空心)
		paint.setStyle(Style.STROKE);
		// 抗锯齿（使图形比较圆润）
		paint.setAntiAlias(true);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(x, y, radius, paint);
	}
}
