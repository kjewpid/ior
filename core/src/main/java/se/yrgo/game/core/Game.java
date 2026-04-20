package se.yrgo.game.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Intersector;

import com.badlogic.gdx.audio.Music;
import se.yrgo.game.entities.Character;
import se.yrgo.game.entities.Flower;
import se.yrgo.game.renderers.*;

public class Game extends ApplicationAdapter {
    private enum GameState {
        START, MENU, PLAYING, GAME_OVER
    }

    private GameState state = GameState.START;
    private SpriteBatch batch;

    private float screenHeight;
    private float screenWidth;

    // Svårighetsgrad
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private Texture menuBackground;
    private Difficulty difficulty;

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

        menuBackground = new Texture(Gdx.files.internal("Menu/menu.png"));
        difficulty = Difficulty.EASY;

        float buttonWidth = new Texture(Gdx.files.internal("Menu/Easy.png")).getWidth();
        float centerX = (screenWidth - buttonWidth) / 2;

        easyButton = new Button(centerX, 660,
                "Menu/Easy.png",
                "Menu/Easy_hover.png");

        mediumButton = new Button(
                centerX, 450,
                "Menu/Normal.png",
                "Menu/Normal_hover.png");

        hardButton = new Button(
                centerX, 215,
                "Menu/Hard.png",
                "Menu/Hard_hover.png");

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
                startGame();
                startButton.renderStartButton(batch);
                break;
            case MENU:
                handleMenu();

                batch.begin();

                float bgWidth = 750;
                float bgHeight = 950;

                float bgX = (screenWidth - bgWidth) / 2;
                float bgY = (screenHeight - bgHeight) / 2;

                batch.draw(menuBackground, bgX, bgY, bgWidth, bgHeight);

                easyButton.render(batch);
                mediumButton.render(batch);
                hardButton.render(batch);

                batch.end();
                break;
            case PLAYING:
                if (!backgroundMusic.isPlaying()) {
                    backgroundMusic.play();
                }
                handleInput();
                updateGame(delta);
                renderGame();
                break;
            case GAME_OVER:
                // GameOverScreen method
                if (!character.isFinishedDying()) {
                    character.updateCharacter(delta, screenHeight);
                    renderGame();
                }
                handleGameOverInput();
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
        hardButton.dispose();
        mediumButton.dispose();
        easyButton.dispose();
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

        easyButton.update();
        mediumButton.update();
        hardButton.update();

        if (easyButton.isClicked()) {
            difficulty = Difficulty.EASY;
            startPlaying();
        }

        if (mediumButton.isClicked()) {
            difficulty = Difficulty.MEDIUM;
            startPlaying();
        }

        if (hardButton.isClicked()) {
            difficulty = Difficulty.HARD;
            startPlaying();
        }
    }

    private void updateGame(float delta) {
        backgroundRenderer.update(delta, cameraSpeed);

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
        character.setDying(true);
        stateTime = 0f;

        scoreManager.checkHighScore();

        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    private void handleGameOverInput() {
        if (character.isFinishedDying() && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            restartGame();
        }
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
