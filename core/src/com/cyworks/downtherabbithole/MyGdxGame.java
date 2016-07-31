package com.cyworks.downtherabbithole;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
	Texture play_button_pic, map_pic, arrowUpPic, arrowDownPic, arrowLeftPic, arrowRightPic, noArrow_pic, rabbit_pic, carrot_pic, bomb_pic, armedBomb_pic;
	Texture arrowDownSelectedPic, arrowUpSelectedPic, arrowLeftSelectedPic, arrowRightSelectedPic, rabbitHole_pic;
	Sprite play_button, map, noArrow, arrowDownSelected, arrowUpSelected, arrowLeftSelected, arrowRightSelected, rabbit, rabbitHole;
	Sprite[] arrowUp, arrowDown, arrowLeft, arrowRight, carrot, bomb, armedBomb;
	public static final String TAG = "myMessage";
	OrthographicCamera camera;
	float mapRight, mapLeft, mapTop, mapBottom, cameraHalfWidth, cameraHalfHeight, cameraLeft, cameraRight, cameraBottom, cameraTop, screenWidth, screenHeight;
	float levelTextSize;
	int l, c, arrowSelected, upUsed, downUsed, leftUsed, rightUsed, rabbitL, rabbitC, rabbitDirection, nrCarrotsCollected, level, nrStarsUsed;
	Vector3 pozScreen, pozWorld, arrowPos, rabbitPos;
	int[][] M; //1, 2, 3, 4 directions; 5 carrot; 6 invisible bomb; 7 visible bomb; 8 armed bomb
	boolean rabbitMove;
	long rabbitStartTime, delayTime, levelTextStartTime;
	FreeTypeFontGenerator fontGenerator;
	FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
	BitmapFont scoreFont, levelFont, highscoreFont;
	ParticleEffect[] stars;
    ParticleEffect fireworks;
	Preferences userData;
    String highscore;
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
		play_button_pic = new Texture("play_button.png");
		map_pic = new Texture("map.png");
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
		play_button = new Sprite(play_button_pic);
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
        //endregion

        //region geometry
		pozScreen = new Vector3();
		pozWorld = new Vector3();
		arrowPos = new Vector3();
		rabbitPos = new Vector3();

		play_button.setPosition(screenWidth / 2 - 200, screenHeight / 2 + 200);
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

		game_state = 1;
		M = new int[25][25];
		for (i = 1; i <= 20; i++)
			M[i][1] = M[i][20] = -1;
		for (j = 1; j <= 20; j++)
			M[1][j] = M[20][j] = -1;
		delayTime = 550;

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

		if (game_state == 1) {
			menuBatch.begin();
			play_button.draw(menuBatch);
            highscoreFont.draw(menuBatch, highscore, screenWidth / 2 - 400, 300);
			menuBatch.end();
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
			}
			else {
				if (rabbitStartTime > 0) {
					if (System.currentTimeMillis() - rabbitStartTime >= delayTime) {
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
							break;
						case 7:
							game_over();
							break;
						case 8:
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
								if (M[j][i] == 6)
									M[j][i] = 7;
							}
						}
					}
					i = rabbitL - 1;
					j = rabbitC;
					if (i >= 2 && i <= 19) {
						if (M[j][i] == 7)
							M[j][i] = 8;
						else if (M[j][i] == 8)
							game_over();
					}
					i = rabbitL + 1;
					if (i >= 2 && i <= 19) {
						if (M[j][i] == 7)
							M[j][i] = 8;
						else if (M[j][i] == 8)
							game_over();
					}
					i = rabbitL;
					j = rabbitC + 1;
					if (j >= 2 && j <= 19) {
						if (M[j][i] == 7)
							M[j][i] = 8;
						else if (M[j][i] == 8)
							game_over();
					}
					j = rabbitC - 1;
					if (j >= 2 && j <= 19) {
						if (M[j][i] == 7)
							M[j][i] = 8;
						else if (M[j][i] == 8)
							game_over();
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
			rabbit.draw(worldBatch);

            for (i = 1; i <= nrStarsUsed; i++) {
                stars[i].draw(worldBatch);
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

			scoreFont.draw(hudBatch, "" + nrCarrotsCollected, screenWidth - 120, screenHeight);
            fireworks.draw(hudBatch);

			hudBatch.end();
            //endregion
		}
	}

	public void dispose() {
        int i;
		menuBatch.dispose();
		hudBatch.dispose();
		worldBatch.dispose();
		play_button_pic.dispose();
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
			if (x >= play_button.getX() && x <= play_button.getX() + 400 && y >= play_button.getY() && y <= play_button.getY() + 200)
				game_state = 2;
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
				if (M[c][l] >= 0 && M[c][l] <= 4)
					M[c][l] = arrowSelected;
			}
			else {
				if (arrow == -1)
					arrow = 0;
				arrowSelected = arrow;
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
		camera.translate(-deltaX, deltaY);

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
		return true;
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
			if (M[x][y] == 0)
				M[x][y] = 6;
		}
		if (delayTime >= 50)
			delayTime -= 50;
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
		game_state = 1;
        if (nrCarrotsCollected > userData.getInteger("highscore", 0)) {
            userData.putInteger("highscore", nrCarrotsCollected);
            highscore = "Highscore: " + nrCarrotsCollected;
            userData.flush();
        }
		nrCarrotsCollected = 0;
		delayTime = 550;
		level = 0;
	}
}
