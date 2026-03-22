package se.yrgo.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Game extends ApplicationAdapter {
    private enum GameState {
        START,
        PLAYING,
        GAME_OVER
    }

    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;
    private GameState state = GameState.START;
    private SpriteBatch batch;
    private boolean isDying = false;
    private boolean finishedDying = false;

    //StartImage
    private float buttonWidth = 400;
    private float buttonHeight = 200;
    private float buttonX, buttonY;
    private Texture startImage;

    //Karaktär
    private Texture characterImage;
    private float startX = -120;
    private float characterX = 400;
    private float flySpeed = 600;

    //Hinder

    private float obstacleDistance = 500;
    private float obstacleSpeed = 200;
    private float spawnRate = obstacleDistance / obstacleSpeed;
    private Texture obstacleImage;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private float spawnTimer = 0;
    private int totalObstaclesSpawned = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        characterImage = new Texture(Gdx.files.internal("Character.png"));
        startImage = new Texture(Gdx.files.internal("StartImage.png"));
        obstacleImage = new Texture("Obstacle.JPG");
        buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1);  // Tillfälligt för att se bakgrund
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
        characterImage.dispose();
        startImage.dispose();
        obstacleImage.dispose();
    }

    // NY METOD FÖR HOPP
    private void jump() {
        velocity = 600;
    }

    private void startGame() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            state = GameState.PLAYING;
            jump();
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
    }

    private void spawnObstacles(float delta) {
        spawnTimer += delta;

        if (spawnTimer > spawnRate) { // Spawna hinder varje x sekund
            float gapHeight = 300;
            float gapY = (float) (Math.random() * (Gdx.graphics.getHeight() - gapHeight - 50));
            obstacles.add(new Obstacle(Gdx.graphics.getWidth(), gapY, obstacleImage));
            spawnTimer -= spawnRate;

            totalObstaclesSpawned++;

            if (totalObstaclesSpawned % 10 == 0) { // Öka spawnrate och speed
                obstacleSpeed *= 1.1f;
                if (obstacleSpeed == 800) { // Max speed
                    obstacleSpeed = 800;
                }
                spawnRate = obstacleDistance / obstacleSpeed;
            }
        }
    }

    private void updateObstacles(float delta) {
        obstacles.forEach(o -> o.update(delta, obstacleSpeed));
        obstacles.removeIf(o -> o.getX() + 100 < 0);
    }

    private void renderGame() {
        batch.begin();
        batch.draw(characterImage, startX - 30, characterY - 30, 120, 120);

        for (Obstacle o : obstacles) {
            batch.draw(obstacleImage, o.getX(), 0, 100, o.getGapY());

            batch.draw(obstacleImage, o.getX(), o.getGapY() + o.getGapHeight(),
                100, Gdx.graphics.getHeight() - (o.getGapY() + o.getGapHeight()),
                0, 0, obstacleImage.getWidth(), obstacleImage.getHeight(),
                false, true);

        }
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
            Rectangle bottomRectangle = new Rectangle(o.getX(), o.getGapY() + o.getGapHeight(), 100,
                Gdx.graphics.getHeight() - (o.getGapY() + o.getGapHeight()));

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

        state = GameState.PLAYING;
    }
}
