package com.liuhanyu.recipe.ui.number;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * Created by liuhanyu on 14-5-2.
 */
public class GameView extends GridLayout  {
	private static final int TIME_MSG = 0x001;
	private long countTime;
	private int score = 0;
	private TextView scoreTv,timeTv;
	private Button restartBtn;
	
    public GameView(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);

        initGameView();
    }

    public GameView(Context context){
        super(context);

        initGameView();
    }

    public GameView(Context context, AttributeSet attrs){
        super(context, attrs);

        initGameView();
    }

    private void initGameView(){
        setColumnCount(4);
        setBackgroundColor(0xffbbada0);

        setOnTouchListener(new View.OnTouchListener() {

            private float startX,startY,offsetX,offsetY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = motionEvent.getX() - startX;
                        offsetY = motionEvent.getY() - startY;

                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                    swipeLeft();
                                }else if (offsetX > 5) {
                                    swipeRight();
                                }
                        }else{
                            if(offsetY<-5){
                                swipeUp();
                            }else if (offsetY > 5){
                                swipeDown();
                            }
                        }

                        break;
                }

                return true;
            }
        });

    }

    protected void onSizeChanged(int w,int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);

        int cardWidth = (Math.min(w,h)-10)/4;

        addCards(cardWidth,cardWidth);

        startGame();

    }
    
	public void setScoreTv(TextView scoreTv) {
		this.scoreTv = scoreTv;
	}
	
	public void setTimeTv(TextView timeTv) {
		this.timeTv = timeTv;
	}
	public void setRestartBtn(Button restartBtn) {
		if (this.restartBtn != null) {
			this.restartBtn.setOnClickListener(null);
		}

		this.restartBtn = restartBtn;

		this.restartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startGame();
			}
		});
	}
	
    public void clearScore(){
        score = 0;
        showScore();
    }

    public void showScore(){
    	scoreTv.setText("分数："+score);
    }

    public void addScore(int s){
        score += s;
        showScore();
    }
    private void addCards(int cardWidth,int cardHeight){

        Card c;

        for (int y= 0;y < 4;y++){
            for (int x = 0;x < 4; x++){
                c = new Card(getContext());
                c.setNum(0);
                addView(c,cardWidth,cardHeight);

                cardsMap[x][y] = c;
            }
        }

    }

    private void startGame(){
    	handler.removeMessages(TIME_MSG);
    	countTime=0;
        clearScore();

        for (int y= 0;y<4;y++){
            for (int x= 0;x<4;x++){
                cardsMap[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();
        handler.sendEmptyMessage(TIME_MSG);
    }

    private void addRandomNum(){

        emptyPoints.clear();

        for (int y = 0;y < 4;y++){
            for (int x = 0; x < 4;x++){
                if (cardsMap[x][y].getNum()<=0){
                    emptyPoints.add(new Point(x,y));
                }
            }
        }

        Point p =emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
        cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);


    }

    private void swipeLeft(){

        boolean merge = false;

        for (int y =0 ;y < 4;y++){
            for (int x= 0;x<4;x++){

                for(int x1 = x + 1; x1 < 4;x1++){
                    if (cardsMap[x1][y].getNum()>0){

                        if (cardsMap[x][y].getNum()<=0){
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x--;

                            merge = true;

                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);

                            addScore(cardsMap[x][y].getNum());
                            merge = true;

                        }
                        break;
                    }
                }
            }
        }
        if (merge){
            addRandomNum();
            checkComplete();
        }

    }

    private void swipeRight(){

        boolean merge = false;

        for (int y =0 ;y < 4;y++){
            for (int x= 3;x >= 0;x--){

                for(int x1 = x - 1; x1 >= 0;x1--){
                    if (cardsMap[x1][y].getNum()>0){

                        if (cardsMap[x][y].getNum()<=0){
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x1][y])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);


                            addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }

        }
        if (merge){
            addRandomNum();
            checkComplete();
        }

    }

    private void swipeUp(){

        boolean merge = false;

        for (int x =0 ;x < 4;x++){
            for (int y= 0;y<4;y++){

                for(int y1 = y + 1; y1 < 4;y1++){
                    if (cardsMap[x][y1].getNum()>0){

                        if (cardsMap[x][y].getNum()<=0){
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y--;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);

                            addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge){
            addRandomNum();
            checkComplete();
        }
    }
    private void swipeDown(){

        boolean merge = false;

        for (int x =0 ;x < 4;x++){
            for (int y= 3;y>=0;y--){

                for(int y1 = y - 1; y1 >= 0;y1--){
                    if (cardsMap[x][y1].getNum()>0){

                        if (cardsMap[x][y].getNum()<=0){
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y++;
                            merge = true;
                        }else if (cardsMap[x][y].equals(cardsMap[x][y1])){
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);

                            addScore(cardsMap[x][y].getNum());
                            merge= true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge){
            addRandomNum();
            checkComplete();
        }

    }

    private void checkComplete(){

        boolean complete = true;

        ALL:
        for (int y= 0;y < 4; y++){
            for (int x= 0; x < 4; x++){
                if (cardsMap[x][y].getNum() == 0 ||
                        (x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
                        (x<3&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
                        (y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<3&&cardsMap[x][y].equals(cardsMap[x][y+1]))){
                    complete = false;
                    break ALL;
                }
            }
        }

        if (complete){
            new AlertDialog.Builder(getContext()).setTitle("Game Over").setMessage("Press button below to start again.").setPositiveButton("Play Again",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGame();

                }
            }).show();
        }

    }

    private Card[][] cardsMap = new Card[4][4];
    private List<Point> emptyPoints = new ArrayList<Point>();
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
				timeTv.setText(time);
				handler.sendEmptyMessageDelayed(TIME_MSG, 1000);
			default:
				break;
			}
		}

	};

}
