package se.yrgo.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import se.yrgo.game.entities.Obstacle;
import se.yrgo.game.entities.Character;

import java.util.ArrayList;

public class ObstacleRenderer {
    private float obstacleDistance = 700;
    private float obstacleSpeed;
    private float spawnRate = obstacleDistance / obstacleSpeed;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private float spawnTimer = 0;
    private int totalObstaclesSpawned = 0;
    private float gapHeight = 300;
    private float minObstacleHeight = 100;

    private Texture obstacleImage;

    public ObstacleRenderer(float obstacleSpeed) {
        this.obstacleSpeed = obstacleSpeed;
    }

    public void loadAssets() {
        obstacleImage = new Texture("Obstacle.PNG");
    }

    public void renderObstacles(SpriteBatch batch) {
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
                false);

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
                true);
        }
    }

    public void spawnObstacles(float delta) {
        spawnTimer += delta;

        if (spawnTimer > spawnRate) { // Spawna hinder varje x sekund
            float screenHeight = Gdx.graphics.getHeight();
            float screenWidth = Gdx.graphics.getWidth();
            float gapY = minObstacleHeight
                + (float) (Math.random() * (screenHeight - gapHeight - 2 * minObstacleHeight));
            obstacles.add(new Obstacle(screenWidth, gapY, obstacleImage));
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

    public void updateObstacles(float delta) {
        obstacles.forEach(o -> o.update(delta, obstacleSpeed));
        obstacles.removeIf(o -> o.getX() + 100 < 0);
    }

    public boolean checkCollision(Character character) {
        for (Obstacle o : obstacles) {
            Rectangle topRectangle = new Rectangle(o.getX(), 0, 100, o.getGapY());
            Rectangle bottomRectangle = new Rectangle(o.getX(), o.getGapY() + o.getGapHeight(), 100,
                Gdx.graphics.getHeight() - (o.getGapY() + o.getGapHeight()));

            if (Intersector.overlaps(character.getCharacterArea(), topRectangle)
                || Intersector.overlaps(character.getCharacterArea(), bottomRectangle)) {
                return true;
            }
            return false;
        }
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void dispose() {
        obstacleImage.dispose();
    }

    public void resetObstacles(float speed) {
        obstacles.clear();
        this.obstacleSpeed = speed;
        spawnRate = obstacleDistance / obstacleSpeed;
        spawnTimer = 0f;
        totalObstaclesSpawned = 0;
    }

    public float getObstacleSpeed() {
        return obstacleSpeed;
    }
}
