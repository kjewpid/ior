package se.yrgo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Sound;


public class Game extends ApplicationAdapter {
    private enum GameState {
        START, PLAYING, GAME_OVER
    }

    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;
    private GameState state = GameState.START;
    private SpriteBatch batch;
    private boolean isDying = false;
    private boolean finishedDying = false;

    private float buttonWidth = 400;
    private float buttonHeight = 200;
    private float buttonX, buttonY;
    private Texture startImage;

    // Karaktär
    private float startX = -120;
    private float characterX = 400;
    private float flySpeed = 600;

    private TextureAtlas beeBodyAtlas;
    private TextureAtlas frontWingAtlas;

    private TextureAtlas backWingAtlas;
    private Animation<TextureRegion> bodyAnimation;
    private Animation<TextureRegion> frontWingAnimation;
    private Animation<TextureRegion> backWingAnimation;

    private float stateTime = 0f;
    //Hinder
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
    private Sound highscoreSound;
    private boolean newHighscorePlayed = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        startImage = new Texture(Gdx.files.internal("StartImage.png"));
        obstacleImage = new Texture("Obstacle.JPG");
        buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2;

        beeBodyAtlas = new TextureAtlas(Gdx.files.internal("bee/bee.atlas"));
        frontWingAtlas = new TextureAtlas(Gdx.files.internal("bee/wings_front.atlas"));
        backWingAtlas = new TextureAtlas(Gdx.files.internal("bee/wings_back.atlas"));

        bodyAnimation = new Animation<>(0.1f, beeBodyAtlas.getRegions(), Animation.PlayMode.LOOP);
        frontWingAnimation = new Animation<>(0.1f, frontWingAtlas.getRegions(), Animation.PlayMode.LOOP);
        backWingAnimation = new Animation<>(0.1f, backWingAtlas.getRegions(), Animation.PlayMode.LOOP);

        scoreManager = new ScoreManager();
        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);
        font.getData().setScale(3);

        layout = new GlyphLayout();

        highscoreSound = Gdx.audio.newSound(Gdx.files.internal("HighScoreSound.wav"));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1); // Tillfälligt för att se bakgrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        switch (state) {
            case START:
                startGame();
                renderStartButton();
                break;
            case PLAYING:
                handleInput();
                updateGame(delta);
                renderGame();
                checkCollision();
                break;
            case GAME_OVER:
                //GameOverScreen method
                if (!finishedDying) {
                    updateCharacter(delta);
                    renderGame();
                }
                handleGameOverInput();
                break;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        startImage.dispose();
        obstacleImage.dispose();
        highscoreSound.dispose();

    }

    private void jump() {
        velocity = 600;
    }

    private void startGame() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            state = GameState.PLAYING;
            jump();
            newHighscorePlayed = false;
            scoreManager.resetScore();
        }
    }

    private void renderStartButton() {
        batch.begin();
        batch.draw(startImage, buttonX, buttonY, buttonWidth, buttonHeight);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            jump();
        }
    }

    private void updateGame(float delta) {
        updateCharacter(delta);
        spawnObstacles(delta);
        updateObstacles(delta);
    }

    private void updateCharacter(float delta) {
        float screenHeight = Gdx.graphics.getHeight();
        if (startX < characterX) {
            startX += flySpeed * delta;
            if (startX > characterX) {
                startX = characterX;
            }
        }

        if (!isDying) {
            velocity += gravity * delta;
        } else {
            velocity += gravity * 1.5 * delta;
        }
        characterY += velocity * delta;

        if (characterY > screenHeight && !isDying) {
            characterY = screenHeight;
            velocity = 0;
        }

        if (!isDying && characterY < 0) {
            characterY = 0;
            velocity = 0;
        }

        if (isDying && characterY < -120) {
            velocity = 0;
            finishedDying = true;
        }

        stateTime += Gdx.graphics.getDeltaTime();
    }

    private void spawnObstacles(float delta) {
        spawnTimer += delta;

        if (spawnTimer > spawnRate) { // Spawna hinder varje x sekund
            float gapHeight = 300;
            float minObstacleHeight = 100;
            float gapY = minObstacleHeight + (float) (Math.random() * (Gdx.graphics.getHeight() - gapHeight - 2 * minObstacleHeight));
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
            if (o.getX() + 50 < characterX && !o.hasPassed()) { //+50 för att x axel är i mitten av hinder, hinder är 100 brett
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

        if (scoreManager.getScore() == scoreManager.getHighScore() && scoreManager.getScore() > 0 && !newHighscorePlayed) {
            highscoreSound.play(0.7f);
            newHighscorePlayed = true;
        }
    }

    private void renderGame() {
        batch.begin();
        TextureRegion bodyFrame = bodyAnimation.getKeyFrame(stateTime);
        TextureRegion frontWingFrame = frontWingAnimation.getKeyFrame(stateTime);
        TextureRegion backWingFrame = backWingAnimation.getKeyFrame(stateTime);

        float width = 120;
        float height = 120;

// Draw back wing first
        batch.draw(backWingFrame, startX - width / 2, characterY - height / 2, width, height);

// Draw body
        batch.draw(bodyFrame, startX - width / 2, characterY - height / 2, width, height);

// Draw front wing on top
        batch.draw(frontWingFrame, startX - width / 2, characterY - height / 2, width, height);

        for (Obstacle o : obstacles) {
            batch.draw(obstacleImage, o.getX(), 0, 100, o.getGapY());

            batch.draw(obstacleImage, o.getX(), o.getGapY() + o.getGapHeight(), 100, Gdx.graphics.getHeight() - (o.getGapY() + o.getGapHeight()), 0, 0, obstacleImage.getWidth(), obstacleImage.getHeight(), false, true);
        }
        score();
        batch.end();
    }

    private Circle getCharacterArea() {
        float radius = 60;
        float centerX = startX;
        float centerY = characterY;
        return new Circle(centerX, centerY, radius);
    }

    private void checkCollision() {
        Circle character = getCharacterArea();

        for (Obstacle o : obstacles) {
            Rectangle topRectangle = new Rectangle(o.getX(), 0, 100, o.getGapY());
            Rectangle bottomRectangle = new Rectangle(o.getX(), o.getGapY() + o.getGapHeight(), 100, Gdx.graphics.getHeight() - (o.getGapY() + o.getGapHeight()));

            if (Intersector.overlaps(character, topRectangle) || Intersector.overlaps(character, bottomRectangle)) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        state = GameState.GAME_OVER;
        isDying = true;
    }

    private void handleGameOverInput() {
        if (finishedDying && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            restartGame();
        }
    }

    private void restartGame() {
        startX = -120;
        characterY = 540;
        velocity = 600;

        obstacles.clear();
        spawnTimer = 0;
        totalObstaclesSpawned = 0;

        obstacleSpeed = 200;
        spawnRate = obstacleDistance / obstacleSpeed;

        scoreManager.resetScore();

        isDying = false;
        finishedDying = false;

        state = GameState.PLAYING;
    }
}
