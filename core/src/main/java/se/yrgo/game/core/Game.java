package se.yrgo.game.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.Preferences;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.utils.viewport.Viewport;
import se.yrgo.game.entities.Character;
import se.yrgo.game.entities.Flower;
import se.yrgo.game.renderers.*;

import java.awt.*;

public class Game extends ApplicationAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;

    private enum GameState {
        START, MENU, PLAYING, GAME_OVER
    }

    private Preferences prefs;

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

        // Sätt screensize
        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        viewport.apply();
        screenWidth = viewport.getWorldWidth();
        screenHeight = viewport.getWorldHeight();
        camera.position.set(screenWidth / 2f, screenHeight / 2f, 0);
        camera.update();

        prefs = Gdx.app.getPreferences("GameSettings");

        // Ladda sparad difficulty (default = EASY om inget finns)
        String savedDifficulty = prefs.getString("difficulty", "EASY");
        difficulty = Difficulty.valueOf(savedDifficulty);

        batch = new SpriteBatch();

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
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        startButton.renderStartButton(batch);
        batch.end();
    }

    private void renderMenu() {
        handleMenu();
        batch.setProjectionMatrix(camera.combined);
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
        if (state == GameState.MENU &&
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            startPlaying();
        }
    }

    private void requestStart(Difficulty diff) {
        difficulty = diff;

        // Spara valet
        prefs.putString("difficulty", diff.name());
        prefs.flush(); // sparar på riktigt!

        startPlaying();
    }

    private void updateGame(float delta) {
        backgroundRenderer.update(delta, cameraSpeed);
        updateMusic();
        character.updateCharacter(delta, screenHeight);

        hasHitGround();

        obstacleRenderer.spawnObstacles(delta, viewport.getWorldHeight(), viewport.getWorldWidth());
        obstacleRenderer.updateObstacles(delta);

        if (obstacleRenderer.checkCollision(character)) {
            gameOver();
        }

        scoreManager.checkObstaclePassed(character, obstacleRenderer.getObstacles());

        flowerRenderer.updateFlowers(delta, obstacleRenderer.getObstacleSpeed(), viewport.getWorldHeight(), viewport.getWorldWidth());
        handleFlowerCollisions();
    }

    private void hasHitGround() {
        if (character.hasHitGround()) {
            gameOver();
        }
    }

    private void renderGame() {
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        backgroundRenderer.render(batch);
        flowerRenderer.renderFlowers(batch, stateTime);
        characterRenderer.renderBee(character.isDying(), stateTime, batch, character.startX(), character.characterY());
        obstacleRenderer.renderObstacles(batch, viewport.getWorldHeight());
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

        camera.position.set(screenWidth / 2f, screenHeight / 2f, 0);
        camera.update();
    }
}
