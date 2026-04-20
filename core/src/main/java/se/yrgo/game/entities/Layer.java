package se.yrgo.game.entities;

import com.badlogic.gdx.graphics.Texture;

public class Layer {
    private Texture texture;
    private float speed;
    private float y;
    private boolean repeat;
    private int instances;
    private float scale = 1f;
    float offsetX = 0;
    boolean isOverlay = false;

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

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setOverlay(){
        this.isOverlay = true;
    }

    public boolean isOverlay(){
        return isOverlay;
    }
}
