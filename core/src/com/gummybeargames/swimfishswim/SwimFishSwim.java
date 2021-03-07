package com.gummybeargames.swimfishswim;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class SwimFishSwim extends ApplicationAdapter  {
	SpriteBatch batch;
	private Stage stage;
	BitmapFont font;
	BitmapFont scoreFont;
	Texture background, fish, topShark, bottomShark, topShark2,gameOver, pause;
	TextureRegion tr;
	TextureRegionDrawable tdr;
	ImageButton paused;
	float max=300;
	float min =-150;
	float fishY=0;
	float vel=0;
	boolean justDied=true;
	int pauseCount=0;
	int gameState=0;//0 is start, 1 is play, 2 is gameover, 3 is pause
	float offset1, offset2, offset3;
	Random rand;
	int numberOfSharks=6;
	float distanceBetweenSharks;
	float sharkVel=0;
	float sharkVelTemp=sharkVel;
	float [] sharkX= new float[numberOfSharks];
	float[][] offsets = new float[numberOfSharks][3];
	int topCount=0;
	int bottomCount=0;
	Rectangle fishRect;
	Rectangle[][] sharkRects;
	int highscore;
	int score=0;
	int scoringShark=0;
	Preferences prefs;
	int sharkCounter=0;
	AdHandler handler;
	boolean toggle;
	float scale;
	//ShapeRenderer shapeRenderer;

	public SwimFishSwim(AdHandler handler){
		this.handler=handler;
	}
	@Override
	public void create () {
		//toggle= !toggle;
		handler.showAds(toggle);

		//shapeRenderer=new ShapeRenderer();
		prefs = Gdx.app.getPreferences("com.gummybeargames.swimfishswim");
		if(!prefs.contains("highScore")) prefs.putInteger("highScore", 0);
		highscore= prefs.getInteger("highScore");//get highscore from storage
		font= new BitmapFont();
		font.setColor(Color.GREEN);
		if (Gdx.graphics.getWidth()<1500) {
			font.getData().setScale(8);
			scoreFont = new BitmapFont();
			scoreFont.setColor(Color.WHITE);
			scoreFont.getData().setScale(3);
		}else {
			font.getData().setScale(10);
			scoreFont = new BitmapFont();
			scoreFont.setColor(Color.WHITE);
			scoreFont.getData().setScale(5);
		}
		batch = new SpriteBatch();
		background = new Texture ("background.png");
		pause = new Texture("pause.png");
		tr= new TextureRegion(pause);
		tdr= new TextureRegionDrawable(tr);
		paused= new ImageButton(tdr);
		fish= new Texture("fish.png");
		gameOver= new Texture(("gameover2.png"));
		topShark= new Texture("shark.png");
		bottomShark = new Texture("shark.png");
		topShark2= new Texture("shark.png");
		rand = new Random();
		if (Gdx.graphics.getWidth()<1500) {
			scale = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
		}else{
			scale=1;
		}
			max=max*scale;
			min=min*scale;
		distanceBetweenSharks= Gdx.graphics.getWidth()/2;
		sharkRects = new Rectangle[numberOfSharks][3];
		stage = new Stage(new ScreenViewport()); //Set up a stage for the ui
		stage.addActor(paused); //Add the button to the stage to perform rendering and take input.
		Gdx.input.setInputProcessor(stage); //Start taking input from the ui
		startGame();
	}

	public void startGame(){
		score=0;
		scoringShark=0;
		vel=0;
		fishY=Gdx.graphics.getHeight()/2;
		distanceBetweenSharks= Gdx.graphics.getWidth()/2;
		for (int x = 0; x < numberOfSharks; x++) {

			sharkX[x] = Gdx.graphics.getWidth() *3/4 - (topShark.getWidth()*scale) +x *distanceBetweenSharks;
			sharkRects[x][0] = new Rectangle();
			sharkRects[x][1] = new Rectangle();
			sharkRects[x][2] = new Rectangle();

			getOffset(x);
		}
	}
	@Override
	public void render () {

		paused.addListener(new EventListener()
		{
			@Override
			public boolean handle(Event event)
			{
				if(gameState!=2) {
					if (pauseCount == 0) {
						sharkVelTemp = sharkVel;
						sharkVel = 0;
						gameState = 3;
					}
					pauseCount++;
				}
				return true;
			}
		});
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(gameState==1) {

			sharkVelTemp=sharkVel;
				paused.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 200);
				paused.setScale(200, 200);
				if (sharkX[scoringShark] < Gdx.graphics.getWidth() / 4 - 200 + 10) {
					score++;
					if (scoringShark < numberOfSharks - 1) {
						scoringShark++;
					} else {
						scoringShark = 0;
					}
					if (score % 10 == 0) {
						if (score<=100) {
							sharkVel++;
						}
					}
					if (score % 20 == 0 && distanceBetweenSharks > 300*scale) {
						distanceBetweenSharks = distanceBetweenSharks - 50;
					}
				}


				if (Gdx.input.isTouched() && (fishY < Gdx.graphics.getHeight() - 125*scale)) {// moves fish up while holding
					vel = vel - 1.5f;
				}

				if ((fishY > -20*scale && fishY < Gdx.graphics.getHeight() - 125*scale)) {//fish in bounds
					vel = vel + 0.5f;//increases fall speed
					fishY -= vel;//makes fish fall
					topCount = 0;
					bottomCount = 0;
				} else if ((fishY >= Gdx.graphics.getHeight() - 125*scale)) {
					if (topCount == 0) {
						vel = 0;
					}
					vel = vel + 0.5f;//increases fall speed
					fishY -= vel;//makes fish fall
					topCount++;

				} else if ((fishY <= -20*scale)) {
					if (bottomCount == 0) {
						vel = 0;
					}
					vel = vel - 1.5f;
					fishY -= vel;//makes fish fall

					bottomCount++;
				}


		} else if(gameState==0){

			if(Gdx.input.justTouched()){

				gameState=1;
				if(scale!=1){
					sharkVel = 6;
				}else {
					sharkVel = 8;
				}

			}
		}else if(gameState==2){
			if(score>highscore) {
				highscore=score;
				prefs.putInteger("highScore", highscore);
				prefs.flush();
			}
			if(justDied) {
				toggle = !toggle;

				handler.showAds(toggle);
			}
			justDied=false;
			sharkVel=0;
			if(Gdx.input.justTouched()){
				startGame();
				gameState=1;
				sharkVel=8;
				justDied=true;
				toggle= !toggle;

				handler.showAds(toggle);
			}
		}else if(gameState==3){
			if(Gdx.input.justTouched()){
				sharkVel=sharkVelTemp;
				gameState=1;
				pauseCount=0;
			}
		}
		for (int x = 0; x < numberOfSharks; x++) {

			if (sharkX[x]< -300){
				sharkX[x]+=numberOfSharks*distanceBetweenSharks;
				getOffset(x);
			}else {
				sharkX[x] = sharkX[x] - sharkVel;


			}


				batch.draw(topShark, sharkX[x], offsets[x][0], 300*scale, 300*scale);
				batch.draw(bottomShark, sharkX[x], offsets[x][1], 300*scale, 300*scale);
				batch.draw(topShark2, sharkX[x], offsets[x][2], 300*scale, 300*scale);
				sharkRects[x][0] = new Rectangle(sharkX[x] + 13, offsets[x][0] + 178*scale, 263*scale, 80*scale);
				sharkRects[x][1] = new Rectangle(sharkX[x] + 13, offsets[x][1] + 178*scale, 263*scale, 80*scale);
				sharkRects[x][2] = new Rectangle(sharkX[x] + 13, offsets[x][2] + 178*scale, 263*scale, 80*scale);




		}

			batch.draw(pause, Gdx.graphics.getWidth() - 200*scale, Gdx.graphics.getHeight() - 200*scale, 200*scale, 200*scale);
			fishRect = new Rectangle(Gdx.graphics.getWidth() / 4 - 200*scale + 15*scale, fishY + 57*scale, 125*scale, 50*scale);
			batch.draw(fish, Gdx.graphics.getWidth() / 4 - 200*scale, fishY, 150*scale, 150*scale);
			font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 50);

		if ((gameState==2)){

				batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - 800*scale, Gdx.graphics.getHeight() / 2 - 400*scale,
						1400*scale, 800*scale);
				scoreFont.draw(batch, String.valueOf((highscore)), (Gdx.graphics.getWidth() / 2), (Gdx.graphics.getHeight() / 2 - 300*scale));

		}

		batch.end();

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.BLACK);
		//shapeRenderer.rect(Gdx.graphics.getWidth() / 4 - 200*scale+15*scale, fishY+57*scale, 125*scale, 50*scale);
		for (int x = 0; x < numberOfSharks; x++) {
		  //shapeRenderer.rect(sharkX[x]+13, offsets[x][0]+178*scale, 263*scale, 80*scale);
			//shapeRenderer.rect(sharkX[x]+13, offsets[x][1]+178*scale, 263*scale, 80*scale);
			//shapeRenderer.rect(sharkX[x]+13, offsets[x][2]+178*scale, 263*scale, 80*scale);

			if(Intersector.overlaps(fishRect,sharkRects[x][0])||Intersector.overlaps(fishRect,sharkRects[x][1])
			||Intersector.overlaps(fishRect,sharkRects[x][2])){
				gameState=2;
			}

		}
	//	shapeRenderer.end();

	}


	public void getOffset(int x){
		sharkCounter++;
		if(sharkCounter%3==0){
			offset1 = Gdx.graphics.getHeight()-max;//* (max - min + 1) + min
			offset2 = min;//* (max - min + 1) + min
		}else{
			offset1 = (rand.nextFloat() * ((Gdx.graphics.getHeight() - max) - min + 1f) + min);//* (max - min + 1) + min
			offset2 = (rand.nextFloat() * ((Gdx.graphics.getHeight() - max) - min + 1f) + min);//* (max - min + 1) + min
		}
		offset3 = (rand.nextFloat() * ((Gdx.graphics.getHeight() - max) - -min + 1f) + min);//* (max - min + 1) + min

		while (offset2 > offset1 - topShark.getHeight() && offset2 < offset1 + topShark.getHeight()) {
			offset2 = (rand.nextFloat() * ((Gdx.graphics.getHeight() - max) - min + 1f) + min);//* (max - min + 1) + min
		}

		while ((offset3 > offset1 - topShark.getHeight() && offset3 < offset1 + topShark.getHeight())
				|| (offset3 > offset2 - topShark.getHeight() && offset3 < offset2 + topShark.getHeight())) {

			offset3 = (rand.nextFloat() * ((Gdx.graphics.getHeight() - max) - min + 1f) + min);//* (max - min + 1) + min

		}
		offsets[x][0]= offset1;
		offsets[x][1]= offset2;
		offsets[x][2]= offset3;
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

	public int setScale(int scaling, int scaler){
		scale=(Gdx.graphics.getWidth()/1920)+100;
		return (int)(scaler/scale);
	}

}
