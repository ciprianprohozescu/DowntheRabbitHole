package com.cyworks.downtherabbithole;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter implements GestureListener {

    //region variables
	SpriteBatch menuBatch, worldBatch, hudBatch;
	public int game_state; //1 main menu; 2 new level; 3 in game; 4 powerups menu
	Texture map_pic, arrowUpPic, arrowDownPic, arrowLeftPic, arrowRightPic, noArrow_pic, carrot_pic, bomb_pic, armedBomb_pic, playButtonPic, powerupsButtonPic, homeButtonPic;
	Texture arrowDownSelectedPic, arrowUpSelectedPic, arrowLeftSelectedPic, arrowRightSelectedPic, rabbitHole_pic, cameraLockedPic, cameraUnlockedPic, rabbitLeftPic, rabbitRightPic, rabbitUpPic, rabbitDownPic;
    Texture shieldPortraitPic, shieldPic, upgradeButonPic, chargeButtonPic, powerupPic, nextButtonPic, previousButtonPic, magnetPic, magnetPortraitPic, flarePortraitPic, backgroundPic;
	Sprite map, noArrow, arrowDownSelected, arrowUpSelected, arrowLeftSelected, arrowRightSelected, rabbitHole, cameraLocked, cameraUnlocked, rabbitLeft, rabbitRight, rabbitUp, rabbitDown;
    Sprite playButton, powerupsButton, homeButton, shieldPortrait, shield, upgradeButton, chargeButton, nextButton, previousButton, magnet, magnetPortrait, flarePortrait, background;
	Sprite[] arrowUp, arrowDown, arrowLeft, arrowRight, carrot, bomb, armedBomb, powerup;
	public static final String TAG = "myMessage";
	OrthographicCamera camera, menuCamera, hudCamera;
	float mapRight, mapLeft, mapTop, mapBottom, cameraHalfWidth, cameraHalfHeight, cameraLeft, cameraRight, cameraBottom, cameraTop, screenWidth, screenHeight;
	float levelTextSize, gameMusicVolume, rabbitAnimationTime;
	int l, c, arrowSelected, upUsed, downUsed, leftUsed, rightUsed, rabbitL, rabbitC, rabbitDirection, nrCarrotsCollected, level, nrMaxStars;
	Vector3 pozScreen, pozWorld, arrowPos, rabbitPos;
	int[][] M; //1 - 4 directions; 5 carrot; 6 invisible bomb; 7 visible bomb; 8 armed bomb; 9 hole; 10 trap; 11 powerup
	boolean rabbitMove, drawExplosion, isCameraLocked, isTrapActive, isInvisibleBombsTrapActive;
	long rabbitStartTime, rabbitSpeedDelay, levelTextStartTime, deathTime;
	FreeTypeFontGenerator fontGenerator;
	FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
	BitmapFont scoreFont, levelFont, highscoreFont, titleFont, powerupsFont, powerupsSmallFont;
	ParticleEffect[] stars;
    ParticleEffect fireworks, leaves, explosion, trap, powerupEffect, shieldEffect, flare;
	Preferences userData;
    String highscore;
	Music menuMusic;
	Sound buttonSound, carrotCollectSound, bombAppearSound, bombArmSound, arrowSelectSound, arrowPlaceSound, arrowCancelSound, arrowRemoveSound, trapSound, powerupSound, shieldSound;
    Music[] gameMusic;
    Sound[] bombExplosionSound;
    int nrGameMusic, nrbombExplosionSound, nrMaxGameMusic, nrMaxBombExplosionSound, nrTrapTypes, nrMaxPowerups, carrotBank, currentPowerup, nrAvailablePowerups;
    TextureAtlas rabbitRightPics, rabbitLeftPics, rabbitUpPics, rabbitDownPics;
    Animation rabbitRightAnimation, rabbitLeftAnimation, rabbitUpAnimation, rabbitDownAnimation;
    int[] powerupsLevels, powerupsCharges, availablePowerups, powerupsUpgradePrices, powerupsChargePrices, activePowerups;
    Viewport viewport, menuViewport, hudViewport;
    //endregion

    //traps: 1 - carrots turn into bombs; 2 - bombs become invisible; 3 - bombs become armed

    private AdsController adsController;

    public MyGdxGame(AdsController adsController){
        this.adsController = adsController;
    }

	@Override
	public void create () {
		int i, j;
		float l;

		Gdx.input.setInputProcessor(new GestureDetector(this));

		camera = new OrthographicCamera();
        menuCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        menuViewport = new FillViewport(1920, 1080, menuCamera);
        hudViewport = new FillViewport(1920, 1080, hudCamera);
        viewport.apply();
        menuViewport.apply();
        hudViewport.apply();
        menuCamera.position.set(screenWidth/2,screenHeight/2,0);
        hudCamera.position.set(screenWidth/2,screenHeight/2,0);

        //region textures and sprites
		menuBatch = new SpriteBatch();
		worldBatch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		map_pic = new Texture("map1.png");
		arrowUpPic = new Texture("ArrowUp.png");
		arrowDownPic = new Texture("ArrowDown.png");
		arrowLeftPic = new Texture("ArrowLeft.png");
		arrowRightPic = new Texture("ArrowRight.png");
		arrowUpSelectedPic = new Texture("ArrowUpSelected.png");
		arrowDownSelectedPic = new Texture("ArrowDownSelected.png");
		arrowLeftSelectedPic = new Texture("ArrowLeftSelected.png");
		arrowRightSelectedPic = new Texture("ArrowRightSelected.png");
		noArrow_pic = new Texture("NoArrow.png");
		rabbitLeftPic = new Texture("rabbitLeftStill.png");
        rabbitRightPic = new Texture("rabbitRightStill.png");
        rabbitUpPic = new Texture("rabbitUpStill.png");
        rabbitDownPic = new Texture("rabbitDownStill.png");
		carrot_pic = new Texture("carrot.png");
		bomb_pic = new Texture("bomb.png");
		armedBomb_pic = new Texture("armedBomb.png");
		rabbitHole_pic = new Texture("rabbitHole.png");
		cameraLockedPic = new Texture("cameraLocked.png");
		cameraUnlockedPic = new Texture("cameraUnlocked.png");
        playButtonPic = new Texture("playButton.png");
        powerupsButtonPic = new Texture("powerupsButton.png");
        homeButtonPic = new Texture("homeButton.png");
        shieldPortraitPic = new Texture("shieldPortrait.png");
        shieldPic = new Texture("shield.png");
        upgradeButonPic = new Texture("upgradeButton.png");
        chargeButtonPic = new Texture("chargeButton.png");
        powerupPic = new Texture("powerup.png");
        previousButtonPic = new Texture("previousButton.png");
        nextButtonPic = new Texture("nextButton.png");
        magnetPortraitPic = new Texture("magnetPortrait.png");
        magnetPic = new Texture("magnet.png");
        flarePortraitPic = new Texture("flarePortrait.png");
        backgroundPic = new Texture("background.jpg");
		arrowLeftSelected = new Sprite(arrowLeftSelectedPic);
		arrowRightSelected = new Sprite(arrowRightSelectedPic);
		arrowDownSelected = new Sprite(arrowDownSelectedPic);
		arrowUpSelected = new Sprite(arrowUpSelectedPic);
        upgradeButton = new Sprite(upgradeButonPic);
        chargeButton = new Sprite(chargeButtonPic);
		noArrow = new Sprite(noArrow_pic);
		map = new Sprite(map_pic);
		rabbitLeft = new Sprite(rabbitLeftPic);
        rabbitRight = new Sprite(rabbitRightPic);
        rabbitUp = new Sprite(rabbitUpPic);
        rabbitDown = new Sprite(rabbitDownPic);
		rabbitHole = new Sprite(rabbitHole_pic);
		arrowUp = new Sprite[410];
		for (i = 1; i <= 400; i++)
			arrowUp[i] = new Sprite(arrowUpPic);
		arrowDown = new Sprite[410];
		for (i = 1; i <= 400; i++)
			arrowDown[i] = new Sprite(arrowDownPic);
		arrowLeft = new Sprite[410];
		for (i = 1; i <= 400; i++)
			arrowLeft[i] = new Sprite(arrowLeftPic);
		arrowRight = new Sprite[410];
		for (i = 1; i <= 400; i++)
			arrowRight[i] = new Sprite(arrowRightPic);
		carrot = new Sprite[410];
		for (i = 1; i <= 400; i++)
			carrot[i] = new Sprite(carrot_pic);
		bomb = new Sprite[410];
		armedBomb = new Sprite[410];
		for (i = 1; i <= 400; i++)
			bomb[i] = new Sprite(bomb_pic);
		for (i = 1; i <= 400; i++)
			armedBomb[i] = new Sprite(armedBomb_pic);
		cameraLocked = new Sprite(cameraLockedPic);
		cameraUnlocked = new Sprite(cameraUnlockedPic);
        playButton = new Sprite(playButtonPic);
        powerupsButton = new Sprite(powerupsButtonPic);
        homeButton = new Sprite(homeButtonPic);
        shieldPortrait = new Sprite(shieldPortraitPic);
        shield = new Sprite(shieldPic);
        powerup = new Sprite[410];
        for (i = 1; i <= 400; i++)
            powerup[i] = new Sprite(powerupPic);
        previousButton = new Sprite(previousButtonPic);
        nextButton = new Sprite(nextButtonPic);
        magnetPortrait = new Sprite(magnetPortraitPic);
        magnet = new Sprite(magnetPic);
        flarePortrait = new Sprite(flarePortraitPic);
        background = new Sprite(backgroundPic);

        rabbitRightPics = new TextureAtlas(Gdx.files.internal("rabbitRight.pack"));
        rabbitRightAnimation = new Animation(1/60f, rabbitRightPics.getRegions());
        rabbitLeftPics = new TextureAtlas(Gdx.files.internal("rabbitLeft.pack"));
        rabbitLeftAnimation = new Animation(1/60f, rabbitLeftPics.getRegions());
        rabbitUpPics = new TextureAtlas(Gdx.files.internal("rabbitUp.pack"));
        rabbitUpAnimation = new Animation(1/60f, rabbitUpPics.getRegions());
        rabbitDownPics = new TextureAtlas(Gdx.files.internal("rabbitDown.pack"));
        rabbitDownAnimation = new Animation(1/60f, rabbitDownPics.getRegions());
        //endregion

        //region fonts
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Gretoon.ttf"));
		fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = 100;
		fontParameter.color = Color.BLACK;
		fontParameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?: ";
		scoreFont = fontGenerator.generateFont(fontParameter);
		levelFont = fontGenerator.generateFont(fontParameter);
        highscoreFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = 110;
        titleFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = 60;
        powerupsFont = fontGenerator.generateFont(fontParameter);
        fontParameter.size = 40;
        powerupsSmallFont = fontGenerator.generateFont(fontParameter);
        //endregion

        //region sounds
		buttonSound = Gdx.audio.newSound(Gdx.files.internal("button.wav"));
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menuMusic.mp3"));
		menuMusic.setVolume(0.7f);
		menuMusic.play();

        carrotCollectSound = Gdx.audio.newSound(Gdx.files.internal("carrotCollect.wav"));
        bombAppearSound = Gdx.audio.newSound(Gdx.files.internal("bombAppear.wav"));
        bombArmSound = Gdx.audio.newSound(Gdx.files.internal("bombArm.mp3"));
        arrowSelectSound = Gdx.audio.newSound(Gdx.files.internal("arrowSelect.mp3"));
        arrowCancelSound = Gdx.audio.newSound(Gdx.files.internal("arrowCancel.mp3"));
        arrowPlaceSound = Gdx.audio.newSound(Gdx.files.internal("arrowPlace.mp3"));
        arrowRemoveSound = Gdx.audio.newSound(Gdx.files.internal("arrowRemove.mp3"));
        trapSound = Gdx.audio.newSound(Gdx.files.internal("trap.ogg"));
        powerupSound = Gdx.audio.newSound(Gdx.files.internal("powerupSound.wav"));
        shieldSound = Gdx.audio.newSound(Gdx.files.internal("shieldSound.mp3"));

        nrMaxGameMusic = 10;
        gameMusic = new Music[nrMaxGameMusic + 1];
        for (i = 1; i <= nrMaxGameMusic; i++)
            gameMusic[i] = Gdx.audio.newMusic(Gdx.files.internal("gameMusic" + i + ".mp3"));

        nrMaxBombExplosionSound = 2;
        bombExplosionSound = new Sound[nrMaxBombExplosionSound + 1];
        for (i = 1; i <= nrMaxBombExplosionSound; i++)
            bombExplosionSound[i] = Gdx.audio.newSound(Gdx.files.internal("bombExplosion" + i + ".wav"));
        //endregion

        //region user data
        userData = Gdx.app.getPreferences("User Data");
        highscore = "Highscore: " + userData.getInteger("highscore", 0);
        nrMaxPowerups = 3;
        powerupsLevels = new int[nrMaxPowerups + 1];
        powerupsCharges = new int[nrMaxPowerups + 1];
        powerupsUpgradePrices = new int[nrMaxPowerups + 1];
        powerupsChargePrices = new int[nrMaxPowerups + 1];
        activePowerups = new int[nrMaxPowerups + 1];
        availablePowerups = new int[nrMaxPowerups + 1];
        for (i = 1; i <= nrMaxPowerups; i++) {
            powerupsLevels[i] = userData.getInteger("powerupLevel" + i, 0);
            powerupsCharges[i] = userData.getInteger("powerupCharges" + i, 0);
            powerupsChargePrices[i] = 50;
            powerupsUpgradePrices[i] = 300;
        }
        powerupsChargePrices[2] = 100;
        powerupsUpgradePrices[2] = 500;
        carrotBank = userData.getInteger("carrotBank", 0);
        //endregion

        screenWidth = 1920;
        screenHeight = 1080;

        //region pe
        nrMaxStars = 30;
        stars = new ParticleEffect[nrMaxStars + 1];
        for (i = 1; i <= nrMaxStars; i++) {
            stars[i] = new ParticleEffect();
            stars[i].load(Gdx.files.internal("stars.party"),Gdx.files.internal(""));
            stars[i].start();
        }
        fireworks = new ParticleEffect();
        fireworks.load(Gdx.files.internal("fireworks.party"), Gdx.files.internal(""));
        fireworks.setPosition(screenWidth / 2, screenHeight - 200);
        fireworks.start();
		leaves = new ParticleEffect();
		leaves.load(Gdx.files.internal("leaves.party"), Gdx.files.internal(""));
		leaves.setPosition(500, screenHeight - 500);
		leaves.start();
		explosion = new ParticleEffect();
        explosion.load(Gdx.files.internal("explosion.party"), Gdx.files.internal(""));
        explosion.setPosition(100, 100);
        explosion.start();
        trap = new ParticleEffect();
        trap.load(Gdx.files.internal("trap_pe.party"), Gdx.files.internal(""));
        trap.setPosition(100, 100);
        trap.start();
        nrTrapTypes = 3;
        powerupEffect = new ParticleEffect();
        powerupEffect.load(Gdx.files.internal("powerupEffect.party"), Gdx.files.internal(""));
        powerupEffect.start();
        shieldEffect = new ParticleEffect();
        shieldEffect.load(Gdx.files.internal("shieldEffect.party"), Gdx.files.internal(""));
        shieldEffect.start();
        flare = new ParticleEffect();
        flare.load(Gdx.files.internal("flare.party"), Gdx.files.internal(""));
        flare.start();
        //endregion

        //region geometry
		pozScreen = new Vector3();
		pozWorld = new Vector3();
		arrowPos = new Vector3();
		rabbitPos = new Vector3();

		map.setPosition(-map.getWidth() / 2, -map.getHeight() / 2);

		l = (screenWidth - 1000) / 4;
		arrowPos.y = 0;
		arrowPos.x = 0;
		noArrow.setPosition(arrowPos.x, arrowPos.y);
		arrowPos.x = 200 + l;
		arrowUp[1].setPosition(arrowPos.x, arrowPos.y);
		arrowUpSelected.setPosition(arrowPos.x, arrowPos.y);
		arrowPos.x *= 2;
		arrowDown[1].setPosition(arrowPos.x, arrowPos.y);
		arrowDownSelected.setPosition(arrowPos.x, arrowPos.y);
		arrowPos.x += 200 + l;
		arrowLeft[1].setPosition(arrowPos.x, arrowPos.y);
		arrowLeftSelected.setPosition(arrowPos.x, arrowPos.y);
		arrowPos.x += 200 + l;
		arrowRight[1].setPosition(arrowPos.x, arrowPos.y);
		arrowRightSelected.setPosition(arrowPos.x, arrowPos.y);

		cameraLocked.setPosition(screenWidth - 200, screenHeight / 2 - 100);
		cameraUnlocked.setPosition(screenWidth - 200, screenHeight / 2 - 100);

		game_state = 1;
		M = new int[25][25];
		for (i = 1; i <= 20; i++)
			M[i][1] = M[i][20] = -1;
		for (j = 1; j <= 20; j++)
			M[1][j] = M[20][j] = -1;
		rabbitSpeedDelay = 450;

		mapLeft = -map.getWidth() / 2;
		mapRight = map.getWidth() / 2;
		mapBottom = -map.getHeight() / 2;
		mapTop = map.getHeight() / 2;

        playButton.setPosition(screenWidth / 2 - 410, screenHeight / 2 - 200);
        powerupsButton.setPosition(screenWidth / 2 + 10, screenHeight / 2 - 200);
        homeButton.setPosition(10, 10);
        shieldPortrait.setPosition(homeButton.getWidth(), homeButton.getHeight() + 220);
        magnetPortrait.setPosition(homeButton.getWidth(), homeButton.getHeight() + 220);
        flarePortrait.setPosition(homeButton.getWidth(), homeButton.getHeight() + 220);
        upgradeButton.setPosition(screenWidth / 2 - 410, 10);
        chargeButton.setPosition(screenWidth / 2 + 10, 10);
        nextButton.setPosition(screenWidth - nextButton.getWidth() - 10, screenHeight - 200 - nextButton.getHeight());
        previousButton.setPosition(10, screenHeight - 200 - previousButton.getHeight());

        background.setPosition(0, 0);
        //endregion

        adsController.showBannerAd();

	}

	@Override
	public void render () {
		int i, j, nrCarrotsUsed = 0, nrBombsUsed = 0, nrArmedBombsUsed = 0, nrPowerupsUsed = 0;

        camera.update();
        hudCamera.update();
        menuCamera.update();

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (i = 1; i <= nrMaxStars; i++)
            stars[i].update(Gdx.graphics.getDeltaTime());
        fireworks.update(Gdx.graphics.getDeltaTime());
		leaves.update(Gdx.graphics.getDeltaTime());
		explosion.update(Gdx.graphics.getDeltaTime());
        trap.update(Gdx.graphics.getDeltaTime());
        powerupEffect.update(Gdx.graphics.getDeltaTime());
        shieldEffect.update(Gdx.graphics.getDeltaTime());
        flare.update(Gdx.graphics.getDeltaTime());

		if (game_state == 1) {
            menuBatch.setProjectionMatrix(menuCamera.combined);
			menuBatch.begin();
            background.draw(menuBatch);
            titleFont.draw(menuBatch, "Down the Rabbit Hole", screenWidth / 2 - 800, screenHeight - 150);
            playButton.draw(menuBatch);
            powerupsButton.draw(menuBatch);
            highscoreFont.draw(menuBatch, highscore, screenWidth / 2 - 450, 200);
			if (leaves.isComplete())
				leaves.reset();
			leaves.draw(menuBatch);
			menuBatch.end();
            for (i = 1; i <= nrMaxGameMusic; i++)
                if (gameMusic[i].isPlaying())
                    gameMusic[i].stop();
		}
		else if (game_state == 3) {

            //region rabbit update
			if (rabbitMove) {
				switch (rabbitDirection) {
					case 1:
						rabbitPos.y += 10;
						break;
					case 2:
						rabbitPos.x += 10;
						break;
					case 3:
						rabbitPos.y -= 10;
						break;
					case 4:
						rabbitPos.x -= 10;
						break;
					default: break;
				}
				if (rabbitPos.x % 200 == 0 && rabbitPos.y % 200 == 0) {
					rabbitMove = false;
					switch (rabbitDirection) {
						case 1:
							rabbitL--;
							break;
						case 2:
							rabbitC++;
							break;
						case 3:
							rabbitL++;
							break;
						case 4:
							rabbitC--;
							break;
						default: break;
					}
				}
                rabbitUp.setPosition(rabbitPos.x, rabbitPos.y);
				rabbitRight.setPosition(rabbitPos.x, rabbitPos.y);
                rabbitDown.setPosition(rabbitPos.x, rabbitPos.y);
                rabbitLeft.setPosition(rabbitPos.x, rabbitPos.y);
                if (isCameraLocked)
                    safeTranslate(rabbitPos.x - camera.position.x, rabbitPos.y - camera.position.y);
			}
			else {
				if (rabbitStartTime > 0) {
					if (System.currentTimeMillis() - rabbitStartTime >= rabbitSpeedDelay) {
						rabbitMove = true;
						rabbitStartTime = 0;
					}
				}
				else {
					rabbitStartTime = System.currentTimeMillis();
					switch (M[rabbitC][rabbitL]) {
						case 1:
							rabbitDirection = 1;
							break;
						case 2:
							rabbitDirection = 3;
							break;
						case 3:
							rabbitDirection = 4;
							break;
						case 4:
							rabbitDirection = 2;
							break;
						case 5:
							nrCarrotsCollected++;
							M[rabbitC][rabbitL] = 0;
                            for (i = 1; i <= nrMaxStars; i++) {
                                if (stars[i].isComplete()) {
                                    stars[i].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                    stars[i].reset();
                                    break;
                                }
                            }
                            carrotCollectSound.play();
							break;
						case 8:
							M[rabbitC][rabbitL] = 0;
                            explosion.setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                            game_over();
							break;
						case 9:
							createLevel();
							break;
                        case 10:
                            if (!isTrapActive) {
                                trapSound.play();
                                trap.setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                trap.reset();
                                isTrapActive = true;
                                int nrTrapUsed = randInt(1, nrTrapTypes);
                                if (nrTrapUsed == 1) {
                                    int turning;
                                    for (i = 2; i <= 19; i++) {
                                        for (j = 2; j <= 19; j++) {
                                            if (M[i][j] == 5) {
                                                turning = randInt(0, 1);
                                                if (turning == 1)
                                                    M[i][j] = 6;
                                            }
                                        }
                                    }
                                }
                                else if (nrTrapUsed == 2) {
                                    for (i = 2; i <= 19; i++) {
                                        for (j = 2; j <= 19; j++) {
                                            if (M[i][j] == 7) {
                                                M[i][j] = 6;
                                            }
                                        }
                                    }
                                    isInvisibleBombsTrapActive = true;
                                }
                                else if (nrTrapUsed == 3) {
                                    for (i = 2; i <= 19; i++) {
                                        for (j = 2; j <= 19; j++) {
                                            if (M[i][j] == 7 || M[i][j] == 6) {
                                                M[i][j] = 8;
                                            }
                                        }
                                    }
                                }
                                Gdx.app.log(TAG, "" + nrTrapUsed);
                            }
                            break;
                        case 11:
                            int randomPowerup = randInt(1, nrAvailablePowerups);
                            randomPowerup = availablePowerups[randomPowerup];
                            activePowerups[randomPowerup] = powerupsLevels[randomPowerup];
                            M[rabbitC][rabbitL] = 0;
                            powerupEffect.setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                            powerupEffect.reset();
                            powerupSound.play();
                            if (randomPowerup == 3) {
                                for (i = 2; i <= 19; i++) {
                                    for (j = 2; j <= 19; j++) {
                                        if (M[i][j] == 10)
                                            M[i][j] = 0;
                                    }
                                }
                                flare.setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                flare.reset();
                                if (activePowerups[randomPowerup] >= 2) {
                                    for (i = 2; i <= 19; i++) {
                                        for (j = 2; j <= 19; j++) {
                                            if (M[i][j] == 6)
                                                M[i][j] = 7;
                                        }
                                    }
                                }
                                if (activePowerups[randomPowerup] == 3) {
                                    for (i = 2; i <= 19; i++) {
                                        for (j = 2; j <= 19; j++) {
                                            if (M[i][j] == 8)
                                                M[i][j] = 0;
                                        }
                                    }
                                }
                            }
                            break;
						default: break;
					}
					if (rabbitL == 2 && rabbitDirection == 1)
						rabbitDirection = 3;
					else if (rabbitL == 19 && rabbitDirection == 3)
						rabbitDirection = 1;
					else if (rabbitC == 2 && rabbitDirection == 4)
						rabbitDirection = 2;
					else if (rabbitC == 19 && rabbitDirection == 2)
						rabbitDirection = 4;
					for (i = rabbitL - 4; i <= rabbitL + 4; i++) {
						for (j = rabbitC - 4; j <= rabbitC + 4; j++) {
							if (i >= 2 && j >= 2 && i <= 19 && j <= 19) {
								if (M[j][i] == 6) {
                                    if (!isInvisibleBombsTrapActive) {
                                        M[j][i] = 7;
                                        bombAppearSound.play();
                                    }
                                }
							}
						}
					}
					i = rabbitL - 1;
					j = rabbitC;
					if (i >= 2 && i <= 19) {
						if (M[j][i] == 7 || M[j][i] == 6) {
                            M[j][i] = 8;
                            bombArmSound.play();
                        }
						else if (M[j][i] == 8) {
							M[j][i] = 0;
							explosion.setPosition((j - 1) * 200 - 1900, (20 - i) * 200 - 1900);
							game_over();
						}
					}
					i = rabbitL + 1;
					if (i >= 2 && i <= 19) {
                        if (M[j][i] == 7 || M[j][i] == 6) {
                            M[j][i] = 8;
                            bombArmSound.play();
                        }
						else if (M[j][i] == 8) {
							M[j][i] = 0;
							explosion.setPosition((j - 1) * 200 - 1900, (20 - i) * 200 - 1900);
							game_over();
						}
					}
					i = rabbitL;
					j = rabbitC + 1;
					if (j >= 2 && j <= 19) {
                        if (M[j][i] == 7 || M[j][i] == 6) {
                            M[j][i] = 8;
                            bombArmSound.play();
                        }
						else if (M[j][i] == 8) {
							M[j][i] = 0;
							explosion.setPosition((j - 1) * 200 - 1900, (20 - i) * 200 - 1900);
							game_over();
						}
					}
					j = rabbitC - 1;
					if (j >= 2 && j <= 19) {
                        if (M[j][i] == 7 || M[j][i] == 6) {
                            M[j][i] = 8;
                            bombArmSound.play();
                        }
						else if (M[j][i] == 8) {
							M[j][i] = 0;
							explosion.setPosition((j - 1) * 200 - 1900, (20 - i) * 200 - 1900);
							game_over();
						}
					}
                    if (activePowerups[2] > 0) {
                        if (activePowerups[2] == 1) {
                            i = rabbitL - 1;
                            j = rabbitC;
                            if (i >= 2 && i <= 19) {
                                if (M[j][i] == 5) {
                                    nrCarrotsCollected++;
                                    M[j][i] = 0;
                                    for (int k = 1; k <= nrMaxStars; k++) {
                                        if (stars[k].isComplete()) {
                                            stars[k].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                            stars[k].reset();
                                            break;
                                        }
                                    }
                                    carrotCollectSound.play();
                                }
                            }
                            i = rabbitL + 1;
                            if (i >= 2 && i <= 19) {
                                if (M[j][i] == 5) {
                                    nrCarrotsCollected++;
                                    M[j][i] = 0;
                                    for (int k = 1; k <= nrMaxStars; k++) {
                                        if (stars[k].isComplete()) {
                                            stars[k].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                            stars[k].reset();
                                            break;
                                        }
                                    }
                                    carrotCollectSound.play();
                                }
                            }
                            i = rabbitL;
                            j = rabbitC + 1;
                            if (j >= 2 && j <= 19) {
                                if (M[j][i] == 5) {
                                    nrCarrotsCollected++;
                                    M[j][i] = 0;
                                    for (int k = 1; k <= nrMaxStars; k++) {
                                        if (stars[k].isComplete()) {
                                            stars[k].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                            stars[k].reset();
                                            break;
                                        }
                                    }
                                    carrotCollectSound.play();
                                }
                            }
                            j = rabbitC - 1;
                            if (j >= 2 && j <= 19) {
                                if (M[j][i] == 5) {
                                    nrCarrotsCollected++;
                                    M[j][i] = 0;
                                    for (int k = 1; k <= nrMaxStars; k++) {
                                        if (stars[k].isComplete()) {
                                            stars[k].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                            stars[k].reset();
                                            break;
                                        }
                                    }
                                    carrotCollectSound.play();
                                }
                            }
                        }
                        else if (activePowerups[2] == 2) {
                            for (i = rabbitL - 1; i <= rabbitL + 1; i++) {
                                for (j = rabbitC - 1; j <= rabbitC + 1; j++) {
                                    if (M[j][i] == 5) {
                                        nrCarrotsCollected++;
                                        M[j][i] = 0;
                                        for (int k = 1; k <= nrMaxStars; k++) {
                                            if (stars[k].isComplete()) {
                                                stars[k].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                                stars[k].reset();
                                                break;
                                            }
                                        }
                                        carrotCollectSound.play();
                                    }
                                }
                            }
                        }
                        else {
                            for (i = rabbitL - 2; i <= rabbitL + 2; i++) {
                                for (j = rabbitC - 2; j <= rabbitC + 2; j++) {
                                    if (M[j][i] == 5) {
                                        nrCarrotsCollected++;
                                        M[j][i] = 0;
                                        for (int k = 1; k <= nrMaxStars; k++) {
                                            if (stars[k].isComplete()) {
                                                stars[k].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                                                stars[k].reset();
                                                break;
                                            }
                                        }
                                        carrotCollectSound.play();
                                    }
                                }
                            }
                        }
                    }
				}
			}
            //endregion

            //region world
			worldBatch.setProjectionMatrix(camera.combined);
			worldBatch.begin();

			map.draw(worldBatch);

			upUsed = downUsed = leftUsed = rightUsed = 1;
			for (i = 1; i <= 20; i++) {
				for (j = 1; j <= 20; j++) {
					if (M[i][j] == 1) {
						arrowUp[++upUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						arrowUp[upUsed].draw(worldBatch);
					}
					else if (M[i][j] == 2) {
						arrowDown[++downUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						arrowDown[downUsed].draw(worldBatch);
					}
					else if (M[i][j] == 3) {
						arrowLeft[++leftUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						arrowLeft[leftUsed].draw(worldBatch);
					}
					else if (M[i][j] == 4) {
						arrowRight[++rightUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						arrowRight[rightUsed].draw(worldBatch);
					}
					else if (M[i][j] == 5) {
						carrot[++nrCarrotsUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						carrot[nrCarrotsUsed].draw(worldBatch);
					}
					else if (M[i][j] == 7) {
						bomb[++nrBombsUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						bomb[nrBombsUsed].draw(worldBatch);
					}
					else if (M[i][j] == 8) {
						armedBomb[++nrArmedBombsUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
						armedBomb[nrArmedBombsUsed].draw(worldBatch);
					}
                    else if (M[i][j] == 11) {
                        powerup[++nrPowerupsUsed].setPosition((i - 1) * 200 - 2000, (20 - j) * 200 - 2000);
                        powerup[nrPowerupsUsed].draw(worldBatch);
                    }
				}
			}

			rabbitHole.draw(worldBatch);
			if (deathTime == 0) {
                if (rabbitPos.x % 200 == 0 && rabbitPos.y % 200 == 0) {
                    if (rabbitDirection == 1)
                        rabbitUp.draw(worldBatch);
                    else if (rabbitDirection == 2)
                        rabbitRight.draw(worldBatch);
                    else if (rabbitDirection == 3)
                        rabbitDown.draw(worldBatch);
                    else if (rabbitDirection == 4)
                        rabbitLeft.draw(worldBatch);
                    rabbitAnimationTime = 0;
                }
                else {
                    rabbitAnimationTime += Gdx.graphics.getRawDeltaTime();
                    if (rabbitDirection == 1)
                        worldBatch.draw(rabbitUpAnimation.getKeyFrame(rabbitAnimationTime, false), rabbitPos.x, rabbitPos.y);
                    else if (rabbitDirection == 2)
                        worldBatch.draw(rabbitRightAnimation.getKeyFrame(rabbitAnimationTime, false), rabbitPos.x, rabbitPos.y);
                    else if (rabbitDirection == 3)
                        worldBatch.draw(rabbitDownAnimation.getKeyFrame(rabbitAnimationTime, false), rabbitPos.x, rabbitPos.y);
                    else if (rabbitDirection == 4)
                        worldBatch.draw(rabbitLeftAnimation.getKeyFrame(rabbitAnimationTime, false), rabbitPos.x, rabbitPos.y);
                }
            }

            if (activePowerups[1] > 0) {
                shield.setPosition(rabbitPos.x - 50, rabbitPos.y  -50);
                shield.draw(worldBatch);
            }
            if (activePowerups[2] > 0 && deathTime == 0) {
                magnet.setPosition(rabbitPos.x - 200, rabbitPos.y - 200);
                magnet.draw(worldBatch);
            }
            flare.draw(worldBatch);

            for (i = 1; i <= nrMaxStars; i++) {
                stars[i].draw(worldBatch);
            }

			if (deathTime > 0) {
				explosion.draw(worldBatch);
				game_over();
                if (gameMusicVolume > 0)
                    gameMusicVolume -= 0.005;
                gameMusic[nrGameMusic].setVolume(gameMusicVolume);
			}

            trap.draw(worldBatch);
            powerupEffect.draw(worldBatch);
            shieldEffect.draw(worldBatch);

			worldBatch.end();
            //endregion

            //region hud
            hudBatch.setProjectionMatrix(hudCamera.combined);
			hudBatch.begin();

			noArrow.draw(hudBatch);
			if (arrowSelected == 1)
				arrowUpSelected.draw(hudBatch);
			else
				arrowUp[1].draw(hudBatch);
			if (arrowSelected == 2)
				arrowDownSelected.draw(hudBatch);
			else
				arrowDown[1].draw(hudBatch);
			if (arrowSelected == 3)
				arrowLeftSelected.draw(hudBatch);
			else
				arrowLeft[1].draw(hudBatch);
			if (arrowSelected == 4)
				arrowRightSelected.draw(hudBatch);
			else
				arrowRight[1].draw(hudBatch);

			if (levelTextSize > 0) {
				levelFont.getData().setScale(levelTextSize);
				levelFont.draw(hudBatch, "Level " + level, screenWidth / 2 - 200, screenHeight - 150);
				if (levelTextSize < 1 && levelTextStartTime == 0) {
					levelTextSize += 0.05;
				}
				else {
					if (levelTextStartTime > 0) {
						if (System.currentTimeMillis() - levelTextStartTime > 3000) {
							if (levelTextSize > 0)
								levelTextSize -= 0.05;
						}
					}
					else {
                        levelTextStartTime = System.currentTimeMillis();
                        fireworks.reset();
                    }
				}
			}

			scoreFont.draw(hudBatch, "" + nrCarrotsCollected, screenWidth - 200, screenHeight - 100);
            fireworks.draw(hudBatch);

			if (isCameraLocked)
				cameraLocked.draw(hudBatch);
			else
				cameraUnlocked.draw(hudBatch);

			hudBatch.end();
            //endregion

            if (!gameMusic[nrGameMusic].isPlaying() && deathTime == 0) {
                nrGameMusic = randInt(1, nrMaxGameMusic);
                gameMusic[nrGameMusic].setVolume(gameMusicVolume);
                gameMusic[nrGameMusic].play();
            }
		}
        else if (game_state == 4) {
            menuBatch.setProjectionMatrix(menuCamera.combined);
            menuBatch.begin();
            background.draw(menuBatch);
            homeButton.draw(menuBatch);
            powerupsFont.draw(menuBatch, "" + carrotBank, screenWidth - 400, 145);
            carrot[1].setPosition(screenWidth - 560, 0);
            carrot[1].draw(menuBatch);
            if (currentPowerup > 1)
                previousButton.draw(menuBatch);
            if (currentPowerup < nrMaxPowerups)
                nextButton.draw(menuBatch);

            switch (currentPowerup) {
                case 1:
                    powerupsFont.draw(menuBatch, "beta-Carotene Refractor", screenWidth / 2 - 550, screenHeight - 150);
                    shieldPortrait.draw(menuBatch);
                    if (powerupsLevels[currentPowerup] == 0 || powerupsLevels[currentPowerup] == 1)
                        powerupsSmallFont.draw(menuBatch, "Protects you from \n\none explosion.", screenWidth / 2 - 100, screenHeight - 350);
                    else if (powerupsLevels[currentPowerup] == 2)
                        powerupsSmallFont.draw(menuBatch, "Protects you from \n\none explosion. \n\nDeactivates all adjacent \n\nbombs when destroyed.", screenWidth / 2 - 100, screenHeight - 350);
                    else
                        powerupsSmallFont.draw(menuBatch, "Protects you from \n\none explosion. \n\nDeactivates all bombs \n\naround you when destroyed.", screenWidth / 2 - 100, screenHeight - 350);
                break;
                case 2:
                    powerupsFont.draw(menuBatch, "Hungry Heart", screenWidth / 2 - 325, screenHeight - 150);
                    magnetPortrait.draw(menuBatch);
                    if (powerupsLevels[currentPowerup] == 0 || powerupsLevels[currentPowerup] == 1)
                        powerupsSmallFont.draw(menuBatch, "Attracts adjacent carrots.", screenWidth / 2 - 100, screenHeight - 350);
                    else if (powerupsLevels[currentPowerup] == 2)
                        powerupsSmallFont.draw(menuBatch, "Attracts carrots in a small \n\narea around you.", screenWidth / 2 - 100, screenHeight - 350);
                    else
                        powerupsSmallFont.draw(menuBatch, "Attracts carrots in a large \n\narea around you.", screenWidth / 2 - 100, screenHeight - 350);
                    break;
                case 3:
                    powerupsFont.draw(menuBatch, "True Rabbit Sight", screenWidth / 2 - 375, screenHeight - 150);
                    flarePortrait.draw(menuBatch);
                    if (powerupsLevels[currentPowerup] == 0 || powerupsLevels[currentPowerup] == 1)
                        powerupsSmallFont.draw(menuBatch, "Destroys all traps.", screenWidth / 2 - 100, screenHeight - 350);
                    else if (powerupsLevels[currentPowerup] == 2)
                        powerupsSmallFont.draw(menuBatch, "Destroys all traps and \n\nreveals all bombs.", screenWidth / 2 - 100, screenHeight - 350);
                    else
                        powerupsSmallFont.draw(menuBatch, "Destroys all traps, \n\nreveals all bombs and \n\ndeactivates armed bombs.", screenWidth / 2 - 100, screenHeight - 350);
                    break;
                default: break;
            }
            powerupsFont.draw(menuBatch, "Level " + powerupsLevels[currentPowerup], screenWidth / 2 - 200, screenHeight - 250);
            if (powerupsLevels[currentPowerup] < 3)
                powerupsSmallFont.draw(menuBatch, "Upgrade cost: " + powerupsUpgradePrices[currentPowerup], screenWidth / 2 - 100, screenHeight - 675);
            if (powerupsLevels[currentPowerup] > 0) {
                powerupsSmallFont.draw(menuBatch, "Charges: " + powerupsCharges[currentPowerup], screenWidth / 2 - 100, screenHeight - 725);
                if (powerupsCharges[currentPowerup] == 0)
                    powerupsSmallFont.draw(menuBatch, "Charge cost: " + powerupsChargePrices[currentPowerup], screenWidth / 2 - 100, screenHeight - 775);
            }
            if (powerupsLevels[currentPowerup] < 3 && carrotBank >= powerupsUpgradePrices[currentPowerup])
                upgradeButton.draw(menuBatch);
            if (powerupsCharges[currentPowerup] == 0 && powerupsLevels[currentPowerup] > 0 && carrotBank >= powerupsChargePrices[currentPowerup])
                chargeButton.draw(menuBatch);
            menuBatch.end();
        }
	}

	public void dispose() {
        int i;
		menuBatch.dispose();
		hudBatch.dispose();
		worldBatch.dispose();
		map_pic.dispose();
		arrowUpPic.dispose();
		arrowDownPic.dispose();
		arrowLeftPic.dispose();
		arrowRightPic.dispose();
		arrowUpSelectedPic.dispose();
		arrowDownSelectedPic.dispose();
		arrowLeftSelectedPic.dispose();
		arrowRightSelectedPic.dispose();
		noArrow_pic.dispose();
		rabbitLeftPic.dispose();
        rabbitRightPic.dispose();
        rabbitUpPic.dispose();
        rabbitDownPic.dispose();
		carrot_pic.dispose();
		bomb_pic.dispose();
		armedBomb_pic.dispose();
		fontGenerator.dispose();
		scoreFont.dispose();
        for (i = 1; i <= nrMaxStars; i++)
            stars[i].dispose();
		fireworks.dispose();
		leaves.dispose();
		menuMusic.dispose();
		buttonSound.dispose();
		explosion.dispose();
		cameraLockedPic.dispose();
		cameraUnlockedPic.dispose();
        carrotCollectSound.dispose();
        bombAppearSound.dispose();
        bombArmSound.dispose();
        arrowSelectSound.dispose();
        arrowPlaceSound.dispose();
        arrowCancelSound.dispose();
        arrowRemoveSound.dispose();
        for (i = 1; i <= nrMaxBombExplosionSound; i++)
            bombExplosionSound[i].dispose();
        for (i = 1; i <= nrMaxGameMusic; i++)
            gameMusic[i].dispose();
        rabbitRightPics.dispose();
        trap.dispose();
        trapSound.dispose();
        playButtonPic.dispose();
        powerupsButtonPic.dispose();
        homeButtonPic.dispose();
        powerupsFont.dispose();
        shieldPortraitPic.dispose();
        upgradeButonPic.dispose();
        chargeButtonPic.dispose();
        shieldPic.dispose();
        powerupPic.dispose();
        powerupEffect.dispose();
        shieldEffect.dispose();
        powerupSound.dispose();
        shieldSound.dispose();
        nextButtonPic.dispose();
        previousButtonPic.dispose();
        magnetPic.dispose();
        magnetPortraitPic.dispose();
        flarePortraitPic.dispose();
        flare.dispose();
        backgroundPic.dispose();
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		int arrow = isArrowSelected(x, y);

		if (game_state == 1) {
            pozScreen.x = x;
            pozScreen.y = y;
            pozWorld = menuCamera.unproject(pozScreen);
            x = pozWorld.x;
            y = pozWorld.y;
			if (isButtonTouched(playButton, x , y)) {
				game_state = 2;
				buttonSound.play();
				menuMusic.stop();
                nrGameMusic = randInt(1, nrMaxGameMusic);
                gameMusicVolume = 0.8f;
                gameMusic[nrGameMusic].setVolume(gameMusicVolume);
                gameMusic[nrGameMusic].play();
                for (int i = 1; i <= nrMaxPowerups; i++) {
                    if (powerupsCharges[i] > 0) {
                        nrAvailablePowerups++;
                        availablePowerups[nrAvailablePowerups] = i;
                    }
                }
			}
            else if (isButtonTouched(powerupsButton, x , y)) {
                game_state = 4;
                buttonSound.play();
                currentPowerup = 1;
            }
		}
		if (game_state == 2) {
			createLevel();
			game_state = 3;
		}
		if (game_state == 3) {
			if (arrow == 0) {
				pozScreen.x = x;
				pozScreen.y = y;
				pozWorld = camera.unproject(pozScreen);
				pozWorld.x += 2000;
				pozWorld.y += 2000;
				l = 20 - (int) pozWorld.y / 200;
				c = (int) pozWorld.x / 200;
				c++;
				if (M[c][l] >= 0 && M[c][l] <= 4) {
                    int aux = M[c][l];
                    M[c][l] = arrowSelected;
                    if (arrowSelected != 0)
                        arrowPlaceSound.play();
                    else if (M[c][l] == 0 && aux >= 1 && aux <= 4)
                        arrowRemoveSound.play();
                    arrowSelected = 0;
                }
                else if (arrowSelected != 0)
                    arrowCancelSound.play();
			}
			else {
				if (arrow == -1) {
                    arrow = 0;
                    arrowCancelSound.play();
                }
                else
                    arrowSelectSound.play();
				arrowSelected = arrow;
			}
		}
        if (game_state == 4) {
            pozScreen.x = x;
            pozScreen.y = y;
            pozWorld = menuCamera.unproject(pozScreen);
            x = pozWorld.x;
            y = pozWorld.y;
            if (isButtonTouched(homeButton, x, y)) {
                buttonSound.play();
                game_state = 1;
            }
            if (currentPowerup > 1 && isButtonTouched(previousButton, x, y)) {
                buttonSound.play();
                currentPowerup--;
            }
            if (currentPowerup < nrMaxPowerups && isButtonTouched(nextButton, x, y)) {
                buttonSound.play();
                currentPowerup++;
            }
            if (isButtonTouched(upgradeButton, x, y) && powerupsLevels[currentPowerup] < 3 && carrotBank >= powerupsUpgradePrices[currentPowerup]) {
                carrotBank -= powerupsUpgradePrices[currentPowerup];
                powerupsLevels[currentPowerup]++;
                userData.putInteger("powerupLevel" + currentPowerup, powerupsLevels[currentPowerup]);
                userData.flush();
                buttonSound.play();
            }
            if (isButtonTouched(chargeButton, x, y) && powerupsCharges[currentPowerup] == 0 && powerupsLevels[currentPowerup] > 0 && carrotBank >= powerupsChargePrices[currentPowerup]) {
                carrotBank -= powerupsChargePrices[currentPowerup];
                powerupsCharges[currentPowerup] = 5;
                userData.putInteger("powerupCharges" + currentPowerup, powerupsCharges[currentPowerup]);
                userData.flush();
                buttonSound.play();
            }
        }
		return true;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if (!isCameraLocked) {
			deltaX = -deltaX;
			safeTranslate(deltaX, deltaY);
			return true;
		}
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {}

	public void createLevel() {
		int i, j, x, y, nrCarrotsCreated = randInt(20 + level, 50 + level),
                nrBombsCreated = randInt(15 + 3 * level, 20 + 3 * level), nrTrapsCreated = randInt(3, 6 + level),
                nrPowerupsCreated = randInt(0, nrAvailablePowerups);
        if (nrCarrotsCreated > 350)
            nrCarrotsCreated = 350;
        if (nrBombsCreated > 350)
            nrBombsCreated = 350;
		level++;
		rabbitL = 10;
		rabbitC = 10;
		rabbitPos.x = (rabbitL - 1) * 200 - 2000;
		rabbitPos.y = (20 - rabbitC) * 200 - 2000;
		rabbitMove = true;
		rabbitDirection = 1;
		for (i = 2; i <= 19; i++) {
			for (j = 2; j <= 19; j++) {
				M[i][j] = 0;
			}
		}
		x = randInt(2, 19);
		y = randInt(2, 19);
		M[x][y] = 9;
		rabbitHole.setPosition((x - 1) * 200 - 2000, (20 - y) * 200 - 2000);
        for (i = 1; i <= nrPowerupsCreated; i++) {
            x = randInt(2, 19);
            y = randInt(2, 19);
            if (M[x][y] == 0)
                M[x][y] = 11;
        }
        if (level > 1) {
            for (i = 1; i <= nrTrapsCreated; i++) {
                x = randInt(2, 19);
                y = randInt(2, 19);
                if (M[x][y] == 0)
                    M[x][y] = 10;
            }
        }
		for (i = 1; i <= nrCarrotsCreated; i++) {
			x = randInt(2, 19);
			y = randInt(2, 19);
			if (M[x][y] == 0)
				M[x][y] = 5;
		}
		for (i = 1; i <= nrBombsCreated; i++) {
			x = randInt(2, 19);
			y = randInt(2, 19);
			if (M[x][y] == 0 && (x > 13 || x < 7 || y > 13 || y < 7))
				M[x][y] = 6;
		}
		if (rabbitSpeedDelay >= 90)
            rabbitSpeedDelay -= 90;
        camera.translate(-camera.position.x, -camera.position.y);
		camera.update();
		levelTextSize = 0.1f;
		levelTextStartTime = 0;
        isTrapActive = false;
        isInvisibleBombsTrapActive = false;
        for (i = 1; i <= nrMaxPowerups; i++)
            activePowerups[i] = 0;
	}

	public int isArrowSelected(float x, float y) {
		float l = (screenWidth - 1000) / 4;
        pozScreen.x = x;
        pozScreen.y = y;
        pozWorld = hudCamera.unproject(pozScreen);
        x = pozWorld.x;
        y = pozWorld.y;
        if (x >= screenWidth - 200 && y >= screenHeight / 2 - 100 && y <= screenHeight / 2 + 100) {
            if (!isCameraLocked) {
                isCameraLocked = true;
                safeTranslate(rabbitPos.x - camera.position.x, rabbitPos.y - camera.position.y);
            }
            else
                isCameraLocked = false;
        }
		if (y > 200)
			return 0;
		if (x <= 200)
			return -1;
		else if (x >= 200 + l && x <= 400 + l)
			return 1;
		else if (x >= 400 + 2 * l && x <= 600 + 2 * l)
			return 2;
		else if (x >= 600 + 3 * l && x <= 800 + 3 * l)
			return 3;
		else if (x >= 800 + 4 * l)
			return 4;
		return 0;
	}

	public static int randInt(int min, int max) {
		Random rand;
		rand = new Random();

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public void game_over() {
        if (activePowerups[1] > 0) {
            shieldEffect.setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
            shieldEffect.reset();
            shieldSound.play();
            if (activePowerups[1] == 2) {
                int i, j;
                i = rabbitL - 1;
                j = rabbitC;
                if (M[j][i] >= 6 && M[j][i] <= 8)
                    M[j][i] = 0;
                i = rabbitL + 1;
                if (M[j][i] >= 6 && M[j][i] <= 8)
                    M[j][i] = 0;
                i = rabbitL;
                j = rabbitC + 1;
                if (M[j][i] >= 6 && M[j][i] <= 8)
                    M[j][i] = 0;
                j = rabbitC - 1;
                if (M[j][i] >= 6 && M[j][i] <= 8)
                    M[j][i] = 0;
            }
            else if (activePowerups[1] == 3) {
                int i, j;
                for (i = rabbitL - 1; i <= rabbitL + 1; i++) {
                    for (j = rabbitC - 1; j <= rabbitC + 1; j++) {
                        if (M[j][i] >= 6 && M[j][i] <= 8)
                            M[j][i] = 0;
                    }
                }
            }
            activePowerups[1] = 0;
        }
        else {
            rabbitDirection = 0;
            if (deathTime == 0) {
                deathTime = System.currentTimeMillis();
                drawExplosion = true;
                explosion.reset();
                nrbombExplosionSound = randInt(1, nrMaxBombExplosionSound);
                bombExplosionSound[nrbombExplosionSound].play();
            } else if (System.currentTimeMillis() - deathTime > 5000) {
                game_state = 1;
                menuMusic.play();
                leaves.reset();
                if (nrCarrotsCollected > userData.getInteger("highscore", 0)) {
                    userData.putInteger("highscore", nrCarrotsCollected);
                    highscore = "Highscore: " + nrCarrotsCollected;
                    userData.flush();
                }
                carrotBank += nrCarrotsCollected;
                userData.putInteger("carrotBank", carrotBank);
                userData.flush();
                nrCarrotsCollected = 0;
                rabbitSpeedDelay = 450;
                level = 0;
                deathTime = 0;
                arrowSelected = 0;
                nrAvailablePowerups = 0;
                for (int i = 1; i <= nrMaxPowerups; i++) {
                    if (powerupsCharges[i] > 0) {
                        powerupsCharges[i]--;
                        userData.putInteger("powerupCharges" + i, powerupsCharges[i]);
                        userData.flush();
                    }
                }
            }
        }
	}

	public void safeTranslate(float deltaX, float deltaY) {
		camera.translate(deltaX, deltaY);

		cameraHalfWidth = camera.viewportWidth * .5f * camera.zoom;
		cameraHalfHeight = camera.viewportHeight * .5f * camera.zoom;

		cameraLeft = camera.position.x - cameraHalfWidth;
		cameraRight = camera.position.x + cameraHalfWidth;
		cameraBottom = camera.position.y - cameraHalfHeight;
		cameraTop = camera.position.y + cameraHalfHeight;

		if (map.getWidth() < camera.viewportWidth) {
			camera.position.x = mapRight / 2;
		} else if (cameraLeft <= mapLeft) {
			camera.position.x = mapLeft + cameraHalfWidth;
		} else if (cameraRight >= mapRight) {
			camera.position.x = mapRight - cameraHalfWidth;
		}

		if (map.getHeight() < camera.viewportHeight) {
			camera.position.y = mapTop / 2;
		} else if (cameraBottom <= mapBottom) {
			camera.position.y = mapBottom + cameraHalfHeight;
		} else if (cameraTop >= mapTop) {
			camera.position.y = mapTop - cameraHalfHeight;
		}

		camera.update();
	}

    public boolean isButtonTouched(Sprite button, float x, float y) {
        return(x >= button.getX() && x <= button.getX() + button.getWidth() && y >= button.getY() && y <= button.getY() + button.getHeight());
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        menuViewport.update(width, height);
        hudViewport.update(width, height);
        menuCamera.position.set(screenWidth/2,screenHeight/2,0);
        hudCamera.position.set(screenWidth/2,screenHeight/2,0);
    }
}
