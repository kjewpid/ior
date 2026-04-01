package se.yrgo.game;

import com.badlogic.gdx.graphics.Texture;

public class Obstacle {
    private float x;
    private float gapY;
    private float gapHeight = 300;
    private float obstacleWidth;
    private boolean passedObstacle = false;

    public Obstacle(float x, float gapY, Texture texture) {
        this.x = x;
        this.gapY = gapY;
        this.obstacleWidth = texture.getWidth() / 2;
    }

    public void update(float deltaTime, float speed) {
        x -= speed * deltaTime;
    }

    public boolean isOffScreen() {
        return x + 60 < 0;
    }

    public float getX() {
        return x;
    }

    public float getGapY() {
        return gapY;
    }

    public float getGapHeight() {
        return gapHeight;
    }

    public float getObstacleWidth() {
        return obstacleWidth;
    }

    public boolean hasPassed() {
        return passedObstacle;
    }

    public void setPassed() {
        this.passedObstacle = true;
    }
}

