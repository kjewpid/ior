package se.yrgo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Game extends ApplicationAdapter {
    private float characterY = 540;
    private float velocity = 600;
    private float gravity = -1500;
    private boolean gameStarted = false;
    private SpriteBatch batch;

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

    @Override
    public void create() {
        batch = new SpriteBatch();
        characterImage = new Texture(Gdx.files.internal("assets/Character.png"));
        startImage = new Texture(Gdx.files.internal("assets/StartImage.png"));
        buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2;
        buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1);  // Tillfälligt för att se bakgrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameStarted && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameStarted = true;
            jump();
        }

        if (!gameStarted) {
            batch.begin();
            batch.draw(startImage, buttonX, buttonY, buttonWidth, buttonHeight);
            batch.end();
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        // Kolla input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            jump();
        }

        // Karaktär-kod
        float screenHeight = Gdx.graphics.getHeight();
        if (startX < characterX) {
            startX += flySpeed * delta;
            if (startX > characterX) {
                startX = characterX;
            }
        }

        if (characterY > screenHeight) {
            characterY = screenHeight;
            velocity = 0;
        }

        if (characterY < 0) {
            characterY = 0;
            if (velocity < 0) {
                velocity = 0;
            }
        }

        batch.begin();
        batch.draw(characterImage, startX - 30, characterY - 30, 120, 120);
        batch.end();

        velocity += gravity * delta;
        characterY += velocity * delta;
    }

    // NY METOD FÖR HOPP
    private void jump() {
        velocity = 600;
    }

    @Override
    public void dispose() {
        batch.dispose();
        characterImage.dispose();
        startImage.dispose();
    }
}
