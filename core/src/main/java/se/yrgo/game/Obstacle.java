package se.yrgo.game;

import com.badlogic.gdx.graphics.Texture;

public class Obstacle {
    private float x;
    private float gapY;
    private float gapHeight = 300;
    //private Texture texture;
    private boolean passedObstacle = false;

    public Obstacle(float x, float gapY, Texture texture) {
        this.x = x;
        this.gapY = gapY;
        // this.texture = texture;
    }

    public void update(float deltaTime, float speed) {
        x -= speed * deltaTime;
    }

    public boolean isOffScreen() {
        // return x + texture.getWidth() < 0;
        return x + 50 < 0;
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

    public boolean passedObstacle() {
        return passedObstacle;
    }

    public void setPassedObstacle() {
        this.passedObstacle = true;
    }
}

