package com.liuhanyu.recipe.ui.lianliankan;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;

import com.liuhanyu.recipe.R;
import com.liuhanyu.recipe.support.DisplayUtils;
import com.liuhanyu.recipe.ui.lianliankan.game.Config;
import com.liuhanyu.recipe.ui.lianliankan.game.GameView;
import com.liuhanyu.recipe.ui.lianliankan.reader.InnerGameReader;

public class LinkGameActivity extends Activity {

	private GameView gameView;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String configFile = "sg_config.json";
		Config.setScreenWidth(DisplayUtils.dip2px(getApplicationContext(), 300));
		Config.setScreenHeight(DisplayUtils
				.dip2px(getApplicationContext(), 300));

		setContentView(R.layout.activity_game_lianliankan);

		gameView = (GameView) findViewById(R.id.activity_game_lianliankan_gameview);
		gameView.setTimeTv((TextView) findViewById(R.id.activity_game_lianliankan_time));
		gameView.setNoteBtn((Button) findViewById(R.id.activity_game_lianliankan_remind));
		gameView.setRestartBtn((Button) findViewById(R.id.activity_game_lianliankan_restart));

		// 根据游戏资源包初始化游戏
		gameView.initWithGamePkg(InnerGameReader.readGame(this, configFile));

		// 开始启动游戏
		gameView.restartGame();
	}

}