package com.liuhanyu.recipe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.liuhanyu.recipe.R;
import com.liuhanyu.recipe.ui.lianliankan.LinkGameActivity;
import com.liuhanyu.recipe.ui.number.NumberActivity;
import com.liuhanyu.recipe.ui.pintu.PinTuActivity;
import com.liuhanyu.recipe.ui.zhaocha.ZhaoChaActivity;

public class MainActivity extends Activity {
	Button zhaocha,lianliankan,pintu,number;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		zhaocha=(Button)findViewById(R.id.main_game_zhaocha);
		lianliankan=(Button)findViewById(R.id.main_game_lianliankan);
		pintu=(Button)findViewById(R.id.main_game_pintu);
		number=(Button)findViewById(R.id.main_game_2048);
		setListener();
	}
	private void setListener(){
		zhaocha.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, ZhaoChaActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		lianliankan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, LinkGameActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		pintu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, PinTuActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, NumberActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}

}
