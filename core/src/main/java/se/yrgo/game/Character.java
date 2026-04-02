package se.yrgo.game;

import com.badlogic.gdx.math.Circle;

public class Character {
    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;
    private boolean isDying = false;
    private boolean finishedDying = false;
    private boolean hitGround = false;
    private float startX = -120;
    private float characterX = 400;
    private float flySpeed = 600;

    public Character() {

    }

    public Character(float startX, float characterY, float velocity, float gravity, float characterX, float flySpeed) {
        this.startX = startX;
        this.characterY = characterY;
        this.velocity = velocity;
        this.gravity = gravity;
        this.characterX = characterX;
        this.flySpeed = flySpeed;
    }


    public void jump() {
        velocity = 600;
    }

    public void updateCharacter(float delta, float screenHeight) {
        if (startX < characterX) {
            startX += flySpeed * delta;
            if (startX > characterX) {
                startX = characterX;
            }
        }

        if (!isDying) {
            velocity += gravity * delta;
        } else {
            velocity += gravity * 1.5f * delta;
        }
        characterY += velocity * delta;

        if (characterY > screenHeight && !isDying) {
            characterY = screenHeight;
            velocity = 0;
        }

        if (!isDying && characterY < 0) {
            characterY = 0;
            velocity = 0;
            hitGround = true;
        }

        if (isDying && characterY < -120) {
            velocity = 0;
            finishedDying = true;
        }
    }

    public void resetCharacter(float startX, float characterY, float velocity) {
        this.startX = startX;
        this.characterY = characterY;
        this.velocity = velocity;
        this.isDying = false;
        this.finishedDying = false;
        this.hitGround = false;
    }

    public Circle getCharacterArea() {
        float radius = 50;
        float centerX = startX;
        float centerY = characterY;
        return new Circle(centerX, centerY, radius);
    }

    public boolean hasHitGround(){
        return hitGround;
    }
    public float characterY() {
        return characterY;
    }

    public boolean isDying() {
        return isDying;
    }

    public void setDying(boolean isDying) {
        this.isDying = isDying;
    }

    public boolean isFinishedDying() {
        return finishedDying;
    }

    public float startX() {
        return startX;
    }

    public float characterX() {
        return characterX;
    }
}
