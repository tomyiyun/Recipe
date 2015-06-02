package com.liuhanyu.recipe.ui.number;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import com.liuhanyu.recipe.R;
import com.liuhanyu.recipe.ui.AcitivityWithHelpMenu;

public class NumberActivity extends AcitivityWithHelpMenu {
	private TextView timeTv,scoreTv;
	private GameView gameview;
	private Button restartBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_number);
		scoreTv = (TextView) findViewById(R.id.activity_game_number_score);
		timeTv = (TextView) findViewById(R.id.activity_game_number_time);
		gameview = (GameView) findViewById(R.id.activity_game_number_gameview);
		restartBtn=(Button) findViewById(R.id.activity_game_number_restart);
		gameview.setScoreTv(scoreTv);
		gameview.setTimeTv(timeTv);
		gameview.setRestartBtn(restartBtn);
		
	}

	@Override
	public int getHelpStingId() {
		// TODO Auto-generated method stub
		return R.string.number_help;
	}

}
