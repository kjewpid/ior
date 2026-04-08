package se.yrgo.game.entities;

import com.badlogic.gdx.math.Circle;

public class Flower {
    private float x;
    private float y;
    private boolean collected = false;
    private float radius = 30; // används för collision

    public Flower(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta, float speed) {
        x -= speed * delta;
    }

    public Circle getHitbox() {
        return new Circle(x, y, radius);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}
