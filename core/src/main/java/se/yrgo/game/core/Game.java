package se.yrgo.game.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Music;
import se.yrgo.game.entities.Character;
import se.yrgo.game.entities.Flower;
import se.yrgo.game.entities.Obstacle;
import se.yrgo.game.renderers.CharacterRenderer;

public class Game extends ApplicationAdapter {
    private enum GameState {
        START, PLAYING, GAME_OVER
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

    // Hinder
    private float obstacleDistance = 700;
    private float obstacleSpeed = 250;
    private float spawnRate = obstacleDistance / obstacleSpeed;
    private Texture obstacleImage;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private float spawnTimer = 0;
    private int totalObstaclesSpawned = 0;

    // Poäng
    private ScoreManager scoreManager;
    private BitmapFont font;
    private GlyphLayout layout;

    private TextureAtlas flowerAtlas;
    private TextureAtlas flowerGlowAtlas;
    private Animation<TextureRegion> flowerAnimation;
    private Animation<TextureRegion> flowerGlowAnimation;

    private float flowerX;
    private float flowerY;

    // Ljud
    private Music backgroundMusic;
    private Sound highscoreSound;
    private boolean newHighscorePlayed = false;

    // Blommor
    private ArrayList<Flower> flowers = new ArrayList<>();
    private Texture flowerImage;

    @Override
    public void create() {
        batch = new SpriteBatch();
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        startButton = new StartButton(400, 200, (screenWidth - 400) / 2, (screenHeight - 200) / 2);
        startButton.loadButton();

        obstacleImage = new Texture("Obstacle.PNG");


        character = new Character();
        characterRenderer = new CharacterRenderer();
        characterRenderer.loadAssets();

        flowerImage = new Texture("flower.png");

        flowerAtlas = new TextureAtlas(Gdx.files.internal("flower/flower.atlas"));
        flowerGlowAtlas = new TextureAtlas(Gdx.files.internal("flower/glow.atlas"));
        flowerAnimation = new Animation<>(0.1f, flowerAtlas.getRegions(), Animation.PlayMode.LOOP);
        flowerGlowAnimation = new Animation<>(0.1f, flowerGlowAtlas.getRegions(), Animation.PlayMode.LOOP);

        scoreManager = new ScoreManager();
        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);
        font.getData().setScale(3);

        layout = new GlyphLayout();

        highscoreSound = Gdx.audio.newSound(Gdx.files.internal("HighScoreSound.wav"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("MusicBackground.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
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
                checkCollision(delta, screenHeight);
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
        obstacleImage.dispose();
        highscoreSound.dispose();
        backgroundMusic.dispose();
    }


    private void startGame() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            state = GameState.PLAYING;
            character.jump();
            newHighscorePlayed = false;
            scoreManager.resetScore();

            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            character.jump();
        }
    }

    private void updateGame(float delta) {
        character.updateCharacter(delta, screenHeight);
        hasHitGround();
        spawnObstacles(delta);
        updateObstacles(delta);
        updateFlowers(delta);
    }

    private void hasHitGround(){
        if (character.hasHitGround()){
            gameOver();
        }
    }
    private void updateFlowers(float delta) {
        // Blommor
        // Slumpmässig spawn av blommor
        if (Math.random() < 0.02) { // ca 2% chans varje frame
            float y = 50 + (float) (Math.random() * (screenHeight - 100));
            flowers.add(new Flower(screenWidth, y));
        }

        // Uppdatera blommor, kolla collision och ta bort samlade eller missade
        for (int i = flowers.size() - 1; i >= 0; i--) {
            Flower f = flowers.get(i);
            f.update(delta, obstacleSpeed);

            // Ta bort blommor som lämnat skärmen
            if (f.getX() < -50 || f.isCollected()) {
                flowers.remove(i);
                continue;
            }

            // Kolla collision med spelaren
            if (Intersector.overlaps(character.getCharacterArea(), f.getHitbox())) {
                f.collect();
                scoreManager.incrementPoint();
            }
        }
    }

    private void spawnObstacles(float delta) {
        spawnTimer += delta;

        if (spawnTimer > spawnRate) { // Spawna hinder varje x sekund
            float gapHeight = 300;
            float minObstacleHeight = 100;
            float gapY = minObstacleHeight
                + (float) (Math.random() * (Gdx.graphics.getHeight() - gapHeight - 2 * minObstacleHeight));
            obstacles.add(new Obstacle(Gdx.graphics.getWidth(), gapY, obstacleImage));
            spawnTimer -= spawnRate;

            totalObstaclesSpawned++;

            if (totalObstaclesSpawned % 10 == 0) { // Öka spawnrate och speed
                obstacleSpeed *= 1.1f;
                if (obstacleSpeed >= 800) { // Max speed
                    obstacleSpeed = 800;
                }
                spawnRate = obstacleDistance / obstacleSpeed;
            }
        }
    }

    private void updateObstacles(float delta) {
        obstacles.forEach(o -> o.update(delta, obstacleSpeed));
        obstacles.removeIf(o -> o.getX() + 100 < 0);
        obstacles.forEach(o -> {
            if (o.getX() + o.getObstacleWidth() / 2 < character.characterX() && !o.hasPassed()) {
                scoreManager.incrementPoint();
                o.setPassed();
            }
        });
    }

    public void score() {
        String scoreText = "Score: " + scoreManager.getScore();
        String highScoreText = "Highscore: " + scoreManager.getHighScore();

        layout.setText(font, scoreText);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = Gdx.graphics.getHeight() - 100;
        font.draw(batch, scoreText, x, y);

        font.draw(batch, highScoreText, 20, Gdx.graphics.getHeight() - 20);

        if (scoreManager.getScore() == scoreManager.getHighScore() && scoreManager.getScore() > 0
            && !newHighscorePlayed) {
            highscoreSound.play(0.7f);
            newHighscorePlayed = true;
        }
    }

    private void renderGame() {
        batch.begin();
        renderFlowers();
        characterRenderer.renderBee(character.isDying(), stateTime, batch, character.startX(), character.characterY());
        renderObstacles();
        // Rita poäng
        score();
        batch.end();
    }

    private void renderFlowers() {
        TextureRegion flowerFrame;
        TextureRegion glowFrame;

        float width = 50;
        float height = 50;

        for (Flower f : flowers) {
            flowerFrame = flowerAnimation.getKeyFrame(stateTime);
            glowFrame = flowerGlowAnimation.getKeyFrame(stateTime);

            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batch.draw(glowFrame, f.getX() - width / 2, f.getY() - height / 2, width, height);

            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            // batch.setColor(1f, 0.95f, 0.95f, 2f);
            batch.draw(flowerFrame, f.getX() - width / 2, f.getY() - height / 2, width, height);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderObstacles() {
        for (Obstacle o : obstacles) {
            // Scale the image proportionally
            float scale = o.getObstacleWidth() / obstacleImage.getWidth();
            float scaledHeight = obstacleImage.getHeight() * scale;

            // Draw bottom obstacle
            float bottomHeight = o.getGapY();
            batch.draw(
                obstacleImage,
                o.getX(),
                0,
                o.getObstacleWidth(),
                Math.min(scaledHeight, bottomHeight),
                0,
                0,
                obstacleImage.getWidth(),
                (int) Math.min(obstacleImage.getHeight(), bottomHeight / scale),
                false,
                false
            );

            // Draw top obstacle
            float topY = o.getGapY() + o.getGapHeight();
            float topHeight = Gdx.graphics.getHeight() - topY;
            batch.draw(
                obstacleImage,
                o.getX(),
                topY,
                o.getObstacleWidth(),
                Math.min(scaledHeight, topHeight),
                0,
                0,
                obstacleImage.getWidth(),
                (int) Math.min(obstacleImage.getHeight(), topHeight / scale),
                false,
                true
            );
        }
    }


    private void checkCollision(float delta, float screenHeight) {
        for (Obstacle o : obstacles) {
            Rectangle topRectangle = new Rectangle(o.getX(), 0, 100, o.getGapY());
            Rectangle bottomRectangle = new Rectangle(o.getX(), o.getGapY() + o.getGapHeight(), 100,
                Gdx.graphics.getHeight() - (o.getGapY() + o.getGapHeight()));

            if (Intersector.overlaps(character.getCharacterArea(), topRectangle) || Intersector.overlaps(character.getCharacterArea(), bottomRectangle)) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        state = GameState.GAME_OVER;
        character.setDying(true);
        stateTime = 0f;
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

        obstacles.clear();
        flowers.clear();
        spawnTimer = 0;
        totalObstaclesSpawned = 0;

        obstacleSpeed = 200;
        spawnRate = obstacleDistance / obstacleSpeed;

        scoreManager.resetScore();
        newHighscorePlayed = false;

        state = GameState.PLAYING;

        stateTime = 0f;
        backgroundMusic.play();
    }
}
