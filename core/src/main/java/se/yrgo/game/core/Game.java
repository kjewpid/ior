package se.yrgo.game.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
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

    // Karaktär
    Character character;
    CharacterRenderer characterRenderer;

    StartButton startButton;
    private float stateTime = 0f;

    //Hinder
    ObstacleRenderer obstacleRenderer;

    //Blommor
    FlowerRenderer flowerRenderer;

    // Poäng
    ScoreRenderer scoreRenderer;
    ScoreManager scoreManager;
    // Ljud
    private Music backgroundMusic;

    //Bakgrund
    private BackgroundRenderer backgroundRenderer;
    private float cameraSpeed;

    @Override
    public void create() {
        batch = new SpriteBatch();
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        startButton = new StartButton(400, 200, (screenWidth - 400) / 2, (screenHeight - 200) / 2);
        startButton.loadButton();

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
            case PLAYING:
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
    }

    private void startGame() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            state = GameState.PLAYING;
            character.jump();

            scoreManager.resetScore();
            scoreRenderer.resetHighscoreFlag();


            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            character.jump();
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

        state = GameState.PLAYING;

        stateTime = 0f;
        backgroundMusic.play();
    }
}
