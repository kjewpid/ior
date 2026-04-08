package se.yrgo.game.entities;

import com.badlogic.gdx.graphics.Texture;

public class Layer {
    Texture texture;
    float speed;
    float y;
    boolean repeat;
    int instances;

    float offsetX = 0;

    public Layer(Texture texture, float speed, float y, boolean repeat, int instances) {
        this.texture = texture;
        this.speed = speed;
        this.y = y;
        this.repeat = repeat;
        this.instances = instances;

    }

    public Texture getTexture() {
        return texture;
    }

    public float getSpeed() {
        return speed;
    }

    public float getY() {
        return y;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getInstances() {
        return instances;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }
}
