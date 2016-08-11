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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter implements GestureListener {

    //region variables
	SpriteBatch menuBatch, worldBatch, hudBatch;
	public int game_state; //1 main menu; 2 new level; 3 in game
	Texture map_pic, arrowUpPic, arrowDownPic, arrowLeftPic, arrowRightPic, noArrow_pic, rabbit_pic, carrot_pic, bomb_pic, armedBomb_pic;
	Texture arrowDownSelectedPic, arrowUpSelectedPic, arrowLeftSelectedPic, arrowRightSelectedPic, rabbitHole_pic, cameraLockedPic, cameraUnlockedPic;
	Sprite map, noArrow, arrowDownSelected, arrowUpSelected, arrowLeftSelected, arrowRightSelected, rabbit, rabbitHole, cameraLocked, cameraUnlocked;
	Sprite[] arrowUp, arrowDown, arrowLeft, arrowRight, carrot, bomb, armedBomb;
	public static final String TAG = "myMessage";
	OrthographicCamera camera;
	float mapRight, mapLeft, mapTop, mapBottom, cameraHalfWidth, cameraHalfHeight, cameraLeft, cameraRight, cameraBottom, cameraTop, screenWidth, screenHeight;
	float levelTextSize, gameMusicVolume;
	int l, c, arrowSelected, upUsed, downUsed, leftUsed, rightUsed, rabbitL, rabbitC, rabbitDirection, nrCarrotsCollected, level, nrStarsUsed;
	Vector3 pozScreen, pozWorld, arrowPos, rabbitPos;
	int[][] M; //1, 2, 3, 4 directions; 5 carrot; 6 invisible bomb; 7 visible bomb; 8 armed bomb
	boolean rabbitMove, drawExplosion, isCameraLocked;
	long rabbitStartTime, rabbitSpeedDelay, levelTextStartTime, deathTime;
	FreeTypeFontGenerator fontGenerator;
	FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
	BitmapFont scoreFont, levelFont, highscoreFont, titleFont;
	ParticleEffect[] stars;
    ParticleEffect fireworks, leaves, explosion;
	Preferences userData;
    String highscore;
	Music menuMusic;
	Sound buttonSound, carrotCollectSound, bombAppearSound, bombArmSound;
    Music[] gameMusic;
    Sound[] bombExplosionSound;
    int nrGameMusic, nrbombExplosionSound, nrMaxGameMusic, nrMaxBombExplosionSound;
    //endregion

	@Override
	public void create () {
		int i, j;
		float l;

		Gdx.input.setInputProcessor(new GestureDetector(this));

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
		rabbit_pic = new Texture("rabbit.png");
		carrot_pic = new Texture("carrot.png");
		bomb_pic = new Texture("bomb.png");
		armedBomb_pic = new Texture("armedBomb.png");
		rabbitHole_pic = new Texture("rabbitHole.png");
		cameraLockedPic = new Texture("cameraLocked.png");
		cameraUnlockedPic = new Texture("cameraUnlocked.png");
		arrowLeftSelected = new Sprite(arrowLeftSelectedPic);
		arrowRightSelected = new Sprite(arrowRightSelectedPic);
		arrowDownSelected = new Sprite(arrowDownSelectedPic);
		arrowUpSelected = new Sprite(arrowUpSelectedPic);
		noArrow = new Sprite(noArrow_pic);
		map = new Sprite(map_pic);
		rabbit = new Sprite(rabbit_pic);
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
        //endregion

        //region sounds
		buttonSound = Gdx.audio.newSound(Gdx.files.internal("button.wav"));
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menuMusic.mp3"));
		menuMusic.setVolume(0.7f);
		menuMusic.play();

        carrotCollectSound = Gdx.audio.newSound(Gdx.files.internal("carrotCollect.wav"));
        bombAppearSound = Gdx.audio.newSound(Gdx.files.internal("bombAppear.wav"));
        bombArmSound = Gdx.audio.newSound(Gdx.files.internal("bombArm.mp3"));

        nrMaxGameMusic = 3;
        gameMusic = new Music[nrMaxGameMusic + 1];
        for (i = 1; i <= nrMaxGameMusic; i++)
            gameMusic[i] = Gdx.audio.newMusic(Gdx.files.internal("gameMusic" + i + ".mp3"));

        nrMaxBombExplosionSound = 2;
        bombExplosionSound = new Sound[nrMaxBombExplosionSound + 1];
        for (i = 1; i <= nrMaxBombExplosionSound; i++)
            bombExplosionSound[i] = Gdx.audio.newSound(Gdx.files.internal("bombExplosion" + i + ".wav"));
        //endregion

        userData = Gdx.app.getPreferences("User Data");
        highscore = "Highscore: " + userData.getInteger("highscore", 0);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        //region pe
        stars = new ParticleEffect[410];
        for (i = 1; i <= 400; i++) {
            stars[i] = new ParticleEffect();
            stars[i].load(Gdx.files.internal("stars.party"),Gdx.files.internal(""));
            stars[i].start();
        }
        fireworks = new ParticleEffect();
        fireworks.load(Gdx.files.internal("fireworks.party"), Gdx.files.internal(""));
        fireworks.setPosition(screenWidth / 2, screenHeight - 100);
        fireworks.start();
		leaves = new ParticleEffect();
		leaves.load(Gdx.files.internal("leaves.party"), Gdx.files.internal(""));
		leaves.setPosition(500, screenHeight - 500);
		leaves.start();
		explosion = new ParticleEffect();
		explosion.load(Gdx.files.internal("explosion.party"),Gdx.files.internal(""));
		explosion.setPosition(100, 100);
		explosion.start();
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
        //endregion

	}

	@Override
	public void render () {
		int i, j, nrCarrotsUsed = 0, nrBombsUsed = 0, nrArmedBombsUsed = 0;

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (i = 1; i <= 400; i++)
            stars[i].update(Gdx.graphics.getDeltaTime());
        fireworks.update(Gdx.graphics.getDeltaTime());
		leaves.update(Gdx.graphics.getDeltaTime());
		explosion.update(Gdx.graphics.getDeltaTime());

		if (game_state == 1) {
			menuBatch.begin();
            titleFont.draw(menuBatch, "Down the Rabbit Hole", screenWidth / 2 - 800, screenHeight - 150);
			highscoreFont.draw(menuBatch, "Play", screenWidth / 2 - 120, screenHeight / 2 - 50);
            highscoreFont.draw(menuBatch, highscore, screenWidth / 2 - 400, 200);
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
				rabbit.setPosition(rabbitPos.x, rabbitPos.y);
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
                            stars[++nrStarsUsed].setPosition(rabbitPos.x + 100, rabbitPos.y + 100);
                            stars[nrStarsUsed].reset();
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
                                    M[j][i] = 7;
                                    bombAppearSound.play();
                                }
							}
						}
					}
					i = rabbitL - 1;
					j = rabbitC;
					if (i >= 2 && i <= 19) {
						if (M[j][i] == 7) {
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
                        if (M[j][i] == 7) {
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
                        if (M[j][i] == 7) {
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
                        if (M[j][i] == 7) {
                            M[j][i] = 8;
                            bombArmSound.play();
                        }
						else if (M[j][i] == 8) {
							M[j][i] = 0;
							explosion.setPosition((j - 1) * 200 - 1900, (20 - i) * 200 - 1900);
							game_over();
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
				}
			}

			rabbitHole.draw(worldBatch);
			if (deathTime == 0)
				rabbit.draw(worldBatch);

            for (i = 1; i <= nrStarsUsed; i++) {
                stars[i].draw(worldBatch);
            }

			if (deathTime > 0) {
				explosion.draw(worldBatch);
				game_over();
				Gdx.app.log(TAG, "" + rabbitPos.x + " " + rabbitPos.y);
                if (gameMusicVolume > 0)
                    gameMusicVolume -= 0.005;
                gameMusic[nrGameMusic].setVolume(gameMusicVolume);
			}

			worldBatch.end();
            //endregion

            //region hud
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
				levelFont.draw(hudBatch, "Level " + level, screenWidth / 2 - 200, screenHeight - 50);
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

			scoreFont.draw(hudBatch, "" + nrCarrotsCollected, screenWidth - 150, screenHeight);
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
		rabbit_pic.dispose();
		carrot_pic.dispose();
		bomb_pic.dispose();
		armedBomb_pic.dispose();
		fontGenerator.dispose();
		scoreFont.dispose();
        for (i = 1; i <= 400; i++)
            stars[i].dispose();
		fireworks.dispose();
		leaves.dispose();
		menuMusic.dispose();
		buttonSound.dispose();
		explosion.dispose();
		cameraLockedPic.dispose();
		cameraUnlockedPic.dispose();
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		int arrow = isArrowSelected(x, y);

		if (game_state == 1) {
			y = screenHeight - y;
			if (x >= screenWidth / 2 - 200 && x <= screenWidth / 2 - 220 + 400 && y >= screenHeight / 2 - 200 && y <= screenHeight / 2 - 400 + 400) {
				game_state = 2;
				buttonSound.play();
				menuMusic.stop();
                nrGameMusic = randInt(1, nrMaxGameMusic);
                gameMusicVolume = 0.8f;
                gameMusic[nrGameMusic].setVolume(gameMusicVolume);
                gameMusic[nrGameMusic].play();
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
                    M[c][l] = arrowSelected;
                    arrowSelected = 0;
                }
			}
			else {
				if (arrow == -1)
					arrow = 0;
				arrowSelected = arrow;
			}
			if (x >= screenWidth - 200 && y >= screenHeight / 2 - 100 && y <= screenHeight / 2 + 100) {
				if (!isCameraLocked) {
					isCameraLocked = true;
					safeTranslate(rabbitPos.x - camera.position.x, rabbitPos.y - camera.position.y);
				}
				else
					isCameraLocked = false;
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
		int i, j, x, y, nrCarrotsCreated = randInt(20, 50), nrBombsCreated = randInt(10, 20);
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
        nrStarsUsed = 0;
	}

	public int isArrowSelected(float x, float y) {
		float l = (screenWidth - 1000) / 4;
		if (y < screenHeight - 200)
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
		rabbitDirection = 0;
		if (deathTime == 0) {
			deathTime = System.currentTimeMillis();
			drawExplosion = true;
			Gdx.app.log(TAG, "ok");
			explosion.reset();
            nrbombExplosionSound = randInt(1, nrMaxBombExplosionSound);
            bombExplosionSound[nrbombExplosionSound].play();
		}
		else if (System.currentTimeMillis() - deathTime > 5000) {
			game_state = 1;
			menuMusic.play();
			leaves.reset();
			if (nrCarrotsCollected > userData.getInteger("highscore", 0)) {
				userData.putInteger("highscore", nrCarrotsCollected);
				highscore = "Highscore: " + nrCarrotsCollected;
				userData.flush();
			}
			nrCarrotsCollected = 0;
			rabbitSpeedDelay = 450;
			level = 0;
			deathTime = 0;
            arrowSelected = 0;
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
}
