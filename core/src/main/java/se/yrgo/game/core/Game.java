package se.yrgo.game.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Intersector;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import se.yrgo.game.entities.Character;
import se.yrgo.game.entities.Flower;
import se.yrgo.game.renderers.*;

import java.awt.*;
import java.security.DigestException;

public class Game extends ApplicationAdapter {
    private enum GameState {
        START, MENU, PLAYING, GAME_OVER
    }

    private GameState state = GameState.START;
    private SpriteBatch batch;

    private float screenHeight;
    private float screenWidth;

    // Svårighetsgrad
    private MenuRenderer menuRenderer;
    private Difficulty difficulty = Difficulty.EASY;
    // Karaktär
    Character character;
    CharacterRenderer characterRenderer;

    StartButton startButton;
    private float stateTime = 0f;

    // Hinder
    ObstacleRenderer obstacleRenderer;

    // Blommor
    FlowerRenderer flowerRenderer;

    // Poäng
    ScoreRenderer scoreRenderer;
    ScoreManager scoreManager;
    // Ljud
    private Music backgroundMusic;
    private Sound deathSound;

    // Bakgrund
    private BackgroundRenderer backgroundRenderer;
    private float cameraSpeed;

    @Override
    public void create() {
        batch = new SpriteBatch();
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        startButton = new StartButton(400, 200, (screenWidth - 400) / 2, (screenHeight - 200) / 2);
        startButton.loadButton();
        menuRenderer = new MenuRenderer();
        menuRenderer.load(screenWidth);

        obstacleRenderer = new ObstacleRenderer(250);
        obstacleRenderer.loadAssets();

        character = new Character();
        characterRenderer = new CharacterRenderer();
        characterRenderer.loadAssets();

        flowerRenderer = new FlowerRenderer();
        flowerRenderer.loadAssets();

        scoreManager = new ScoreManager();
        scoreRenderer = new ScoreRenderer(scoreManager);
        scoreRenderer.loadAssets();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("MusicBackground.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        deathSound = Gdx.audio.newSound(Gdx.files.internal("BeeDyingSound.mp3"));

        backgroundRenderer = new BackgroundRenderer();
        backgroundRenderer.loadAssets();
        backgroundRenderer.setupLayers();
        cameraSpeed = 100f;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1); // Tillfälligt för att se bakgrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        switch (state) {
            case START:
                renderStart();
                break;
            case MENU:
                renderMenu();
                break;
            case PLAYING:
                renderPlaying(delta);
                break;
            case GAME_OVER:
                renderGameOver(delta);
                break;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        obstacleRenderer.dispose();
        characterRenderer.dispose();
        scoreRenderer.dispose();
        backgroundMusic.dispose();
        flowerRenderer.dispose();
        backgroundRenderer.dispose();
        menuRenderer.dispose();
        deathSound.dispose();
    }

    private void renderStart() {
        startGame();
        batch.begin();
        startButton.renderStartButton(batch);
        batch.end();
    }

    private void renderMenu() {
        handleMenu();
        batch.begin();
        menuRenderer.render(batch, screenWidth, screenHeight);
        batch.end();
    }

    private void renderPlaying(float delta) {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
        handleInput();
        updateGame(delta);
        renderGame();
    }

    private void renderGameOver(float delta) {
        if (!character.isFinishedDying()) {
            character.updateCharacter(delta, screenHeight);
            renderGame();
        }
        handleGameOverInput();
    }

    private void startGame() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            state = GameState.MENU;
        }
    }

    private void startPlaying() {
        state = GameState.PLAYING;

        scoreManager.resetScore();
        scoreRenderer.resetHighscoreFlag();

        character.jump();

        applyDifficulty();
    }

    private void applyDifficulty() {
        switch (difficulty) {

            case EASY:
                obstacleRenderer.setSpeed(200);
                break;

            case MEDIUM:
                obstacleRenderer.setSpeed(300);
                break;

            case HARD:
                obstacleRenderer.setSpeed(450);
                break;
        }
    }

    /*
     * private void startGame() {
     * if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
     * Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
     * Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
     * state = GameState.PLAYING;
     *
     * character.jump();
     *
     * scoreManager.resetScore();
     * scoreRenderer.resetHighscoreFlag();
     *
     * if (!backgroundMusic.isPlaying()) {
     * backgroundMusic.play();
     * }
     * }
     * }
     */

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            character.jump();
        }
    }

    private void handleMenu() {
        menuRenderer.update();

        Difficulty selected = menuRenderer.checkSelection();
        if (selected != null) {
            requestStart(selected);
        }
    }

    private void requestStart(Difficulty diff) {
        difficulty = diff;
        startPlaying();
    }

    private void updateGame(float delta) {
        backgroundRenderer.update(delta, cameraSpeed);
        updateMusic();
        character.updateCharacter(delta, screenHeight);

        hasHitGround();

        obstacleRenderer.spawnObstacles(delta);
        obstacleRenderer.updateObstacles(delta);

        if (obstacleRenderer.checkCollision(character)) {
            gameOver();
        }

        scoreManager.checkObstaclePassed(character, obstacleRenderer.getObstacles());

        flowerRenderer.updateFlowers(delta, obstacleRenderer.getObstacleSpeed());
        handleFlowerCollisions();
    }

    private void hasHitGround() {
        if (character.hasHitGround()) {
            gameOver();
        }
    }

    private void renderGame() {
        batch.begin();
        backgroundRenderer.render(batch);
        flowerRenderer.renderFlowers(batch, stateTime);
        characterRenderer.renderBee(character.isDying(), stateTime, batch, character.startX(), character.characterY());
        obstacleRenderer.renderObstacles(batch);
        // Rita poäng
        scoreRenderer.renderScore(batch);
        batch.end();
    }

    private void handleFlowerCollisions() {
        for (Flower f : flowerRenderer.getFlowers()) {
            if (Intersector.overlaps(character.getCharacterArea(), f.getHitbox())) {
                f.collect();
                scoreManager.incrementPoint();
                flowerRenderer.getFlowerSound().play(0.5f);
            }
        }
    }

    private void gameOver() {
        state = GameState.GAME_OVER;
        deathSound.play();
        character.setDying(true);
        stateTime = 0f;

        scoreManager.checkHighScore();

        if (backgroundMusic.isPlaying()) {
            resetMusic();
        }
    }

    private void handleGameOverInput() {
        if (character.isFinishedDying() && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            restartGame();
        }
    }

    private void updateMusic() {
        if (state == GameState.PLAYING) {
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            }
        } else {
            if (backgroundMusic.isPlaying()) {
                backgroundMusic.stop();
            }
        }
    }

    private void resetMusic() {
        backgroundMusic.stop();
        backgroundMusic.setPosition(0f);
    }

    private void restartGame() {
        character.resetCharacter(-120, 540, 600);
        obstacleRenderer.resetObstacles(250);
        flowerRenderer.clearFlowers();

        scoreManager.resetScore();
        scoreRenderer.resetHighscoreFlag();


        state = GameState.MENU;

        stateTime = 0f;
    }
}
