/**
 * Copyright © 2011,2013 Konstantin Livitski
 * 
 * This file is part of n-Puzzle application. n-Puzzle application is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * n-Puzzle application contains adaptations of artwork covered by the Creative
 * Commons Attribution-ShareAlike 3.0 Unported license. Please refer to the
 * NOTICE.md file at the root of this distribution or repository for licensing
 * terms that apply to that artwork.
 * 
 * n-Puzzle application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * n-Puzzle application; if not, see the LICENSE/gpl.txt file of this distribution
 * or visit <http://www.gnu.org/licenses>.
 */
package com.liuhanyu.recipe.ui.pintu;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.liuhanyu.recipe.R;
import com.liuhanyu.recipe.ui.AcitivityWithHelpMenu;
import com.liuhanyu.recipe.ui.pintu.game.Board;
import com.liuhanyu.recipe.ui.pintu.game.Game;
import com.liuhanyu.recipe.ui.pintu.game.Game.Level;
import com.liuhanyu.recipe.ui.pintu.game.ImageProcessingException;
import com.liuhanyu.recipe.ui.pintu.game.Move;
import com.liuhanyu.recipe.ui.pintu.game.MoveListener;
import com.liuhanyu.recipe.ui.pintu.game.Tile;

/**
 * User's interface to the puzzle.
 */
public class PinTuActivity extends AcitivityWithHelpMenu implements OnClickListener,
		OnLongClickListener, MoveListener {
	private static final int TIME_MSG = 0x001;
	private Game game;
	private RelativeLayout windowLayout;
	private TableLayout numericBoardView, imageBoardView;
	private TextView[][] numericCells;
	private ImageView[][] imageCells;
	private boolean dialogActive=false;
	private int lastWidth = -1, lastHeight = -1;
	private CountDownTimer previewTimer, dimTimer;
	private TextView countDownCell;
	private Button restartBtn;
	private TextView timeTxt;
	private long countTime;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_pintu);
		initLayout();
		initGame();
		initBoard();
		findViews();
		setListener();
	}

	private void findViews() {
		// TODO Auto-generated method stub
		restartBtn = (Button) findViewById(R.id.activity_game_pintu_restart);
		timeTxt=(TextView)findViewById(R.id.activity_game_pintu_time);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		restartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				restart(Game.Level.MEDIUM);
				resumeImpl();
			}
		});
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
	/**
	 * Handles user's clicks and taps on the board.
	 */
	public void onClick(View v) {
		final Object tag = v.getTag();
		if (null == previewTimer && tag instanceof Tile) {
			final Board board = getBoardModel();
			final Move move = board.permittedMoveFor((Tile) tag);
			if (null != move) {
				board.move(move);
				if (game.isSolved())
					congratulate();
			} else {
				wrongClick();
			}
		}
	}

	/**
	 * Handles a long click on the blank tile.
	 */
	public boolean onLongClick(View v) {
		final Object tag = v.getTag();
		if (null == previewTimer && tag instanceof Tile
				&& 0 == ((Tile) tag).getNumber()) {
			toggleBoardType();
			return true;
		} else
			return false;
	}

	/**
	 * Updates the view when the model posts a tile move notification.
	 */
	public void tileMoved(final Tile from, final Tile to) {
		// repeat for both tiles
		for (Tile tile : new Tile[] { from, to }) {
			TextView numericCell = numericCells[tile.getRow()][tile.getColumn()];
			assignTile(numericCell, tile);
			ImageView imageCell = imageCells[tile.getRow()][tile.getColumn()];
			assignTile(imageCell, tile);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		resumeImpl();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveSettings();
	}

	/** Returns the current board size. */
	public int getBoardSize() {
		return getBoardModel().getSize();
	}

	/**
	 * Discards the current board state and starts a new game. Unless you are
	 * switching activities, you have to follow up this method with a call to
	 * {@link #resumeImpl()} to show the new board.
	 * 
	 * @param selectNewImage
	 *            tells the puzzle whether the user would like to select a new
	 *            image
	 */
	protected void restart(boolean selectNewImage) {
		cancelPreviewTimer();
		hideBoard();
		if (selectNewImage)
			newGame(null);
		else
			game.start();
		initBoard();
	}

	/**
	 * Starts a new game with a specific difficulty level. If there is an image
	 * selected for the current game, uses the same image for the new game.
	 * Unless you are switching activities, you have to follow up this method
	 * with a call to {@link #resumeImpl()} to show the new board.
	 * 
	 * @param difficulty
	 *            difficulty level for the new game
	 */
	protected void restart(Game.Level difficulty) {
		cancelPreviewTimer();
		hideBoard();
		Serializable imageId = null;
		if (null != game && game.isImageSelected())
			imageId = game.getSelectedImageId();
		newGame(difficulty);
		if (null != imageId)
			changeBoardImage(imageId);
		initBoard();
	}

	protected void cancelPreviewTimer() {
		if (null != previewTimer) {
			previewTimer.cancel();
			previewTimer = null;
		}
	}

	protected void hideBoard() {
		windowLayout.removeAllViews();
		windowLayout.invalidate();
	}

	protected void showNumericBoard() {
		// force resizeContent() if the board was hidden since onMeasure()
		// events might have been missed
		if (0 == windowLayout.getChildCount())
			lastWidth = lastHeight = -1;
		windowLayout.removeAllViews();
		windowLayout.addView(numericBoardView);
		windowLayout.invalidate();
	}

	protected void showImageBoard() {
		// force resizeContent() if the board was hidden since onMeasure()
		// events might have been missed
		if (0 == windowLayout.getChildCount())
			lastWidth = lastHeight = -1;
		windowLayout.removeAllViews();
		windowLayout.addView(imageBoardView);
		windowLayout.invalidate();
	}

	/**
	 * Toggles the "cheat mode" that shows the tiles' numbers.
	 */
	protected void toggleBoardType() {
		if (0 == windowLayout.getChildCount())
			return;
		else if (windowLayout.getChildAt(0) == numericBoardView)
			showImageBoard();
		else
			showNumericBoard();
	}

	protected Board getBoardModel() {
		return game.getBoard();
	}

	protected void initLayout() {
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_game_pintu_main_layout);
		windowLayout = new RelativeLayout(this) {
			@Override
			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				int width = MeasureSpec.getSize(widthMeasureSpec);
				int height = MeasureSpec.getSize(heightMeasureSpec);
				if (width != lastWidth || height != lastHeight) {
					// Log.d(
					// getClass().getName(),
					// "resizing to width = " +
					// Integer.toHexString(widthMeasureSpec) + " ("
					// + width + ") " + ", height = "
					// + Integer.toHexString(heightMeasureSpec) + " ("
					// + height + ")");
					if (0 < getChildCount())
						resizeContent(width, height);
					lastWidth = width;
					lastHeight = height;
				}
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}
		};
		frameLayout.addView(windowLayout);
	}

	protected void initGame() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		Map<String, ?> settings = preferences.getAll();
		try {
			if (settings.containsKey(Game.DIFFICULTY_SETTING))
				newGame(Game.Level.valueOf((String) settings
						.get(Game.DIFFICULTY_SETTING)));
			else
				newGame(null);
			if (settings.containsKey(Game.IMAGE_ID_SETTING))
				changeBoardImage((Serializable) settings
						.get(Game.IMAGE_ID_SETTING));
		} catch (RuntimeException invalid) {
			Log.w(getClass().getName(), "Error loading settings", invalid);
			newGame(null);
		}
		game.load(preferences);
	}

	protected void newGame(Level difficulty) {
		if (null != difficulty)
			game = new Game(difficulty);
		else if (null == game)
			game = new Game();
		else
			game = new Game(game.getDifficulty());
	}

	protected void initBoard() {
		numericBoardView = new TableLayout(this);
		imageBoardView = new TableLayout(this);
		final Board board = getBoardModel();
		board.addMoveListener(this);
		final int boardSize = getBoardSize();
		numericCells = new TextView[boardSize][boardSize];
		imageCells = new ImageView[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			TableRow numericRow = new TableRow(this);
			TableRow imageRow = new TableRow(this);
			for (int j = 0; j < boardSize; j++) {
				TextView numericCell = new TextView(this);
				ImageView imageCell = new ImageView(this);
				numericCells[i][j] = numericCell;
				imageCells[i][j] = imageCell;
				initNumericCell(numericCell);
				initImageCell(imageCell);
				numericRow.addView(numericCell);
				imageRow.addView(imageCell);
			}
			numericBoardView.addView(numericRow);
			imageBoardView.addView(imageRow);
		}
		countDownCell = new TextView(this);
		initNumericCell(countDownCell);
	}

	protected void initImage() {
		if (!game.isImageSelected()) {
			changeBoardImage(R.drawable.pintu);
		}
	}

	protected void resumeImpl() {
		if (dialogActive)
			hideBoard();
		else {
			initImage();
			if (game.isImageSelected()) {
				showImageBoard();
				if (!game.isStarted())
					showPreview();
				else{
					handler.sendEmptyMessage(TIME_MSG);
				}
			}
		}
	}

	protected void saveSettings() {
		Editor settings = getPreferences(MODE_PRIVATE).edit();
		game.save(settings);
		settings.commit();
	}

	protected void congratulate() {

	}

	protected void showPreview() {
		if (null != previewTimer)
			return;
		handler.removeMessages(TIME_MSG);
		game.preview();
		int lastIndex = getBoardSize() - 1;
		final View blankTileCell = imageCells[lastIndex][lastIndex];
		final ViewGroup parent = (ViewGroup) blankTileCell.getParent();
		parent.removeView(blankTileCell);
		countDownCell.setTextColor(getResources().getColor(R.color.black));
		parent.addView(countDownCell);
		previewTimer = new CountDownTimer(5000, 500) {
			@Override
			public void onTick(long millisUntilFinished) {
				int secondsRemaining = Math
						.round((float) millisUntilFinished / 1000);
				countDownCell.setText(Integer.toString(secondsRemaining));
			}

			@Override
			public void onFinish() {
				hideBoard();
				parent.removeView(countDownCell);
				parent.addView(blankTileCell);
				game.start();
				showImageBoard();
				previewTimer = null;
				handler.sendEmptyMessage(TIME_MSG);
			}
		};

		previewTimer.start();
	}

	protected void wrongClick() {
		if (null != dimTimer)
			return;
		dimTimer = new CountDownTimer(500L, 500L) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				dimImmovableTiles(false);
				dimTimer = null;
			}
		};
		dimImmovableTiles(true);
		dimTimer.start();
	}

	protected void dimImmovableTiles(boolean on) {
		final Board board = getBoardModel();
		final int boardSize = board.getSize();
		final int color = getResources().getColor(R.color.tile_dimmer);
		for (int r = 0; r < boardSize; r++)
			for (int c = 0; c < boardSize; c++) {
				final ImageView cell = imageCells[r][c];
				final Object tag = cell.getTag();
				if (tag instanceof Tile
						&& null == board.permittedMoveFor((Tile) tag)
						&& 0 != ((Tile) tag).getNumber()) {
					if (on)
						cell.setColorFilter(color);
					else
						cell.clearColorFilter();
				}
			}
	}

	protected void resizeContent(final int screenWidth, final int screenHeight) {
		if (null == game)
			throw new IllegalStateException(this
					+ " must be initialized with onCreate()");
		final float imageRatio = game.isImageSelected() ? game
				.getImageAspectRatio() : 1f;
		final int boardSize = getBoardSize();
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		// border width 1 dp rounded up to nearest whole pixels
		final int borderWidth = (int) Math.ceil(metrics.density);
		// calculate spacing allotment
		final int spacing = borderWidth * 2 * boardSize;
		if (screenWidth < spacing + boardSize
				|| screenHeight < spacing + boardSize)
			throw new UnsupportedOperationException("Screen size ("
					+ screenWidth + " x " + screenHeight
					+ ") too small for a board of " + boardSize + " rows");
		final float adjustedScreenRatio = (float) (screenWidth - spacing)
				/ (screenHeight - spacing);

		int width, height;
		if (adjustedScreenRatio > imageRatio) {
			// scale to screen height
			height = screenHeight;
			// fix width = imageRatio * height
			width = (int) (imageRatio * height);
			if (width < spacing + boardSize)
				throw new UnsupportedOperationException(
						"Need a wider image to make a board: scaled to "
								+ width + " pixels, need "
								+ (spacing + boardSize));
		} else {
			// scale to screen width
			width = screenWidth;
			// fix height = width / imageRatio
			height = (int) (width / imageRatio);
			if (height < spacing + boardSize)
				throw new UnsupportedOperationException(
						"Need a taller image to make a board: scaled to "
								+ height + " pixels, need "
								+ (spacing + boardSize));
		}

		// make the dimensions divisible by row/column count
		height -= height % boardSize;
		width -= width % boardSize;
		RelativeLayout.LayoutParams boardLayoutParams = new RelativeLayout.LayoutParams(
				width, height);
		boardLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		numericBoardView.setLayoutParams(boardLayoutParams);
		imageBoardView.setLayoutParams(boardLayoutParams);

		// load and resize the image
		width = width / boardSize - 2 * borderWidth;
		height = height / boardSize - 2 * borderWidth;
		game.setTileSize(width, height);
		if (game.isImageSelected())
			try {
				game.loadImage(this);
			} catch (ImageProcessingException failure) {
				game.resetSelectedImage();
				Log.e(getClass().getName(), failure.getMessage(), failure);
				hideBoard();
				return;
			}

		// size and fill cell views
		TableRow.LayoutParams cellParams = new TableRow.LayoutParams(width,
				height);
		cellParams.setMargins(borderWidth, borderWidth, borderWidth,
				borderWidth);
		float fontSize = width * 4f / 3;
		if (fontSize > height)
			fontSize = height;
		fontSize *= .5f;
		final Board board = getBoardModel();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				TextView numericCell = numericCells[i][j];
				numericCell.setLayoutParams(cellParams);
				numericCell.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
				ImageView imageCell = imageCells[i][j];
				imageCell.setLayoutParams(cellParams);
				Tile tile = board.getTileAt(i, j);
				assignTile(numericCell, tile);
				assignTile(imageCell, tile);
			}
		}
		countDownCell.setLayoutParams(cellParams);
		countDownCell.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
	}

	private void assignTile(final TextView cell, final Tile tile) {
		final int number = tile.getNumber();
		cell.setText(0 == number ? " " : Integer.toString(number));
		cell.setTag(tile);
		cell.setBackgroundColor(getResources().getColor(
				0 == number ? R.color.white : R.color.white));
	}

	private void assignTile(final ImageView cell, final Tile tile) {
		cell.setImageDrawable(tile.getDrawable());
		cell.setTag(tile);
	}

	private void initImageCell(ImageView imageCell) {
		imageCell.setOnClickListener(this);
		imageCell.setOnLongClickListener(this);
	}

	private void initNumericCell(TextView numericCell) {
		numericCell.setGravity(Gravity.CENTER);
		numericCell.setTextColor(getResources().getColor(R.color.white));
		numericCell.setOnClickListener(this);
		numericCell.setOnLongClickListener(this);
	}

	private void changeBoardImage(Serializable id) {
		game.setSelectedImage(id);
		try {
			game.updateImageSize(this);
		} catch (ImageProcessingException failure) {
			game.resetSelectedImage();
			Log.e(getClass().getName(), failure.getMessage(), failure);
		}
	}

	@Override
	public int getHelpStingId() {
		return R.string.pintu_help;
	}
}